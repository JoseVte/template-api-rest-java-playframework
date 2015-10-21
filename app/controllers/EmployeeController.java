package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import views.html.*;
import play.libs.Json;
import play.libs.Json.*;
import play.data.Form;
import play.db.jpa.*;

import models.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

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
        Long count = EmployeeService.count();

        ObjectNode result = Json.newObject();
        result.put("data", Json.toJson(models));
        result.put("total", count);
        if (page > 1) result.put("link-prev", routes.EmployeeController.list(page-1, size).toString());
        if (page*size < count) result.put("link-next", routes.EmployeeController.list(page+1, size).toString());
        result.put("link-self", routes.EmployeeController.list(page, size).toString());

        return jsonResult(ok(result));
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
