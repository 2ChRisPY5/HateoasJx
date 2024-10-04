package com.github.chrispy.app;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.chrispy.hateoasjx.api.Related;
import com.github.chrispy.hateoasjx.client.HateoasProxy;

@SuppressWarnings("all")
public class Parents extends Human
{
	private final String name = "Müller";

	@Related(path = "/children?parents=@name", subordinate = false)
	@HateoasProxy
	private List<Human> children;

	@HateoasProxy(anchor = "different")
	private Set<Human> moreChildren;

	@HateoasProxy
	private Human partner;

	@HateoasProxy
	private Optional<Human> brother;

	@HateoasProxy
	private Human[] dontKnow;

	@HateoasProxy
	private Collection<Human> tooGeneric;

	/**
	 * @return the children
	 */
	public List<Human> getChildren()
	{
		return this.children;
	}

	/**
	 * @return the moreChildren
	 */
	public Set<Human> getMoreChildren()
	{
		return this.moreChildren;
	}

	/**
	 * @return the partner
	 */
	public Human getPartner()
	{
		return this.partner;
	}

	/**
	 * @return the brother
	 */
	public Optional<Human> getBrother()
	{
		return this.brother;
	}

	/**
	 * @return the dontKnow
	 */
	public Human[] getDontKnow()
	{
		return this.dontKnow;
	}

	/**
	 * @return the tooGeneric
	 */
	public Collection<Human> getTooGeneric()
	{
		return this.tooGeneric;
	}
}
