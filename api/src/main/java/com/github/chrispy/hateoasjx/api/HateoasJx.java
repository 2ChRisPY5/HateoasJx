package com.github.chrispy.hateoasjx.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.chrispy.hateoasjx.api.config.SelfConfig;
import com.github.chrispy.hateoasjx.api.internal.LinkBuilder;
import com.github.chrispy.hateoasjx.common.log.Log;

/**
 * This is your main entry point for generating links. You can pass any value you want but you will only get
 * an instance if the type is supported. Supported are:
 * <ul>
 * <li>Any object annotated with {@code Linkable}</li>
 * <li>{@code Collection} type which actual type parameter is annotated with {@code Linkable}</li>
 * <li>{@code Array} which component type is annotated with {@code Linkable}</li>
 * </ul>
 * The links generated are never absolut URLs. It is your responsibility to add the correct protocol, host,
 * port ect. Also all links always have a leading / but no trailing one.
 * <br>
 * <br>
 * The class cannot be reused!
 */
public final class HateoasJx
{
	private final GenerationStrategy strategy;
	private final Object entity;
	private HateoasJxResolvable resolvable;

	/**
	 * Constructor
	 */
	private HateoasJx(final GenerationStrategy strategy, final Object entity)
	{
		this.strategy = strategy;
		this.entity = entity;
	}

	/**
	 * Try to obtain an instance for given entity. If entity does not match any supported type the result will
	 * be empty and a message will be logged.
	 * 
	 * @param entity the entity object
	 * @return an optional instance
	 */
	public static Optional<HateoasJx> getInstance(final Object entity)
	{
		final var optType = ensureType(entity);
		if(optType.isEmpty())
		{
			Log.warn("Skipping link generation because no type implementing HateoasJxResolvable for {0} could be found", entity);
			return Optional.empty();
		}

		return Optional.of(new HateoasJx(optType.get(), entity));
	}

	/**
	 * Starts processing the entity.
	 * 
	 * @see HateoasJx Link explanation
	 */
	public Stream<String> generateLinks()
	{
		return this.strategy.processor.apply(this, this.entity);
	}

	/**
	 * Process the links for a single entity.
	 * 
	 * @return list of {@link LinkBuilder}s
	 */
	private Stream<String> process(final Function<SelfConfig, LinkBuilder> start)
	{
		final var self = this.resolvable.$$_hjx_self();

		// create selfLink
		final var selfLink = start.apply(self);

		// create subordinates
		final var related = this.resolvable.$$_hjx_relations()
			.stream()
			.map(rel -> LinkBuilder.related(rel, selfLink));

		// build all links
		final var substitutions = getEncodedValues();
		return Stream.concat(Stream.of(selfLink), related)
			.map(lb -> lb.build(substitutions));
	}

	/**
	 * Get the substituation values as URL encoded strings
	 * 
	 * @return encoded substitution values
	 */
	private Map<String, String> getEncodedValues()
	{
		return this.resolvable.$$_hjx_substitutions()
			.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey,
				e -> URLEncoder.encode(Objects.toString(e.getValue()), StandardCharsets.UTF_8)));
	}

	/**
	 * Ensure that given entity can be processed by HateoasJx. This returns the correct
	 * {@code GenerationStrategy} if one of the following applies:
	 * <ul>
	 * <li>Entity itself is an instance of {@code HateoasJxResolvable}</li>
	 * <li>Entity is an array and its' component type is an instance of {@code HateoasJxResolvable}</li>
	 * <li>Entity is a collection, contains at least one item and the actual type is an instance of
	 * {@code HateoasJxResolvable}</li>
	 * </ul>
	 * 
	 * @param entity the entity to generate for
	 * @return an optional {@code GenerationStrategy}
	 */
	private static Optional<GenerationStrategy> ensureType(final Object entity)
	{
		if(Objects.isNull(entity))
			return Optional.empty();

		// check interface directly
		if(entity instanceof HateoasJxResolvable)
			return Optional.of(GenerationStrategy.SINGLE);

		// check collection
		if(entity instanceof final Collection ec && !ec.isEmpty() && ec.iterator().next() instanceof HateoasJxResolvable)
			return Optional.of(GenerationStrategy.COLLECTION);

		// check array
		if(entity instanceof HateoasJxResolvable[])
			return Optional.of(GenerationStrategy.ARRAY);

		return Optional.empty();
	}

	/**
	 * Custom type for setting the processing strategy.
	 */
	private static enum GenerationStrategy
	{
		SINGLE((hjx, e) -> {
			hjx.resolvable = (HateoasJxResolvable) e;
			return hjx.process(LinkBuilder::self);
		}),

		ARRAY((hjx, e) -> {
			final var stream = Arrays.stream(HateoasJxResolvable[].class.cast(e));
			return withIteration(hjx, stream);
		}),

		@SuppressWarnings("unchecked")
		COLLECTION((hjx, e) -> {
			final var stream = ((Collection<HateoasJxResolvable>) e).stream();
			return withIteration(hjx, stream);
		});

		private final BiFunction<HateoasJx, Object, Stream<String>> processor;

		/**
		 * Constructor
		 */
		private GenerationStrategy(final BiFunction<HateoasJx, Object, Stream<String>> processor)
		{
			this.processor = processor;
		}

		/**
		 * Helper function for processing multiple entities.
		 */
		private static Stream<String> withIteration(final HateoasJx hjx, final Stream<HateoasJxResolvable> entities)
		{
			return entities.flatMap(e -> {
				hjx.resolvable = e;
				return hjx.process(LinkBuilder::item);
			});
		}
	}
}
