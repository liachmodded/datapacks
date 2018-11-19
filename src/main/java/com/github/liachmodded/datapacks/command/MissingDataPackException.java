package com.github.liachmodded.datapacks.command;

import net.minecraft.command.CommandException;

/**
 *
 */
public class MissingDataPackException extends CommandException {
  public MissingDataPackException(String message, Object... objects) {
    super(message, objects);
  }
}
