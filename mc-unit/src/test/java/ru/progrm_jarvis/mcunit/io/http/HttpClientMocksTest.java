package ru.progrm_jarvis.mcunit.io.http;

import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class HttpClientMocksTest {

    @Test
    void testMockedHttpClient() throws IOException {
        val someResponse = mock(HttpResponse.class);

        val client = HttpClientMocks.mockHttpClient()
                .responding(new HttpGet("http://example.com/"), "Hello world")
                .responding(new HttpPost("http://example.com/"), "Pochta Rossii != Post")
                .responding(new HttpGet("http://example2.com/"), someResponse);

        assertEquals("Hello world", IOUtils.toString(
                client.execute(new HttpGet("http://example.com/")).getEntity().getContent(), StandardCharsets.UTF_8
        ));

        assertEquals("Pochta Rossii != Post", IOUtils.toString(
                client.execute(new HttpPost("http://example.com/")).getEntity().getContent(), StandardCharsets.UTF_8
        ));

        assertSame(someResponse, client.execute(new HttpGet("http://example2.com/")));

        assertNull(client.execute(new HttpGet("http://not-example.com/")));
    }
}