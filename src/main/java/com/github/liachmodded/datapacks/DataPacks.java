package com.github.liachmodded.datapacks;

import com.google.gson.Gson;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.FunctionObject;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Main mod class.
 */
@Mod(
    modid = DataPacks.ID,
    name = "Data Packs Helper",
    useMetadata = true,
    acceptedMinecraftVersions = "[1.12.2, 1.13)",
    acceptableRemoteVersions = "*"
)
public final class DataPacks {
  public static final String ID = "datapacks";
  private static final DataPacks INSTANCE = new DataPacks();
  private final File mcDir;
  private boolean hasInit = false;
  private DataPackManager manager;

  public static ResourceLocation locate(String path) {
    return new ResourceLocation(ID, path);
  }

  @Mod.InstanceFactory
  public static DataPacks getInstance() {
    return INSTANCE;
  }

  private DataPacks() {
    mcDir = new File(System.getProperty("datapacks.gamedirectory"));
    System.clearProperty("datapacks.gamedirectory");
  }

  @Nullable
  public LootTable getLootTable(LootTableManager manager, ResourceLocation location, Gson gson) {
    if (!hasInit) {
      return null; // Logger debug
    }
    return this.manager.getEnabled()
        .stream()
        .map(pack -> {
          try {
            return pack.get("loot_tables").getContent(location);
          } catch (IOException ex) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .findFirst()
        .map(st -> ForgeHooks.loadLootTable(gson, location, st, true, manager))
        .orElse(null);
  }

  public void fillAdvancements(Map<ResourceLocation, Advancement.Builder> toFill) {
    manager.getEnabled()
        .forEach(pack -> pack.get("advancements").forEachContent("json", (location, content) -> {
              System.out.println("Loading advancement at " + location);
              toFill.computeIfAbsent(location, loc -> JsonUtils.fromJson(AdvancementManager.GSON, content.get(), Advancement.Builder.class, false));
            }
            )
        );
  }

  public void fillFunctions(FunctionManager functionManager, Map<ResourceLocation, FunctionObject> toFill) {
    manager.getEnabled()
        .forEach(pack -> pack.get("functions").forEachContent("mcfunction", (location, content) -> {
              if (!location.getPath().isEmpty()) {
                toFill.computeIfAbsent(location, loc -> FunctionObject.create(functionManager, Arrays.asList(content.get().split("\\r?\\n"))));
              }
            }
            )
        );
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    manager = new DataPackManager(new File(mcDir, "datapacks"));
    hasInit = true;
  }

  @Mod.EventHandler
  public void beforeServerStart(FMLServerAboutToStartEvent event) {
    manager.rescan();
  }

  @Mod.EventHandler
  public void onServerStart(FMLServerStartingEvent event) {

  }
}
