/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.github.liachmodded.datapacks.loading;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

@SuppressWarnings("unused")
public final class MinecraftServerTransformer implements IClassTransformer {
  @Override
  public byte[] transform(String name, String transformedName, byte[] basicClass) {
    if (!"net.minecraft.server.MinecraftServer".equals(transformedName)) {
      return basicClass;
    }

    ClassNode classNode = new ClassNode();
    ClassReader reader = new ClassReader(basicClass);
    reader.accept(classNode, 0);

    for (MethodNode methodNode : classNode.methods) {
      if (methodNode.name.equals(DataPacksCore.searge ? "func_193031_aM" : "reload")) {
        InsnList list = new InsnList();
        // local var: server
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/github/liachmodded/datapacks/CallHooks", "onServerReload", "(Lnet/minecraft/server/MinecraftServer;)V", false));

        InsnList original = methodNode.instructions;
        for (Iterator<AbstractInsnNode> iterator = original.iterator(); iterator.hasNext(); ) {
          final AbstractInsnNode node = iterator.next();
          if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
            final MethodInsnNode call = (MethodInsnNode) node;
            if ("net/minecraft/server/management/PlayerList".equals(call.owner)
                && (DataPacksCore.searge ? "func_72389_g" : "saveAllPlayerData").equals(call.name)
                && "()V".equals(call.desc)) {
              original.insert(call, list); // Insert right after player list saving player data
              DataPacksCore.LOGGER.info("Inserted reload hook to minecraft server");
            }
          }
        }

        break;
      }
    }

    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    classNode.accept(writer);
    return writer.toByteArray();
  }
}
