package models;


import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;


@Entity
public class VatRate extends Model {
    public DecimalFormat rate;
    public Date validFrom;
    public String vatStage;
    public String description;
    public Timestamp createdAt;
    public Timestamp updatedAt;
}
