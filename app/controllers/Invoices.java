package controllers;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Invoices extends CRUD {

//    public static void index() {
//        render();
//    }
    public static void show(Long id) {
        render();
    }
}
