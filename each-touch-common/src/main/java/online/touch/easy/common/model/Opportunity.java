/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.common.model;

import java.util.Date;
import javax.persistence.Entity;

/**
 *
 * @author George
 */
@Entity
public class Opportunity extends ContactMain implements HistoryCommonFieldsInterface {
    
    private String salesStage;
    private String type;
    private Date initialSales;
    private Date followUp;
    private Date interestKnown;
    private Date closeDate;
    private Date expirationDate;
    private Date deliveryDate;
    private Date reminder;

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Date getReminder() {
        return reminder;
    }

    public void setReminder(Date reminder) {
        this.reminder = reminder;
    }

    public Date getInitialSales() {
        return initialSales;
    }

    public void setInitialSales(Date initialSales) {
        this.initialSales = initialSales;
    }

    public Date getFollowUp() {
        return followUp;
    }

    public void setFollowUp(Date followUp) {
        this.followUp = followUp;
    }

    public Date getInterestKnown() {
        return interestKnown;
    }

    public void setInterestKnown(Date interestKnown) {
        this.interestKnown = interestKnown;
    }
    
    /* Opportunity title
    Sales stage
    Link to (any contact type)
    probability
    Expected Revenue
    Close date
    Activities and completion date
    products and services (optional)
    Notes in the details tab
    
    */

    public String getSalesStage() {
        return salesStage;
    }

    public void setSalesStage(String salesStage) {
        this.salesStage = salesStage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
            return getId().equals(((Opportunity) that).getId());
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
