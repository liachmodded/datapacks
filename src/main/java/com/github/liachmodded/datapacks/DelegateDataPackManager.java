package com.github.liachmodded.datapacks;

import com.google.common.collect.ImmutableSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
class DelegateDataPackManager implements IDataPackManager {
  private static final Logger LOGGER = LogManager.getLogger();
  // TODO better way to store disabled status, etc.
  private static final String DISABLED_SEPARATOR = ":::DISABLED:::";
  private Path orderFile;
  private final List<IDataPack> enabledPacks = new ArrayList<>();

  private final List<IDataPackProvider> providers = new ArrayList<>();
  private Set<IDataPack> allCache = null;

  DelegateDataPackManager(Path path, IDataPackProvider... providers) {
    setOrderLocation(path);
    Collections.addAll(this.providers, providers);
  }

  public void setOrderLocation(Path path) {
    this.orderFile = path.resolve("order.txt");
    if (!Files.exists(orderFile)) {
      try {
        Files.createFile(orderFile);
      } catch (IOException ex) {
        LOGGER.error("Cannot create \"{}\" for the data packs helper mod", path.getFileName().toString());
      }
    }
  }

  public List<IDataPackProvider> getProviders() {
    return providers;
  }

  @Override
  public void addDataType(String type) {
    for (IDataPackProvider provider : providers) {
      provider.addDataType(type);
    }
  }

  @Override
  public Set<IDataPack> getAll() {
    if (allCache == null) {
      ImmutableSet.Builder<IDataPack> builder = ImmutableSet.builder();
      for (IDataPackProvider provider : providers) {
        builder.addAll(provider.getAll());
      }
      allCache = builder.build();
    }
    return allCache;
  }

  @Override
  public IDataPack getByName(String name) {
    for (IDataPackProvider provider : providers) {
      IDataPack pack = provider.getByName(name);
      if (pack != null) {
        return pack;
      }
    }
    return null;
  }

  @Override
  public void rescan() {
    allCache = null;
    for (IDataPackProvider provider : providers) {
      provider.rescan();
    }
    enabledPacks.clear();
    loadOrder();
  }

  // Data pack sorting

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
  public void putFirst(IDataPack pack) {
    enabledPacks.remove(pack);
    enabledPacks.add(0, pack);
  }

  @Override
  public void putBefore(IDataPack pack, IDataPack old) {
    int prevIndex = enabledPacks.indexOf(pack);
    int index = enabledPacks.indexOf(old);
    if (prevIndex < 0) {
      enabledPacks.add(index, pack);
    } else {
      if (prevIndex < index) {
        rank(index - 1, pack);
      } else {
        rank(index, pack);
      }
    }
  }

  @Override
  public void putAfter(IDataPack pack, IDataPack old) {
    int prevIndex = enabledPacks.indexOf(pack);
    int index = enabledPacks.indexOf(old);
    if (prevIndex < 0) {
      enabledPacks.add(index + 1, pack);
    } else {
      if (prevIndex > index) {
        rank(index + 1, pack);
      } else {
        rank(index, pack);
      }
    }
  }

  @Override
  public void putLast(IDataPack pack) {
    enabledPacks.remove(pack);
    enabledPacks.add(pack);
  }

  @Override
  public void saveOrder() {
    List<String> lines = new ArrayList<>();
    Set<IDataPack> disabled = new HashSet<>(getAll());
    for (IDataPack pack : enabledPacks) {
      lines.add(pack.getName());
      disabled.remove(pack);
    }
    lines.add(DISABLED_SEPARATOR);
    for (IDataPack each : disabled) {
      lines.add(each.getName());
    }

    try {
      Files.write(orderFile, lines, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      LOGGER.error("Cannot save the load order and choice of data packs", ex);
    }
  }

  private void loadOrder() {
    enabledPacks.addAll(getAll());
    final List<String> lines;
    try {
      lines = Files.readAllLines(orderFile, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      LOGGER.error("Cannot load the load order and choice of data packs", ex);
      return;
    }

    boolean loadingEnabled = true;
    for (String line : lines) {
      if (DISABLED_SEPARATOR.equals(line)) {
        loadingEnabled = false;
      } else {
        IDataPack pack = getByName(line);
        if (pack != null) {
          if (loadingEnabled) {
            putLast(pack);
          } else {
            enabledPacks.remove(pack);
          }
        }
      }
    }
  }


}
