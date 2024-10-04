package com.github.chrispy.app;

import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.client.HateoasProxy;

@SuppressWarnings("all")
@Linkable(path = "/humans/@id", identifiedBy = "id")
@HateoasProxy(anchor = "id")
public class Human
{
	private final long id = 0;
}
