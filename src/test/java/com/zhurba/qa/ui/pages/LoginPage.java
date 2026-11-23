package com.zhurba.qa.ui.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement userNameInput = $("[data-test='username']");
    private final SelenideElement passwordInput = $("[data-test='password']");
    private final SelenideElement loginButton = $("[data-test='login-button']");
    private final SelenideElement errorBanner = $("[data-test='error']");

    public void login(String userName, String password) {
        userNameInput.setValue(userName);
        passwordInput.setValue(password);
        loginButton.click();
    }

    public void assertErrorShown(String message) {
        errorBanner.shouldBe(visible).shouldHave(exactText(message));
    }
}
