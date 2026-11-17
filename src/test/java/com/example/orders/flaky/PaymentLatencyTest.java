package com.example.orders.flaky;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.orders.PaymentClient;
import com.github.tomakehurst.wiremock.http.UniformDistribution;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class PaymentLatencyTest {
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    /** F11 (network, ~10%): stub latency uniform 100-400 ms against the client's 500 ms timeout. */
    @Test
    @Tag("F11")
    @DisplayName("F11 network: random stub latency vs 500ms client timeout")
    void f11_authorizeSucceedsDespiteRandomLatency() {
        wm.stubFor(post(urlPathEqualTo("/payments/f11"))
                .willReturn(aResponse().withStatus(200).withRandomDelay(new UniformDistribution(100, 400))));
        PaymentClient client = new PaymentClient("http://localhost:" + wm.getPort(), Duration.ofMillis(500));
        assertTrue(client.authorize("f11", 100));
    }
}
