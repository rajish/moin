package controllers;

import java.math.BigDecimal;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import controllers.Invoices.ItemPresentationModel;

import models.Invoice;
import models.Item;
import models.SoldItem;
import models.VatRate;
import play.cache.Cache;
import play.data.validation.Valid;
import play.db.jpa.Model;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Invoices extends Controller {
	public static void index() {
		List<Invoice> entities = models.Invoice.all().fetch();
		render(entities);
	}

	public static void create(Invoice entity) {
		render(entity);
	}

	public static void show(java.lang.Long id) {
	    Invoice entity = Invoice.findById(id);
		render(entity);
	}

	public static void edit(java.lang.Long id) {
	    Invoice entity = Invoice.findById(id);
		render(entity);
	}

	public static void delete(java.lang.Long id) {
        Invoice entity = Invoice.findById(id);
        entity.delete();
		index();
	}

	private static void saveNewSoldItems(Invoice entity) {
	    List<SoldItem> newItems = Cache.get(session.getId() + "-newSoldItems", List.class);
	    for(SoldItem item : newItems) {
	        item.invoice = entity;
	        item.save();
	    }
	    Cache.delete(session.getId() + "-newSoldItems");
	}
	
	public static void save(@Valid Invoice entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@create", entity);
		}
		
		entity.save();
		saveNewSoldItems(entity);

		flash.success(Messages.get("scaffold.created", "Invoice"));
		index();
	}

	public static void update(@Valid Invoice entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@edit", entity);
		}
      	
		entity = entity.merge();
		entity.save();
		saveNewSoldItems(entity);
		
		flash.success(Messages.get("scaffold.updated", "Invoice"));
		index();
	}

	public static void saveItem(@Valid SoldItem item) {
	    System.out.println("Invoices.saveItem() " + params.allSimple());
	    if(validation.hasErrors()) {
	        System.out.println("Invoices.saveItem()" + validation.errorsMap());
	    }
	    List<SoldItem> newItems = Cache.get(session.getId() + "-newSoldItems", List.class);
	    System.out.println("Invoices.saveItem() " + item);
	    System.out.println("Invoices.saveItem() request: " + request);
	    
	    newItems.add(item);
	    // This will add the list to cache only for the first time,
	    Cache.add(session.getId() + "-newSoldItems", newItems);
	    // and this will replace the list with new contents any other time.
	    Cache.replace(session.getId() + "-newSoldItems", newItems);
	    
	    render(newItems);
	}
	
	public static void getCompletions(String startsWith, int maxRows) {
	    System.out.println("Invoices.getCompletions() startsWith:" + startsWith);
	    List<Item> items = Item.find("byNameIlike", "%" + startsWith + "%").fetch(maxRows);
	    List<ItemPresentationModel> completions = new ArrayList<ItemPresentationModel>(maxRows);
	    Locale loc = Lang.getLocale();
	    for (Item item : items) {
	        VatRate vatRate = null;
	        try {
    	        vatRate = (VatRate) VatRate.find("byVatStageAndValidFromLessThanEquals", 
    	                item.vatRateStage, new Date()).fetch(1).get(0);
	        } catch (Exception e) {
	            // discard
	        }
            ItemPresentationModel elem = new ItemPresentationModel(item.id, 
                    item.name, item.description, item.getPrice(), vatRate.rate, loc);
            completions.add(elem);
        }
	    System.out.println("Invoices.getCompletions() number of completions = " + completions.size());
        renderJSON(completions);
	}
	
	public static class ItemPresentationModel //extends Model
	{
	    public Long id;
	    public String name;
	    public String description;
	    public BigDecimal price;
	    public BigDecimal vatRate;
	    public String currFmt;
	    public String decimalPoint; // unicode code of decimal point according to locale
	    public ItemPresentationModel(Long ID, String nm, String ds, BigDecimal pr, BigDecimal rate, Locale loc) {
	        NumberFormat currfmt = NumberFormat.getCurrencyInstance(loc);
	        id = ID;
	        name = nm;
	        description = ds;
	        price = pr;
	        vatRate = rate;
	        currFmt = currfmt.getCurrency().getSymbol();
	        decimalPoint = currfmt.format(0.1).substring(1, 2);
	    }
	}
}
