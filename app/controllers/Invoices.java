package controllers;

import java.util.List;

import models.Invoice;
import models.Item;
import models.SoldItem;
import play.cache.Cache;
import play.data.validation.Valid;
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
	    List<Item> completions = Item.find("byNameIlike", startsWith + "%").fetch(maxRows);
	    System.out.println("Invoices.getCompletions() number of completions = " + completions.size());
        renderJSON(completions);
	}
}
