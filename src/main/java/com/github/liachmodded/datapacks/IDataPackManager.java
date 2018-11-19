package com.github.liachmodded.datapacks;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 *
 */
public interface IDataPackManager {

  void addDataType(String type);

  Set<IDataPack> getAll();

  @Nullable
  IDataPack getByName(String name);

  List<IDataPack> getEnabled();

  void disable(IDataPack pack);

  void rank(int index, IDataPack pack);

  void putFirst(IDataPack pack);

  void putBefore(IDataPack pack, IDataPack old);

  void putAfter(IDataPack pack, IDataPack old);

  void putLast(IDataPack pack);

  void rescan();

  void saveOrder();
}
