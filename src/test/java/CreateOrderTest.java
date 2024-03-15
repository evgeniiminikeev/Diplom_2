import client.OrderClient;
import client.UserClient;
import com.github.javafaker.Faker;
import io.qameta.allure.Issue;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.ClientStep;
import step.OrderStep;

import static org.apache.http.HttpStatus.*;
import static config.Config.*;

public class CreateOrderTest {
    private OrderStep orderStep;
    private ClientStep clientStep;
    private final Faker faker = new Faker();
    private final String email = faker.internet().emailAddress();
    private final String password = faker.internet().password(1, 10, true, true);
    private final String name = faker.name().name();

    @Before
    public void setUp() {
        orderStep = new OrderStep(new OrderClient());
        clientStep = new ClientStep(new UserClient());
        ValidatableResponse response = clientStep.createClient(email, password, name);
        clientStep.addClientsDataToClear(clientStep.setAccessToken(response));
    }

    @Test
    @DisplayName("Создание заказа с корректным токеном и одним ингредиентом")
    public void createOrderWithCorrectTokenShouldReturnOk() {
        String[] ingredients = new String[]{orderStep.getRandomIngredient()};
        ValidatableResponse response = orderStep.createOrder(ingredients, clientStep.getToken());
        orderStep.checkStatusCode(response, SC_OK);
        orderStep.checkResponseBodyOrder(true, response, ingredients, null, name, email);
    }

    @Test
    @DisplayName("Создание заказа с несуществующим токеном")
    @Issue("BUG-1")
    public void createOrderWithNonExistenTokenShouldReturnError() {
        String[] ingredients = orderStep.getAllIngredient();
        ValidatableResponse response = orderStep.createOrder(ingredients, faker.internet().password());
        orderStep.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Создание заказа со всеми ингредиентами")
    public void createOrderWithAllIngredientsShouldReturnOk() {
        String[] ingredients = orderStep.getAllIngredient();
        ValidatableResponse response = orderStep.createOrder(ingredients, clientStep.getToken());
        orderStep.checkStatusCode(response, SC_OK);
        orderStep.checkResponseBodyOrder(true, response, ingredients, null, name, email);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsShouldReturnError() {
        ValidatableResponse response = orderStep.createOrder(null, clientStep.getToken());
        orderStep.checkStatusCode(response, SC_BAD_REQUEST);
        orderStep.checkResponseBodyOrder(false, response, null, CREATE_ORDER_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_400, null, null);
    }

    @Test
    @DisplayName("Создание заказа с невалидным хэшем ингредиента")
    public void createOrderWithInvalidIngredientHashShouldReturnError() {
        String[] ingredients = new String[]{faker.internet().password()};
        ValidatableResponse response = orderStep.createOrder(ingredients, clientStep.getToken());
        orderStep.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        clientStep.clearTestClientData();
    }
}