/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.server.resource;

import java.util.List;
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
import online.touch.easy.common.model.Opportunity;

/**
 *
 * @author George
 */
@Stateless
@Path("opportunitys")
public class OpportunityResource extends AbstractFacade<Opportunity> {

    private static final Logger LOG = Logger.getLogger(OpportunityResource.class.getName());

    @PersistenceContext(unitName = "online.touch.easy_pu")
    private EntityManager em;

    public OpportunityResource() {
        super(Opportunity.class);
    }

    @POST
    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(Opportunity entity) {
        LOG.fine("call create");
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void edit(@PathParam("id") Long id, Opportunity entity) {
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
    public Opportunity find(@PathParam("id") Long id) {
        LOG.fine("call find");
        return super.find(id);
    }

//    @GET
//    @Override
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<Opportunity> findAll() {
//        return super.findAll();
//    }

    @GET
    @Path("{from}/{to}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Opportunity> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Opportunity> findAll(@QueryParam("contact.id") Long contactId) {
        System.err.println("opportunity call findAll with contact query param: " + contactId);
        if (contactId == null) {
            return super.findAll();
        } else {
            List<Opportunity> result = em.createQuery("select o from Opportunity o where o.parent.id = :contactId")
                    .setParameter("contactId", contactId)
                    .getResultList();
            System.err.println("opportunity restuls size: " + result.size());
            return result;

        }
    }

}
