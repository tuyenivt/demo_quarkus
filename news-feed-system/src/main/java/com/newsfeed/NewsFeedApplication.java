package com.newsfeed;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.jboss.logging.Logger;

@QuarkusMain
public class NewsFeedApplication implements QuarkusApplication {
    private static final Logger LOG = Logger.getLogger(NewsFeedApplication.class);

    public static void main(String... args) {
        Quarkus.run(NewsFeedApplication.class, args);
    }

    @Override
    public int run(String... args) {
        LOG.info("Starting News Feed System...");
        Quarkus.waitForExit();
        return 0;
    }
} 