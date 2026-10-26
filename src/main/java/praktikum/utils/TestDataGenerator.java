package praktikum.utils;

import praktikum.model.User;

public class TestDataGenerator {

    public static User getRandomUser() {
        return new User(getRandomEmail(), getRandomPassword(), getRandomName());
    }


    public static String getRandomEmail() {
        return "test_" + System.currentTimeMillis() + "@yandex.ru";
    }

    public static String getRandomPassword() {
        return "pass_" + System.currentTimeMillis();
    }

    public static String getRandomName() {
        return "User_" + System.currentTimeMillis();
    }
}
