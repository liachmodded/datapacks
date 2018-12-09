/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.datapacks.command;

import com.github.liachmodded.datapacks.IDataPack;
import com.github.liachmodded.datapacks.IDataPackManager;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

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
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, manager.getEnabled()
          .stream()
          .map(IDataPack::getName)
          .sorted()
          .collect(Collectors.toList())
      );
    }
    return super.getTabCompletions(server, sender, args, targetPos);
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

    notifyCommandListener(sender, this, "command.datapacks.datapack.disable.success", packName);
  }
}
