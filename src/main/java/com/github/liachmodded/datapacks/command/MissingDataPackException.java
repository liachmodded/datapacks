/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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
