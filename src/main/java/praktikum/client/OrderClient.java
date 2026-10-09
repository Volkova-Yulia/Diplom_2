package praktikum.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import praktikum.config.ApiConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class OrderClient {

    public Response getIngredients() {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .when()
                .get(ApiConfig.INGREDIENTS_PATH)
                .then()
                .extract()
                .response();
    }

    public Response createOrder(Map<String, Object> body, String accessToken) {
        RequestSpecification spec = given()
                .baseUri(ApiConfig.BASE_URL)
                .contentType(JSON)
                .body(body);

        if (accessToken != null) {
            spec = spec.header("Authorization", accessToken);
        }

        return spec
                .when()
                .post(ApiConfig.ORDERS_PATH)
                .then()
                .extract()
                .response();
    }

    public Response createOrderWithIngredients(List<String> ingredientIds, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("ingredients", ingredientIds);
        return createOrder(body, accessToken);
    }
}
