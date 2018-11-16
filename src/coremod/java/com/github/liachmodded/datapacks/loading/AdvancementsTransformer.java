package com.github.liachmodded.datapacks.loading;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

@SuppressWarnings("unused")
public class AdvancementsTransformer implements IClassTransformer {

  @Override
  public byte[] transform(String name, String transformedName, byte[] basicClass) {
    if (!"net.minecraft.advancements.AdvancementManager".equals(transformedName)) {
      return basicClass;
    }
    ClassNode classNode = new ClassNode();
    ClassReader reader = new ClassReader(basicClass);
    reader.accept(classNode, 0);

    for (MethodNode methodNode : classNode.methods) {
      if (methodNode.name.equals(DataPacksCore.searge ? "func_192777_a" : "loadBuiltInAdvancements")) {
        InsnList list = new InsnList();
        // local var: manager, map
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/github/liachmodded/datapacks/CallHooks", "loadMainAdvancements", "(Ljava/util/Map;)V", false));
        methodNode.instructions.insert(list);

        System.out.println("Inserted call to advancement manager");
        break;
      }
    }

    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    classNode.accept(writer);
    return writer.toByteArray();
  }
}
