package com.github.chrispy.hateoasjx.client;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.chrispy.hateoasjx.client.internal.ClassEntityType;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxInitializer;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxProxy;
import com.github.chrispy.hateoasjx.client.type.EntityType;
import com.github.chrispy.hateoasjx.client.type.GenericType;
import com.github.chrispy.hateoasjx.common.HateoasJxException;
import com.github.chrispy.hateoasjx.common.concurrent.SingletonSupplier;
import com.github.chrispy.hateoasjx.common.log.Log;

/**
 * This client is used in conjunction with the server-sided HateoasJx API for processing the recieved link
 * headers and injecting them to the resolved entities. Calling an entitys' getter will in turn call the
 * registered {@link RequestExecutor} with the getters' return type and related link URL.
 * <br/>
 * <br/>
 * Even if the the client object itself is thread-safe it can only be considered fully thread-safe if the
 * supplied {@link RequestExecutor} is also thread-safe.
 */
public class HateoasJxClient
{
	private static final Pattern ABSOLUTE_URL = Pattern.compile("^http[s]?://");
	private static final Pattern ANCHOR = Pattern.compile("anchor=\\\"(.+?)?#(.+?)\\\"");

	private final Supplier<RequestExecutor> reqExecutor;

	/**
	 * Construct a new client using the given {@link RequestExecutor}.
	 * 
	 * @param executor the {@link RequestExecutor}
	 */
	public HateoasJxClient(final RequestExecutor executor)
	{
		this.reqExecutor = () -> executor;
	}

	/**
	 * Construct a new client using the given {@link RequestExecutor}. Supplier evaluation will be deferred
	 * until first use of the client.
	 * 
	 * @param executor the {@link RequestExecutor}
	 */
	public HateoasJxClient(final Supplier<RequestExecutor> executor)
	{
		this.reqExecutor = new SingletonSupplier<>(executor);
	}

	/**
	 * Request a resource available at given absolute URL.
	 * 
	 * @param type the expected result type
	 * @param url the absolute URL
	 * @return requested entity
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(final Class<T> type, final String url)
	{
		return (T) get(new ClassEntityType<>(type), url);
	}

	/**
	 * Request a resource available at given absolute URL.
	 * 
	 * @param <T> generic result type declaration; f.e.: List<...>
	 * @param type a {@link GenericType} representing the actual result type
	 * @param url the absolute URL
	 * @return requested entity
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(final GenericType<T> type, final String url)
	{
		return (T) get((EntityType) type, url);
	}

	/**
	 * Request a resource available at given absolute URL.
	 * 
	 * @param type the {@code EntityType}
	 * @param url the absolute URL
	 * @see #get(Class, String)
	 * @see #get(GenericType, String)
	 */
	public Object get(final EntityType type, final String url)
	{
		if(!ABSOLUTE_URL.matcher(url).find())
			new HateoasJxException("URL must be absolute but %s was passed".formatted(url));

		final var response = this.reqExecutor.get().request(type, url);
		final var entity = response.getEntity();

		if(Objects.nonNull(entity))
		{
			injectInitializer(type, entity, response.getLinks());
		}

		return entity;
	}

	/**
	 * Inject the field initializer for every deserialized entity.
	 * 
	 * @param type the requested {@link EntityType}
	 * @param resolved the deserialized payload
	 * @param links all found links in the request
	 */
	@SuppressWarnings("unchecked")
	private void injectInitializer(final EntityType type, final Object resolved, final Collection<String> links)
	{
		// handle single entity
		if(!type.hasMultiple())
		{
			final var linkMap = findRelated(links, l -> true)
				.map(l -> Map.entry(getAnchorValue(l), getUrl(l)))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			((HateoasJxProxy) resolved).$$_hjx_initializer(new HateoasJxInitializer(this, linkMap));
			return;
		}

		// build anchor-link map for each item link
		// 1. iterate all rel=item links
		// 2. group them by anchor value
		// 3. find all rel=related links where anchor starts with link from iteration 1
		// 4. build map anchor-link
		final var entityAnchorLinks = links.stream()
			.filter(l -> l.contains("rel=\"item\""))
			.collect(Collectors.groupingBy(HateoasJxClient::getAnchorValue, Collectors.flatMapping(l -> {
				final var filter = "anchor=\"" + getUrl(l) + '#';
				return findRelated(links, rel -> rel.contains(filter));
			}, Collectors.toMap(HateoasJxClient::getAnchorValue, HateoasJxClient::getUrl))));

		// get typed entities
		final var entities = type.isArray()
			? Stream.of((HateoasJxProxy[]) resolved)
			: ((Collection<HateoasJxProxy>) resolved).stream();

		// inject initializer
		entities.forEach(e -> {
			final Map<String, String> relatedLinks;
			if(Objects.isNull(e.$$_hjx_identifier()))
			{
				Log.warn("@HateoasProxy on entity {0} has no anchor value set. Lazy initialization is disabled", e);
				relatedLinks = Map.of();
			}
			else
				relatedLinks = entityAnchorLinks.get(e.$$_hjx_identifier());

			e.$$_hjx_initializer(new HateoasJxInitializer(this, relatedLinks));
		});
	}

	/**
	 * @param link the full link
	 * @return the anchor part after #
	 */
	private static String getAnchorValue(final String link)
	{
		final var matcher = ANCHOR.matcher(link);
		matcher.find();
		return matcher.group(2);
	}

	/**
	 * @param link the full link
	 * @return the part between < and >
	 */
	private static String getUrl(final String link)
	{
		return link.substring(1, link.indexOf('>'));
	}

	/**
	 * Find all links in given pool with relation type is 'related' and fulfills the given test.
	 * 
	 * @param links pool of links
	 * @param test additional filter
	 * @return stream of iltered links
	 */
	private static Stream<String> findRelated(final Collection<String> links, final Predicate<String> test)
	{
		return links.stream()
			.filter(l -> l.contains("rel=\"related\""))
			.filter(test);
	}
}
