package controllers;

import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.libs.Json;
import java.util.*;
import models.*;

import models.*;

import views.html.*;

public class EmployeeController extends Controller {
    static Form<Employee> employeeForm = Form.form(Employee.class);

    public Result jsonResult(Result httpResponse) {
        response().setContentType("application/json; charset=utf-8");
        return httpResponse;
    }

    public Result index() {
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    public Result list(Integer page) {
        List models = Employee.find.all();
        return jsonResult(ok(Json.toJson(models)));
    }

    public Result get(Long id) {
        Employee employee = Employee.find.byId(id);
        if (employee == null ) {
            return jsonResult(notFound(Json.toJson("Not found " + id)));
        }
        return jsonResult(ok(Json.toJson(employee)));
    }

    public Result create() {
        Form<Employee> employee = employeeForm.bindFromRequest();
        if (employee.hasErrors()) {
            return jsonResult(badRequest(employee.errorsAsJson()));
        }
        employee.get().save();
        return jsonResult(created(Json.toJson(employee.get())));
    }

    public Result update() {
        Form<Employee> employee = employeeForm.bindFromRequest();
        if (employee.hasErrors()) {
            return jsonResult(badRequest(employee.errorsAsJson()));
        }
        employee.get().update();
        return jsonResult(ok(Json.toJson(employee.get())));
    }

    public Result delete(Long id) {
        Employee employee = Employee.find.byId(id);
        if (employee == null) {
            return jsonResult(notFound(Json.toJson("Not found " + id)));
        }
        employee.delete();
        return jsonResult(ok(Json.toJson("Deleted " + id)));
    }

}
