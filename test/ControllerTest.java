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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.ws.*;
import play.libs.Json;

public class ControllerTest {
    int timeout = 4000;
    JndiDatabaseTester databaseTester;
    Application app;
    ObjectNode dataOk;
    ObjectNode dataError1;
    ObjectNode dataError2;

    public ControllerTest() {
        dataOk = Json.newObject();
        dataOk.put("name", "Yasuo");

        dataError1 = Json.newObject();
        dataError1.put("name", "");

        dataError2 = Json.newObject();
    }

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
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees/1")
                .get()
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id"), 1);
            assertEquals(responseJson.get("name"), "Josrom");
        });
    }

    @Test
    public void testFindEmployeeNotFound() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees/5")
                .get()
                .get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error"), "Not found 5");
        });
    }

    @Test
    public void testPageEmployees() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .get()
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertTrue(responseJson.get("data").isArray());
            assertEquals(responseJson.get("data").size(), 3);
            assertEquals(responseJson.get("total"), 4);
            assertNotNull(responseJson.get("link-self"));
            assertNotNull(responseJson.get("link-next"));
            assertNull(responseJson.get("link-prev"));
        });
    }

    @Test
    public void testCreateEmployee() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .post(dataOk)
                .get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id"), 5);
            assertEquals(responseJson.get("name"), "Yasuo");
        });
    }

    @Test
    public void testCreateEmployeeBadRequest1() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .post(dataError1)
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name"), "This field is required");
        });
    }

    @Test
    public void testCreateEmployeeBadRequest2() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .post(dataError2)
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name"), "This field is required");
        });
    }

    @Test
    public void testUpdateEmployee() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .put(dataOk.put("id", 1))
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id"), 1);
            assertEquals(responseJson.get("name"), "Yasuo");
        });
    }

    @Test
    public void testUpdateEmployeeBadRequest1() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .put(dataError1.put("id", 1))
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name"), "This field is required");
        });
    }

    @Test
    public void testUpdateEmployeeBadRequest2() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .put(dataError2.put("id", 2))
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name"), "This field is required");
        });
    }

    @Test
    public void testDeleteEmployee() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees/1")
                .delete()
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg"), "Deleted 1");
        });
    }

    @Test
    public void testDeleteEmployeeNotFound() {
        running(testServer(3333, app), () -> {
            WSResponse response = WS
                .url("http://localhost:3333/employees/5")
                .delete()
                .get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error"), "Not found 5");
        });
    }
}
