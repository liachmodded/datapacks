/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.datapacks;

import com.google.common.io.MoreFiles;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Provides data packs.
 */
public interface IDataPackProvider {
  DirectoryStream.Filter<Path> FILTER = path ->
      (Files.isRegularFile(path) && MoreFiles.getFileExtension(path).equals("zip")) ||
          Files.isDirectory(path) && Files.isRegularFile(path.resolve("pack.mcmeta"));

  void addDataType(String type);

  Set<IDataPack> getAll();

  @Nullable
  IDataPack getByName(String name);

  void rescan();
}
