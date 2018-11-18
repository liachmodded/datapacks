package com.github.liachmodded.datapacks;

import com.google.common.collect.ImmutableSet;

import net.minecraft.util.text.ITextComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
  private final boolean old;

  FileDataPack(Path root, ITextComponent description, boolean old) {
    this.name = root.getFileName().toString();
    this.directory = root;
    this.old = old;
    this.typeRoot = old ? determineTypeDirectory(root) : root.resolve("data");
    this.description = description;
    this.domains = old ? setupOldDomains() : setupAquaticDomains();
  }

  private static Path determineTypeDirectory(Path root) {
    Path data = root.resolve("data");
    return Files.isDirectory(data) ? data : root;
  }

  private Set<String> setupOldDomains() {
    // (optional) data/type/modid/path
    try (Stream<Path> stream = Files.list(typeRoot)) {
      return stream.filter(Files::isDirectory).flatMap(file -> {
        try {
          return Files.list(file);
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      }).map(Path::getFileName).map(Path::toString).collect(ImmutableSet.toImmutableSet());
    } catch (IOException | UncheckedIOException ex) {
      LOGGER.error("I/O error loading 1.12 format domains in data pack {}", name, ex);
      return Collections.emptySet();
    }
  }

  private Set<String> setupAquaticDomains() {
    // data/modid/type/path
    try (Stream<Path> stream = Files.list(typeRoot.resolve("data"))) {
      return stream.filter(Files::isDirectory).map(Path::getFileName).map(Path::toString).collect(ImmutableSet.toImmutableSet());
    } catch (IOException | UncheckedIOException ex) {
      LOGGER.error("I/O error loading 1.13 format domains in data pack {}", name, ex);
      return Collections.emptySet();
    }
  }

  @Override
  public IData get(String type) {
    return dataMap.computeIfAbsent(type, key -> new FileData(this, typeRoot, key, old));
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
