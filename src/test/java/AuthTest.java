import com.codeborne.selenide.Condition;
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

    void setUp(String status) {
        Gson gson = new Gson();
        user = UserGenerator.Registration.generateUser("en", status);
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(gson.toJson(user)) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
        open("http://localhost:9999");
    }

    void fillIn(String login, String password) {
        if (login.equals("invalid")) {
            $("[data-test-id='login'] .input__control").setValue("foo");
        } else if (login.equals("valid")) {
            $("[data-test-id='login'] .input__control").setValue(user.getLogin());
        }
        if (password.equals("invalid")) {
            $("[data-test-id='password'] .input__control").setValue("foo");
        } else if (password.equals("valid")) {
            $("[data-test-id='password'] .input__control").setValue(user.getPassword());
        }
        $(".button[data-test-id='action-login']").click();
    }

    @Test
    public void invalidUserTest() {
        setUp("active");
        fillIn("invalid", "invalid");
        $(".notification[data-test-id='error-notification']")
                .shouldBe(Condition.visible).
                shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void activeUserValidCredentialsTest() {
        setUp("active");
        fillIn("valid", "valid");
        $(".heading").shouldHave(Condition.exactText("Личный кабинет"));
    }

    @Test
    public void blockedUserValidCredentialsTest() {
        setUp("blocked");
        fillIn("valid", "valid");
        $(".notification[data-test-id='error-notification']")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("заблокирован"));
    }

    @Test
    public void activeUserInvalidLoginTest() {
        setUp("active");
        fillIn("invalid", "valid");
        $(".notification[data-test-id='error-notification']").
                shouldBe(Condition.visible).
                shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void blockedUserInvalidLoginTest() {
        setUp("blocked");
        fillIn("invalid", "valid");
        $(".notification[data-test-id='error-notification']").
                shouldBe(Condition.visible).
                shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void activeUserInvalidPasswordTest() {
        setUp("active");
        fillIn("valid", "invalid");
        $(".notification[data-test-id='error-notification']")
                .shouldBe(Condition.visible).
                shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

    @Test
    public void blockedUserInvalidPasswordTest() {
        setUp("blocked");
        fillIn("valid", "invalid");
        $(".notification[data-test-id='error-notification']").
                shouldBe(Condition.visible).
                shouldHave(Condition.text("Неверно указан логин или пароль"));
    }

}
