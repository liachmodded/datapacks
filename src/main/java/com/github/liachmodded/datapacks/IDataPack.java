package com.github.liachmodded.datapacks;

import net.minecraft.util.text.ITextComponent;

import java.util.Set;

/**
 *
 */
public interface IDataPack {

  IData get(String type);

  ITextComponent getDescription();

  Set<String> getDomains();

  String getName();
}
