package com.github.chrispy.hateoasjx.client;

import java.net.http.HttpResponse;
import java.util.Collection;

import com.github.chrispy.hateoasjx.client.internal.SimpleResponse;

/**
 * Response object used by {@link HateoasJxClient} for resolving Link header values and the entity body.
 */
public interface HateoasJxResponse
{
	/**
	 * Get all Link header values.
	 */
	Collection<String> getLinks();

	/**
	 * Get the entity object.
	 */
	Object getEntity();

	/**
	 * Convenient method to construct a new response from links and entity.
	 * 
	 * @param links the Link header values
	 * @param entity the entity object
	 * @return a new hateoas response
	 */
	static HateoasJxResponse simple(final Collection<String> links, final Object entity)
	{
		return new SimpleResponse(links, entity);
	}

	/**
	 * Convenient method to construct a new response when using the Java HTTP API.
	 * 
	 * @param response the {@link HttpResponse}
	 * @return a new hateoas response
	 */
	static HateoasJxResponse ofHttpApi(final HttpResponse<?> response)
	{
		return new SimpleResponse(response.headers().allValues("link"), response.body());
	}
}
