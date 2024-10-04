package com.github.chrispy.hateoasjx.client.model;

import java.util.Collection;

import com.github.chrispy.hateoasjx.client.proxy.HateoasJxInitializer;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxProxy;
import com.github.chrispy.hateoasjx.client.type.EntityType;

public class Child implements HateoasJxProxy
{
	private final int id;
	private Parent parent;

	private transient HateoasJxInitializer $$_hjx_initializer;

	public Child(final int id)
	{
		this.id = id;
	}

	public Parent getParent()
	{
		this.parent = $$_hjx_init("parent", Parent.class, this.parent);
		return this.parent;
	}

	public void setParent(final Parent parent)
	{
		this.parent = parent;
	}

	public Collection<String> getTraversed()
	{
		return $$_hjx_init("traversed", (EntityType) null, null);
	}

	@Override
	public String $$_hjx_identifier()
	{
		return String.valueOf(this.id);
	}

	@Override
	public void $$_hjx_initializer(final HateoasJxInitializer initializer)
	{
		this.$$_hjx_initializer = initializer;
	}

	@Override
	public HateoasJxInitializer $$_hjx_initializer()
	{
		return this.$$_hjx_initializer;
	}
}
