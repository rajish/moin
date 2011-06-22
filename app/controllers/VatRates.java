package controllers;

import java.util.List;

import models.VatRate;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class VatRates extends Controller {
	public static void index() {
		List<VatRate> entities = models.VatRate.all().fetch();
		render(entities);
	}

	public static void create(VatRate entity) {
		render(entity);
	}

	public static void show(java.lang.Long id) {
    VatRate entity = VatRate.findById(id);
		render(entity);
	}

	public static void edit(java.lang.Long id) {
    VatRate entity = VatRate.findById(id);
		render(entity);
	}

	public static void delete(java.lang.Long id) {
    VatRate entity = VatRate.findById(id);
    entity.delete();
		index();
	}
	
	public static void save(@Valid VatRate entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@create", entity);
		}
		entity.save();
		flash.success(Messages.get("scaffold.created", "VatRate"));
		index();
	}

	public static void update(@Valid VatRate entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@edit", entity);
		}
		entity = entity.merge();
		entity.save();
		flash.success(Messages.get("scaffold.updated", "VatRate"));
		index();
	}

}
