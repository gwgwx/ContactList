/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.common.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author George-2014
 */
@Entity
@Table (name="contact_attachments")
public class ContactAttachment extends Attachment {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private ContactMain contact;

    public ContactMain getContact() {
        return contact;
    }

    public void setContact(ContactMain contact) {
        this.contact = contact;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        if (getId() != null) {
            return getId().equals(((ContactAttachment) that).getId());
        }
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }
    
}
