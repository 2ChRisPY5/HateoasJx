package com.github.chrispy.hateoasjx.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Any field annotated will be registered as a related resource to its' declaring class. Any time an instance
 * is getting processed by link generation it will also produce a related link.
 * <br>
 * <br>
 * The annotation will be processed by bytecode enhancement.
 */
@Target(ElementType.FIELD)
public @interface Related
{
	/**
	 * Set the relative URL for the resource this relation corresponds to. You can also use parameters by
	 * using
	 * {@code @} as a prefix. The name must match a field name and will be replaced by its' stringyfied
	 * value. Query parameters can also be used.
	 * <h4>Example</h4>
	 * 
	 * <pre>
	 * &#064;Linkable(path = "/persons/@id")
	 * public class Person
	 * {
	 * 	&#064;Related(path = "/more-friends")
	 * 	private List<Person> friends;
	 * }
	 * </pre>
	 * 
	 * This will result in the link {@code /persons/@id/more-friends}.
	 * <br>
	 * <br>
	 * If you you are not setting any value the fields name will be used as path. The above example would then
	 * result in {@code /persons/@id/friends}.
	 * 
	 * @return the relative resource path
	 */
	String path() default "";

	/**
	 * This flag controls if the link for this relation will be appended to its' declaring {@code Linkable} or
	 * not.
	 * <h4>Example if true</h4>
	 * 
	 * <pre>
	 * &#064;Linkable(path = "/persons/@id")
	 * public class Person
	 * {
	 * 	&#064;Related // default is true
	 * 	private List<Person> friends;
	 * }
	 * </pre>
	 * 
	 * will result in {@code /persons/@id/friends}
	 * <h4>Example if false</h4>
	 * 
	 * <pre>
	 * &#064;Linkable(path = "/persons/@id")
	 * public class Person
	 * {
	 * 	&#064;Related(path = "/friends?person=@id", subordinate = false)
	 * 	private List<Person> friends;
	 * }
	 * </pre>
	 * 
	 * would generate {@code /friends?person=@id}.
	 * 
	 * @return true or false
	 */
	boolean subordinate() default true;
}
