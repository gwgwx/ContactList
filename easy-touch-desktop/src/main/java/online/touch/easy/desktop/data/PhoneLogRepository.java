/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.data;

import javax.ws.rs.client.WebTarget;
import online.touch.easy.common.model.PhoneLog;
import online.touch.easy.common.model.Opportunity;



/**
 *
 * @author Georgios
 */
public class PhoneLogRepository extends AbstractRepository<PhoneLog> {
    
    private static PhoneLogRepository instance = null;

    private PhoneLogRepository() {
        super(PhoneLog.class, "businessNotes");
    }

    public static PhoneLogRepository getInstance() {
        if (instance == null) {
            instance = new PhoneLogRepository();
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
