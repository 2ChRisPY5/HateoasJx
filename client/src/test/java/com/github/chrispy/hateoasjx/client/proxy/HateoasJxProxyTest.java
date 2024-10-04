package com.github.chrispy.hateoasjx.client.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chrispy.hateoasjx.client.type.EntityType;
import com.github.chrispy.hateoasjx.client.type.GenericType;

/**
 * Unit tests for {@link HateoasJxProxy}
 */
@ExtendWith(MockitoExtension.class)
public class HateoasJxProxyTest
{
	@Mock
	private HateoasJxInitializer initializer;
	@Spy
	private HateoasJxProxy proxy;

	/**
	 * No action is performed if initializer was not injected.
	 */
	@Test
	public void notInjected()
	{
		this.proxy.$$_hjx_init(null, (EntityType) null, null);

		verifyNoInteractions(this.initializer);
	}

	/**
	 * Anchor is getting marked as already resolved if "current" is not null.
	 */
	@Test
	public void alreadyInitialized()
	{
		// mock getting initializer
		when(this.proxy.$$_hjx_initializer()).thenReturn(this.initializer);

		// execute
		final var init = List.of();
		final var result = this.proxy.$$_hjx_init("users", (EntityType) null, init);

		assertEquals(init, result);
		verify(this.initializer).markResolved("users");
		verifyNoMoreInteractions(this.initializer);
	}

	/**
	 * Anchor is getting resolved by the initializer.
	 */
	@Test
	public void resolveAndSetterCalled()
	{
		// mock resolve
		final var users = List.of();
		final var type = GenericType.listOf(Object.class);
		when(this.initializer.resolve("users", type)).thenReturn(users);

		// mock getting initializer
		when(this.proxy.$$_hjx_initializer()).thenReturn(this.initializer);

		// execute
		final var result = this.proxy.$$_hjx_init("users", type, null);

		assertEquals(users, result);
		verify(this.initializer).resolve("users", type);
		verifyNoMoreInteractions(this.initializer);
	}
}
