package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;

public class EmployeeService
 {
    public static Employee create(Employee data) {
        return (Employee)EmployeeDAO.create(data);
    }

    public static Employee update(Employee data) {
        return (Employee)EmployeeDAO.update(data);
    }

    public static Employee find(Integer id) {
        return (Employee)EmployeeDAO.find(id);
    }

    public static Boolean delete(Integer id) {
        Employee employee = (Employee)EmployeeDAO.find(id);
        if (employee != null) {
            EmployeeDAO.delete(id);
            return true;
        } else {
            return false;
        }
    }

    public static List<? extends Model> all() {
        return EmployeeDAO.all();
    }

    public static List<? extends Model> pageEmployee(Integer page, Integer size) {
        return EmployeeDAO.paginate(page, size);
    }

    public static Long count() {
        return EmployeeDAO.count();
    }
}