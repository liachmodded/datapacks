package com.github.liachmodded.datapacks.command;

import net.minecraft.command.ICommand;
import net.minecraft.util.text.ITextComponent;

/**
 *
 */
public interface IElementalCommand extends ICommand {
  ITextComponent getDescription();

  void setParentName(String call);
}
