package com.github.chrispy.hateoasjx.maven.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * 
 */
@Mojo(name = "test-enhance", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES)
public class EnhanceTestMojo extends EnhanceMojo
{
	/**
	 * Constructor
	 */
	public EnhanceTestMojo()
	{
		super(true);
	}
}
