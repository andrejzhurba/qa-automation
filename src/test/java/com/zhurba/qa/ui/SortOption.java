package com.zhurba.qa.ui;

public enum SortOption {

    NAME_A_TO_Z("az"),
    NAME_Z_TO_A("za"),
    PRICE_LOW_TO_HIGH("lohi"),
    PRICE_HIGH_TO_LOW("hilo");

    private final String value;

    SortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isDescending() {
        return this == NAME_Z_TO_A || this == PRICE_HIGH_TO_LOW;
    }
}
