/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.common.model;

import java.util.Date;

/**
 *
 * @author George
 */
public interface Timestampable {
    
    /**
     * @return the Date when this Entity was created
     */
    Date getCreatedOn();

    /**
     * Returns the LastUpdated, or the Created Date
     * if this Entity has never been updated.
     *
     * @return the Date when this Entity was last modified
     */
    Date getModifiedOn();
    
}
