package praktikum;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.utils.TestDataGenerator;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

public class UserCreationTest {
    private String email;
    private String password;
    private String name;
    private String accessToken;
    private UserClient userClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        accessToken = null;
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    public void createUniqueUserSuccess() {
        this.email = TestDataGenerator.getRandomEmail();
        this.password = TestDataGenerator.getRandomPassword();
        this.name = TestDataGenerator.getRandomName();

        Response response = userClient.createUser(email, password, name);
        accessToken = response.path("accessToken");
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue());

    }

    @Test
    public void createDuplicateUserFails() {
        this.email = TestDataGenerator.getRandomEmail();
        this.password = TestDataGenerator.getRandomPassword();
        this.name = TestDataGenerator.getRandomName();

        Response firstResponse = userClient.createUser(email, password, name);
        firstResponse.then().statusCode(200);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userClient.createUser(email, password, name);
        secondResponse.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    public void createUserWithoutRequiredFieldFails() {
        this.email = TestDataGenerator.getRandomEmail();
        this.password = TestDataGenerator.getRandomPassword();
        this.name = TestDataGenerator.getRandomName();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("name", name);

        Response response = userClient.createUser(requestBody);
        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

}
