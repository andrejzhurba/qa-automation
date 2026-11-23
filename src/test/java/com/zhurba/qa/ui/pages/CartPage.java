package com.zhurba.qa.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CartPage {

    private final SelenideElement title = $("[data-test='title']");
    private final ElementsCollection itemNames = $$("[data-test='cart-list'] [data-test='inventory-item-name']");

    public void assertOpened() {
        title.shouldBe(visible).shouldHave(exactText("Your Cart"));
    }

    public void assertItems(String... names) {
        itemNames.shouldHave(exactTexts(names));
    }

    public void assertEmpty() {
        itemNames.shouldHave(size(0));
    }
}
