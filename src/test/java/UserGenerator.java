import com.github.javafaker.Faker;

import java.util.Locale;

public class UserGenerator {
    private UserGenerator() {

    }

    public static class User {
        private String login;
        private String password;
        private boolean status;

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public User(String login, String password, boolean status) {
            this.login = login;
            this.password = password;
            this.status = status;
        }
    }

    public static String generateLogin(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.name().firstName().toLowerCase();
    }

    public static String generatePassword(String locale) {
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.hobbit().character().toLowerCase();
    }

    public static class Registration {
        private Registration() {
        }

        public static User generateUser(String locale, boolean activeAccount) {
            return new User(generateLogin(locale), generatePassword(locale), activeAccount);
        }
    }
}
