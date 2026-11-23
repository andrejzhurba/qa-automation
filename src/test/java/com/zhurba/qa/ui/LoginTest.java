package com.zhurba.qa.ui;

import com.zhurba.qa.config.Config;
import com.zhurba.qa.ui.pages.LoginPage;
import com.zhurba.qa.ui.pages.ProductsPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@Tag("ui")
class LoginTest extends BaseUiTest {

    private final LoginPage loginPage = new LoginPage();
    private final ProductsPage productsPage = new ProductsPage();

    @Test
    void standardUserCanLogIn() {
        open("/");
        loginPage.login(Config.UI_USERNAME, Config.UI_PASSWORD);

        productsPage.assertOpened();
    }

    @Test
    void lockedOutUserCannotLogIn() {
        open("/");
        loginPage.login("locked_out_user", Config.UI_PASSWORD);

        loginPage.assertErrorShown("Epic sadface: Sorry, this user has been locked out.");
    }
}
