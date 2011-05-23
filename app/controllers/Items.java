package controllers;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import javax.persistence.PersistenceException;

import models.Currency;
import models.Item;
import models.VatRate;

import org.hibernate.PropertyAccessException;

import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Items extends Controller {
    @Before
    public static void setupVars () {
        Locale locale = Locale.getDefault();
        List<Currency> curr = Currency.findAll();
        renderArgs.put("currencies", curr);
        java.util.Currency currency = java.util.Currency.getInstance(locale);
        renderArgs.put("defaultCurrency", currency);
        
        List<VatRate> vatrate = VatRate.findAll();
        renderArgs.put("vatRates", vatrate);
    }
    
	public static void index() {
		List<Item> entities = models.Item.all().fetch();
		render(entities);
	}

	public static void create(Item entity) {
		render(entity);
	}

	public static void show(java.lang.Long id) {
    Item entity = Item.findById(id);
		render(entity);
	}

	public static void edit(java.lang.Long id) {
    Item entity = Item.findById(id);
		render(entity);
	}

	public static void delete(java.lang.Long id) {
    Item entity = Item.findById(id);
    entity.delete();
		index();
	}
	
	public static void save(@Valid Item entity) {
	    System.out.println("Items.save(" + entity + ")" + params.allSimple());
		if (validation.hasErrors()) {
		    System.out.println("Items.save() Item: " + entity + " validation errors: " + validation.errors());
			flash.error(Messages.get("scaffold.validation"));
			render("@create", entity);
		}
		try {
		entity.save();
		flash.success(Messages.get("scaffold.created", "Item"));
		} catch (PersistenceException e) {
		    System.out.println("Items.save() Exception: " + e );
		    if(e.getCause() instanceof PropertyAccessException) {
		        PropertyAccessException cause = (PropertyAccessException) e.getCause();
		        System.out.println("Property: " + cause.getPropertyName());
		        Field[] fields = cause.getPersistentClass().getDeclaredFields();
		        for(Field field : fields) 
		            System.out.println("class info: " + field);
		    }
		    flash.error(Messages.get("scaffold.validation"));
		    render("@create", entity);
		}
		index();
	}

	public static void update(@Valid Item entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@edit", entity);
		}
		
      		entity = entity.merge();
		
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Item"));
		index();
	}

}
