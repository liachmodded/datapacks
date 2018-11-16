package com.github.liachmodded.datapacks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class DataPackManager {
  private final File directory;
  private final Set<IDataPack> packs = new HashSet<>();
  private final List<IDataPack> enabledPacks = new ArrayList<>();
  private static final FileFilter FILTER = file -> {
//    boolean flag = file.isFile() && file.getName().endsWith(".zip");
    return file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
  };
  private static final Logger LOGGER = LogManager.getLogger();

  DataPackManager(File directory) {
    this.directory = directory;
    if (!directory.exists()) {
      if (!directory.mkdirs()) {
        LOGGER.warn("Cannot create {0} directory", directory.getName());
      }
    }
  }

  public Set<IDataPack> getAll() {
    return packs;
  }

  public List<IDataPack> getEnabled() {
    return enabledPacks;
  }

  public void disable(IDataPack pack) {
    enabledPacks.remove(pack);
  }

  public void rank(int index, IDataPack pack) {
    enabledPacks.remove(pack);
    enabledPacks.add(index, pack);
  }

  void rescan() {
    enabledPacks.clear();
    packs.clear();
    File[] sub = directory.listFiles(FILTER);

    if (sub == null) {
      return;
    }

    for (File each : sub) {
      if (each.isDirectory()) {
        File meta = new File(each, "pack.mcmeta");
        if (meta.exists() && meta.isFile()) {
          ITextComponent text;
          boolean old = false;
          try {
            String json = FileUtils.readFileToString(meta, StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonUtils.getJsonObject(new JsonParser().parse(json), "");
            JsonObject packObject = JsonUtils.getJsonObject(jsonObject, "pack");
            old = JsonUtils.getBoolean(packObject, "old");
            String desc = JsonUtils.getString(packObject, "description");
            text = new TextComponentString(desc);
          } catch (IOException | IllegalStateException | JsonSyntaxException ex) {
            FMLLog.log.info("Error reading pack mcmeta", ex);
            text = new TextComponentString("");
          }
          packs.add(new FileDataPack(each, text, old));
        }
      } else if (each.isFile()) {
        // TODO zip
      }
    }

    enabledPacks.addAll(packs);
  }
}
