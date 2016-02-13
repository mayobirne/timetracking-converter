package com.mayobirne.enums;

public enum CategoryNumbers {

    DOCUMENTATION ("Specification/Documentation", 1),
    SOFTWARE_DEVELOPMENT ("Software Development", 2),
    HARDWARE_DEVELOPMENT ("Hardware Development", 3),
    TEST ("Test", 4),
    INTERNAL_MEETINGS ("Internal Meetings", 5),
    CUSTOMER_COORDINATION ("Customer Coordination", 6),
    PROJECT_MANAGEMENT ("Project Management", 7),
    ADMINISTRATION ("Administration", 8),
    AA ("AA", 9),
    AE ("AE", 10);

    String text;
    Integer id;

    CategoryNumbers(String text, Integer id) {
        this.text = text;
        this.id = id;
    }

    public Integer getNumber() {
        return id;
    }

    @Override
    public String toString() {
        return text;
    }
}
