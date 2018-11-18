package com.github.liachmodded.datapacks;

import com.google.common.io.MoreFiles;

import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 *
 */
class FileData implements IData {

  private static final Logger LOGGER = LogManager.getLogger();
  private final FileDataPack pack;
  private final Path typeDirectory;
  private final String type;
  private final PackFormat format;

  FileData(FileDataPack pack, Path typeDirectory, String type, PackFormat format) {
    this.pack = pack;
    this.typeDirectory = typeDirectory;
    this.type = type;
    this.format = format;
  }

  private Path locateDomain(String domain) {
    // 1.12: advancements/minecraft/...
    // 1.13: minecraft/advancements/...
    return format.locateDomain(typeDirectory, type, domain);
  }

  private Path path(ResourceLocation location) {
    Path domain = locateDomain(location.getNamespace());
    return domain.resolve(domain.getFileSystem().getPath("", location.getPath().split("/")));
  }

  @Override
  public String getContent(ResourceLocation location) throws IOException {
    return new String(Files.readAllBytes(path(location)), StandardCharsets.UTF_8);
  }

  @Override
  public boolean has(ResourceLocation location) {
    return Files.exists(path(location));
  }

  @Override
  public Set<String> getDomains() {
    return pack.getDomains();
  }

  @Override
  public void forEachContent(String suffix, BiConsumer<ResourceLocation, Supplier<String>> consumer) {
    for (String domain : getDomains()) {
      Path domainPath = locateDomain(domain);
      if (Files.exists(domainPath)) {
        try (Stream<Path> stream = Files.walk(domainPath)) {
          stream.filter(path -> MoreFiles.getFileExtension(path).equals(suffix))
              .forEach(path -> {
                ResourceLocation location = key(domain, domainPath, path);
                try {
                  consumer.accept(location, () -> {
                        try {
                          return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                        } catch (IOException ex) {
                          throw new UncheckedIOException(ex);
                        }
                      }
                  );
                } catch (UncheckedIOException ex) {
                  LOGGER.error("Error retrieving content of content of type {} at {}, skipping", type, location, ex.getCause());
                }
              });
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      }
    }
  }

  private ResourceLocation key(String domain, Path domainPath, Path current) {
    String name = StreamSupport.stream(current.relativize(domainPath).spliterator(), false).map(Path::toString).collect(Collectors.joining("/"));
    return new ResourceLocation(domain, FilenameUtils.removeExtension(name));
  }
}
