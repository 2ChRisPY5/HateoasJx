package com.github.chrispy.hateoasjx.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link HateoasJxResponse}
 */
public class HateoasJxResponseTest
{
	/**
	 * Test the simple response.
	 */
	@Test
	public void testSimpleResponse()
	{
		final var entity = new Object();
		final var response = HateoasJxResponse.simple(List.of(), entity);

		assertEquals(List.of(), response.getLinks());
		assertEquals(entity, response.getEntity());
	}

	/**
	 * Test building response from Java Http Response.
	 */
	@Test
	public void testHttpResponse()
	{
		final var entity = new Object();
		final var httpResp = mock(HttpResponse.class);
		final var headers = mock(HttpHeaders.class);

		when(httpResp.headers()).thenReturn(headers);
		when(httpResp.body()).thenReturn(entity);
		when(headers.allValues("link")).thenReturn(List.of("some link"));

		final var response = HateoasJxResponse.ofHttpApi(httpResp);
		assertThat(response.getLinks(), hasItem("some link"));
		assertEquals(response.getEntity(), entity);
	}
}
