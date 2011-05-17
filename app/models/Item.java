package models;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;

@Entity
public class Item extends TemporalModel {
    @Required
    public String code;
    @Required
    public String name;
    public String description;
    @Required
    private BigDecimal price;
    @Required
    public String vatRateStage;
    @Required
    @ManyToOne
    public Currency currency;
    
    public String toString(){
        return "Item {"
            + super.toString()
            + "code: " + code + ", "
            + "name: " + name + ", "
            + "description: " + description + ", "
            + "price: " + getPrice() + ", "
            + "vatRate: " + vatRateStage + ", "
            + "currency: " + currency
            + "}";
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }
}
