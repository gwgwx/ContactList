/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.common.model;

/**
 *
 * @author George
 */
public interface Identifiable {
    
    /**
     * @return The primary key, or ID, of this entity
     */
    Long getId();
    
    String getUid();
    
}
