import org.junit.*;
import play.test.*;
import play.Application;
import play.mvc.*;
import static play.test.Helpers.*;
import static org.junit.Assert.*;
import play.db.jpa.*;
import java.util.List;
import models.*;
import org.dbunit.*;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.*;
import java.util.HashMap;
import java.io.FileInputStream;

import play.libs.ws.*;

public class ModelTest {
    JndiDatabaseTester databaseTester;
    Application app;

    // Data needed for create the fake
    private static HashMap<String, String> settings() {
        HashMap<String, String> settings = new HashMap<String, String>();
        settings.put("db.default.url", "jdbc:mysql://api.template-java.com:3306/play_test");
        settings.put("db.default.username", "root");
        settings.put("db.default.password", "");
        settings.put("db.default.jndiName", "DefaultDS");
        settings.put("jpa.default", "mySqlPersistenceUnit");
        return(settings);
    }

    @BeforeClass
    public static void createTables() {
        Application fakeApp = Helpers.fakeApplication(settings());
        running (fakeApp, () -> {
            JPA.withTransaction(() -> {});
        });
    }

    @Before
    public void initializeData() throws Exception {
        app = Helpers.fakeApplication(settings());
        databaseTester = new JndiDatabaseTester("DefaultDS");
        IDataSet initialDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("test/resources/employee_dataset_1.xml"));
        databaseTester.setDataSet(initialDataSet);
        databaseTester.onSetup();
    }

    @After
    public void closeDB() throws Exception {
        databaseTester.onTearDown();
    }

    @Test
    public void testFindEmployee() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Employee e = EmployeeService.find(1);
                assertEquals(e.name, "Josrom");
            });
        });
    }

    @Test
    public void testFindEmployeeNotFound() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Employee e = EmployeeService.find(5);
                assertNull(e);
            });
        });
    }

    @Test
    public void testFindAllEmployees() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                List<Employee> e = EmployeeService.all();
                long count = EmployeeService.count();
                assertEquals(count, 4);

                assertTrue(e.contains(new Employee("Josrom")));
                assertTrue(e.contains(new Employee("Dantar")));
                assertTrue(e.contains(new Employee("Ericmaster")));
                assertTrue(e.contains(new Employee("xChaco")));
            });
        });
    }

    @Test
    public void testPageEmployees() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                List<Employee> e = EmployeeService.paginate(0, 3);

                assertTrue(e.contains(new Employee("Josrom")));
                assertTrue(e.contains(new Employee("Dantar")));
                assertTrue(e.contains(new Employee("Ericmaster")));
                assertFalse(e.contains(new Employee("xChaco")));

                e = EmployeeService.paginate(1, 3);
                assertEquals(e.size(), 1);
            });
        });
    }

    @Test
    public void testCreateEmployee() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Employee create = new Employee("New test");
                Employee e = EmployeeService.create(create);
                assertEquals(e, create);
            });
        });
    }

    @Test
    public void testUpdateEmployee() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Employee create = new Employee("New test");
                Employee e = EmployeeService.create(create);
                e.name = "Update test";
                Employee update = EmployeeService.update(e);
                assertEquals(update.name, "Update test");
            });
        });
    }

    @Test
    public void testDeleteEmployee() {
        running (app, () -> {
            JPA.withTransaction(() -> {
                Employee create = new Employee("New test");
                Employee e = EmployeeService.create(create);
                
                assertTrue(EmployeeService.delete(e.id));
                assertFalse(EmployeeService.delete(e.id));
            });
        });
    }
}
