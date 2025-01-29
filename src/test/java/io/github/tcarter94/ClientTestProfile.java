package io.github.tcarter94;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class ClientTestProfile implements QuarkusTestProfile {

    public static final String PORT = "8081";

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("konflux-build-driver.url", "http://localhost:" + PORT, "quarkus.mockserver.devservices.port", PORT);
    }
}