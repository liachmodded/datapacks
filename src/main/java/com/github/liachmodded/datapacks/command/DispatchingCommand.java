package com.github.liachmodded.datapacks.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class DispatchingCommand extends BaseElementalCommand {

  protected final Map<String, IElementalCommand> subCommands = new HashMap<>();

  @Override
  public abstract String getName();

  protected void addSubCommand(IElementalCommand command) {
    subCommands.put(command.getName(), command);
    command.setParentName(fullName);
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if (args.length == 0) {
      onWrongUsage(sender);
    }
    String subName = args[0];
    ICommand command = subCommands.get(subName);
    if (command == null) {
      onWrongUsage(sender);
      return;
    }
    String[] newArgs = ArrayUtils.remove(args, 0);
    command.execute(server, sender, newArgs);
  }

  @Override
  protected void onWrongUsage(ICommandSender sender) throws WrongUsageException {
    printSubCommands(sender);
    super.onWrongUsage(sender);
  }

  private void printSubCommands(ICommandSender sender) {
    sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.info", fullName, getDescription()));
    sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.begin"));
    for (Map.Entry<String, IElementalCommand> entry : subCommands.entrySet()) {
      sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.entry", entry.getKey(), entry.getValue()));
    }
    sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.end"));
  }
}
