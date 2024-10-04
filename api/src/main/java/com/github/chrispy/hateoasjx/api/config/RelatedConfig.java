package com.github.chrispy.hateoasjx.api.config;

import com.github.chrispy.hateoasjx.api.Related;

/**
 * Reflects the configuration done by using the {@link Related} annotation. This class is usally not created
 * manually but by build-time instrumentation.
 */
public record RelatedConfig(String anchor, String path, boolean subordinate)
{
}
