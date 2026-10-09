package praktikum;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.utils.TestDataGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest {
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
    public void loginSuccess() {
        this.email = TestDataGenerator.getRandomEmail();
        this.password = TestDataGenerator.getRandomPassword();
        this.name = TestDataGenerator.getRandomName();

        Response registerResponse = userClient.createUser(email, password, name);
        accessToken = registerResponse.path("accessToken");
        Response loginResponse = userClient.login(email, password);
        loginResponse.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue());
    }

    @Test
    public void loginWithWrongCredentialsFails() {
        String nonExistentEmail = TestDataGenerator.getRandomEmail();
        String nonExistentPassword = TestDataGenerator.getRandomPassword();

        Response loginResponse = userClient.login(nonExistentEmail, nonExistentPassword);
        loginResponse.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("Email or password are incorrect"));
    }
}
