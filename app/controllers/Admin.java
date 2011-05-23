package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {

    @Before
    static void setConnectedUser() {
        System.out.println("Admin.setConnectedUser()");
        if(Security.isConnected()) {
            User user = User.find("byLogin", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
    }

    
    public static void index() {
        System.out.println("Admin.index()");
        render();
    }

}
