package ru.progrm_jarvis.mcunit.io.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientArgumentMatchersTest {

    @Test
    void testHttpUriRequestMatcher() {
        assertTrue(HttpClientArgumentMatchers.httpUriRequestMatcher(new HttpGet("https://github.com/"))
                .matches(new HttpGet("https://github.com/")));

        assertTrue(HttpClientArgumentMatchers.httpUriRequestMatcher(new HttpPost("https://github.com/"))
                .matches(new HttpPost("https://github.com/")));

        assertFalse(HttpClientArgumentMatchers.httpUriRequestMatcher(new HttpGet("https://github.com/"))
                .matches(new HttpPost("https://github.com/")));

        assertFalse(HttpClientArgumentMatchers.httpUriRequestMatcher(new HttpGet("https://example.com/"))
                .matches(new HttpGet("https://github.com/")));

        assertFalse(HttpClientArgumentMatchers.httpUriRequestMatcher(new HttpPost("https://example.com/"))
                .matches(new HttpPost("https://github.com/")));

        assertTrue(HttpClientArgumentMatchers.httpUriRequestMatcher(null).matches(null));

        assertFalse(HttpClientArgumentMatchers.httpUriRequestMatcher(new HttpPost("https://example.com/"))
                .matches(null));
    }
}