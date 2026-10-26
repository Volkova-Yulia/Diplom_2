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

import static org.hamcrest.CoreMatchers.*;

public class UserCreationTest {
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
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания пользователя с уникальными данными: код 200, success=true, поля user и accessToken заполнены")
    public void createUniqueUserSuccess() {
        User user = TestDataGenerator.getRandomUser();

        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание дубликата пользователя")
    @Description("Повторная регистрация с теми же данными: код 403, сообщение об ошибке User already exists")
    public void createDuplicateUserFails() {
        User user = TestDataGenerator.getRandomUser();

        Response firstResponse = userClient.createUser(user);
        firstResponse.then().statusCode(200);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userClient.createUser(user);
        secondResponse.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Регистрация без обязательного поля email: код 403, сообщение Email, password and name are required fields")
    public void createUserWithoutEmailFails() {
        User user = TestDataGenerator.getRandomUser();
        user.setEmail(null);

        Response response = userClient.createUser(user);
        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Регистрация без обязательного поля password: код 403, сообщение Email, password and name are required fields")
    public void createUserWithoutPasswordFails() {
        User user = TestDataGenerator.getRandomUser();
        user.setPassword(null);

        Response response = userClient.createUser(user);
        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Регистрация без обязательного поля name: код 403, сообщение Email, password and name are required fields")
    public void createUserWithoutNameFails() {
        User user = TestDataGenerator.getRandomUser();
        user.setName(null);

        Response response = userClient.createUser(user);
        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }
}
