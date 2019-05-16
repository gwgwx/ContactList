/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.common.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author George
 */
@Entity
@JsonSubTypes({
    @JsonSubTypes.Type(value = Contact.class)
    ,
    @JsonSubTypes.Type(value = Account.class)})
public abstract class ContactMain extends BaseEntity implements Serializable {

    private String name;

    private String email;

    private boolean active;

    private String subject;

  

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ContactMain parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ContactMain getParent() {
        return parent;
    }

    public void setParent(ContactMain parent) {
        this.parent = parent;
    }

}


