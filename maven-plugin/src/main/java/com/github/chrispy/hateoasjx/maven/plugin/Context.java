package com.github.chrispy.hateoasjx.maven.plugin;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.client.HateoasProxy;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.Descriptor;

/**
 * Current execution context
 */
public class Context
{
	private final Path dir;
	private final List<ClassNode> classRoots = new ArrayList<ClassNode>();

	static
	{
		ClassPool.doPruning = true;
	}

	/**
	 * Constructor
	 */
	private Context(final Path dir)
	{
		this.dir = dir;
	}

	/**
	 * Open a new context for the given source directory
	 * 
	 * @param sourceDir the class source directory
	 * @return a new context
	 * @throws IOException if given directory could not be processed
	 */
	static Context open(final Path sourceDir) throws IOException
	{
		final var ctx = new Context(sourceDir);

		Files.walkFileTree(sourceDir, new ClassTreeBuilder(sourceDir, ctx));

		return ctx;
	}

	/**
	 * @return the directory
	 */
	public Path getDir()
	{
		return this.dir;
	}

	/**
	 * @return the class roots
	 */
	public List<ClassNode> getClassRoots()
	{
		return this.classRoots;
	}

	/**
	 * File visitor for building the class tree
	 */
	private static class ClassTreeBuilder extends SimpleFileVisitor<Path>
	{
		private final Path root;
		private final List<ClassNode> classRoots;

		/**
		 * Constructor
		 */
		private ClassTreeBuilder(final Path root, final Context ctx)
		{
			this.root = root;
			this.classRoots = ctx.classRoots;
		}

		/**
		 * {@inherited}
		 */
		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
		{
			// abort if not a .class file
			final var name = file.toString();
			if(!attrs.isRegularFile() || !name.endsWith(".class") || name.endsWith("module-info.class"))
				return FileVisitResult.CONTINUE;

			// build the class name
			final var relative = this.root.relativize(file).toString();
			final var className = Descriptor.toJavaName(relative.substring(0, relative.length() - 6));

			// load the class and check type and if annotated
			final var ctClass = JavassistUtil.load(className);
			if(ctClass.isInterface() || ctClass.isEnum() || !canBeProcessed(ctClass))
			{
				ctClass.detach();
				return FileVisitResult.CONTINUE;
			}

			// insert class in tree hierarchy
			// iterate existing roots
			for(final var root : this.classRoots)
			{
				// check if new class is super-type of current root
				if(root.getCtClass().subclassOf(ctClass))
				{
					final var newNode = new ClassNode(ctClass, null);
					newNode.addChild(root);
					classRoots.add(newNode);
					classRoots.remove(root);
					return FileVisitResult.CONTINUE;
				}

				// try inserting to current root
				if(root.insert(ctClass))
					return FileVisitResult.CONTINUE;
			}

			// else create new root
			classRoots.add(new ClassNode(ctClass, null));
			return FileVisitResult.CONTINUE;
		}

		/**
		 * Check if given class should be processed.
		 * 
		 * @param ctClass the {@code CtClass}
		 * @return true or false
		 */
		private static boolean canBeProcessed(final CtClass ctClass)
		{
			if(Objects.isNull(ctClass))
				return false;

			if(ctClass.hasAnnotation(Linkable.class) || ctClass.hasAnnotation(HateoasProxy.class))
				return true;

			return canBeProcessed(JavassistUtil.getSuperclass(ctClass));
		}
	}
}
