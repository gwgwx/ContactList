/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.converters;

import java.util.HashMap;
import java.util.Map;
import javafx.util.StringConverter;
import online.touch.easy.common.model.ContactMain;

/**
 *
 * @author George
 */
public class AccountConverter extends StringConverter<ContactMain> {

    private final Map<Long, ContactMain> map = new HashMap<>();

    public AccountConverter() {
    }

    @Override
    public String toString(ContactMain contact) {
        if (contact == null) return null;
        map.put(contact.getId(), contact);
        return contact.getName();
    }

    @Override
    public ContactMain fromString(String id) {
        if (id == null) return null;
        return map.get(Long.parseLong(id));
    }

}
