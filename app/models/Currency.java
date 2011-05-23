package models;

import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;

@Entity
public class Currency extends TemporalModel {
    @Required
    @MinSize(value = 3)
    @MaxSize(value = 3)
    public String symbol;
    @Required
    public String name;
    
    public String localSymbol() {
        java.util.Currency currency = java.util.Currency.getInstance(symbol);
        return currency.getSymbol();
    }
    
    public String toString() {
        return "Currency { "
        + "symbol: " + symbol + ", "
        + "name: " + name
        + "}";
    }
}
