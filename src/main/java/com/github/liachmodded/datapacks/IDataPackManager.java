package com.github.liachmodded.datapacks;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface IDataPackManager {

  Set<IDataPack> getAll();

  List<IDataPack> getEnabled();

  void disable(IDataPack pack);

  void rank(int index, IDataPack pack);

  void rescan();
}
