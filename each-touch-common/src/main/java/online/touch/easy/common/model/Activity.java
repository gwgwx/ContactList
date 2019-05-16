/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.common.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import javax.persistence.Entity;

/**
 *
 * @author George
 */
@Entity
@JsonSubTypes({
    @JsonSubTypes.Type(value = BusinessNote.class),
    @JsonSubTypes.Type(value = PhoneLog.class)})
public class Activity extends BaseEntity implements HistoryCommonFieldsInterface {
      
    private String subject;
    
    private String note;

    @Override
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
            return getId().equals(((Activity) that).getId());
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
