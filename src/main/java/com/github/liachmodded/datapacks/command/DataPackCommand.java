/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
