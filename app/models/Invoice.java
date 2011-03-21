package models;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Invoice extends Model {
    public String number;
    public Date date;
   
    @ManyToOne
    public Customer customer;
    public boolean isPaid;
    public int paymentPeriod;
    
    @Lob
    public String notes;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
