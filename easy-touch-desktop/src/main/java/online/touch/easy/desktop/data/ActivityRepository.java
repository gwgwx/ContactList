/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.data;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import online.touch.easy.common.model.Activity;



/**
 *
 * @author Georgios
 */
public class ActivityRepository extends AbstractRepository<Activity> {
    
    private static ActivityRepository instance = null;

    private ActivityRepository() {
        super(Activity.class, "activities");
    }

    public static ActivityRepository getInstance() {
        if (instance == null) {
            instance = new ActivityRepository();
        }
        return instance;
    }

    public void createForContact(Activity entity, Long contactId) {
        Response response = webTarget
                .path(contactId.toString())
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .post(javax.ws.rs.client.Entity.entity(entity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
        try {
            if (response.getStatus() == 200) {
            }
        } finally {
            response.close();
        }
    }
    
    public <T> T findAll(Class<T> responseType, Long contactId) throws javax.ws.rs.ClientErrorException {
        WebTarget resource = webTarget;
        return resource
                .queryParam("contact.id", contactId)
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(responseType);
    }


}
