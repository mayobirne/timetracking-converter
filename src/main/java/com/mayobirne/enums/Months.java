package com.mayobirne.enums;

/**
 * Created by Christian on 11.02.2016.
 */
public enum Months {

    JANUARY ("January", 0),
    FEBRUARY ("February", 1),
    MARCH ("March", 2),
    APRIL ("April", 3),
    MAY ("May", 4),
    JUNE ("June", 5),
    JULY ("July", 6),
    AUGUST ("August", 7),
    SEPTEMBER ("September", 8),
    OCTOBER ("October", 9),
    NOVEMBER ("November", 10),
    DECEMBER ("December", 11);

    String text;
    Integer number;

    Months(String text, Integer number) {
        this.text = text;
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return text;
    }
}
