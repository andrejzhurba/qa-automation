package com.zhurba.qa.config;

public final class Config {

    public static final String UI_BASE_URL = System.getProperty("ui.baseUrl", "https://www.saucedemo.com/");
    public static final String UI_USERNAME = System.getProperty("ui.username", "standard_user");
    public static final String UI_PASSWORD = System.getProperty("ui.password", "secret_sauce");

    public static final String API_BASE_URL = System.getProperty("api.baseUrl", "https://restful-booker.herokuapp.com");
    public static final String API_USERNAME = System.getProperty("api.username", "admin");
    public static final String API_PASSWORD = System.getProperty("api.password", "password123");
    public static final int API_TIMEOUT = Integer.getInteger("api.timeout", 10_000);

    private Config() {
    }
}
