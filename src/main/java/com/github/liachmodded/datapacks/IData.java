package com.github.liachmodded.datapacks;

import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Represents a type of data.
 */
public interface IData {

  String getContent(ResourceLocation location) throws IOException;

  boolean has(ResourceLocation location);

  Set<String> getDomains();

  void forEachContent(String suffix, BiConsumer<ResourceLocation, Supplier<String>> consumer);
}
