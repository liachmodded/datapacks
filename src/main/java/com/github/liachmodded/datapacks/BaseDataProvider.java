package com.github.liachmodded.datapacks;

import com.google.common.collect.Sets;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

/**
 * An abstract class for data pack providers.
 */
abstract class BaseDataProvider implements IDataPackProvider {
  private static final Logger LOGGER = LogManager.getLogger();
  final Set<String> testTypes = Sets.newHashSet("advancements", "loot_tables", "functions");

  @Override
  public void addDataType(String type) {
    testTypes.add(type);
  }

  // data/modid/type or data/type/modid
  private PackFormat detectFormat(Path root) {
    Path data = root.resolve("data/");
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

  IDataPack resolve(String name, Path each) {
    Path meta = each.resolve("pack.mcmeta");
    if (Files.isRegularFile(meta)) {
      ITextComponent text;
      Boolean old = null; // require old version
      try {
        String json = new String(Files.readAllBytes(meta), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonUtils.getJsonObject(new JsonParser().parse(json), "");
        JsonObject packObject = JsonUtils.getJsonObject(jsonObject, "pack");
        if (JsonUtils.hasField(packObject, "old")) {
          old = JsonUtils.getBoolean(packObject, "old");
        }
        String desc = JsonUtils.getString(packObject, "description");
        text = new TextComponentString(desc);
      } catch (IOException | IllegalStateException | JsonSyntaxException | UncheckedIOException ex) {
        LOGGER.error("Error reading pack mcmeta for pack {}", name, ex);
        text = new TextComponentString(name);
      }
      return new FileDataPack(name, each, text, old == null ? detectFormat(each) : old ? PackFormat.TYPE_NAMESPACE : PackFormat.NAMESPACE_TYPE);
    } else {
      return new FileDataPack(name, each, new TextComponentString(name), detectFormat(each));
    }
  }
}
