package com.github.chrispy.hateoasjx.maven.plugin.enhancements;

/**
 * Collects process information for a class node.
 */
public final class ProcessInformation
{
	private boolean selfExists = false;
	private boolean relationsExists = false;
	private boolean subsitutionsExists = false;
	private boolean identifierExists = false;
	private boolean initializerExists = false;

	/**
	 * @return the selfExists
	 */
	public boolean isSelfExists()
	{
		return selfExists;
	}

	/**
	 * @param selfExists the selfExists to set
	 */
	public void selfExists(final boolean selfExists)
	{
		this.selfExists = selfExists;
	}

	/**
	 * @return the relationsExists
	 */
	public boolean isRelationsExists()
	{
		return relationsExists;
	}

	/**
	 * @param relationsExists the relationsExists to set
	 */
	public void relationsExists(final boolean exists)
	{
		this.relationsExists = exists;
	}

	/**
	 * @return the subsitutionsExists
	 */
	public boolean isSubsitutionsExists()
	{
		return subsitutionsExists;
	}

	/**
	 * @param subsitutionsExists the subsitutionsExists to set
	 */
	public void subsitutionsExists(final boolean exists)
	{
		this.subsitutionsExists = exists;
	}

	/**
	 * @return the identifierExists
	 */
	public boolean isIdentifierExists()
	{
		return identifierExists;
	}

	/**
	 * @param identifierExists the identifierExists to set
	 */
	public void identifierExists(final boolean identifierExists)
	{
		this.identifierExists = identifierExists;
	}

	/**
	 * @return the initializerExists
	 */
	public boolean isInitializerExists()
	{
		return initializerExists;
	}

	/**
	 * @param initializerExists the initializerExists to set
	 */
	public void initializerExists(final boolean initializerExists)
	{
		this.initializerExists = initializerExists;
	}
}
