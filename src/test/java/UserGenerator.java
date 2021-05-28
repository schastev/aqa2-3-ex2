import com.github.javafaker.Faker;

import java.util.Locale;
import java.util.Random;

public class UserGenerator {
    private UserGenerator() {

    }

    public static class User {
        private String login;
        private String password;
        private String status;

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getStatus() {
            return status;
        }

        public User(String login, String password, String status) {
            this.login = login;
            this.password = password;
            this.status = status;
        }
    }

    public static String generateLogin(String locale){
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.name().firstName().toLowerCase();
    }
    public static String generatePassword(String locale){
        Faker faker = new Faker(Locale.forLanguageTag(locale));
        return faker.hobbit().character().toLowerCase();
    }
    public static String generateStatus(String status){
        if (status.equals("active") || status.equals("blocked")){
            return status;
        }
        return null;
    }
    public static class Registration {
        private Registration() {
        }

        public static User generateUser(String locale, String status) {
            User user = new User(generateLogin(locale), generatePassword(locale), generateStatus(status));
            return user;
        }
    }
}
