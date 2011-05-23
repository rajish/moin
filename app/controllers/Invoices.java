package controllers;

import java.util.List;

import models.Invoice;
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
	
	public static void save(@Valid Invoice entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@create", entity);
		}
    entity.save();
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
		flash.success(Messages.get("scaffold.updated", "Invoice"));
		index();
	}

}
