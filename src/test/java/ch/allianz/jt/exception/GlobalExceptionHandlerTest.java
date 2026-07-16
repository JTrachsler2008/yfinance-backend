package ch.allianz.jt.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private WebRequest fakeRequest(String uri) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI(uri);
        return new ServletWebRequest(req);
    }

    @Test
    void handleNotFound_sollStatus404UndNachrichtZurueckgeben() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new ResourceNotFoundException("Portfolio not found: 99"), fakeRequest("/portfolios/99"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Portfolio not found: 99", response.getBody().get("message"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void handleInsufficientFunds_sollStatus400UndNachrichtZurueckgeben() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleInsufficientFunds(new InsufficientFundsException("Nicht genug Cash"), fakeRequest("/transactions/1"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Nicht genug Cash", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void handleRuntime_sollStatus500UndNachrichtZurueckgeben() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleRuntime(new RuntimeException("Unerwarteter Fehler"), fakeRequest("/portfolios/1"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unerwarteter Fehler", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    void alleResponses_sollenTimestampEnthalten() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new ResourceNotFoundException("x"), fakeRequest("/x"));

        assertNotNull(response.getBody().get("timestamp"));
    }
}
