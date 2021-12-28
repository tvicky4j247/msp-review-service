package co.siegerand.reviewservice;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public class MongoDBInstance {

    // declare mongo db container following singleton pattern
    @Container
    private static final MongoDBContainer database = new MongoDBContainer("mongo:4.4.2");

    static {
        // start singleton server. Container will be stopped automatically when tests complete.
        database.start();
    }

    @DynamicPropertySource
    private static void updateDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", database::getHost);
        registry.add("spring.data.mongodb.port", () -> database.getMappedPort(27017));
        registry.add("spring.data.mongodb.database", () -> "test");
    }

}
