package controllers;

import java.util.List;

import models.Currency;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;

public class Currencies extends Controller {

    public static void index() {
        List<Currency> entities = Currency.all().fetch();
        render(entities);
    }

    public static void create(Currency entity) {
        render(entity);
    }
    
    public static void show(Long id) {
        Currency entity = Currency.findById(id);
        render(entity);
    }
    
    public static void edit(Long id) {
        Currency entity = Currency.findById(id);
        render(entity);
    }
    
    public static void delete(Long id) {
        Currency entity = Currency.findById(id);
        entity.delete();
        index();
    }
    
    public static void save(@Valid Currency entity) {
        if (validation.hasErrors()) {
            System.out.println("Currencies.save() Currency: " + entity + " validation errors: " + validation.errors());
            flash.error(Messages.get("scaffold.validation"));
            render("@create", entity);
        }
        entity.save();
        flash.success(Messages.get("scaffold.created", "Currency"));
        index();
    }
    
    public static void update(@Valid Currency entity) {
        if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@edit", entity);
		}
      	
        entity = entity.merge();
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Currency"));
		index();
    }
}
