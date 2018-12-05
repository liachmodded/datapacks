/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.datapacks.loading;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("com.github.liachmodded.datapacks.loading")
@IFMLLoadingPlugin.Name("Data Packs Core Mod")
public class DataPacksCore implements IFMLLoadingPlugin {

  public static final Logger LOGGER = LogManager.getLogger("Data Packs Core Mod");
  static boolean searge = false;

  public DataPacksCore() {
    LOGGER.info("Datapacks core loaded");
  }

  @Override
  public String[] getASMTransformerClass() {
    return new String[] {
        "com.github.liachmodded.datapacks.loading.AdvancementsTransformer",
        "com.github.liachmodded.datapacks.loading.LootTablesTransformer",
        "com.github.liachmodded.datapacks.loading.FunctionsTransformer",
        "com.github.liachmodded.datapacks.loading.MinecraftServerTransformer"
    };
  }

  @Override
  public String getModContainerClass() {
    return null;
  }

  @Nullable
  @Override
  public String getSetupClass() {
    return null;
  }

  @Override
  public void injectData(Map<String, Object> data) {
    DataPacksCore.searge = (Boolean) data.get("runtimeDeobfuscationEnabled");
    File mcDir = (File) data.get("mcLocation");
    System.setProperty("datapacks.gamedirectory", mcDir.toPath().toAbsolutePath().toString());
  }

  @Override
  public String getAccessTransformerClass() {
    return null;
  }
}
