package com.github.liachmodded.datapacks;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Manages data packs in addition to providing them.
 */
public interface IDataPackManager extends IDataPackProvider {

  List<IDataPack> getEnabled();

  void disable(IDataPack pack);

  void rank(int index, IDataPack pack);

  void putFirst(IDataPack pack);

  void putBefore(IDataPack pack, IDataPack old);

  void putAfter(IDataPack pack, IDataPack old);

  void putLast(IDataPack pack);

  void saveOrder();
}
