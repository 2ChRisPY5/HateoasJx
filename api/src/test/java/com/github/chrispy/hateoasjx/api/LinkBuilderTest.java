package com.github.chrispy.hateoasjx.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.chrispy.hateoasjx.api.config.RelatedConfig;
import com.github.chrispy.hateoasjx.api.config.SelfConfig;
import com.github.chrispy.hateoasjx.api.internal.LinkBuilder;

/**
 * Tests for {@link LinkBuilder}.
 */
public class LinkBuilderTest
{
	private static final SelfConfig LINKABLE = new SelfConfig("/tests/@id", "id");
	private static final RelatedConfig RELATED_CONFIG = new RelatedConfig("tasks", "/tasks", true);

	/**
	 * Test the self link creation
	 */
	@Test
	public void buildSelf()
	{
		assertEquals("</tests/4567>; rel=\"self\"", LinkBuilder.self(LINKABLE)
			.build(Map.of("id", "4567")));
	}

	/**
	 * Test the item link creation
	 */
	@Test
	public void buildItem()
	{
		assertEquals("</tests/4567>; rel=\"item\"; anchor=\"#4567\"", LinkBuilder.item(LINKABLE)
			.build(Map.of("id", "4567")));
	}

	/**
	 * Test related by item
	 */
	@Test
	public void buildRelatedItem()
	{
		final var link = LinkBuilder.related(RELATED_CONFIG, LinkBuilder.item(LINKABLE))
			.build(Map.of("id", "4567"));

		assertEquals("</tests/4567/tasks>; rel=\"related\"; anchor=\"/tests/4567#tasks\"", link);
	}

	/**
	 * Test related by self
	 */
	@Test
	public void buildRelatedSelf()
	{
		final var link = LinkBuilder.related(RELATED_CONFIG, LinkBuilder.self(LINKABLE))
			.build(Map.of("id", "4567"));

		assertEquals("</tests/4567/tasks>; rel=\"related\"; anchor=\"#tasks\"", link);
	}
}
