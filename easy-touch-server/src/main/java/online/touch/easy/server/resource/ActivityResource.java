/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.server.resource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import online.touch.easy.common.model.Activity;
import online.touch.easy.common.model.ActivityContact;
import online.touch.easy.common.model.Contact;
import online.touch.easy.common.model.ContactMain;

/**
 *
 * @author George
 */
@Stateless
@Path("activities")
public class ActivityResource extends AbstractFacade<Activity> {

    private static final Logger LOG = Logger.getLogger(ActivityResource.class.getName());

    @PersistenceContext(unitName = "online.touch.easy_pu")
    private EntityManager em;

    public ActivityResource() {
        super(Activity.class);
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createForContactId(@PathParam("id") Long id, Activity entity) {
        LOG.log(Level.FINE, "call createForContactId: {0}", id);

        ContactMain contact = em.find(ContactMain.class, id);
        if (contact == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ActivityContact ac = new ActivityContact();
        ac.setActivity(entity);
        ac.setContact(contact);
        em.persist(entity);
        em.persist(ac);

//        return Response.created(UriBuilder.fromResource(Activity.class)
//                .path(String.valueOf(entity.getId())).build()).build();
        return Response.ok().build();
    }

    @POST
    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(Activity entity) {
        LOG.fine("call create");
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void edit(@PathParam("id") Long id, Activity entity) {
        LOG.fine("call edit");
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        LOG.fine("call remove");
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Activity find(@PathParam("id") Long id) {
        LOG.fine("call find");
        return super.find(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Activity> findAll(@QueryParam("contact.id") Long contactId) {
        System.err.println("call findAll with contact query param: " + contactId);
        if (contactId == null) {
            return super.findAll();
        } else {
            List<ActivityContact> acs = em.createQuery("select ac from ActivityContact ac").getResultList();
            System.err.println("size of acs: " + acs.size());
            List<Activity> result = em.createQuery("select a from Activity a where a.id in (select ac.activity.id from ActivityContact ac where ac.contact.id = :contactId)")
                    .setParameter("contactId", contactId)
                    .getResultList();
            System.err.println("restuls size: " + result.size());
            return result;

        }
    }

    @GET
    @Path("{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Activity> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
