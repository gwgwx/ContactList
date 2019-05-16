/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.common.model;

import java.util.Date;

/**
 *
 * @author George
 */
public interface HistoryCommonFieldsInterface {

    Long getId();

    Long getVersion();

    Date getCreatedOn();

    Date getModifiedOn();

    String getModifiedBy();

    String getCreatedBy();

    String getUid();

    String getSubject();

}
