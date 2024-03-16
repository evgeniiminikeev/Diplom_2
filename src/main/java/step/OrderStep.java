package step;

import client.OrderClient;
import dto.CreateOrder;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.*;

public class OrderStep {
    private final OrderClient orderClient;
    private List<Integer> orderList = new ArrayList<>();
    public OrderStep(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    public List<Integer> getOrderList() {
        return orderList;
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(String[] ingredients, String token) {
        CreateOrder createOrder = new CreateOrder(ingredients);
        return orderClient.createOrder(createOrder, token).then();
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders(String token) {
        return orderClient.getUserOrder(token);
    }

    @Step("Выбор рандомного ингредиента")
    public String getRandomIngredient() {
        Response response = orderClient.getIngredients();
        List<String> ingredientIds = response.jsonPath().getList("data._id");
        int randomIndex = new Random().nextInt(ingredientIds.size());
        return ingredientIds.get(randomIndex);
    }

    @Step("Выбор всех ингредиентов")
    public String[] getAllIngredient() {
        Response response = orderClient.getIngredients();
        List<String> ingredientIds = response.jsonPath().getList("data._id");
        return ingredientIds.toArray(new String[0]);
    }

    @Step("Проверка статус кода в ответе")
    public void checkStatusCode(ValidatableResponse response, int code) {
        response.statusCode(code);
    }

    @Step("Проверка тела ответа при создании заказа")
    public void checkResponseBodyOrder(boolean isTestPositive, ValidatableResponse response, String[] ingredients, String detailedError, String ownerName, String ownerEmail) {
        if (isTestPositive) {
            response.assertThat().body("success", is(true))
                    .and().body("name", notNullValue())
                    .and().body("order.number", notNullValue())
                    .and().body("order.owner.name", is(ownerName))
                    .and().body("order.owner.email", is(ownerEmail));
            for (int i = 0; i < ingredients.length - 1; i++) {
                response.assertThat().body("order.ingredients[" + i + "]._id", equalTo(ingredients[i]));
            }
        } else {
            response.assertThat().body("success", is(false))
                    .and().body("message", equalTo(detailedError));
        }
    }

    @Step("Получение списка созданных заказов для дальнейшего сравнения")
    public List<Integer> getOrdersForCompare(ValidatableResponse response) {
        Integer orderNumber = response.assertThat().extract().path("order.number");
        orderList.add(orderNumber);
        return orderList;
    }

    @Step("Проверка тела ответа при получении списка заказов пользователя")
    public void checkResponseBodyUser(boolean isTestPositive, ValidatableResponse response, List<Integer> orderList, String detailedError) {
        if (isTestPositive) {
            response.assertThat().body("success", is(true));
            for (int i = 0; i < orderList.size(); i++) {
                response.assertThat().body("orders[" + i + "].number", equalTo(orderList.get(i)));
            }
        } else {
            response.assertThat().body("success", is(false))
                    .and().body("message", equalTo(detailedError));
        }
    }
}
