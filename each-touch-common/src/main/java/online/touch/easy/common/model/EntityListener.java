/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.common.model;

import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author George
 */
public class EntityListener {
    
    private static final Logger LOG = Logger.getLogger(EntityListener.class.getName());

    @PrePersist
    void onCreate(BaseEntity baseEntity) {
        LOG.finest("call EntityListener#onCreate");
        Date d = new Date();
        baseEntity.setCreatedOn(d);
        baseEntity.setModifiedOn(d);
        baseEntity.setCreatedBy("system");
        baseEntity.setModifiedBy("system");
        // force generate of uid if not already created
        baseEntity.getUid();
    }

    @PreUpdate
    void onEdit(BaseEntity baseEntity) {
        LOG.finest("call EntityListener#onEdit");
        baseEntity.setModifiedOn(new Date());
        baseEntity.setModifiedBy("system");
    }
    
}
