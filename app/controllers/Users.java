package controllers;

import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import models.User;


public class Users extends CRUD {

//    public static void index() {
//        render();
//    }
    public static void show(Long id) {
        if(id != null) {
            User user = User.findById(id);
            render(user);
        }
        render();
    }
    
    public static void save(Long id) {
        ObjectType type = ObjectType.get(getControllerClass());
        User user = User.findById(id);
        validation.valid(user);
        if (validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html");
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, user);
            }
        }
        user._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", user._key());
    }
}
