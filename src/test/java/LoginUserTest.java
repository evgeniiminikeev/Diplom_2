import client.UserClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.ClientStep;

import static config.Config.*;
import static org.apache.http.HttpStatus.*;

public class LoginUserTest {
    private ClientStep clientStep;
    private final Faker faker = new Faker();
    private final String email = faker.internet().emailAddress();
    private final String password = faker.internet().password(5, 10, true, true);
    private final String name = faker.name().name();

    @Before
    public void setUp() {
        clientStep = new ClientStep(new UserClient());
        clientStep.addClientsDataToClear(clientStep.setAccessToken(clientStep.createClient(email, password, name)));
    }

    @Test
    @DisplayName("Логин клиента со всеми данными")
    public void loginUserWithAllDataShouldReturnOk() {
        ValidatableResponse response = clientStep.loginClient(email, password);
        clientStep.checkStatusCode(response, SC_OK);
        clientStep.checkResponseBody(true, response, email, name, null);
    }

    @Test
    @DisplayName("Логин клиента без email")
    public void loginUserWithoutEmailShouldReturnError() {
        ValidatableResponse response = clientStep.loginClient(null, password);
        clientStep.checkStatusCode(response, SC_UNAUTHORIZED);
        clientStep.checkResponseBody(false, response, null, null, LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401);
    }

    @Test
    @DisplayName("Логин клиента без password")
    public void loginUserWithoutPasswordShouldReturnError() {
        ValidatableResponse response = clientStep.loginClient(email, null);
        clientStep.checkStatusCode(response, SC_UNAUTHORIZED);
        clientStep.checkResponseBody(false, response, null, null, LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401);
    }

    @After
    public void tearDown() {
        clientStep.clearTestClientData();
    }
}