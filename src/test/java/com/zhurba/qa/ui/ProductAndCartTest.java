package com.zhurba.qa.ui;

import com.zhurba.qa.config.Config;
import com.zhurba.qa.ui.pages.CartPage;
import com.zhurba.qa.ui.pages.LoginPage;
import com.zhurba.qa.ui.pages.ProductsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;

@Tag("ui")
class ProductAndCartTest extends BaseUiTest {

    private final LoginPage loginPage = new LoginPage();
    private final ProductsPage productsPage = new ProductsPage();

    @BeforeEach
    void logIn() {
        open("/");
        loginPage.login(Config.UI_USERNAME, Config.UI_PASSWORD);
        productsPage.assertOpened();
        productsPage.assertCartCount(0);
    }

    @Test
    void cheapestProductCanBeAddedToCart() {
        List<String> expectedPrices = productsPage.itemPrices().stream()
                .sorted(Comparator.comparing(ProductAndCartTest::price))
                .toList();

        productsPage.sortBy(SortOption.PRICE_LOW_TO_HIGH);
        productsPage.assertPricesInOrder(expectedPrices);
        String productName = productsPage.firstProductName();

        productsPage.addToCart(productName);
        productsPage.assertCartCount(1);

        CartPage cartPage = productsPage.openCart();
        cartPage.assertOpened();
        cartPage.assertItems(productName);
    }

    @Test
    void removedProductDoesNotAppearInCart() {
        String productName = "Sauce Labs Backpack";
        productsPage.addToCart(productName);
        productsPage.assertCartCount(1);

        productsPage.removeFromCart(productName);
        productsPage.assertCartCount(0);

        CartPage cartPage = productsPage.openCart();
        cartPage.assertOpened();
        cartPage.assertEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = SortOption.class, names = {"NAME_A_TO_Z", "NAME_Z_TO_A"})
    void sortProductsByName(SortOption option) {
        List<String> expected = new ArrayList<>(productsPage.itemNames());
        expected.sort(option.isDescending() ? Comparator.reverseOrder() : Comparator.naturalOrder());

        productsPage.sortBy(option);

        productsPage.assertNamesInOrder(expected);
    }

    @ParameterizedTest
    @EnumSource(value = SortOption.class, names = {"PRICE_LOW_TO_HIGH", "PRICE_HIGH_TO_LOW"})
    void sortProductsByPrice(SortOption option) {
        List<String> expected = new ArrayList<>(productsPage.itemPrices());
        Comparator<String> byPrice = Comparator.comparing(ProductAndCartTest::price);
        expected.sort(option.isDescending() ? byPrice.reversed() : byPrice);

        productsPage.sortBy(option);

        productsPage.assertPricesInOrder(expected);
    }

    private static BigDecimal price(String text) {
        return new BigDecimal(text.replace("$", ""));
    }
}
