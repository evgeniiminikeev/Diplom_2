package client;

import dto.CreateOrder;
import io.restassured.response.Response;

import static config.Config.INGREDIENTS;
import static config.Config.ORDER;

public class OrderClient extends RestClient {
    public Response createOrder(CreateOrder CreateOrder, String token) {
        return getRequestSpecificationWithToken(token)
                .body(CreateOrder)
                .when()
                .post(ORDER);
    }

    public Response getUserOrder(String token) {
        return getRequestSpecificationWithToken(token)
                .when()
                .get(ORDER);
    }

    public Response getIngredients() {
        return getDefaultRequestSpecificationWithoutToken()
                .when()
                .get(INGREDIENTS);
    }
}