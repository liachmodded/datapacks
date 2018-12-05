/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
