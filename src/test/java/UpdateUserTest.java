import client.UserClient;
import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import step.ClientStep;

import static config.Config.*;
import static org.apache.http.HttpStatus.*;

public class UpdateUserTest {
    private static ClientStep clientStep;
    private static final Faker faker = new Faker();
    private static final String existedClientEmail = faker.internet().emailAddress();
    private static final String originalEmail = faker.internet().emailAddress();
    private static final String newEmail = faker.internet().emailAddress();
    private static final String password = faker.internet().password(1, 10, true, true);
    private static final String originalName = faker.name().name();
    private static final String newName = faker.name().name();

    @BeforeClass
    public static void setUp() {
        clientStep = new ClientStep(new UserClient());
        clientStep.addClientsDataToClear(clientStep.setAccessToken(clientStep.createClient(existedClientEmail, password, originalName)));
        clientStep.addClientsDataToClear(clientStep.setAccessToken(clientStep.createClient(originalEmail, password, originalName)));
    }

    @Test
    @DisplayName("Изменение данных клиента с корректным токеном")
    public void patchClientDataWithCorrectTokenShouldReturnOk() {
        ValidatableResponse response = clientStep.patchClient(newEmail, newName, clientStep.getToken());
        clientStep.checkStatusCode(response, SC_OK);
        clientStep.checkResponseBodyAfterPatching(true, response, newEmail, newName, null);
    }

    @Test
    @DisplayName("Изменение данных клиента с некорректным токеном")
    public void patchClientDataWithIncorrectTokenShouldReturnOk() {
        ValidatableResponse response = clientStep.patchClient(newEmail, newName, "");
        clientStep.checkStatusCode(response, SC_UNAUTHORIZED);
        clientStep.checkResponseBodyAfterPatching(false, response, null, null, PATCH_WITHOUT_TOKEN_DETAILED_ERROR_TEXT_401);
    }


    @Test
    @DisplayName("Изменение email клиента на уже существующий email")
    public void patchClientEmailToAlreadyExistingEmailShouldReturnError() {
        ValidatableResponse response = clientStep.patchClient(existedClientEmail, originalName, clientStep.getToken());
        clientStep.checkStatusCode(response, SC_FORBIDDEN);
        clientStep.checkResponseBodyAfterPatching(false, response, null, null, PATCH_DETAILED_ERROR_TEXT_403);
    }

    @AfterClass
    public static void tearDown() {
        clientStep.clearTestClientData();
    }

}