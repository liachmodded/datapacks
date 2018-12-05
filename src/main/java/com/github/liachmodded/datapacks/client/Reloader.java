/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.datapacks.client;

import com.github.liachmodded.datapacks.IDataPackManager;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Predicate;

/**
 * An object to reload data packs on client resource reload.
 */
@SideOnly(Side.CLIENT)
public class Reloader implements ISelectiveResourceReloadListener {

  private final IDataPackManager manager;

  public Reloader(IDataPackManager manager) {
    this.manager = manager;
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
    if (resourcePredicate.test(DataPacksResourceType.DATA)) {
      manager.rescan();
    }
  }
}
