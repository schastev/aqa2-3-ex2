import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class AuthTest {
    private RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setContentType("application/json")
            .setAccept("application/json")
            .log(LogDetail.ALL)
            .build();
    UserGenerator.User user;
    private SelenideElement loginField = $("[data-test-id='login'] .input__control");
    private SelenideElement passwordField = $("[data-test-id='password'] .input__control");
    private SelenideElement loginButton = $(".button[data-test-id='action-login']");
    private SelenideElement notification = $(".notification[data-test-id='error-notification']");
    private SelenideElement heading = $(".heading");

    void setUp(boolean activeAccount) {
        Gson gson = new Gson();
        user = UserGenerator.Registration.generateUser("en", activeAccount);
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(gson.toJson(user)) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
        open("http://localhost:9999");
    }

    void inputPassword(boolean passwordValidity) {
        if (!passwordValidity) {
            passwordField.setValue("foo");
        } else {
            passwordField.setValue(user.getPassword());
        }
        loginButton.click();
    }

    void inputLogin(boolean loginValidity) {
        if (!loginValidity) {
            loginField.setValue("foo");
        } else {
            loginField.setValue(user.getLogin());
        }
    }

    @Test
    public void invalidUserTest() {
        setUp(true);
        inputLogin(false);
        inputPassword(false);
        notification
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void activeUserValidCredentialsTest() {
        setUp(true);
        inputLogin(true);
        inputPassword(true);
        heading.shouldHave(Condition.exactText("Личный кабинет"));
    }

    @Test
    public void blockedUserValidCredentialsTest() {
        setUp(false);
        inputLogin(true);
        inputPassword(true);
        notification
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("заблокирован"));
    }

    @Test
    public void activeUserInvalidLoginTest() {
        setUp(true);
        inputLogin(false);
        inputPassword(true);
        notification
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void blockedUserInvalidLoginTest() {
        setUp(false);
        inputLogin(false);
        inputPassword(true);
        notification
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void activeUserInvalidPasswordTest() {
        setUp(false);
        inputLogin(true);
        inputPassword(false);
        notification
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void blockedUserInvalidPasswordTest() {
        setUp(false);
        inputLogin(true);
        inputPassword(false);
        notification
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

}
