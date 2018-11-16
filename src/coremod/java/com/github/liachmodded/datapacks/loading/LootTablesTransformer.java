package com.github.liachmodded.datapacks.loading;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@SuppressWarnings("unused")
public class LootTablesTransformer implements IClassTransformer {

  @Override
  public byte[] transform(String name, String transformedName, byte[] basicClass) {
    if ("net.minecraft.world.storage.loot.LootTableManager".equals(transformedName)) {
      ClassNode classNode = new ClassNode();
      ClassReader reader = new ClassReader(basicClass);
      reader.accept(classNode, 0);
      for (FieldNode field : classNode.fields) {
        if (field.desc.equals("Lcom/google/gson/Gson;") && field.name.equals(DataPacksCore.searge ? "field_186526_b" : "GSON_INSTANCE")) {
          field.access ^= Opcodes.ACC_PRIVATE;
          break;
        }
      }
      ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
      classNode.accept(writer);
      return writer.toByteArray();
    }
    if (!"net.minecraft.world.storage.loot.LootTableManager$Loader".equals(transformedName)) {
      return basicClass;
    }
    ClassNode classNode = new ClassNode();
    ClassReader reader = new ClassReader(basicClass);
    reader.accept(classNode, 0);

    for (MethodNode methodNode : classNode.methods) {
      if (methodNode.name.equals(DataPacksCore.searge ? "func_186518_c" : "loadBuiltinLootTable")) {
        InsnList list = new InsnList();
        /*
          LootTable v2 = CallHooks.loadMainLootTable(LootTableManager.this, resource);
          if (v2 != null) {
            return v2;
          }
         */
        // local var: loader (0), resource location (1)
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/storage/loot/LootTableManager$Loader", "this$0", "Lnet/minecraft/world/storage/loot/LootTableManager;"));
        // stack: loot table manager
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        // stack: loot table manager, resource location
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/world/storage/loot/LootTableManager", DataPacksCore.searge ? "field_186526_b" : "GSON_INSTANCE", "Lcom/google/gson/Gson;"));
        // stack: loot table manager, resource location, gson
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/github/liachmodded/datapacks/CallHooks", "loadMainLootTable", "(Lnet/minecraft/world/storage/loot/LootTableManager;Lnet/minecraft/util/ResourceLocation;Lcom/google/gson/Gson;)Lnet/minecraft/world/storage/loot/LootTable;", false));
        // stack: loot table
        list.add(new InsnNode(Opcodes.DUP));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNULL, labelNode));
        // stack: loot table
        list.add(new InsnNode(Opcodes.ARETURN));
        list.add(labelNode);
        list.add(new InsnNode(Opcodes.POP));
        // stack:
        methodNode.instructions.insert(list);

        System.out.println("Inserted call to loot table manager loader");
        break;
      }
    }

    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    classNode.accept(writer);
    return writer.toByteArray();
  }
}
