package controllers;

import java.util.List;

import models.SoldItem;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class SoldItems extends Controller {
	public static void index() {
		List<SoldItem> entities = models.SoldItem.all().fetch();
		render(entities);
	}

	public static void create(SoldItem entity) {
		render(entity);
	}

	public static void show(java.lang.Long id) {
    SoldItem entity = SoldItem.findById(id);
		render(entity);
	}

	public static void edit(java.lang.Long id) {
    SoldItem entity = SoldItem.findById(id);
		render(entity);
	}

	public static void delete(java.lang.Long id) {
    SoldItem entity = SoldItem.findById(id);
    entity.delete();
		index();
	}
	
	public static void save(@Valid SoldItem entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@create", entity);
		}
    entity.save();
		flash.success(Messages.get("scaffold.created", "SoldItem"));
		index();
	}

	public static void update(@Valid SoldItem entity) {
		if (validation.hasErrors()) {
			flash.error(Messages.get("scaffold.validation"));
			render("@edit", entity);
		}
		
      		entity = entity.merge();
		
		entity.save();
		flash.success(Messages.get("scaffold.updated", "SoldItem"));
		index();
	}

}
