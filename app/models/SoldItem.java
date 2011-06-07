package models;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class SoldItem extends TemporalModel {
    @ManyToOne
    public Item item;
    
    @ManyToOne
    public Invoice invoice;
    
    public BigDecimal retailPrice;

    @ManyToOne
    public VatRate vatRate;
    
    public BigDecimal rebate;
    
    public BigDecimal quantity;
    
    public String notes;
    
    public String toString() {
        return "SoldItem: {"
            + "item:" + item + ", "
            + "invoice:" + invoice + ", "
            + "vatRate:" + vatRate + ", "
            + "rebate:" + rebate + ", "
            + "quantity:" + quantity + ", "
            + "notes:" + notes
            + "}"
            ;
    }
}
