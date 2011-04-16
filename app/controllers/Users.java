package controllers;

import java.util.Date;
import java.util.List;
import java.sql.Timestamp;

import play.data.binding.Binder;
import play.data.validation.Required;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.With;
import models.User;

@With(Secure.class)
public class Users extends CRUD {

    public static void show(Long id) {
        System.out.println("Users.show(" + id +")");
        if(id != null) {
            User user = User.findById(id);
            render(user);
        }
        render();
    }
    
    public static void create(User user, @Required String confirmation) {
        System.out.println("Users.create(" + user + ")");
        validation.valid(user);
        validation.equals(user.password, confirmation);
        User duplUser = User.find("byLogin", user.login).first(); 
        if(duplUser != null) {
            validation.addError("user.login", Messages.get("admin.user.error.duplicate.login", user.login));
        }
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            render("@show", user, confirmation);
        }
        user.password = user.encrypt(confirmation);
        user._save();
        flash.success(Messages.get("admin.user.created", user.login));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", user._key());
    }
    
    public static void save(Long id, User userChgd, String confirmation) {
        System.out.println("Users.save("+ id + ", " + confirmation + "): " + params.allSimple());
        User user = User.findById(id);
        Binder.bind(user, "user", params.all());
        validation.valid(user.login);
        validation.valid(user.fullname);
        validation.valid(user.email);
        if(!user.password.isEmpty())
            validation.equals(user.password, confirmation);
        if (validation.hasErrors()) {
            params.flash();
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            render("@show", user, confirmation);
        }
        user.password = user.encrypt(confirmation);
        user._save();
        flash.success(Messages.get("admin.user.saved", user.login));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", user._key());
    }
    
    public static void delete(Long id){
        System.out.println("Users.delete(" + id + ")");
        User user = null;
        if (id != null) {
            user = User.findById(id);
        }
        notFoundIfNull(user);
        try {
            user._delete();
        } catch (Exception e) {
            flash.error(Messages.get("user.delete.error"));
            redirect("@show", user);
        }
        flash.success(Messages.get("user.delete.success"));
        redirect(request.controller + ".list");
    }
}
