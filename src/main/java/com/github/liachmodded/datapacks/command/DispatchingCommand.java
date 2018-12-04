package com.github.liachmodded.datapacks.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 *
 */
public abstract class DispatchingCommand extends BaseElementalCommand {

  protected final Map<String, IElementalCommand> subCommands = new HashMap<>();

  @Override
  public abstract String getName();

  public void addSubCommand(IElementalCommand command) {
    subCommands.put(command.getName(), command);
    command.setParentName(fullName);
    for (String alias : command.getAliases()) {
      subCommands.putIfAbsent(alias, command);
    }
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

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, subCommands.entrySet()
          .stream()
          .filter(entry -> entry.getValue().checkPermission(server, sender))
          .map(Map.Entry::getKey)
          .sorted()
          .collect(Collectors.toList())
      );
    }

    if (args.length > 1) {
      ICommand command = subCommands.get(args[0]);
      if (command == null) {
        return Collections.emptyList();
      }
      String[] newArgs = ArrayUtils.remove(args, 0);
      return command.getTabCompletions(server, sender, newArgs, targetPos);
    }

    return Collections.emptyList();
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {
    if (index <= 0) {
      return false;
    }
    if (args.length > 1) {
      ICommand command = subCommands.get(args[0]);
      if (command == null) {
        return false;
      }
      String[] newArgs = ArrayUtils.remove(args, 0);
      return command.isUsernameIndex(newArgs, index - 1);
    }
    return super.isUsernameIndex(args, index);
  }

  private void printSubCommands(ICommandSender sender) {
    sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.info", fullName, getDescription()));
    sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.begin"));
    for (Map.Entry<String, IElementalCommand> entry : subCommands.entrySet()) {
      sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.entry", entry.getKey(), entry.getValue().getDescription()));
    }
    sender.sendMessage(new TextComponentTranslation("command.datapacks.subcommand.end"));
  }
}
