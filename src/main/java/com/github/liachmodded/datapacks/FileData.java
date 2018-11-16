package com.github.liachmodded.datapacks;

import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 *
 */
public class FileData implements IData {

  private final FileDataPack pack;
  private final File typeDirectory;
  private final String key;
  private final boolean old;

  FileData(FileDataPack pack, File typeDirectory, String key, boolean old) {
    this.pack = pack;
    this.typeDirectory = typeDirectory;
    this.key = key;
    this.old = old;
  }

  private File locateDomain(String domain) {
    // 1.12: advancements/minecraft/...
    // 1.13: minecraft/advancements/...
    return new File(typeDirectory, old ?
        key + File.separator + domain + File.separator :
        domain + File.separator + key + File.separator);
  }

  private File locate(ResourceLocation location) {
    return new File(locateDomain(location.getNamespace()), location.getPath().replace('/', File.separatorChar));
  }

  @Override
  public String getContent(ResourceLocation location) throws IOException {
    return FileUtils.readFileToString(locate(location), StandardCharsets.UTF_8);
  }

  @Override
  public boolean has(ResourceLocation location) {
    return locate(location).exists();
  }

  @Override
  public Set<String> getDomains() {
    return pack.getDomains();
  }

  @Override
  public void forEachContent(String suffix, BiConsumer<ResourceLocation, Supplier<String>> consumer) {
    for (String domain : getDomains()) {
      File base = locateDomain(domain);
      if (base.exists()) {
        for (File f : FileUtils.listFiles(base, new String[]{suffix}, true)) {
          consumer.accept(new ResourceLocation(domain, FilenameUtils.removeExtension(base.toPath().relativize(f.toPath()).toString()).replace('\\', '/')), () -> {
            try {
              return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
            } catch (IOException ex) {
              throw new UncheckedIOException(ex);
            }
          });
        }
      }
    }
  }
}
