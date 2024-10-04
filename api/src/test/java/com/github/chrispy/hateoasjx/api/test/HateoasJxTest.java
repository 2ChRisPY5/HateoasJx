package com.github.chrispy.hateoasjx.api.test;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import com.github.chrispy.hateoasjx.api.HateoasJx;

/**
 * Tests for {@link HateoasJx}.
 */
public class HateoasJxTest
{
	/**
	 * Test link generation for a single entity
	 */
	@Test
	public void singleEntity()
	{
		final var entity = new Incident(1);
		final var links = HateoasJx.getInstance(entity)
			.orElseThrow()
			.generateLinks()
			.toList();

		MatcherAssert.assertThat(links, Matchers.containsInAnyOrder("</incidents/1>; rel=\"self\"",
			"</incidents/1/tasks>; rel=\"related\"; anchor=\"#tasks\"",
			"</users?incident=1>; rel=\"related\"; anchor=\"#creator\""));
	}

	/**
	 * Test link generation for array of entities.
	 */
	@Test
	public void arrayEntities()
	{
		multipleEntities(new Incident[] {
			new Incident(1),
			new Incident(2)
		});
	}

	/**
	 * Test link generation for collection of entities.
	 */
	@Test
	public void collectionEntities()
	{
		multipleEntities(List.of(new Incident(1), new Incident(2)));
	}

	/**
	 * Perform the assertions
	 * 
	 * @param entities the entities to process
	 */
	private static final void multipleEntities(final Object entities)
	{
		final var links = HateoasJx.getInstance(entities)
			.orElseThrow()
			.generateLinks()
			.toList();
		MatcherAssert.assertThat(links, Matchers.containsInAnyOrder(
			"</incidents/1>; rel=\"item\"; anchor=\"#1\"",
			"</incidents/1/tasks>; rel=\"related\"; anchor=\"/incidents/1#tasks\"",
			"</users?incident=1>; rel=\"related\"; anchor=\"/incidents/1#creator\"",
			"</incidents/2>; rel=\"item\"; anchor=\"#2\"",
			"</incidents/2/tasks>; rel=\"related\"; anchor=\"/incidents/2#tasks\"",
			"</users?incident=2>; rel=\"related\"; anchor=\"/incidents/2#creator\""));
	}
}
