package praktikum.client;

import io.restassured.response.Response;
import praktikum.config.ApiConfig;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class UserClient {

    public Response createUser(String email, String password, String name) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("name", name);
        return createUser(body);
    }

    public Response createUser(Map<String, Object> body) {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .contentType(JSON)
                .body(body)
                .when()
                .post(ApiConfig.REGISTER_PATH)
                .then()
                .extract()
                .response();
    }

    public Response login(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .contentType(JSON)
                .body(body)
                .when()
                .post(ApiConfig.LOGIN_PATH)
                .then()
                .extract()
                .response();
    }

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