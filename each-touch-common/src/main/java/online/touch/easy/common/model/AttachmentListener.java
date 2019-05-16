/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.common.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
//import org.apache.deltaspike.core.api.config.ConfigResolver;
//import org.imgscalr.Scalr;

/**
 *
 * @author George-2014
 */
public class AttachmentListener {

    public AttachmentListener() {
//        resouceLocation = ConfigResolver.getPropertyValue("config.filelocation");
    }

    private static final Logger log = Logger.getLogger(AttachmentListener.class.getName());

    private String resouceLocation;

    @PrePersist
    public void create(Attachment attachment) {
        //store attachment to filesystem.
        if (attachment.getFileContent() == null || attachment.getFileContent().length == 0) {
            throw new IllegalStateException("Cannot save null or emtpy content!");
        }
        moveToUploadFolder(attachment);
    }

    @PreUpdate
    public void update(Attachment attachment) {
        // not true anymore, can be set to default!
//        throw new IllegalStateException("This entity cannot be updated! Monitoring attachment update event to test code");
    }

    /**
     * Remove the attachment from disk after the entity attachment has been
     * removed.
     *
     * @param attachment the entity attachment that has been removed from the
     * database.
     */
    @PostRemove
    public void onRemove(Attachment attachment) {
        log.finer("call ResourceListener#onRemove");
        if (resouceLocation == null) {
            throw new IllegalStateException("resouceLocation is null");
        }
        log.log(Level.FINER, "call ResourceListener#onRemove resouceLocation{0}", resouceLocation);
        removeFile(attachment.getFilePath(), attachment.getThumbnailPath());

    }

    private String getResourceLocation() {
        return resouceLocation;
    }

    public void removeFile(String filePath, String thumbnailPath) {
        try {

            String uploadPath = "";
            if (getResourceLocation() != null
                    && getResourceLocation().trim().length() > 0) {
                uploadPath = getResourceLocation();
            }

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                log.severe("upload directory does not exists!");
                uploadDir.mkdirs();
            }

            File file = new File(uploadDir, filePath);

            if (file.delete()) {
                log.log(Level.FINER, "{0} is deleted!", file.getName());
            } else {
                log.finer("Delete operation has failed.");
            }

            if (thumbnailPath != null) {

                file = new File(uploadDir, thumbnailPath);

                if (file.delete()) {
                    log.log(Level.FINER, "{0} is deleted!", file.getName());
                } else {
                    log.finer("Delete operation has failed.");
                }

            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception while removing file", e);
        }
    }

    private void moveToUploadFolder(Attachment attachment) {
        InputStream in = new ByteArrayInputStream(attachment.getFileContent());
        if (resouceLocation == null) {
            throw new IllegalStateException("resouceLocation is null");
        }
        String uploadPath = getResourceLocation();

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String prefix = UUID.randomUUID().toString();
        String newFileName = prefix + "-" + attachment.getFileName();
        String newThumbnailFileName = prefix + "-thumbnail-" + attachment.getFileName();
//		String newFileName=fileName;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String pathPrefix = year + "/" + month + "/" + day + "/";
        //String pathPrefix="/";
        File uploadedFileDir = new File(uploadDir, pathPrefix);
        if (!uploadedFileDir.exists()) {
            uploadedFileDir.mkdirs();
        }

        String newFilePathName = pathPrefix + newFileName;
        String thumbnailFilePathName = pathPrefix + newThumbnailFileName;
//		if (log.isLoggable(Level.FINE)) {
//			log.fine("new file path name@" + newFilePathName);
//		}

        File uploadedFile = new File(uploadDir, newFilePathName);
        File thumbnailFile = new File(uploadDir, thumbnailFilePathName);
        try {
            IOUtils.copy(in, new FileOutputStream(uploadedFile));

            //create thumbnail and save to file
            if (attachment.getMimeType().startsWith("image")) {
                BufferedImage imBuff = ImageIO.read(uploadedFile);
//                BufferedImage thumbnail = Scalr.resize(imBuff, 150);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
//                ImageIO.write(thumbnail, FilenameUtils.getExtension(thumbnailFilePathName), os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                IOUtils.copy(is, new FileOutputStream(thumbnailFile));
            }

            if (log.isLoggable(Level.FINE)) {
                log.log(Level.FINE, "copying file from@{0}-->{1}", new Object[]{attachment.getFileName(), uploadedFile});
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        }
        attachment.setFilePath(newFilePathName);
        attachment.setThumbnailPath(thumbnailFilePathName);
    }

}
