package com.github.liachmodded.datapacks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
@SuppressWarnings("unused")
public class ConfigGui implements IModGuiFactory {
  @Override
  public void initialize(Minecraft minecraftInstance) {
  }

  @Override
  public boolean hasConfigGui() {
    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return null;
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
    return Collections.emptySet();
  }
}
