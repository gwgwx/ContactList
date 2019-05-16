/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.data;

import online.touch.easy.common.model.Contact;



/**
 *
 * @author Georgios
 */
public class ContactRepository extends AbstractRepository<Contact> {
    
    private static ContactRepository instance = null;

    private ContactRepository() {
        super(Contact.class, "contacts");
    }

    public static ContactRepository getInstance() {
        if (instance == null) {
            instance = new ContactRepository();
        }
        return instance;
    }


}
