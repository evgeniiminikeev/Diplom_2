package client;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static config.Config.BASE_URI;
import static io.restassured.RestAssured.given;

public abstract class RestClient {
    protected RequestSpecification getDefaultRequestSpecificationWithoutToken() {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON);

    }
    protected RequestSpecification getRequestSpecificationWithToken(String token) {
        return given()
                .baseUri(BASE_URI)
                .header("Authorization", token)
                .contentType(ContentType.JSON);
    }
}
