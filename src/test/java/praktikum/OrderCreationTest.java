package praktikum;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.utils.TestDataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String email = TestDataGenerator.getRandomEmail();
        String password = TestDataGenerator.getRandomPassword();
        String name = TestDataGenerator.getRandomName();

        Response registerResponse = userClient.createUser(email, password, name);
        accessToken = registerResponse.path("accessToken");
    }

    @Test
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
    public void createOrderWithoutAuthFails() {
        Response response = orderClient.createOrderWithIngredients(ingredientIds, null);
        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Test
    public void createOrderWithoutIngredientsFails() {
        createUserAndGetToken();
        List<String> ingredientsList = new ArrayList<>();

        Map<String, Object> requestBody = Map.of("ingredients", ingredientsList);

        Response response = orderClient.createOrder(requestBody, accessToken);
        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithInvalidHashFails() {
        createUserAndGetToken();

        Map<String, Object> requestBody = Map.of("ingredients", List.of("invalid_hash"));

        Response response = orderClient.createOrder(requestBody, accessToken);
        response.then()
                .statusCode(500)
                .body("message", is("Internal Server Error"));
    }
}
