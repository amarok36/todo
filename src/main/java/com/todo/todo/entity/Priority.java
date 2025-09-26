package com.todo.todo.entity;

public enum Priority {
    HIGH("высокий"),
    MEDIUM("средний"),
    LOW("низкий");

    private final String russianLabel;

    Priority(String russianLabel) {
        this.russianLabel = russianLabel;
    }

    public String getRussianLabel() {
        return russianLabel;
    }
}