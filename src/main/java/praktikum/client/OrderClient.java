package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import praktikum.config.ApiConfig;
import praktikum.model.Order;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class OrderClient {

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .when()
                .get(ApiConfig.INGREDIENTS_PATH)
                .then()
                .extract()
                .response();
    }

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {
        RequestSpecification spec = given()
                .baseUri(ApiConfig.BASE_URL)
                .contentType(JSON)
                .body(order);

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

    @Step("Создание заказа с ингредиентами: {ingredientIds}")
    public Response createOrderWithIngredients(List<String> ingredientIds, String accessToken) {
        return createOrder(new Order(ingredientIds), accessToken);
    }
}
