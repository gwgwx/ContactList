/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.data;

import javax.ws.rs.client.WebTarget;
import online.touch.easy.common.model.Opportunity;



/**
 *
 * @author Georgios
 */
public class OpportunityRepository extends AbstractRepository<Opportunity> {
    
    private static OpportunityRepository instance = null;

    private OpportunityRepository() {
        super(Opportunity.class, "opportunitys");
    }

    public static OpportunityRepository getInstance() {
        if (instance == null) {
            instance = new OpportunityRepository();
        }
        return instance;
    }
    
    public <T> T findAll(Class<T> responseType, Long contactId) throws javax.ws.rs.ClientErrorException {
        System.err.println("called the repo!");
        WebTarget resource = webTarget;
        return resource
                .queryParam("contact.id", contactId)
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .get(responseType);
    }


}
