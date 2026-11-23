package com.zhurba.qa.ui;

import com.codeborne.selenide.Configuration;
import com.zhurba.qa.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public abstract class BaseUiTest {

    @BeforeAll
    static void configureBrowser() {
        Configuration.baseUrl = Config.UI_BASE_URL;
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "true"));
        Configuration.timeout = Long.getLong("selenide.timeout", 8000L);
        Configuration.browserSize = System.getProperty("selenide.browserSize", "1280x900");
        Configuration.reportsFolder = System.getProperty("selenide.reportsFolder", "target/selenide");

        if (Boolean.parseBoolean(System.getenv("QA_CONTAINER"))) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
            Configuration.browserCapabilities = options;
        }
    }

    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }
}
