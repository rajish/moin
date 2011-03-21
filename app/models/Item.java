package models;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Currency;

import javax.persistence.Entity;
import javax.persistence.IdClass;

import play.db.jpa.Model;

@Entity
@IdClass(play.db.jpa.Model.class)
public class Item extends Model {
    public String code;
    public String name;
    public String description;
    public DecimalFormat price;
    public String vatRateStage;
    public Currency currency;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
