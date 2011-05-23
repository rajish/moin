package controllers;

import java.util.List;

import models.Customer;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Customers extends Controller {
	public static void index() {
		List<Customer> entities = models.Customer.all().fetch();
		render(entities);
	}

	public static void create(Customer entity) {
		render(entity);
	}

	public static void show(java.lang.Long id) {
    Customer entity = Customer.findById(id);
		render(entity);
	}

	public static void edit(java.lang.Long id) {
    Customer entity = Customer.findById(id);
		render(entity);
	}

	public static void delete(java.lang.Long id) {
    Customer entity = Customer.findById(id);
    entity.delete();
		index();
	}
	
	public static void save(@Valid Customer entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@create", entity);
		}
    entity.save();
		flash.success(Messages.get("scaffold.created", "Customer"));
		index();
	}

	public static void update(@Valid Customer entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@edit", entity);
		}
		
      		entity = entity.merge();
		
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Customer"));
		index();
	}

}
