import client.OrderClient;
import client.UserClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.ClientStep;
import step.OrderStep;

import static config.Config.*;
import static org.apache.http.HttpStatus.*;

public class GetUserOrderTest {
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
        ValidatableResponse responseClient = clientStep.createClient(email, password, name);
        clientStep.addClientsDataToClear(clientStep.setAccessToken(responseClient));
        ValidatableResponse firstResponseOrder = orderStep.createOrder(orderStep.getAllIngredient(), clientStep.getToken());
        ValidatableResponse secondResponseOrder = orderStep.createOrder(orderStep.getAllIngredient(), clientStep.getToken());
        orderStep.getOrdersForCompare(firstResponseOrder);
        orderStep.getOrdersForCompare(secondResponseOrder);
    }

    @Test
    @DisplayName("Получение списка заказов пользователя с корректным токеном")
    public void getUserOrdersWithCorrectTokenShouldReturnOk() {
        ValidatableResponse response = orderStep.getUserOrders(clientStep.getToken()).then();
        orderStep.checkStatusCode(response, SC_OK);
        orderStep.checkResponseBodyUser(true, response, orderStep.getOrderList(), null);
    }

    @Test
    @DisplayName("Получение списка заказов пользователя с некорректным токеном")
    public void getUserOrdersWithIncorrectTokenShouldReturnError() {
        ValidatableResponse response = orderStep.getUserOrders(faker.internet().password()).then();
        orderStep.checkStatusCode(response, SC_UNAUTHORIZED);
        orderStep.checkResponseBodyUser(false, response, null, GET_USER_ORDER_DETAILED_ERROR_TEXT_401);
    }

    @After
    public void tearDown() {
        clientStep.clearTestClientData();
    }
}