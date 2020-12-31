package com.aqulasoft.disyam;

public class Main {

    public static void main(final String[] args) {
        final String token = args[0];
        final String username = args[1];
        final String password = args[2];

        DisYamBot disYamBot = new DisYamBot(token, username, password);
        disYamBot.Start();
    }
}
