/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.datapacks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * A dummy gui for the mod config. Potential place for sorting data packs.
 */
@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ConfigGui implements IModGuiFactory {
  @Override
  public void initialize(Minecraft minecraftInstance) {
  }

  @Override
  public boolean hasConfigGui() {
    return false;
  }

  @Override
  @Nullable
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return null; // TODO add gui
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
    return Collections.emptySet();
  }
}
