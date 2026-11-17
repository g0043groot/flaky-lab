package com.example.orders;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Calls the external payment HTTP service. */
@Component
public class PaymentClient {
    public static class PaymentException extends RuntimeException {
        public PaymentException(String msg, Throwable cause) { super(msg, cause); }
    }

    private final HttpClient http = HttpClient.newHttpClient();
    private final String baseUrl;
    private final Duration timeout;

    @Autowired
    public PaymentClient(@Value("${payment.base-url:http://localhost:9999}") String baseUrl) {
        this(baseUrl, Duration.ofMillis(500));
    }

    public PaymentClient(String baseUrl, Duration timeout) {
        this.baseUrl = baseUrl;
        this.timeout = timeout;
    }

    /** @return true when the payment service approves (HTTP 200), false when it declines (HTTP 402). */
    public boolean authorize(String orderId, long amountCents) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/payments/" + orderId + "?amount=" + amountCents))
                .timeout(timeout)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) return true;
            if (resp.statusCode() == 402) return false;
            throw new PaymentException("unexpected status " + resp.statusCode(), null);
        } catch (HttpTimeoutException e) {
            throw new PaymentException("payment service timed out", e);
        } catch (IOException e) {
            throw new PaymentException("payment service unreachable", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentException("interrupted", e);
        }
    }
}
