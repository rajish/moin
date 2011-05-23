package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Invoice extends TemporalModel{
    public String number;
    public Date date;
   
    @ManyToOne
    public Customer customer;
    public boolean isPaid;
    public int paymentPeriod;
    
    @Lob
    public String notes;
    
    @OneToMany
    public List<SoldItem> items;
}
