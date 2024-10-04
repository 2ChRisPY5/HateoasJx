package com.github.chrispy.hateoasjx.maven.plugin;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode.Visitor;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.Dispatcher;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.Write;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

/**
 * 
 */
@Mojo(name = "enhance", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class EnhanceMojo extends AbstractMojo
{
	private final Visitor logger = n -> getLog().info("[HateoasJx] Processing " + n.getCtClass().getName());

	@Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
	private String buildDir;

	private final String classPath;

	/**
	 * Constructor
	 */
	public EnhanceMojo()
	{
		this(false);
	}

	/**
	 * Constructor
	 * 
	 * @param testCompile flag if testCompile is running
	 */
	protected EnhanceMojo(final boolean testCompile)
	{
		this.classPath = testCompile ? "/test-classes" : "/classes";
	}

	/**
	 * {@inherited}
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		final var srcDir = Paths.get(this.buildDir, this.classPath);
		try
		{
			final var ctx = Context.open(srcDir);
			final var chain = this.logger.andThen(new Dispatcher()).andThen(new Write(ctx));
			ctx.getClassRoots().forEach(chain);
		}
		catch(final IOException ex)
		{
			throw new HateoasJxEnhanceException("Class tree could not be built from directory %s".formatted(srcDir)).initCause(ex);
		}
		finally
		{
			JavassistUtil.close();
		}
	}
}
