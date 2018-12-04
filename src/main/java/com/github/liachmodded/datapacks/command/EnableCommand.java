package com.github.liachmodded.datapacks.command;

import com.github.liachmodded.datapacks.IDataPack;
import com.github.liachmodded.datapacks.IDataPackManager;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 *
 */
class EnableCommand extends BaseElementalCommand {
  private final IDataPackManager manager;

  EnableCommand(IDataPackManager manager) {
    this.manager = manager;
  }

  @Override
  public String getName() {
    return "enable";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 3;
  }

  @Override
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, manager.getAll()
          .stream()
          .map(IDataPack::getName)
          .sorted()
          .collect(Collectors.toList())
      );
    }
    if (args.length == 2) {
      return getListOfStringsMatchingLastWord(args, "first", "last", "before", "after");
    }
    if (args.length == 3 && ("before".equals(args[1]) || "after".equals(args[1]))) {
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
    if (args.length < 2) {
      onWrongUsage(sender);
      return;
    }
    String packName = args[0];
    String arg = args[1];
    IDataPack pack = manager.getByName(packName);
    if (pack == null) {
      onWrongUsage(sender);
      return;
    }
    if (arg.equals("first") || arg.equals("last")) {
      if (args.length != 2) {
        throw new WrongUsageException("command.datapacks.datapack.enable.usage.extreme");
      }
      if (arg.equals("first")) {
        manager.putFirst(pack);
      } else {
        manager.putLast(pack);
      }
    } else if (arg.equals("before") || arg.equals("after")) {
      if (args.length != 3) {
        throw new WrongUsageException("command.datapacks.datapack.enable.usage.rank");
      }
      IDataPack goal = manager.getByName(args[2]);
      if (goal == null) {
        throw new WrongUsageException("command.datapacks.datapack.enable.usage.rank");
      }
      if (arg.equals("before")) {
        manager.putBefore(pack, goal);
      } else {
        manager.putAfter(pack, goal);
      }
    } else {
      onWrongUsage(sender);
    }

    manager.saveOrder();
  }
}
