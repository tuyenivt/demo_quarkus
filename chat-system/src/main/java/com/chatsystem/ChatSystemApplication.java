package com.chatsystem;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class ChatSystemApplication {
    public static void main(String... args) {
        Quarkus.run(args);
    }
} 