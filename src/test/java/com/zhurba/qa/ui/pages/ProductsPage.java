package com.zhurba.qa.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.zhurba.qa.ui.SortOption;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProductsPage {

    private final SelenideElement title = $("[data-test='title']");
    private final SelenideElement sortDropdown = $("[data-test='product-sort-container']");
    private final SelenideElement cartLink = $("[data-test='shopping-cart-link']");
    private final SelenideElement cartBadge = $("[data-test='shopping-cart-badge']");
    private final ElementsCollection items = $$("[data-test='inventory-item']");
    private final ElementsCollection names = $$("[data-test='inventory-item-name']");
    private final ElementsCollection prices = $$("[data-test='inventory-item-price']");

    public void assertOpened() {
        title.shouldBe(visible).shouldHave(exactText("Products"));
        items.shouldHave(size(6));
    }

    public void sortBy(SortOption option) {
        sortDropdown.selectOptionByValue(option.getValue());
        sortDropdown.shouldHave(value(option.getValue()));
    }

    public List<String> itemNames() {
        return names.shouldHave(size(6)).texts();
    }

    public List<String> itemPrices() {
        return prices.shouldHave(size(6)).texts();
    }

    public void assertNamesInOrder(List<String> expected) {
        names.shouldHave(exactTexts(expected));
    }

    public void assertPricesInOrder(List<String> expected) {
        prices.shouldHave(exactTexts(expected));
    }

    public String firstProductName() {
        return names.first().shouldBe(visible).getText();
    }

    public void addToCart(String productName) {
        item(productName).$("button").shouldHave(exactText("Add to cart")).click();
    }

    public void removeFromCart(String productName) {
        item(productName).$("button").shouldHave(exactText("Remove")).click();
    }

    public void assertCartCount(int expected) {
        if (expected == 0) {
            cartBadge.shouldNot(exist);
        } else {
            cartBadge.shouldBe(visible).shouldHave(exactText(String.valueOf(expected)));
        }
    }

    public CartPage openCart() {
        cartLink.click();
        return new CartPage();
    }

    private SelenideElement item(String productName) {
        return names.findBy(exactText(productName))
                .closest("[data-test=inventory-item]").shouldBe(visible);
    }
}
