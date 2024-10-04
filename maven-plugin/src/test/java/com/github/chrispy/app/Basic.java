package com.github.chrispy.app;

import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.api.Related;
import com.github.chrispy.hateoasjx.client.HateoasProxy;

/**
 * Simple incident class
 */
@SuppressWarnings("all")
@Linkable(path = "/incidents/@i", identifiedBy = "string")
@HateoasProxy
public class Basic
{
	private final int i = 0;

	@Related(path = "/@b")
	private final byte b = 0;

	@Related(path = "/@s")
	private final short s = 0;

	@Related(path = "/@l")
	private final long l = 0;

	@Related(path = "/@d")
	private final double d = 0D;

	@Related(path = "/@f")
	private final float f = 0F;

	private final String string = "string";
}
