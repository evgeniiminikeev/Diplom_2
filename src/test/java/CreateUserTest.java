import client.UserClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import step.ClientStep;

import static org.apache.http.HttpStatus.*;
import static config.Config.*;

public class CreateUserTest {
    private final Faker faker = new Faker();
    private final String email = faker.internet().emailAddress();
    private final String password = faker.internet().password(1, 10, true, true);
    private final String name = faker.name().name();
    private static ClientStep clientStep;

    @Before
    public void setUp() {
        clientStep = new ClientStep(new UserClient());
    }

    @Test
    @DisplayName("Создание клиента со всеми полями")
    public void createClientWithAllDataShouldReturnOk() {
        ValidatableResponse response = clientStep.createClient(email, password, name);
        clientStep.checkStatusCode(response, SC_OK);
        clientStep.checkResponseBody(true, response, email, name, null);
        clientStep.addClientsDataToClear(clientStep.setAccessToken(response));
    }

    @Test
    @DisplayName("Создание уже существующего клиента")
    public void createAlreadyExistingClientShouldReturnError() {
        ValidatableResponse firstResponse = clientStep.createClient(email, password, name);
        clientStep.addClientsDataToClear(clientStep.setAccessToken(firstResponse));
        ValidatableResponse response = clientStep.createClient(email, password, name);
        clientStep.checkStatusCode(response, SC_FORBIDDEN);
        clientStep.checkResponseBody(false, response, null, null, CREATE_USER_EXIST_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента с пустым email")
    public void createClientWithEmptyEmailShouldReturnError() {
        ValidatableResponse response = clientStep.createClient("", password, name);
        clientStep.checkStatusCode(response, SC_FORBIDDEN);
        clientStep.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента с пустым password")
    public void createClientWithEmptyPasswordShouldReturnError() {
        ValidatableResponse response = clientStep.createClient(email, "", name);
        clientStep.checkStatusCode(response, SC_FORBIDDEN);
        clientStep.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента с пустым name")
    public void createClientWithEmptyNameShouldReturnError() {
        ValidatableResponse response = clientStep.createClient(email, password, "");
        clientStep.checkStatusCode(response, SC_FORBIDDEN);
        clientStep.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @Test
    @DisplayName("Создание клиента без указания данных")
    public void createClientWithoutDataShouldReturnError() {
        ValidatableResponse response = clientStep.createClient(null, null, null);
        clientStep.checkStatusCode(response, SC_FORBIDDEN);
        clientStep.checkResponseBody(false, response, null, null, CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403);
    }

    @AfterClass
    public static void tearDown() {
        clientStep.clearTestClientData();
    }
}
