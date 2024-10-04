package com.github.chrispy.hateoasjx.common.concurrent;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Thread-safe supplier for singleton instances.
 */
public final class SingletonSupplier<T> implements Supplier<T>
{
	private final Lock lock = new ReentrantLock();
	private Supplier<T> delegate;
	private T instance;

	/**
	 * Constructor
	 * 
	 * @param delegate the actual instance supplier
	 */
	public SingletonSupplier(final Supplier<T> delegate)
	{
		this.delegate = delegate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get()
	{
		if(Objects.nonNull(this.instance))
			return this.instance;

		this.lock.lock();

		try
		{
			if(Objects.nonNull(this.instance))
				return this.instance;

			this.instance = this.delegate.get();
			this.delegate = null;
			return this.instance;
		}
		finally
		{
			this.lock.unlock();
		}
	}
}