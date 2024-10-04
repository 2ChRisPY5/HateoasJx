package com.github.chrispy.hateoasjx.client.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.chrispy.hateoasjx.client.internal.ClassEntityType;

/**
 * Unit tests for {@code EntityType} and all its implementations.
 */
public class EntityTypeTest
{
	/**
	 * Test a simple class type.
	 */
	@Test
	public void simpleClassTest()
	{
		final var type = new ClassEntityType<>(String.class);

		assertEquals(String.class, type.getRawClass());
		assertEquals(0, type.getTypeArguments().length);
		assertEquals(String.class, type.getEntityType());
		assertFalse(type.isParameterized());
		assertFalse(type.isArray());
		assertFalse(type.hasMultiple());
		assertEquals(String.class.getTypeName(), type.getTypeName());
	}

	/**
	 * Test an array class type.
	 */
	@Test
	public void arrayClassTest()
	{
		final var type = new ClassEntityType<>(String[].class);

		assertEquals(String[].class, type.getRawClass());
		assertEquals(0, type.getTypeArguments().length);
		assertEquals(String.class, type.getEntityType());
		assertFalse(type.isParameterized());
		assertTrue(type.isArray());
		assertTrue(type.hasMultiple());
		assertEquals(String[].class.getTypeName(), type.getTypeName());
	}

	/**
	 * Creating a {@code GenericType} with a non-parameterized type should throw an exception.
	 */
	@Test
	public void notParameterizedForGenericType()
	{
		assertThrows(IllegalArgumentException.class, () -> new GenericType<String>() {
		});
	}

	/**
	 * A call to {@code getEntityType} must throw an exception.
	 */
	@Test
	public void unknownGenericType()
	{
		final var type = new GenericType<Optional<String>>() {
		};

		assertType(type, Optional.class, "java.util.Optional<java.lang.String>");
		assertThrows(UnsupportedOperationException.class, () -> assertEntityType(type));
	}

	/**
	 * Test the listOf factory method.
	 */
	@Test
	public void listType()
	{
		final var type = GenericType.listOf(String.class);
		assertType(type, List.class, "java.util.List<java.lang.String>");
		assertTrue(type.hasMultiple());
		assertEntityType(type);
	}

	/**
	 * Test the setOf factory method
	 */
	@Test
	public void setType()
	{
		final var type = GenericType.setOf(String.class);
		assertType(type, Set.class, "java.util.Set<java.lang.String>");
		assertTrue(type.hasMultiple());
		assertEntityType(type);
	}

	/**
	 * Test the optionalOf factory method
	 */
	@Test
	public void optionalType()
	{
		final var type = GenericType.optionalOf(String.class);

		assertType(type, Optional.class, "java.util.Optional<java.lang.String>");
		assertFalse(type.hasMultiple());
		assertEntityType(type);
	}

	/**
	 * Assertions for listType(), setType() and optionaType()
	 * 
	 * @param type the {@code GenericType} to assert
	 * @param expectedRawClass the expected raw type
	 * @param expectedTypeName name of the expected type
	 */
	private static void assertType(final GenericType<?> type, final Class<?> expectedRawClass, final String expectedTypeName)
	{
		assertEquals(expectedRawClass, type.getRawClass());
		assertEquals(1, type.getTypeArguments().length);
		assertEquals(String.class, type.getActualTypeArguments()[0]);
		assertTrue(type.isParameterized());
		assertFalse(type.isArray());
		assertEquals(expectedTypeName, type.getTypeName());
	}

	/**
	 * Assert the {@code getEntityType}.
	 * 
	 * @param type to assert
	 */
	private static void assertEntityType(final GenericType<?> type)
	{
		assertEquals(String.class, type.getEntityType());
	}
}
