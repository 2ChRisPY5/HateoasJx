package com.github.chrispy.hateoasjx.client.model;

import java.util.List;

import com.github.chrispy.hateoasjx.client.proxy.HateoasJxInitializer;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxProxy;
import com.github.chrispy.hateoasjx.client.type.GenericType;

public class Parent implements HateoasJxProxy
{
	private final int id;
	private List<Child> children;

	private transient HateoasJxInitializer $$_hjx_initializer;

	public Parent(final int id)
	{
		this.id = id;
	}

	public List<Child> getChildren()
	{
		this.children = $$_hjx_init("children", GenericType.listOf(Child.class), this.children);
		return this.children;
	}

	public void setChildren(final List<Child> children)
	{
		this.children = children;
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
