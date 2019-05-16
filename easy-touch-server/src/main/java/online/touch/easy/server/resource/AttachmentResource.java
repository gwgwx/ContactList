/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.server.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import online.touch.easy.common.model.Attachment;
import org.apache.commons.io.IOUtils;
//import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author George-2014
 */
@Path("/attachments")
public class AttachmentResource {

    public static final String UPLOADED_FILE_PARAMETER_NAME = "file";

    public static final String UPLOADED_UID_PARAMETER_NAME = "uid";

    public static final String UPLOAD_DIR = "/tmp";

    private static final Logger LOG = Logger.getLogger(AttachmentResource.class.getName());

//    @Inject
//    @ConfigProperty(name = "config.filelocation")
    private String resourceLocation = "C:/junk/easy-touch";

    @PersistenceContext(unitName = "online.touch.easy_pu")
    private EntityManager em;

    public String getResourceLocation() {
        return resourceLocation;
    }

    private String uid;

    @POST
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput file) {
        LOG.fine(">>>> starting file upload...");

        Map<String, List<InputPart>> uploadForm = file.getFormDataMap();
        List<InputPart> inputIds = uploadForm.get(UPLOADED_UID_PARAMETER_NAME);
        if (inputIds == null || inputIds.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"article uid is required\"}").build();
        }
        for (InputPart inputPart : inputIds) {
            try {
                System.err.println("inputIds is: " + inputPart.getBodyAsString());
                uid = inputPart.getBodyAsString();
            } catch (IOException ex) {
                Logger.getLogger(AttachmentResource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (uid == null || uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"article uid is required\"}").build();
        }

        List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();

        for (InputPart inputPart : inputParts) {
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String filename = getFileName(headers);

            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);

                LOG.log(Level.FINE, ">>> File '{0}' has been read, size: #{1} bytes", new Object[]{filename, bytes.length});

                java.nio.file.Path path = FileSystems.getDefault().getPath(getServerFilename(filename));
                LOG.log(Level.FINE, "inputPart type: {0}", inputPart.getMediaType());
                LOG.log(Level.FINE, "probe type: {0}", Files.probeContentType(path));

                String temp = moveToUploadFolder(bytes, filename, uid);

                jsonObjBuilder.add("link", "soccer-manager/ResourceServlet/" + temp);

            } catch (IOException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        }

        JsonObject jsonObj = jsonObjBuilder.build();
        return Response.status(Response.Status.CREATED).entity(jsonObj.toString()).build();
    }

    /**
     * Build filename local to the server.
     *
     * @param filename
     * @return
     */
    private String getServerFilename(String filename) {
        return resourceLocation + UPLOAD_DIR + "/" + filename;
    }

    private String moveToUploadFolder(byte[] fileContent, String filename, String uid) {
        InputStream in = new ByteArrayInputStream(fileContent);
        if (resourceLocation == null) {
            throw new IllegalStateException("resouceLocation is null");
        }
        String uploadPath = resourceLocation;

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String prefix = UUID.randomUUID().toString();
        String newFileName = prefix + "-" + filename;

        String pathPrefix = uid + "/";
        File uploadedFileDir = new File(uploadDir, pathPrefix);
        if (!uploadedFileDir.exists()) {
            uploadedFileDir.mkdirs();
        }

        String newFilePathName = pathPrefix + newFileName;

        File uploadedFile = new File(uploadDir, newFilePathName);

        try (FileOutputStream fos = new FileOutputStream(uploadedFile)) {
            IOUtils.copy(in, fos);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
        return newFilePathName;
    }

    /**
     * Extract filename from HTTP headers.
     *
     * @param headers
     * @return
     */
    private String getFileName(MultivaluedMap<String, String> headers) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = sanitizeFilename(name[1]);
                return finalFileName;
            }
        }
        return "unknown";
    }

    private String sanitizeFilename(String s) {
        return s.trim().replaceAll("\"", "");
    }

    // return file
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response getAttachment(@PathParam("id") Long id) {

        Attachment attachment = em.find(Attachment.class, id);

        if (attachment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        File f = new File(attachment.getFilePath());

        Response.ResponseBuilder response = Response.ok(f);
        response.type(attachment.getMimeType());
        response.header("Content-Length", String.valueOf(attachment.getSize()));
        response.header("Content-Disposition", "inline; filename=\"" + (attachment.getFileName().length() > 37 ? attachment.getFileName().substring(37) : attachment.getFileName()) + "\"");

        return response.build();
    }

}
