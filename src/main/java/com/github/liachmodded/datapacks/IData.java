package com.github.liachmodded.datapacks;

import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Represents a type of data.
 */
public interface IData {

  String getContent(ResourceLocation location, String suffix) throws IOException;

  // TODO copying data from general folder to each world's save
  Path getPath(ResourceLocation location, String suffix);

  boolean has(ResourceLocation location, String suffix);

  Set<String> getDomains();

  void forEachContent(String suffix, BiConsumer<ResourceLocation, Supplier<String>> consumer);
}
