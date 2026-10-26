package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.config.ApiConfig;
import praktikum.model.User;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class UserClient {

    @Step("Создание пользователя: email={user.email}, name={user.name}")
    public Response createUser(User user) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .contentType(JSON)
                .body(user)
                .when()
                .post(ApiConfig.REGISTER_PATH)
                .then()
                .extract()
                .response();
    }

    @Step("Логин пользователя: email={user.email}")
    public Response login(User user) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .contentType(JSON)
                .body(user)
                .when()
                .post(ApiConfig.LOGIN_PATH)
                .then()
                .extract()
                .response();
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .delete(ApiConfig.USER_PATH)
                .then()
                .extract()
                .response();
    }
}
