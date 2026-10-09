package praktikum.utils;


public class TestDataGenerator {

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
