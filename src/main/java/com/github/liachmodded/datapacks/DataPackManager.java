package com.github.liachmodded.datapacks;

import com.google.common.collect.Sets;
import com.google.common.io.MoreFiles;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
class DataPackManager implements IDataPackManager {
  private static final DirectoryStream.Filter<Path> FILTER = path ->
      (Files.isRegularFile(path) && MoreFiles.getFileExtension(path).equals(".zip")) ||
          Files.isDirectory(path) && Files.isRegularFile(path.resolve("pack.mcmeta"));
  private static final Logger LOGGER = LogManager.getLogger();
  private final Set<String> testTypes = Sets.newHashSet("advancements", "loot_tables", "functions");
  private final Path directory;
  private final Set<IDataPack> packs = new HashSet<>();
  private final List<IDataPack> enabledPacks = new ArrayList<>();

  DataPackManager(Path directory) {
    this.directory = directory;
    if (!Files.exists(directory)) {
      try {
        Files.createDirectories(directory);
      } catch (IOException ex) {
        LOGGER.warn("Cannot create \"{}\" directory", directory.getFileName().toString());
      }
    }
  }

  @Override
  public void addDataType(String type) {
    testTypes.add(type);
  }

  @Override
  public Set<IDataPack> getAll() {
    return packs;
  }

  @Override
  public List<IDataPack> getEnabled() {
    return enabledPacks;
  }

  @Override
  public void disable(IDataPack pack) {
    enabledPacks.remove(pack);
  }

  @Override
  public void rank(int index, IDataPack pack) {
    enabledPacks.remove(pack);
    enabledPacks.add(index, pack);
  }

  @Override
  public void rescan() {
    enabledPacks.clear();
    packs.clear();

    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, FILTER)) {
      for (Path each : directoryStream) {
        if (Files.isDirectory(each)) {
          resolveMetaAndAdd(each);
        } else if (Files.isRegularFile(each)) {
          FileSystem zip = FileSystems.newFileSystem(each, null);
          resolveMetaAndAdd(zip.getPath(""));
        }
      }
    } catch (IOException ex) {
      LOGGER.error("Failed to reload data packs", ex);
    }

    enabledPacks.addAll(packs);
  }

  private void resolveMetaAndAdd(Path each) {
    Path meta = each.resolve("pack.mcmeta");
    if (Files.isRegularFile(meta)) {
      ITextComponent text;
      Boolean old = null;
      try {
        String json = new String(Files.readAllBytes(meta), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonUtils.getJsonObject(new JsonParser().parse(json), "");
        JsonObject packObject = JsonUtils.getJsonObject(jsonObject, "pack");
        old = JsonUtils.getBoolean(packObject, "old");
        String desc = JsonUtils.getString(packObject, "description");
        text = new TextComponentString(desc);
      } catch (IOException | IllegalStateException | JsonSyntaxException | UncheckedIOException ex) {
        LOGGER.error("Error reading pack mcmeta for directory {}", each.getFileName(), ex);
        text = new TextComponentString("");
      }
      packs.add(new FileDataPack(each, text, old == null ? detectFormat(each) : old ? PackFormat.TYPE_NAMESPACE : PackFormat.NAMESPACE_TYPE));
    } else {
      packs.add(new FileDataPack(each, new TextComponentString(""), detectFormat(each)));
    }
  }

  // data/modid/type or data/type/modid
  private PackFormat detectFormat(Path root) {
    Path data = root.resolve("data");
    if (Files.notExists(data)) {
      return PackFormat.TYPE_NAMESPACE;
    }

    boolean typeNamespacePossible = false;
    boolean namespaceTypePossible = false;

    try (Stream<Path> stream = Files.list(data)) {
      typeNamespacePossible = stream.filter(Files::isDirectory).map(Path::toString).anyMatch(testTypes::contains);
    } catch (IOException | UncheckedIOException ignored) {
    }

    try (Stream<Path> stream = Files.list(data)) {
      namespaceTypePossible = stream.filter(Files::isDirectory).flatMap(file -> {
        try {
          return Files.list(file);
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      }).map(Path::getFileName).map(Path::toString).anyMatch(testTypes::contains);
    } catch (IOException | UncheckedIOException ignored) {
    }

    if (typeNamespacePossible != namespaceTypePossible) {
      if (typeNamespacePossible) {
        return PackFormat.TYPE_NAMESPACE;
      } else {
        return PackFormat.NAMESPACE_TYPE;
      }
    } else {
      return PackFormat.UNSURE;
    }
  }
}
