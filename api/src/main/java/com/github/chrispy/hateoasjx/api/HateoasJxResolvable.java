package com.github.chrispy.hateoasjx.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.chrispy.hateoasjx.api.config.RelatedConfig;
import com.github.chrispy.hateoasjx.api.config.SelfConfig;

/**
 * Enhancement interface providing link configurations and substitution values.
 */
public interface HateoasJxResolvable
{
	final Collection<RelatedConfig> $$_hjx_NO_RELS = List.of();

	/**
	 * Get the configuration for self link generation.
	 * 
	 * @return the {@code SelfConfig}
	 */
	SelfConfig $$_hjx_self();

	/**
	 * Get the configurations for all link relations.
	 * 
	 * @return a collection of {@code RelatedConfig}
	 */
	default Collection<RelatedConfig> $$_hjx_relations()
	{
		return $$_hjx_NO_RELS;
	}

	/**
	 * Get the values used for path substitution.
	 * 
	 * @return map of available values
	 */
	Map<String, Object> $$_hjx_substitutions();
}
