package com.github.liachmodded.datapacks;

import com.google.common.base.MoreObjects;

import net.minecraft.util.text.ITextComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
class FileDataPack implements IDataPack {

  private static final Logger LOGGER = LogManager.getLogger();
  private final String name;
  private final Path directory;
  private final Path typeRoot;
  private final ITextComponent description;
  private final Set<String> domains;
  private final Map<String, FileData> dataMap = new HashMap<>();
  private final PackFormat format;

  FileDataPack(Path root, ITextComponent description, PackFormat format) {
    this.name = root.getFileName().toString();
    this.directory = root;
    this.format = format;
    this.typeRoot = format.getTypeRoot(root);
    this.description = description;
    this.domains = setupDomains();
  }

  private Set<String> setupDomains() {
    // (optional) data/type/modid/path
    try {
      return format.setupDomains(typeRoot);
    } catch (IOException | UncheckedIOException ex) {
      LOGGER.error("I/O error loading {} format domains in data pack {}", format, name, ex);
      return Collections.emptySet();
    }
  }

  @Override
  public IData get(String type) {
    return dataMap.computeIfAbsent(type, key -> new FileData(this, typeRoot, key, format));
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

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("name", name)
        .add("directory", directory)
        .add("type root", typeRoot)
        .add("format", format).toString();
  }
}
