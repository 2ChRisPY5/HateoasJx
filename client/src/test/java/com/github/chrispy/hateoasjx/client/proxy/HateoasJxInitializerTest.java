package com.github.chrispy.hateoasjx.client.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chrispy.hateoasjx.client.HateoasJxClient;
import com.github.chrispy.hateoasjx.client.internal.ClassEntityType;
import com.github.chrispy.hateoasjx.client.model.Parent;
import com.github.chrispy.hateoasjx.client.type.EntityType;
import com.github.chrispy.hateoasjx.client.type.GenericType;

/**
 * Unit tests for {@link HateoasJxInitializer}
 */
@ExtendWith(MockitoExtension.class)
public class HateoasJxInitializerTest
{
	private static final String USERS = "users";

	@Mock
	private HateoasJxClient client;
	@Mock
	private Map<String, String> links;
	@Spy
	@InjectMocks
	private HateoasJxInitializer initializer;

	/**
	 * If no link exists for an anchor; then it's always empty result and no interactions.
	 */
	@Test
	public void noLinkExists()
	{
		when(this.links.containsKey(USERS)).thenReturn(false);

		final var result = this.initializer.resolve(USERS, null);

		assertNull(result);
		verify(this.initializer).markResolved(USERS);
		verifyNoInteraction();
	}

	/**
	 * If anchor is already resolved; then it's always empty result and no interactions.
	 */
	@Test
	public void alreadyResolved()
	{
		final var entityType = new ClassEntityType<>(Parent.class);
		when(this.links.containsKey(USERS)).thenReturn(true);

		this.initializer.resolve(USERS, entityType);
		final var result = this.initializer.resolve(USERS, entityType);

		assertNull(result);
		final var inOrder = inOrder(this.initializer, this.client);
		inOrder.verify(this.initializer).markResolved(USERS);
		inOrder.verify(this.client).get(entityType, null);
		inOrder.verify(this.initializer).markResolved(USERS);
		inOrder.verify(this.client, never()).get(entityType, null);
	}

	/**
	 * Check that client is properly called if all preconditions have been met.
	 */
	@Test
	public void doClientAction()
	{
		final var link = "https://localhost/users";
		final EntityType type = GenericType.listOf(Object.class);
		final var mockResult = List.of();

		when(this.links.containsKey(USERS)).thenReturn(true);
		when(this.links.get(USERS)).thenReturn(link);
		when(this.initializer.markResolved(USERS)).thenReturn(true);
		when(this.client.get(type, link)).thenReturn(mockResult);

		final var result = this.initializer.resolve(USERS, type);

		assertEquals(mockResult, result);
		verify(this.client).get(type, link);
	}

	/**
	 * Verify that no client interaction was issued.
	 */
	private void verifyNoInteraction()
	{
		verify(this.links, never()).get(any());
		verifyNoInteractions(this.client);
	}
}
