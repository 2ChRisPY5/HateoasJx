package com.github.chrispy.hateoasjx.api.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.chrispy.hateoasjx.api.HateoasJxResolvable;
import com.github.chrispy.hateoasjx.api.config.RelatedConfig;
import com.github.chrispy.hateoasjx.api.config.SelfConfig;

/**
 * Incident class
 */
public class Incident implements HateoasJxResolvable
{
	private final int number;

	/**
	 * Constructor
	 */
	public Incident(final int number)
	{
		this.number = number;
	}

	/**
	 * @return the number
	 */
	public int getNumber()
	{
		return this.number;
	}

	@Override
	public SelfConfig $$_hjx_self()
	{
		return new SelfConfig("/incidents/@number", "number");
	}

	@Override
	public Collection<RelatedConfig> $$_hjx_relations()
	{
		return List.of(new RelatedConfig("tasks", "tasks", true),
			new RelatedConfig("creator", "/users?incident=@number", false));
	}

	@Override
	public Map<String, Object> $$_hjx_substitutions()
	{
		return Map.of("number", this.number);
	}
}
