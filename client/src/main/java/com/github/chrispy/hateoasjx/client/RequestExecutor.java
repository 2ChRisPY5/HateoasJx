package com.github.chrispy.hateoasjx.client;

import com.github.chrispy.hateoasjx.client.type.EntityType;

/**
 * Given the requested type and URL an implementation is responsible for actually performing the HTTP
 * request, deserializing the response body and getting the Link header values.
 * <br/>
 * <br/>
 * Implementers are encouraged to do a thread-safe implementation otherwise {@link HateoasJxClient} won't
 * be fully thread-safe.
 */
@FunctionalInterface
public interface RequestExecutor
{
	/**
	 * Request the resource for given {@link EntityType} and URL.
	 * 
	 * @param type the result {@link EntityType}
	 * @param url the request URL
	 * @return a new {@link HateoasJxResponse}
	 */
	HateoasJxResponse request(final EntityType type, final String url);
}