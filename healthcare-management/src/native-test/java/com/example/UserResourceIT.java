package com.example;

import com.example.resource.UserResourceTest;
import io.quarkus.test.junit.QuarkusIntegrationTest;

@QuarkusIntegrationTest
class UserResourceIT extends UserResourceTest {
    // Execute the same tests but in packaged mode.
}
