package com.github.chrispy.hateoasjx.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.chrispy.hateoasjx.client.internal.ClassEntityType;
import com.github.chrispy.hateoasjx.client.model.Child;
import com.github.chrispy.hateoasjx.client.model.Parent;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxInitializer;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxProxy;
import com.github.chrispy.hateoasjx.client.type.EntityType;
import com.github.chrispy.hateoasjx.client.type.GenericType;

/**
 * Test suite for {@link HateoasJxClient}.
 */
@ExtendWith(MockitoExtension.class)
public class HateoasJxClientTest
{
	private static final String DUMMY_URL = "http://dummy";

	@Mock
	private RequestExecutor executor;

	private HateoasJxClient client;

	/**
	 * Create the mocked client.
	 */
	@BeforeEach
	void init()
	{
		this.client = new HateoasJxClient(this.executor);
	}

	/**
	 * An URL must be absolute; it has to fail if not
	 */
	@Test
	void invalidUrl()
	{
		final var ex = assertThrows(IllegalArgumentException.class, () -> this.client.get(Parent.class, "/invalid"));
		assertEquals("URL must be absolute but /invalid was passed", ex.getMessage());
	}

	/**
	 * Just a functional test that null entity does not throw an exception.
	 */
	@Test
	void nullEntity()
	{
		final var response = mock(HateoasJxResponse.class);
		when(response.getEntity()).thenReturn(null);
		when(this.executor.request(new ClassEntityType<>(Parent.class), DUMMY_URL)).thenReturn(response);

		assertThat(this.client.get(Parent.class, DUMMY_URL), nullValue());
		verify(response, never()).getLinks();
	}

	/**
	 * Verify that executor was correctly called and initialier was set.
	 */
	@Test
	void requestSingle()
	{
		// mock response
		final var entityType = new ClassEntityType<>(Parent.class);
		prepareResponse(spy(new Parent(1)), List.of(), entityType, DUMMY_URL);

		// do call
		final var parent = this.client.get(Parent.class, DUMMY_URL);

		// verify
		verifyInitializer(parent);
		verify(this.executor).request(entityType, DUMMY_URL);
	}

	/**
	 * Test if a chained call is correctly delegated to the executor. Also tests correct assignment of the
	 * links.
	 */
	@Test
	void resolveMultipleFromSingle()
	{
		// mock response
		// parent call
		final var parentType = new ClassEntityType<>(Parent.class);
		prepareResponse(spy(new Parent(1)), List.of("<http://dummy/children>; rel=\"related\"; anchor=\"#children\""),
			parentType, DUMMY_URL);

		// children call
		final var childrenUrl = DUMMY_URL + "/children";
		final var childrenType = GenericType.listOf(Child.class);
		prepareResponse(List.of(spy(new Child(1)), spy(new Child(2))), List.of(), childrenType, childrenUrl);

		// do call
		final var parent = this.client.get(Parent.class, DUMMY_URL);
		final var children = parent.getChildren();

		// verify
		verify(this.executor).request(parentType, DUMMY_URL);
		verify(this.executor).request(childrenType, childrenUrl);
		verifyInitializer(Stream.concat(Stream.of(parent), children.stream()).toArray(HateoasJxProxy[]::new));
	}

	/**
	 * Test if a chained call is correctly delegated to the executor. Also tests correct assignment of the
	 * links.
	 */
	@Test
	void resolveSingleFromSingle()
	{
		// mock first call
		final var childType = new ClassEntityType<>(Child.class);
		prepareResponse(spy(new Child(1)), List.of("<http://dummy/parent>; rel=\"related\"; anchor=\"#parent\""),
			childType, DUMMY_URL);

		// mock second call
		final var parentType = new ClassEntityType<>(Parent.class);
		prepareResponse(spy(new Parent(1)), List.of(), parentType, DUMMY_URL + "/parent");

		// do calls
		final var child = this.client.get(Child.class, DUMMY_URL);
		final var parent = child.getParent();

		verify(this.executor).request(childType, DUMMY_URL);
		verify(this.executor).request(parentType, DUMMY_URL + "/parent");
		verifyInitializer(child, parent);
	}

	/**
	 * Verify that executor was correctly called and initialier was set.
	 */
	@Test
	void requestMultiple()
	{
		final var reqType = GenericType.listOf(Parent.class);

		// mock response
		prepareResponse(List.of(spy(new Parent(1)), spy(new Parent(2))), List.of(), reqType, DUMMY_URL);

		// do call
		final var parents = this.client.get(reqType, DUMMY_URL);

		// verify
		verify(this.executor).request(reqType, DUMMY_URL);
		verifyInitializer(parents.toArray(HateoasJxProxy[]::new));
	}

	/**
	 * Test if a chained call is correctly delegated to the executor. Also tests correct assignment of the
	 * links.
	 */
	@Test
	void resolveMultipleFromMultiple()
	{
		// mock parent call
		final var parentType = GenericType.listOf(Parent.class);
		prepareResponse(List.of(spy(new Parent(1)), spy(new Parent(2))),
			List.of("<http://dummy/1>; rel=\"item\"; anchor=\"#1\"",
				"<http://dummy/1/children>; rel=\"related\"; anchor=\"http://dummy/1#children\"",
				"<http://dummy/2>; rel=\"item\"; anchor=\"#2\"",
				"<http://dummy/2/children>; rel=\"related\"; anchor=\"http://dummy/2#children\""),
			parentType, DUMMY_URL);

		// mock children calls
		final var childrenType = GenericType.listOf(Child.class);
		prepareResponse(List.of(spy(new Child(1))), List.of(), childrenType, DUMMY_URL + "/1/children");
		prepareResponse(List.of(spy(new Child(2))), List.of(), childrenType, DUMMY_URL + "/2/children");

		// do calls
		final var results = this.client.get(parentType, DUMMY_URL)
			.stream()
			.flatMap(p -> Stream.concat(Stream.of(p), p.getChildren().stream()))
			.toArray(HateoasJxProxy[]::new);

		verifyInitializer(results);
		verify(this.executor).request(parentType, DUMMY_URL);
		verify(this.executor).request(childrenType, DUMMY_URL + "/1/children");
		verify(this.executor).request(childrenType, DUMMY_URL + "/2/children");
	}

	/**
	 * Test if a chained call is correctly delegated to the executor. Also tests correct assignment of the
	 * links.
	 */
	@Test
	void resolveSingleFromMultiple()
	{
		// mock children
		final var childrenType = GenericType.listOf(Child.class);
		prepareResponse(List.of(spy(new Child(1)), spy(new Child(2))),
			List.of("<http://dummy/1>; rel=\"item\"; anchor=\"#1\"",
				"<http://dummy/1/parent>; rel=\"related\"; anchor=\"http://dummy/1#parent\"",
				"<http://dummy/2>; rel=\"item\"; anchor=\"#2\"",
				"<http://dummy/2/parent>; rel=\"related\"; anchor=\"http://dummy/2#parent\""),
			childrenType, DUMMY_URL);

		// mock parent call
		final var parentType = new ClassEntityType<>(Parent.class);
		prepareResponse(spy(new Parent(1)), List.of(), parentType, DUMMY_URL + "/1/parent");
		prepareResponse(spy(new Parent(2)), List.of(), parentType, DUMMY_URL + "/2/parent");

		// do the calls
		final var results = this.client.get(childrenType, DUMMY_URL)
			.stream()
			.flatMap(c -> Stream.of(c, c.getParent()))
			.toArray(HateoasJxProxy[]::new);

		verifyInitializer(results);
		verify(this.executor).request(childrenType, DUMMY_URL);
		verify(this.executor).request(parentType, DUMMY_URL + "/1/parent");
		verify(this.executor).request(parentType, DUMMY_URL + "/2/parent");
	}

	/**
	 * Test if getter return type is correctly traversed.
	 */
	@Test
	void traverseEntityType()
	{
		final var child = new Child(1);
		final var initializer = new HateoasJxInitializer(this.client, Map.of("traversed", DUMMY_URL));
		child.$$_hjx_initializer(initializer);

		// request executor is not mocked and will throw an exception
		// we dont care in this test because only traverse is necessary
		assertThrowsExactly(NullPointerException.class, child::getTraversed);

		final var captor = ArgumentCaptor.forClass(EntityType.class);
		verify(this.executor).request(captor.capture(), eq(DUMMY_URL));

		// assert the generic type
		final var entityType = captor.getValue();
		assertThat(entityType, instanceOf(GenericType.class));

		final var generic = (GenericType<?>) entityType;
		assertThrowsExactly(UnsupportedOperationException.class, generic::getEntityType);
		assertTrue(generic.isParameterized());
		assertTrue(generic.hasMultiple());
		assertFalse(generic.isArray());
		assertEquals(Collection.class, generic.getRawClass());
		assertEquals(String.class, generic.getActualTypeArguments()[0]);
	}

	/**
	 * Prepare a complete response object.
	 */
	private void prepareResponse(final Object entity, final List<String> links,
		final EntityType reqType, final String reqUrl)
	{
		final var response = mock(HateoasJxResponse.class);
		when(response.getEntity()).thenReturn(entity);
		when(response.getLinks()).thenReturn(links);
		when(this.executor.request(reqType, reqUrl)).thenReturn(response);
	}

	/**
	 * Verify that all proxies got initialized correctly.
	 */
	private void verifyInitializer(final HateoasJxProxy... proxies)
	{
		Stream.of(proxies).forEach(p -> verify(p).$$_hjx_initializer(any(HateoasJxInitializer.class)));
	}
}
