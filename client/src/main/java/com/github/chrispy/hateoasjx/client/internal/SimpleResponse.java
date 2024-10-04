package com.github.chrispy.hateoasjx.client.internal;

import java.util.Collection;

import com.github.chrispy.hateoasjx.client.HateoasJxResponse;

/**
 * A simple {@link HateoasJxResponse} implementation
 */
public record SimpleResponse(Collection<String> links, Object entity) implements HateoasJxResponse
{
	/**
	 * {@inherited}
	 */
	@Override
	public Object getEntity()
	{
		return this.entity;
	}

	/**
	 * {@inherited}
	 */
	@Override
	public Collection<String> getLinks()
	{
		return this.links;
	}
}