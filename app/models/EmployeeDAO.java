package models;

import play.*;
import play.mvc.*;
import play.db.jpa.*;
import java.util.List;
import java.util.Date;

import javax.persistence.*;

public class EmployeeDAO {
    /**
     * Create an employee
     *
     * @param Employee model
     *
     * @return Employee
     */
    public static Employee create (Employee model) {
        model.emptyToNull();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find an employee by id
     *
     * @param Integer id
     *
     * @return Employee
     */
    public static Employee find(Integer id) {
        return JPA.em().find(Employee.class, id);
    }

    /**
     * Update an employee
     *
     * @param Employee model
     *
     * @return Employee
     */
    public static Employee update(Employee model) {
        return JPA.em().merge(model);
    }

    /**
     * Delete an employee by id
     *
     * @param Integer id
     */
    public static void delete(Integer id) {
        Employee model = JPA.em().getReference(Employee.class, id);
        JPA.em().remove(model);
    }

    /**
     * Get all employees
     *
     * @return List<Employee>
     */
    public static List<Employee> all() {
        return (List<Employee>) JPA.em().createQuery("SELECT m FROM " + Employee.TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of employees
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Employee>
     */
    public static List<Employee> paginate(Integer page, Integer size) {
        return (List<Employee>) JPA.em().createQuery("SELECT m FROM " + Employee.TABLE + " m ORDER BY id").setFirstResult(page*size).setMaxResults(size).getResultList();
    }

    /**
     * Get the number of total row
     *
     * @return Long
     */
    public static Long count() {
        return (Long) JPA.em().createQuery("SELECT count(m) FROM " + Employee.TABLE + " m").getSingleResult();
    }
}