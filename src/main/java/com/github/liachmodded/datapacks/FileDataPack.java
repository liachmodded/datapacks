package com.github.liachmodded.datapacks;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.text.ITextComponent;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public class FileDataPack implements IDataPack {

  private final String name;
  private final File directory;
  private final File typeDirectory;
  private final ITextComponent description;
  private final Set<String> domains;
  private final Map<String, FileData> dataMap = new HashMap<>();
  private final boolean old;

  FileDataPack(File root, ITextComponent description, boolean old) {
    this.name = root.getName();
    this.directory = root;
    this.old = old;
    this.typeDirectory = old ? determineTypeDirectory(root) : new File(root, "data");
    this.description = description;
    this.domains = old ? setupOldDomains() : setupAquaticDomains();
  }

  private static File determineTypeDirectory(File root) {
    File data = new File(root, "data");
    return data.exists() ? data : root;
  }

  private Set<String> setupOldDomains() {
    // (optional) data/type/modid/path
    File[] array = typeDirectory.listFiles(File::isDirectory);
    if (array == null) {
      return Collections.emptySet();
    }
    return Arrays.stream(array).map(file -> file.listFiles(File::isDirectory))
        .filter(Objects::nonNull).flatMap(Arrays::stream).map(File::getName).collect(ImmutableSet.toImmutableSet());
  }

  private Set<String> setupAquaticDomains() {
    // data/modid/type/path
    File[] array = new File(typeDirectory, "data").listFiles(File::isDirectory);
    if (array == null) {
      return Collections.emptySet();
    }
    return Arrays.stream(array).map(File::getName).collect(ImmutableSet.toImmutableSet());
  }

  @Override
  public IData get(String type) {
    return dataMap.computeIfAbsent(type, key -> new FileData(this, typeDirectory, key, old));
  }

  @Override
  public Set<String> getDomains() {
    return domains;
  }

  @Override
  public ITextComponent getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name;
  }
}
