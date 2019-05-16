/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.server.resource;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author George
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(online.touch.easy.server.resource.AccountResource.class);
        resources.add(online.touch.easy.server.resource.ActivityResource.class);
        resources.add(online.touch.easy.server.resource.AttachmentResource.class);
        resources.add(online.touch.easy.server.resource.BusinessNoteResource.class);
        resources.add(online.touch.easy.server.resource.ContactResource.class);
        resources.add(online.touch.easy.server.resource.OpportunityResource.class);
        resources.add(online.touch.easy.server.resource.PhoneLogResource.class);
        resources.add(online.touch.easy.server.resource.ProjectResource.class);
        resources.add(online.touch.easy.server.resource.UserResource.class);
    }
    
}
