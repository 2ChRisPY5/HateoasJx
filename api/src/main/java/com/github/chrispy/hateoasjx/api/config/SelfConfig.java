package com.github.chrispy.hateoasjx.api.config;

/**
 * Reflects the configuration done by using the {@code Linkable} annotation. This class is usally not created
 * manually but by build-time instrumentation.
 */
public record SelfConfig(String path, String identifiedBy)
{
}
