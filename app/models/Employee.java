package models;

import java.util.*;
import javax.persistence.*;

import com.avaje.ebean.Model;
import play.data.validation.*;

import com.avaje.ebean.*;

@Entity
public class Employee extends Model {
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;
    
    @Constraints.Required
    public String name;

    /**
     * Generic query helper for entity Employee with id Long
     */
    public static Finder<Long,Employee> find = new Finder<>(Employee.class);

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}