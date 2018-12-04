package com.github.liachmodded.datapacks;

import com.google.gson.Gson;

import com.github.liachmodded.datapacks.client.Reloader;
import com.github.liachmodded.datapacks.command.DataPackCommand;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.command.FunctionObject;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

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
  public static final Logger LOGGER = LogManager.getLogger(ID);
  private static final DataPacks INSTANCE = new DataPacks();
  private final Path mcDir;
  private boolean hasInit = false;
  private IDataPackManager manager;

  public static ResourceLocation locate(String path) {
    return new ResourceLocation(ID, path);
  }

  @Mod.InstanceFactory
  public static DataPacks getInstance() {
    return INSTANCE;
  }

  private DataPacks() {
    mcDir = FileSystems.getDefault().getPath(System.getProperty("datapacks.gamedirectory"));
    System.clearProperty("datapacks.gamedirectory");
  }

  public IDataPackManager getManager() {
    return manager;
  }

  @Nullable
  public LootTable getLootTable(LootTableManager manager, ResourceLocation location, Gson gson) {
    if (!hasInit) {
      return null; // Logger debug
    }
    return this.manager.getEnabled()
        .stream()
        .flatMap(pack -> {
          if (!pack.get("loot_tables").has(location, "json")) {
            return Stream.empty();
          }
          try {
            return Stream.of(pack.get("loot_tables").getContent(location, "json"));
          } catch (IOException ex) {
            LOGGER.error("Error loading loot table at {}", location, ex);
            return Stream.empty();
          }
        })
        .findFirst()
        .map(st -> ForgeHooks.loadLootTable(gson, location, st, true, manager))
        .orElse(null);
  }

  public void fillAdvancements(Map<ResourceLocation, Advancement.Builder> toFill) {
    manager.getEnabled()
        .forEach(pack -> pack.get("advancements").forEachContent("json", (location, content) -> {
              LOGGER.debug("Loading advancement at " + location);
              toFill.computeIfAbsent(location, loc -> JsonUtils.fromJson(AdvancementManager.GSON, content.get(), Advancement.Builder.class, false));
            })
        );
  }

  public void fillFunctions(FunctionManager functionManager, Map<ResourceLocation, FunctionObject> toFill) {
    manager.getEnabled()
        .forEach(pack -> pack.get("functions").forEachContent("mcfunction", (location, content) -> {
              if (!location.getPath().isEmpty()) {
                toFill.computeIfAbsent(location, loc -> FunctionObject.create(functionManager, Arrays.asList(content.get().split("\\r?\\n"))));
              }
            })
        );
  }

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    manager = new DataPackManager(mcDir.resolve("datapacks"));
    hasInit = true;
    if (FMLCommonHandler.instance().getSide().isClient()) {
      handleClient();
    }
  }

  @SideOnly(Side.CLIENT)
  private void handleClient() {
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new Reloader(manager));
  }

  @Mod.EventHandler
  public void beforeServerStart(FMLServerAboutToStartEvent event) {
    manager.rescan();
  }

  @Mod.EventHandler
  public void onServerStarting(FMLServerStartingEvent event) {
    event.registerServerCommand(new DataPackCommand(manager));
  }
}
