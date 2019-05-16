/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author George
 */
@MappedSuperclass
@EntityListeners(EntityListener.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dtype")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ContactMain.class),
    @JsonSubTypes.Type(value = Activity.class)})
public abstract class BaseEntity implements Serializable, Identifiable, Timestampable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /* "UUID" and "UID" are Oracle reserved keywords -> "entity_uid" */
    @Column(name = "entity_uid", unique = true, nullable = false, updatable = false, length = 36)
    private String uid;

    @Column(name = "version", nullable = false)
    @Version
    private Long version = 0L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = false, updatable = false)
    @NotNull
    private Date createdOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_on", nullable = false)
    @NotNull
    private Date modifiedOn;

    @Column(name = "modified_by", nullable = false)
    @NotNull
    private String modifiedBy;

    @Column(name = "created_by", nullable = false, updatable = false)
    @NotNull
    private String createdBy;

    public BaseEntity() {
    }

    public BaseEntity(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isNew() {
        return (this.id == null);
    }
    
    @Override
    public boolean equals(Object o) {
        return (o == this || (o instanceof BaseEntity && uid().equals(((BaseEntity) o).uid())));
    }

    @Override
    public int hashCode() {
        return uid().hashCode();
    }

    private String uid() {
        if (uid == null) {
            uid = UUID.randomUUID().toString();
        }
        return uid;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUid() {
        return uid();
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // hibernate proxy does not return concreate class, cannot use classname in serialiser and HibernateProxy is not available in client.
    // this seems to work!
    public String dtypeAsString() {
        return this.getClass().getSimpleName();
    }

}
