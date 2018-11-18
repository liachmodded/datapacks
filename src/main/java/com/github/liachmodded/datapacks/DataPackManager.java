package com.github.liachmodded.datapacks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
class DataPackManager implements IDataPackManager {
  private final Path directory;
  private final Set<IDataPack> packs = new HashSet<>();
  private final List<IDataPack> enabledPacks = new ArrayList<>();
  private static final DirectoryStream.Filter<Path> FILTER = path -> {
//    boolean flag = file.isFile() && file.getName().endsWith(".zip");
    return Files.isDirectory(path) && Files.isRegularFile(path.resolve("pack.mcmeta"));
  };
  private static final Logger LOGGER = LogManager.getLogger();

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
          Path meta = each.resolve("pack.mcmeta");
          if (Files.isRegularFile(meta)) {
            ITextComponent text;
            boolean old = false;
            try {
              String json = new String(Files.readAllBytes(meta), StandardCharsets.UTF_8);
              JsonObject jsonObject = JsonUtils.getJsonObject(new JsonParser().parse(json), "");
              JsonObject packObject = JsonUtils.getJsonObject(jsonObject, "pack");
              old = JsonUtils.getBoolean(packObject, "old");
              String desc = JsonUtils.getString(packObject, "description");
              text = new TextComponentString(desc);
            } catch (IOException | IllegalStateException | JsonSyntaxException ex) {
              LOGGER.error("Error reading pack mcmeta for directory {}", each.getFileName(), ex);
              text = new TextComponentString("");
            }
            packs.add(new FileDataPack(each, text, old));
          }
        } else if (Files.isRegularFile(each)) {
          // TODO zip
        }
      }
    } catch (IOException ex) {
      LOGGER.error("Failed to reload data packs", ex);
    }

    enabledPacks.addAll(packs);
  }
}
