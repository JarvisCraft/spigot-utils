package ru.progrm_jarvis.mcunit.io.http;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.apache.http.client.methods.HttpUriRequest;
import org.jetbrains.annotations.Nullable;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;

import static org.mockito.ArgumentMatchers.argThat;

/**
 * Matchers for HTTP-client-related stuff.
 * This is made mostly because commonly used classes of HTTP-client library
 * don't override {@link Object#equals(Object)} and {@link Object#hashCode()}
 */
@UtilityClass
public class HttpClientArgumentMatchers {

    /**
     * Creates a matcher for request specified.
     *
     * @param request request for which to create the matcher
     * @return matcher for the request
     *
     * @implNote for {@code null} request {@link ArgumentMatchers#isNull()} is used.
     */
    @NonNull public ArgumentMatcher<HttpUriRequest> httpUriRequestMatcher(final @Nullable HttpUriRequest request) {
        return new HttpUriRequestMatcher(request);
    }

    /**
     * {@link HttpUriRequest} that is equal to the given value.
     *
     * @param value the given value
     * @return {@code null}
     */
    // naming and JavaDocs conventions taken from ArgumentMatchers
    public HttpUriRequest eqHttpUriRequest(final @Nullable HttpUriRequest value) {
        return argThat(httpUriRequestMatcher(value));
    }

    @Value
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class HttpUriRequestMatcher implements ArgumentMatcher<HttpUriRequest> {

        @Nullable HttpUriRequest request;

        @Override
        public boolean matches(final @Nullable HttpUriRequest argument) {
            if (request == argument) return true; // if one non-null (as both not same) => match
            if (request == null || argument == null) return false;

            // equality comparison
            return request.isAborted() == argument.isAborted() // similar abort-status
                    && request.getMethod().equals(argument.getMethod()) // similar method type
                    && request.getURI().equals(argument.getURI()); // similar URI
        }
    }
}
