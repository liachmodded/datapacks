package com.github.liachmodded.datapacks;

import com.google.gson.Gson;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.FunctionObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * A name hook for the transformer.
 */
@SuppressWarnings("unused")
public final class CallHooks {

  private static final Logger LOGGER = LogManager.getLogger();

  private CallHooks() {
  }

  /**
   * {@link net.minecraft.advancements.FunctionManager#loadFunctions()}
   *
   * @param functionManager the function manager
   * @param functions       the functions map in the function manager
   */
  public static void loadMainFunctions(FunctionManager functionManager, Map<ResourceLocation, FunctionObject> functions) {
    LOGGER.debug("loadMainFunctions called!");
    DataPacks.getInstance().fillFunctions(functionManager, functions);
  }

  /**
   * {@link net.minecraft.advancements.AdvancementManager#loadBuiltInAdvancements(Map)}
   *
   * @param toFill the map to fill
   */
  public static void loadMainAdvancements(Map<ResourceLocation, Advancement.Builder> toFill) {
    LOGGER.debug("loadMainAdvancements called!");
    DataPacks.getInstance().fillAdvancements(toFill);
  }

  /**
   * {@link net.minecraft.world.storage.loot.LootTableManager.Loader#loadBuiltinLootTable(ResourceLocation)}
   *
   * @param manager  the loot table manager
   * @param location the resource location
   * @param gson     the gson
   */
  @Nullable
  public static LootTable loadMainLootTable(LootTableManager manager, ResourceLocation location, Gson gson) {
    LOGGER.debug("loadMainLootTable called!");
    return DataPacks.getInstance().getLootTable(manager, location, gson);
  }

  /**
   * {@link MinecraftServer#reload()}
   * Injected after {@link net.minecraft.server.management.PlayerList#saveAllPlayerData} call.
   * This is called before advancements and loot tables are reloaded.
   *
   * @param minecraftServer the minecraft server instance
   */
  public static void onServerReload(MinecraftServer minecraftServer) {
    LOGGER.debug("onServerReload called!");
    DataPacks.getInstance().getManager().rescan();
  }
}
