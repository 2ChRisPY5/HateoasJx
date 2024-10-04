package com.github.chrispy.app;

import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.api.Related;
import com.github.chrispy.hateoasjx.client.HateoasProxy;

@SuppressWarnings("all")
@Linkable(path = "/incidents/@pa", identifiedBy = "pb")
@HateoasProxy(anchor = "pa")
public class MoreThen10
{
	private final String pa = "pa";
	private final String pb = "pb";

	@Related(path = "/@pc")
	private final String pc = "pc";

	@Related(path = "/@pd")
	private final String pd = "pd";

	@Related(path = "/@pe")
	private final String pe = "pe";

	@Related(path = "/@pf")
	private final String pf = "pf";

	@Related(path = "/@pg")
	private final String pg = "pg";

	@Related(path = "/@ph")
	private final String ph = "ph";

	@Related(path = "/@pi")
	private final String pi = "pi";

	@Related(path = "/@pj")
	private final String pj = "pj";

	@Related(path = "/@pk")
	private final String pk = "pk";
}
