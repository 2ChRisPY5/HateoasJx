package com.github.chrispy.hateoasjx.maven.plugin.util;

import java.util.Set;

import javassist.CtClass;

/**
 * Used for evaluating simple expressions to compilable Java calls.
 */
public final class ExpressionTranslator
{
	private static final Set<String> BOOLEANS = Set.of(boolean.class.getName(), Boolean.class.getName());

	/**
	 * Not a constructor
	 */
	private ExpressionTranslator() throws IllegalAccessException
	{
		throw new IllegalAccessException();
	}

	/**
	 * Converts a expression like {@code nested.deeply.id} to {@code this.nested.getDeeply().getId()}.
	 */
	public static GetterChain toGetterChain(final CtClass clazz, final String expression)
	{
		final var parts = expression.split("\\.");
		final var builder = new StringBuilder();

		// add first part always
		builder.append("$0.").append(parts[0]);

		// abort if no other part available
		if(parts.length < 2)
			return new GetterChain(expression, builder.toString(),
				JavassistUtil.getFieldType(JavassistUtil.getField(clazz, expression)));

		// append others
		var currentClass = JavassistUtil.getFieldType(JavassistUtil.getField(clazz, parts[0]));
		for(var idx = 1; idx < parts.length; idx++)
		{
			// append is or get
			final var fieldName = parts[idx];
			final var fieldType = JavassistUtil.getFieldType(JavassistUtil.getField(currentClass, fieldName));
			if(BOOLEANS.contains(fieldType.getName()))
			{
				builder.append(".is");
			}
			else
			{
				builder.append(".get");
			}

			// append name
			builder.append(Character.toUpperCase(fieldName.charAt(0))).append(fieldName.substring(1));
			builder.append("()");

			// set current position
			currentClass = fieldType;
		}

		return new GetterChain(expression, builder.toString(), currentClass);
	}

	public record GetterChain(String expression, String statement, CtClass resultType)
	{
	}
}
