package step;

import client.UserClient;
import dto.CreateUser;
import dto.LoginUser;
import dto.PatchUser;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;
import static config.Config.*;

public class ClientStep {
    private final UserClient userClient;
    private String token = null;
    static List<String> clientData = new ArrayList<>();
    public ClientStep(UserClient userClient) {
        this.userClient = userClient;
    }

    public String getToken() {
        return token;
    }

    @Step("Создание клиента")
    public ValidatableResponse createClient(String email, String password, String name) {
        CreateUser createUser = new CreateUser();
        createUser.setEmail(email);
        createUser.setPassword(password);
        createUser.setName(name);
        return userClient.createClient(createUser).then();
    }

    @Step("Логин клиентом")
    public ValidatableResponse loginClient(String email, String password) {
        LoginUser loginUser = new LoginUser();
        loginUser.setEmail(email);
        loginUser.setPassword(password);
        return userClient.loginClient(loginUser).then();
    }

    @Step("Удаление клиента")
    public ValidatableResponse deleteClient(String token) {
        return userClient.deleteClient(token).then();
    }

    @Step("Изменение клиента")
    public ValidatableResponse patchClient(String email, String name, String token) {
        PatchUser patchUser = new PatchUser();
        patchUser.setEmail(email);
        patchUser.setName(name);
        return userClient.patchClient(patchUser, token).then();
    }

    @Step("Проверка статус кода в ответе")
    public void checkStatusCode(ValidatableResponse response, int code) {
        response.statusCode(code);
    }

    @Step("Проверка тела ответа")
    public void checkResponseBody(boolean isTestPositive, ValidatableResponse response, String email, String name, String detailedError) {
        if (isTestPositive) {
            response.assertThat().body("success", is(true))
                    .and().body("accessToken", notNullValue())
                    .and().body("refreshToken", notNullValue())
                    .and().body("user.email", equalTo(email))
                    .and().body("user.name", equalTo(name));
        } else {
            response.assertThat().body("success", is(false))
                    .and().body("message", equalTo(detailedError));
        }
    }

    @Step("Проверка тела ответа после обновления")
    public void checkResponseBodyAfterPatching(boolean isTestPositive, ValidatableResponse response, String email, String name, String detailedError) {
        if (isTestPositive) {
            response.assertThat().body("success", is(true))
                    .and().body("user.email", equalTo(email))
                    .and().body("user.name", equalTo(name));
        } else {
            response.assertThat().body("success", is(false))
                    .and().body("message", equalTo(detailedError));
        }
    }

    @Step("Проверка ответа при удалении клиента")
    public void checkResponseBodyAfterDeletion(ValidatableResponse response) {
        response.assertThat().body("success", is(true))
                .and().body("message", equalTo(DELETE_CONFIRM_MESSAGE));
    }

    @Step("Получение token из ответа")
    public String setAccessToken(ValidatableResponse response) {
        token = response.assertThat().extract().path("accessToken");
        return token;
    }

    @Step("Удаление созданных клиентов")
    public void clearTestClientData() {
        for (String token : clientData) {
            ValidatableResponse response = deleteClient(token);
            checkStatusCode(response, SC_ACCEPTED);
            checkResponseBodyAfterDeletion(response);
        }
        clientData.clear();
    }

    public void addClientsDataToClear(String token) {
        clientData.add(token);
    }
}
