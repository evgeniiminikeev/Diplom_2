package config;

public class Config {
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    public static final String CREATE_USER = "/api/auth/register";
    public static final String LOGIN_USER = "/api/auth/login";
    public static final String USER = "/api/auth/user";
    public static final String ORDER = "/api/orders";
    public static final String INGREDIENTS = "/api/ingredients";
    public static final String CREATE_USER_EXIST_DETAILED_ERROR_TEXT_403 = "User already exists";
    public static final String CREATE_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_403 = "Email, password and name are required fields";
    public static final String LOGIN_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_401 = "email or password are incorrect";
    public static final String PATCH_DETAILED_ERROR_TEXT_403 = "User with such email already exists";
    public static final String PATCH_WITHOUT_TOKEN_DETAILED_ERROR_TEXT_401 = "You should be authorised";
    public static final String DELETE_CONFIRM_MESSAGE = "User successfully removed";
    public static final String CREATE_ORDER_REQUIRED_FIELDS_DETAILED_ERROR_TEXT_400 = "Ingredient ids must be provided";
    public static final String GET_USER_ORDER_DETAILED_ERROR_TEXT_401 = "You should be authorised";
}