module hateoasjx.client
{
	requires transitive java.net.http;
	requires hateoasjx.common;

	exports com.github.chrispy.hateoasjx.client;
	exports com.github.chrispy.hateoasjx.client.type;
	exports com.github.chrispy.hateoasjx.client.proxy;
}
