/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.datapacks;

import net.minecraft.util.text.ITextComponent;

import java.util.Set;

/**
 * Represents a data pack, or a set of data to be activated or deactivated together.
 */
public interface IDataPack {

  IData get(String type);

  ITextComponent getDescription();

  Set<String> getDomains();

  String getName();
}
