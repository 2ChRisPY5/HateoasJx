package com.github.chrispy.hateoasjx.api.internal;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.api.config.RelatedConfig;
import com.github.chrispy.hateoasjx.api.config.SelfConfig;

/**
 * Simple link builder
 */
public final class LinkBuilder
{
	private static final Pattern REPLACEMENTS = Pattern.compile("@(\\w+)");

	private Rel rel;
	private String anchor;
	private final StringBuilder url = new StringBuilder();

	/**
	 * Create a new self-link builder for a single entity.
	 * 
	 * @param config the {@link Linkable} configuratinon
	 * @return new builder
	 */
	public static LinkBuilder self(final SelfConfig config)
	{
		final var builder = new LinkBuilder();
		builder.rel = Rel.SELF;

		return builder.appendPath(config.path());
	}

	/**
	 * Create a item-link builder for a entity part of a collection.
	 * 
	 * @param config the {@link Linkable} configuratinon
	 * @return new builder
	 */
	public static LinkBuilder item(final SelfConfig config)
	{
		final var builder = new LinkBuilder();
		builder.rel = Rel.ITEM;
		builder.anchor = "#@" + config.identifiedBy();

		return builder.appendPath(config.path());
	}

	/**
	 * Create a related-link builder.
	 * 
	 * @param config the {@link RelatedConfig}
	 * @param base the base link of the new relation
	 * @return new builder
	 */
	public static LinkBuilder related(final RelatedConfig config, final LinkBuilder base)
	{
		final var builder = new LinkBuilder();
		builder.rel = Rel.RELATED;
		builder.anchor = '#' + config.anchor();

		// only chain URL for anchor if not self link
		if(base.rel != Rel.SELF)
		{
			builder.anchor = base.url.toString() + builder.anchor;
		}

		// append base URL first
		if(config.subordinate())
		{
			builder.appendPath(base.url.toString());
		}

		return builder.appendPath(config.path());
	}

	/**
	 * Appends given string to current path.
	 * 
	 * @param path the path to append
	 * @return same builder
	 */
	private LinkBuilder appendPath(final String path)
	{
		// path always starts with /
		if(!path.startsWith("/"))
		{
			this.url.append('/');
		}

		// append value
		this.url.append(path);

		// remove trailing /
		if(path.endsWith("/"))
		{
			this.url.deleteCharAt(this.url.length() - 1);
		}
		return this;
	}

	/**
	 * Build the link by replacing all placeholders with given substitution values.
	 * 
	 * @param subs the substitution values
	 * @return the built link
	 */
	public String build(final Map<String, String> subs)
	{
		// build link
		final var builder = new StringBuilder()
			.append('<')
			.append(this.url)
			.append('>');

		// add relation
		builder.append("; rel=\"")
			.append(this.rel.name().toLowerCase())
			.append('"');

		// add anchor
		if(Objects.nonNull(this.anchor))
		{
			builder.append("; anchor=\"")
				.append(this.anchor)
				.append('"');
		}

		// substitute all values
		return LinkBuilder.REPLACEMENTS.matcher(builder)
			.replaceAll(mr -> subs.get(mr.group(1)));
	}

	/**
	 * Relationship types
	 */
	private enum Rel
	{
		SELF,
		RELATED,
		ITEM
	}
}
