package com.github.chrispy.hateoasjx.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Use this annotation on classes which should be processed by bytecode enhancement so they are supported for
 * link generation.
 * <br>
 * <br>
 * All classes annotated will receive two static fields and their getters containg all the necessary
 * information needed for generating the self and related links.
 */
@Inherited
@Target(ElementType.TYPE)
public @interface Linkable
{
	/**
	 * Set the relative URL for the resource this entity belongs to. You can also use parameters by using
	 * {@code @} as a prefix. The name must match a field name and will be replaced by its' stringyfied
	 * value. <b>Nested field expressions are currently not supported!</b> Query parameters can also be used.
	 * <h4>Example</h4>
	 * 
	 * <pre>
	 * &#064;Linkable(path = "/persons/@id")
	 * public class Person
	 * {
	 * 	private long id;
	 * }
	 * </pre>
	 * 
	 * @return the relative resource path
	 */
	String path();

	/**
	 * A property which is present in the serialized payload and uniquely identifies itself. This is only used
	 * if multiple entities of the same type are getting processed by HateoasJx.
	 * 
	 * @return name of the field uniquely identifying this type.
	 */
	String identifiedBy();
}
