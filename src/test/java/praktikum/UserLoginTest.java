package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.model.User;
import praktikum.utils.TestDataGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserLoginTest {
    private User user;
    private String accessToken;
    private UserClient userClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
        accessToken = null;
        user = TestDataGenerator.getRandomUser();
        Response registerResponse = userClient.createUser(user);
        registerResponse.then().statusCode(200);
        accessToken = registerResponse.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешный логин")
    @Description("Логин существующего пользователя с корректными данными: код 200, success=true, поля user и accessToken заполнены")
    public void loginSuccess() {
        Response loginResponse = userClient.login(user);
        loginResponse.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным логином")
    @Description("Логин с несуществующим email: код 401, сообщение email or password are incorrect")
    public void loginWithWrongEmailFails() {
        User userWithWrongEmail = new User(TestDataGenerator.getRandomEmail(), user.getPassword(), user.getName());

        Response loginResponse = userClient.login(userWithWrongEmail);
        loginResponse.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Логин с неправильным паролем: код 401, сообщение email or password are incorrect")
    public void loginWithWrongPasswordFails() {
        User userWithWrongPassword = new User(user.getEmail(), TestDataGenerator.getRandomPassword(), user.getName());

        Response loginResponse = userClient.login(userWithWrongPassword);
        loginResponse.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }
}
