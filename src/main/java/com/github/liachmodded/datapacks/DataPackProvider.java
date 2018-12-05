package com.github.liachmodded.datapacks;

import com.google.common.io.MoreFiles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A data pack directory searcher.
 */
class DataPackProvider extends BaseDataProvider implements IDataPackProvider {

  private static final Logger LOGGER = LogManager.getLogger();
  final Path directory;
  final Set<IDataPack> packs = new HashSet<>();
  final Map<String, IDataPack> byName = new HashMap<>();

  DataPackProvider(Path directory) {
    this.directory = directory;
    if (!Files.exists(directory)) {
      try {
        Files.createDirectories(directory);
      } catch (IOException ex) {
        LOGGER.error("Cannot create \"{}\" directory", directory.getFileName().toString());
      }
    }
  }

  @Override
  public Set<IDataPack> getAll() {
    return packs;
  }

  @Override
  public IDataPack getByName(String name) {
    return byName.get(name);
  }

  @Override
  public void rescan() {
    byName.clear();
    packs.clear();

    scanPacks();

    for (IDataPack pack : packs) {
      byName.put(pack.getName(), pack);
    }
  }

  void scanPacks() {
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, FILTER)) {
      for (Path each : directoryStream) {
        if (Files.isDirectory(each)) {
          LOGGER.info("Found directory {}", each);
          packs.add(resolve(each.getFileName().toString(), each));
        } else if (Files.isRegularFile(each)) {
          LOGGER.info("Found zip file {}", each);
          FileSystem zip = FileSystems.newFileSystem(each, null);
          packs.add(resolve(MoreFiles.getNameWithoutExtension(each), zip.getPath("/")));
        }
      }
    } catch (IOException ex) {
      LOGGER.error("Failed to reload data packs", ex);
    }
  }
}
