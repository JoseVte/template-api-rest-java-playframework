package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.libs.Json;
import play.libs.Json.*;
import play.data.Form;
import play.db.jpa.*;

import models.*;
import views.html.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class EmployeeController extends Controller {
    static Form<Employee> employeeForm = Form.form(Employee.class);

    /**
     * Add the content-type json to response
     *
     * @param Result httpResponse
     *
     * @return Result
     */
    public Result jsonResult(Result httpResponse) {
        response().setContentType("application/json; charset=utf-8");
        return httpResponse;
    }

    /**
     * Get the index page
     *
     * @return Result
     */
    public Result index() {
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    /**
     * Get the employees with pagination
     *
     * @param Integer page
     * @param Integer size
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size) {
        List models = EmployeeService.paginate(page-1, size);
        Long count = EmployeeService.count();

        ObjectNode result = Json.newObject();
        result.put("data", Json.toJson(models));
        result.put("total", count);
        if (page > 1) result.put("link-prev", routes.EmployeeController.list(page-1, size).toString());
        if (page*size < count) result.put("link-next", routes.EmployeeController.list(page+1, size).toString());
        result.put("link-self", routes.EmployeeController.list(page, size).toString());

        return jsonResult(ok(result));
    }

    /**
     * Get one employee by id
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result get(Integer id) {
        Employee employee = EmployeeService.find(id);
        if (employee == null ) {
            ObjectNode result = Json.newObject();
            result.put("error", "Not found " + id);
            return jsonResult(notFound(result));
        }
        return jsonResult(ok(Json.toJson(employee)));
    }

    /**
     * Create an employee with the data of request
     *
     * @return Result
     */
    @Transactional
    public Result create() {
        Form<Employee> employee = employeeForm.bindFromRequest();
        if (employee.hasErrors()) {
            return jsonResult(badRequest(employee.errorsAsJson()));
        }
        Employee newEmployee = EmployeeService.create(employee.get());
        return jsonResult(created(Json.toJson(newEmployee)));
    }

    /**
     * Update an employee with the data of request
     *
     * @return Result
     */
    @Transactional
    public Result update() {
        Form<Employee> employee = employeeForm.bindFromRequest();
        if (employee.hasErrors()) {
            return jsonResult(badRequest(employee.errorsAsJson()));
        }
        Employee updatedEmployee = EmployeeService.update(employee.get());
        return jsonResult(ok(Json.toJson(updatedEmployee)));
    }

    /**
     * Delete an employee by id
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional
    public Result delete(Integer id) {
        if (EmployeeService.delete(id)) {
            ObjectNode result = Json.newObject();
            result.put("msg", "Deleted " + id);
            return jsonResult(ok(result));
        }
        ObjectNode result = Json.newObject();
        result.put("error", "Not found " + id);
        return jsonResult(notFound(result));
    }
}
