package com.github.liachmodded.datapacks.loading;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("com.github.liachmodded.datapacks.loading")
@IFMLLoadingPlugin.Name("Data Packs Core Mod")
public class DataPacksCore implements IFMLLoadingPlugin {

  static boolean searge = false;

  public DataPacksCore() {
    System.out.println("Datapacks core loaded");
  }

  @Override
  public String[] getASMTransformerClass() {
    return new String[] {
        "com.github.liachmodded.datapacks.loading.AdvancementsTransformer",
        "com.github.liachmodded.datapacks.loading.LootTablesTransformer",
        "com.github.liachmodded.datapacks.loading.FunctionsTransformer"
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
    System.setProperty("datapacks.gamedirectory", mcDir.getAbsolutePath());
  }

  @Override
  public String getAccessTransformerClass() {
    return null;
  }
}
