package com.github.liachmodded.datapacks.command;

import com.github.liachmodded.datapacks.IDataPackManager;

/**
 *
 */
public class DataPackCommand extends DispatchingCommand {

  public DataPackCommand(IDataPackManager manager) {
    addSubCommand(new DisableCommand(manager));
    addSubCommand(new ListCommand(manager));
    addSubCommand(new EnableCommand(manager));
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 3;
  }

  @Override
  public String getName() {
    return "datapack";
  }
}
