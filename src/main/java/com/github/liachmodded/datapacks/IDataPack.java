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
