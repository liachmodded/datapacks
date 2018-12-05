package com.github.liachmodded.datapacks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

/**
 * The data pack provider for data folder in the game directory.
 */
class StandaloneDataProvider extends BaseDataProvider {
  private static final String NAME = "<data>";
  private final Path directory;
  private boolean isPack;
  private IDataPack pack;

  StandaloneDataProvider(Path data) {
    this.directory = data;
  }

  @Override
  public Set<IDataPack> getAll() {
    return isPack ? Collections.singleton(pack) : Collections.emptySet();
  }

  @Nullable
  @Override
  public IDataPack getByName(String name) {
    if (isPack && name.equals(NAME)) {
      return pack;
    }
    return null;
  }

  @Override
  public void rescan() {
    pack = null;
    isPack = false;
    if (!Files.isDirectory(directory)) {
      return;
    }
    try (Stream<Path> stream = Files.list(directory)) {
      isPack = !stream.findAny().isPresent();
    } catch (IOException ignored) {
    }

    if (isPack) {
      pack = resolve(NAME, directory);
    }
  }
}
