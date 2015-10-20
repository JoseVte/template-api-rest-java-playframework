package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;

import javax.persistence.*;

public class EmployeeDAO {
    public static Employee create (Employee model) {
        model.emptyToNull();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    public static Employee find(Integer id) {
        return JPA.em().find(Employee.class, id);
    }

    public static Employee update(Employee model) {
        return JPA.em().merge(model);
    }

    public static void delete(Integer id) {
        Employee model = JPA.em().getReference(Employee.class, id);
        JPA.em().remove(model);
    }

    public static List<Employee> all() {
        return (List<Employee>) JPA.em().createQuery("SELECT m FROM " + Employee.TABLE + " m ORDER BY id").getResultList();
    }

    public static List<Employee> paginate(Integer page, Integer size) {
        return (List<Employee>) JPA.em().createQuery("SELECT m FROM " + Employee.TABLE + " m ORDER BY id").setFirstResult(page*size).setMaxResults(size).getResultList();
    }
}