package client;

import dto.CreateUser;
import dto.LoginUser;
import dto.PatchUser;
import io.restassured.response.Response;

import static config.Config.*;

public class UserClient extends RestClient{
    public Response createClient(CreateUser createUser) {
        return getDefaultRequestSpecificationWithoutToken()
                .body(createUser)
                .when()
                .post(CREATE_USER);
    }
    public Response loginClient(LoginUser loginUser) {
        return getDefaultRequestSpecificationWithoutToken()
                .body(loginUser)
                .when()
                .post(LOGIN_USER);
    }
    public Response deleteClient(String token) {
        return getRequestSpecificationWithToken(token)
                .when()
                .delete(USER);
    }
    public Response patchClient(PatchUser patchUser, String token) {
        return getRequestSpecificationWithToken(token)
                .body(patchUser)
                .when()
                .patch(USER);
    }
}
