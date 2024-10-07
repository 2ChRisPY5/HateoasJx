package com.github.chrispy.app;

import com.github.chrispy.hateoasjx.api.Linkable;

/**
 * Test class for getter chain usage
 */
@Linkable(path = "/@real.deep.id", identifiedBy = "real.deep.flag")
public class Nested
{
	private final Real real = new Real();

	/**
	 * Level 1
	 */
	private static class Real
	{
		private final Deep deep = new Deep();

		public Deep getDeep()
		{
			return this.deep;
		}
	}

	/**
	 * Level 2
	 */
	private static class Deep
	{
		private final long id = 1;
		private final boolean flag = true;

		public long getId()
		{
			return this.id;
		}

		public boolean isFlag()
		{
			return this.flag;
		}
	}
}
