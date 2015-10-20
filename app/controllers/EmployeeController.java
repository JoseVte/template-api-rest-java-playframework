package controllers;

import java.util.List;

import play.libs.Json;
import play.*;
import play.mvc.*;
import views.html.*;
import static play.libs.Json.*;
import play.data.Form;
import play.db.jpa.*;

import models.*;

public class EmployeeController extends Controller {
    static Form<Employee> employeeForm = Form.form(Employee.class);

    public Result jsonResult(Result httpResponse) {
        response().setContentType("application/json; charset=utf-8");
        return httpResponse;
    }

    public Result index() {
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size) {
        List models = EmployeeService.pageEmployee(page-1, size);
        return jsonResult(ok(Json.toJson(models)));
    }

    @Transactional(readOnly = true)
    public Result get(Integer id) {
        Employee employee = EmployeeService.find(id);
        if (employee == null ) {
            return jsonResult(notFound(Json.toJson("Not found " + id)));
        }
        return jsonResult(ok(Json.toJson(employee)));
    }

    @Transactional
    public Result create() {
        Form<Employee> employee = employeeForm.bindFromRequest();
        if (employee.hasErrors()) {
            return jsonResult(badRequest(employee.errorsAsJson()));
        }
        Employee newEmployee = EmployeeService.create(employee.get());
        return jsonResult(created(Json.toJson(newEmployee)));
    }

    @Transactional
    public Result update() {
        Form<Employee> employee = employeeForm.bindFromRequest();
        if (employee.hasErrors()) {
            return jsonResult(badRequest(employee.errorsAsJson()));
        }
        Employee updatedEmployee = EmployeeService.update(employee.get());
        return jsonResult(ok(Json.toJson(updatedEmployee)));
    }

    @Transactional
    public Result delete(Integer id) {
        if (EmployeeService.delete(id)) {
            return jsonResult(ok(Json.toJson("Deleted " + id)));
        }
        return jsonResult(notFound(Json.toJson("Not found " + id)));
    }

}
