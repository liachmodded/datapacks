package com.github.liachmodded.datapacks.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 *
 */
public abstract class BaseElementalCommand extends CommandBase implements IElementalCommand {

  protected String fullName = getName();
  private ITextComponent description = new TextComponentTranslation("command.datapacks." + getName() + ".desc");

  @Override
  public abstract String getName();

  @Override
  public String getUsage(ICommandSender sender) {
    return "commands.datapacks." + fullName.replace(' ', '.') + ".usage";
  }

  @Override
  public abstract void execute(MinecraftServer server, ICommandSender sender, String... args) throws CommandException;

  @Override
  public ITextComponent getDescription() {
    return description;
  }

  @Override
  public void setParentName(String call) {
    this.fullName = call.isEmpty() ? getName() : call + ' ' + getName();
    this.description = new TextComponentTranslation("command.datapacks." + fullName.replace(' ', '.') + ".desc");
  }

  protected void onWrongUsage(ICommandSender sender) throws WrongUsageException {
    throw new WrongUsageException(getUsage(sender));
  }

}
