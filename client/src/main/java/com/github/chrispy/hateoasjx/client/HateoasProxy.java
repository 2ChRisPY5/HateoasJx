package com.github.chrispy.hateoasjx.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * <h3>Used on class</h3>
 * Register the type for bytecode enhancement. Only those classes can be processed by the
 * {@code HateoasJxClient}. Otherwise a {@code ClassCastException} will be thrown.
 * <h3>Used on field</h3>
 * You have to mark each field which should be intercepted by the client for lazy initialization. Bytecode
 * enhancement is then looking for a getter following the Java naming conventions. If one was found the
 * methods' body will be enriched by the initializer mechanism. Otherwise nothing will happen.
 */
@Inherited
@Target({
	ElementType.TYPE, ElementType.FIELD
})
public @interface HateoasProxy
{
	/**
	 * <h3>Meaning for class</h3>
	 * The {@code anchor} value is used by the client for associating links to their correct entities. The
	 * value needs to match that field name which uniquely identifies this entity.
	 * <br>
	 * <br>
	 * This value can be left untouched if you are 100% sure that you will never request a collection of this
	 * type. Otherwise the lazy initialization is not going to work. The client will print a warning instead.
	 * <b>It is recommended to always set the value!</b>
	 * <h3>Used on a field</h3>
	 * You always have to set a value if used on a field! The value has to match the {@code Related}
	 * configuration used by the server part for link generation. Otherwise lazy initialization for this field
	 * won't work.
	 */
	String anchor() default "";
}
