package com.github.chrispy.hateoasjx.maven.plugin.enhancements.server;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.chrispy.hateoasjx.api.Related;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.ProcessInformation;
import com.github.chrispy.hateoasjx.maven.plugin.util.ExpressionTranslator;
import com.github.chrispy.hateoasjx.maven.plugin.util.ExpressionTranslator.GetterChain;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtField;
import javassist.CtNewMethod;
import javassist.Modifier;

/**
 * Enhancement for $$_hjx_substitutions
 */
class SubstitutionValues extends BasicEnhancement
{
	private static final String METHOD_NAME = "$$_hjx_substitutions";
	private static final Pattern REPLACEMENTS = Pattern.compile("@(\\w+(\\.\\w+)*)");

	/**
	 * Constructor
	 */
	SubstitutionValues()
	{
		super(n -> !n.getProcessInformation().isSubsitutionsExists());
	}

	/**
	 * {@inherited}
	 */
	@Override
	public void enhance(final ClassNode node)
	{
		final var processInfo = node.getProcessInformation();
		final var ctClass = node.getCtClass();

		// collect all parameters
		final var mapPairs = buildMapPairs(node);
		if(mapPairs.isEmpty())
			return;

		// generate map creation
		// Map.of() only supports max. 10 key/value pairs; otherwise Map.entry() must be used
		final String createMap;
		if(mapPairs.size() > 10)
		{
			createMap = mapPairs.stream()
				.map(stmt -> "java.util.Map.entry(" + stmt + ')')
				.collect(Collectors.joining(",", "java.util.Map.ofEntries(new java.util.Map.Entry[]{", "})"));
		}
		else
		{
			createMap = "java.util.Map.of(" + String.join(",", mapPairs) + ')';
		}

		// create the body
		final String body;
		if(node.hasAncestorWith(ProcessInformation::isSubsitutionsExists))
		{
			body = """
				{
					final java.util.Map subs = new java.util.HashMap(super.$$_hjx_substitutions());
					subs.putAll(%s);
					return subs;
				}""".formatted(createMap);
		}
		else
		{
			body = "return " + createMap + ';';
		}

		// build the method
		JavassistUtil.compile(() -> {
			final var method = CtNewMethod.make(Modifier.PUBLIC,
				JavassistUtil.load(Map.class),
				METHOD_NAME,
				null,
				null,
				body,
				ctClass);
			method.setGenericSignature("()Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;");
			ctClass.addMethod(method);
		}, () -> "Could not enhance $$_hjx_substitutions");

		processInfo.subsitutionsExists(true);
	}

	/**
	 * Collect all replacement variables.
	 */
	private static List<String> buildMapPairs(final ClassNode node)
	{
		// get from linkable
		final var fromLinkable = ServerChain.getLinkable(node)
			.stream()
			.flatMap(l -> Stream.concat(Stream.of(l.identifiedBy()), getVariables(l.path())))
			.toList();

		// get from related
		final var fromRelated = Stream.of(node.getCtClass().getDeclaredFields())
			.flatMap(f -> ServerChain.getRelated(f)
				.map(Related::path)
				.filter(Predicate.not(String::isBlank))
				.stream()
				.flatMap(SubstitutionValues::getVariables));

		// get the fields and build the statements from them
		final var ctClass = node.getCtClass();
		return Stream.concat(fromLinkable.stream(), fromRelated)
			.distinct()
			.map(ex -> ExpressionTranslator.toGetterChain(ctClass, ex))
			.map(SubstitutionValues::getStatement)
			.toList();
	}

	/**
	 * Add a substitution for the given field.
	 * 
	 * @param body the body
	 * @param field the {@link CtField}
	 */
	private static String getStatement(final GetterChain getter)
	{
		// add key
		final var stmt = new StringBuilder();
		stmt.append('"').append(getter.expression()).append("\",");

		// add value
		if(getter.resultType().isPrimitive())
		{
			stmt.append("java.lang.String.valueOf(")
				.append(getter.statement())
				.append(')');
		}
		else
		{
			stmt.append(getter.statement());
		}

		return stmt.toString();
	}

	/**
	 * Get the variables from the given path.
	 * 
	 * @param path the path
	 * @return stream of field names
	 */
	private static Stream<String> getVariables(final String path)
	{
		return REPLACEMENTS.matcher(path)
			.results()
			.map(mr -> mr.group(1));
	}
}
