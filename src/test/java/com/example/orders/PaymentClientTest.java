package com.example.orders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class PaymentClientTest {
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

    private PaymentClient client() {
        return new PaymentClient("http://localhost:" + wm.getPort(), Duration.ofMillis(300));
    }

    @Test void approvedOn200() {
        wm.stubFor(post(urlPathEqualTo("/payments/ok")).willReturn(aResponse().withStatus(200)));
        assertTrue(client().authorize("ok", 100));
    }
    @Test void declinedOn402() {
        wm.stubFor(post(urlPathEqualTo("/payments/declined")).willReturn(aResponse().withStatus(402)));
        assertFalse(client().authorize("declined", 100));
    }
    @Test void unexpectedStatusThrows() {
        wm.stubFor(post(urlPathEqualTo("/payments/boom")).willReturn(aResponse().withStatus(500)));
        assertThrows(PaymentClient.PaymentException.class, () -> client().authorize("boom", 100));
    }
    @Test void slowServerTimesOutDeterministically() {
        wm.stubFor(post(urlPathEqualTo("/payments/slow")).willReturn(aResponse().withStatus(200).withFixedDelay(1_500)));
        assertThrows(PaymentClient.PaymentException.class, () -> client().authorize("slow", 100));
    }
    @Test void unreachableServerThrows() {
        PaymentClient c = new PaymentClient("http://localhost:1", Duration.ofMillis(300));
        assertThrows(PaymentClient.PaymentException.class, () -> c.authorize("x", 1));
    }
}
