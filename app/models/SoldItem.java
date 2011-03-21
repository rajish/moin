package models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class SoldItem extends Model {
    @ManyToOne
    public Item item;
    @ManyToOne
    public Invoice invoice;
    @ManyToOne
    public VatRate vatRate;
    public Float rebate;
    public Float quantity;
    public String notes;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
