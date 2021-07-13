package ru.progrm_jarvis.mcunit.io.http;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.Answers;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Useful mocks for Apache HttpClient.
 */
@UtilityClass
public class HttpClientMocks {

    /**
     * Creates a mock of {@link HttpClient} allowing easy behaviour mocking for request responses.
     *
     * @return mocked HTTP-client with support of response-mocking
     */
    public MockedHttpClient mockHttpClient() {
        return mock(MockedHttpClient.class, Answers.CALLS_REAL_METHODS);
    }

    /**
     * An HTTP-client mock base which allows easy addition of request-response behaviour mocks.
     */
    public interface MockedHttpClient extends HttpClient {

        /**
         * Makes this HTTP-client return specified response on specified request.
         *
         * @param request request on which to return the response specified
         * @param response response to return on request specified
         * @return this HTTP-client mock
         */
        @SneakyThrows
        default MockedHttpClient responding(final HttpUriRequest request, final HttpResponse response) {
            when(execute(HttpClientArgumentMatchers.eqHttpUriRequest(request))).thenReturn(response);

            return this;
        }

        /**
         * Makes this HTTP-client return a mocked response
         * whose {@link HttpResponse#getEntity()} method will return the one specified.
         *
         * @param request request on which to return the response mock with the specified entity
         * @param responseEntity response entity to be used by response-mock returned on request specified
         * @return this HTTP-client mock
         */
        @SneakyThrows
        default MockedHttpClient responding(final HttpUriRequest request, final HttpEntity responseEntity) {
            val response = mock(HttpResponse.class);
            when(response.getEntity()).thenReturn(responseEntity);

            return responding(request, response);
        }

        /**
         * Makes this HTTP-client return a mocked response
         * whose {@link HttpResponse#getEntity()} method will return the mock
         * whose {@link HttpEntity#getContent()} returns the one specified.
         *
         * @param request request on which to return the response mock with the specified entity
         * @param responseContent response content of the entity-mock
         * used by response-mock returned on request specified
         * @return this HTTP-client mock
         *
         * @implNote does not influence any methods of {@link HttpEntity} except for {@link HttpEntity#getContent()}
         */
        @SneakyThrows
        default MockedHttpClient responding(final HttpUriRequest request, final InputStream responseContent) {
            val responseEntity = mock(HttpEntity.class);
            when(responseEntity.getContent()).thenReturn(responseContent);

            return responding(request, responseEntity);
        }

        /**
         * Makes this HTTP-client return a mocked response
         * whose {@link HttpResponse#getEntity()} method will return the mock
         * whose {@link HttpEntity#getContent()} returns the one based on the one specified.
         *
         * @param request request on which to return the response mock with the specified entity
         * @param responseContent response content to be converted to {@link InputStream} of the entity-mock
         * used by response-mock returned on request specified
         * @param charset charset of the response content
         * @return this HTTP-client mock
         *
         * @implNote does not influence any methods of {@link HttpEntity} except for {@link HttpEntity#getContent()}
         * @implNote uses {@link IOUtils#toInputStream(CharSequence, Charset)}
         * to convert the {@link String} to {@link InputStream}
         */
        default MockedHttpClient responding(final HttpUriRequest request, final @NonNull String responseContent,
                                            final Charset charset) {
            return responding(request, IOUtils.toInputStream(responseContent, charset));
        }


        /**
         * Makes this HTTP-client return a mocked response
         * whose {@link HttpResponse#getEntity()} method will return the mock
         * whose {@link HttpEntity#getContent()} returns the one based on the one specified (treated as UTF-8 encoded).
         *
         * @param request request on which to return the response mock with the specified entity
         * @param responseContent response content to be converted to {@link InputStream}
         * of the entity-mock treated as UTF-8 encoded
         * used by response-mock returned on request specified
         * @return this HTTP-client mock
         *
         * @implNote does not influence any methods of {@link HttpEntity} except for {@link HttpEntity#getContent()}
         * @implNote uses {@link IOUtils#toInputStream(CharSequence, Charset)}
         * to convert the {@link String} to {@link InputStream}
         */
        default MockedHttpClient responding(final HttpUriRequest request, final @NonNull String responseContent) {
            return responding(request, responseContent, StandardCharsets.UTF_8);
        }
    }
}
