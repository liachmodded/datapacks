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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

/**
 *
 */
class ListCommand extends BaseElementalCommand {

  private final IDataPackManager manager;

  ListCommand(IDataPackManager manager) {
    this.manager = manager;
  }

  @Override
  public String getName() {
    return "list";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 3;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, "available", "enabled");
    }
    return super.getTabCompletions(server, sender, args, targetPos);
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String... args) throws CommandException {
    if (args.length != 1) {
      onWrongUsage(sender);
    }
    String arg = args[0];
    if (arg.equals("available")) {
      printAbout("command.datapacks.datapack.list.title.available", sender, manager.getAll());
    } else if (arg.equals("enabled")) {
      printAbout("command.datapacks.datapack.list.title.enabled", sender, manager.getEnabled());
    } else {
      onWrongUsage(sender);
    }
  }

  private void printAbout(String beginning, ICommandSender sender, Collection<IDataPack> packs) {
    sender.sendMessage(new TextComponentTranslation(beginning));
    for (IDataPack pack : packs) {
      sender.sendMessage(new TextComponentTranslation("command.datapacks.datapack.list.each", pack.getName()).setStyle(new Style().setHoverEvent(
          new HoverEvent(HoverEvent.Action.SHOW_TEXT, pack.getDescription())
      )));
    }
  }
}
