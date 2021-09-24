package com.aqulasoft.disyam.models.bot;

public class OperationDelete {
    private final int from;
    private final int to;
    private final String op;

    public OperationDelete(int from, int to) {
        op = "delete";
        this.from = from;
        this.to = to;
    }
}
