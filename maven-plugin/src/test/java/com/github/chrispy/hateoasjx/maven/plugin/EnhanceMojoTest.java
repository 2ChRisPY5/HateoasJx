package com.github.chrispy.hateoasjx.maven.plugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.chrispy.app.Basic;
import com.github.chrispy.app.Human;
import com.github.chrispy.app.MoreThen10;
import com.github.chrispy.app.Nested;
import com.github.chrispy.app.Parents;
import com.github.chrispy.hateoasjx.api.HateoasJxResolvable;
import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.api.config.RelatedConfig;
import com.github.chrispy.hateoasjx.api.config.SelfConfig;
import com.github.chrispy.hateoasjx.client.internal.ClassEntityType;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxInitializer;
import com.github.chrispy.hateoasjx.client.proxy.HateoasJxProxy;
import com.github.chrispy.hateoasjx.client.type.EntityType;
import com.github.chrispy.hateoasjx.client.type.GenericType;

/**
 * Testing the Mojo
 */
public class EnhanceMojoTest
{
	@ClassRule
	public static final MojoRule rule = new MojoRule() {
		private static final File POM = new File("src/test/resources/pom.xml");

		/**
		 * Execute the instrumentation
		 */
		@Override
		protected void before() throws Throwable
		{
			lookupMojo("test-enhance", POM).execute();
		};

		/**
		 * Close after every test
		 */
		@Override
		protected void after()
		{
		};
	};

	/**
	 * Test the basic case.
	 */
	@Test
	public void testResolvableBasic() throws Exception
	{
		final var entity = loadEntity("com.github.chrispy.app.Basic", HateoasJxResolvable.class);
		assertResolver(entity, new SelfConfig("/incidents/@i", "string"),
			List.of(new RelatedConfig("b", "/@b", true),
				new RelatedConfig("s", "/@s", true),
				new RelatedConfig("l", "/@l", true),
				new RelatedConfig("d", "/@d", true),
				new RelatedConfig("f", "/@f", true)),
			Map.of(
				"i", "0",
				"b", "0",
				"s", "0",
				"l", "0",
				"d", "0.0",
				"f", "0.0",
				"string", "string"));
	}

	/**
	 * Test with more then 10 parameters.
	 */
	@Test
	public void testResolvableMoreThen10() throws Exception
	{
		final var entity = loadEntity("com.github.chrispy.app.MoreThen10", HateoasJxResolvable.class);
		assertResolver(entity, new SelfConfig("/incidents/@pa", "pb"),
			List.of(new RelatedConfig("pc", "/@pc", true),
				new RelatedConfig("pd", "/@pd", true),
				new RelatedConfig("pe", "/@pe", true),
				new RelatedConfig("pf", "/@pf", true),
				new RelatedConfig("pg", "/@pg", true),
				new RelatedConfig("ph", "/@ph", true),
				new RelatedConfig("pi", "/@pi", true),
				new RelatedConfig("pj", "/@pj", true),
				new RelatedConfig("pk", "/@pk", true)),
			Map.ofEntries(
				Map.entry("pa", "pa"),
				Map.entry("pb", "pb"),
				Map.entry("pc", "pc"),
				Map.entry("pd", "pd"),
				Map.entry("pe", "pe"),
				Map.entry("pf", "pf"),
				Map.entry("pg", "pg"),
				Map.entry("ph", "ph"),
				Map.entry("pi", "pi"),
				Map.entry("pj", "pj"),
				Map.entry("pk", "pk")));
	}

	/**
	 * Test class inheritance
	 */
	@Test
	public void testResolvableInheritance() throws Exception
	{
		final var self = new SelfConfig("/humans/@id", "id");
		final var human = loadEntity("com.github.chrispy.app.Human", HateoasJxResolvable.class);
		assertResolver(human, self, List.of(), Map.of("id", "0"));

		final var parents = loadEntity("com.github.chrispy.app.Parents", HateoasJxResolvable.class);
		assertResolver(parents, self,
			List.of(new RelatedConfig("children", "/children?parents=@name", false)),
			Map.of(
				"id", "0",
				"name", "Müller"));
	}

	/**
	 * Test expression chain
	 */
	@Test
	public void testGetterChain() throws Exception
	{
		final var self = new SelfConfig("/@real.deep.id", "real.deep.flag");
		final var nested = loadEntity(Nested.class.getName(), HateoasJxResolvable.class);

		assertResolver(nested, self, List.of(), Map.of("real.deep.id", "1",
			"real.deep.flag", "true"));
	}

	/**
	 * Asserts the complete resolver.
	 * 
	 * @param entity the {@link Linkable} domain object
	 * @param self expected {@code SelfConfig}
	 * @param related expected {@code RelatedConfig}s
	 */
	private static void assertResolver(final HateoasJxResolvable entity, final SelfConfig self, final List<RelatedConfig> related,
		final Map<String, Object> subs)
	{
		assertThat(entity.$$_hjx_self(), equalTo(self));
		assertThat(entity.$$_hjx_relations(), equalTo(related));
		assertThat(entity.$$_hjx_substitutions(), equalTo(subs));
	}

	/**
	 * Test that HateoasJxProxy has null identifier.
	 */
	@Test
	public void testProxyNullIdentifier() throws Exception
	{
		final var initializer = new HateoasJxInitializer(null, Map.of());
		final var entity = loadEntity(Basic.class.getName(), HateoasJxProxy.class);
		entity.$$_hjx_initializer(initializer);

		assertThat(entity.$$_hjx_identifier(), nullValue());
		assertThat(entity.$$_hjx_initializer(), sameInstance(initializer));
	}

	/**
	 * Test that HateoasJxProxy is correctly returning the configured anchor value.
	 */
	@Test
	public void testProxyNonNullIdentifier() throws Exception
	{
		final var initializer = new HateoasJxInitializer(null, Map.of());
		final var entity = loadEntity(MoreThen10.class.getName(), HateoasJxProxy.class);
		entity.$$_hjx_initializer(initializer);

		assertThat(entity.$$_hjx_identifier(), equalTo("pa"));
		assertThat(entity.$$_hjx_initializer(), sameInstance(initializer));
	}

	/**
	 * Test if resolving a list is working.
	 */
	@Test
	public void testProxyResolve() throws Exception
	{
		final var entity = spy(loadEntity(Parents.class.getName(), Parents.class));
		final var asProxy = (HateoasJxProxy) entity;

		entity.getChildren();
		entity.getBrother();
		entity.getDontKnow();
		entity.getMoreChildren();
		entity.getPartner();
		entity.getTooGeneric();

		verify(asProxy).$$_hjx_init("children", GenericType.listOf(Human.class), null);
		verify(asProxy).$$_hjx_init("different", GenericType.setOf(Human.class), null);
		verify(asProxy).$$_hjx_init("partner", new ClassEntityType<>(Human.class), null);
		verify(asProxy).$$_hjx_init("brother", GenericType.optionalOf(Human.class), null);
		verify(asProxy).$$_hjx_init("dontKnow", new ClassEntityType<>(Human[].class), null);
		verify(asProxy).$$_hjx_init("tooGeneric", (EntityType) null, null);
	}

	/**
	 * Load and create an entity by its FQDN and cast to given class.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T loadEntity(final String fqdn, final Class<T> type) throws Exception
	{
		return (T) EnhanceMojoTest.class.getClassLoader()
			.loadClass(fqdn)
			.getDeclaredConstructor()
			.newInstance();
	}
}
