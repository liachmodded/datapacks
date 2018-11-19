package com.github.liachmodded.datapacks.command;

import com.github.liachmodded.datapacks.IDataPack;
import com.github.liachmodded.datapacks.IDataPackManager;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 *
 */
class DisableCommand extends BaseElementalCommand {

  private final IDataPackManager manager;

  DisableCommand(IDataPackManager manager) {
    this.manager = manager;
  }

  @Override
  public String getName() {
    return "disable";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 3;
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String... args) throws CommandException {
    if (args.length != 1) {
      onWrongUsage(sender);
    }

    String packName = args[0];
    IDataPack pack = manager.getByName(packName);
    if (pack != null) {
      manager.disable(pack);
    } else {
      throw new MissingDataPackException("command.datapacks.datapack.disable.error", packName);
    }

    manager.saveOrder();
  }
}
