package models;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;


@Entity
public class VatRate extends TemporalModel {
    public BigDecimal rate;
    public Date validFrom;
    public String vatStage;
    public String description;
}
