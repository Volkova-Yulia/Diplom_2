package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.model.Order;
import praktikum.utils.TestDataGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;

public class OrderCreationTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private List<String> ingredientIds;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        accessToken = null;
        Response ingredientsResponse = orderClient.getIngredients();
        ingredientsResponse.then()
                .statusCode(200);

        List<String> allIngredients = ingredientsResponse.path("data._id");
        this.ingredientIds = allIngredients.subList(0, 1);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    public void createUserAndGetToken() {
        Response registerResponse = userClient.createUser(TestDataGenerator.getRandomUser());
        accessToken = registerResponse.path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Авторизованный пользователь создаёт заказ с ингредиентами: код 200, success=true, order.number заполнен")
    public void createOrderWithAuthAndIngredients() {
        createUserAndGetToken();

        Response response = orderClient.createOrderWithIngredients(ingredientIds, accessToken);
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Заказ без токена авторизации: код 401, сообщение You should be authorised")
    public void createOrderWithoutAuthFails() {
        Response response = orderClient.createOrderWithIngredients(ingredientIds, null);
        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Заказ с пустым списком ингредиентов: код 400, сообщение Ingredient ids must be provided")
    public void createOrderWithoutIngredientsFails() {
        createUserAndGetToken();

        Response response = orderClient.createOrder(new Order(new ArrayList<>()), accessToken);
        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем")
    @Description("Заказ с несуществующим id ингредиента: код 400, сообщение One or more ids provided are incorrect")
    public void createOrderWithInvalidHashFails() {
        createUserAndGetToken();

        Response response = orderClient.createOrder(new Order(List.of("invalid_hash")), accessToken);
        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("One or more ids provided are incorrect"));
    }
}
