package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.MapItemColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemTintFunction {
   TypeInfo TYPE_INFO = TypeInfo.of(ItemTintFunction.class);
   ItemTintFunction BLOCK = (stack, index) -> {
      if (stack.getItem() instanceof BlockItem block) {
         BlockState s = block.getBlock().defaultBlockState();
         BlockBuilder internal = s.getBlock().kjs$getBlockBuilder();
         if (internal != null && internal.tint != null) {
            return internal.tint.getColor(s, null, null, index);
         }
      }

      return null;
   };
   ItemTintFunction POTION = (stack, index) -> {
      PotionContents potion = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
      return potion != null ? new SimpleColor(potion.getColor()) : null;
   };
   ItemTintFunction MAP = (stack, index) -> {
      MapItemColor map = (MapItemColor)stack.get(DataComponents.MAP_COLOR);
      return map != null ? new SimpleColor(map.rgb()) : null;
   };
   ItemTintFunction DISPLAY_COLOR_NBT = (stack, index) -> {
      DyedItemColor color = (DyedItemColor)stack.get(DataComponents.DYED_COLOR);
      return color != null ? new SimpleColor(color.rgb()) : null;
   };

   KubeColor getColor(ItemStack stack, int index);

   @Nullable
   static ItemTintFunction wrap(Context cx, Object o) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 000: aload 1
      // 001: astore 2
      // 002: bipush 0
      // 003: istore 3
      // 004: aload 2
      // 005: iload 3
      // 006: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ dev/latvian/mods/rhino/Undefined, dev/latvian/mods/rhino/Scriptable, dev/latvian/mods/kubejs/item/ItemTintFunction, java/util/List, java/lang/CharSequence, dev/latvian/mods/rhino/BaseFunction ]
      // 00b: tableswitch 380 -1 5 41 45 55 78 89 165 359
      // 034: aconst_null
      // 035: goto 195
      // 038: aload 2
      // 039: checkcast dev/latvian/mods/rhino/Undefined
      // 03c: astore 4
      // 03e: aconst_null
      // 03f: goto 195
      // 042: aload 2
      // 043: checkcast dev/latvian/mods/rhino/Scriptable
      // 046: astore 5
      // 048: aload 5
      // 04a: invokestatic dev/latvian/mods/rhino/Undefined.isUndefined (Ljava/lang/Object;)Z
      // 04d: ifne 055
      // 050: bipush 2
      // 051: istore 3
      // 052: goto 004
      // 055: aconst_null
      // 056: goto 195
      // 059: aload 2
      // 05a: checkcast dev/latvian/mods/kubejs/item/ItemTintFunction
      // 05d: astore 6
      // 05f: aload 6
      // 061: goto 195
      // 064: aload 2
      // 065: checkcast java/util/List
      // 068: astore 7
      // 06a: new dev/latvian/mods/kubejs/item/ItemTintFunction$Mapped
      // 06d: dup
      // 06e: invokespecial dev/latvian/mods/kubejs/item/ItemTintFunction$Mapped.<init> ()V
      // 071: astore 8
      // 073: bipush 0
      // 074: istore 9
      // 076: iload 9
      // 078: aload 7
      // 07a: invokeinterface java/util/List.size ()I 1
      // 07f: if_icmpge 0ab
      // 082: aload 0
      // 083: aload 7
      // 085: iload 9
      // 087: invokeinterface java/util/List.get (I)Ljava/lang/Object; 2
      // 08c: invokestatic dev/latvian/mods/kubejs/item/ItemTintFunction.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Ldev/latvian/mods/kubejs/item/ItemTintFunction;
      // 08f: astore 10
      // 091: aload 10
      // 093: ifnull 0a5
      // 096: aload 8
      // 098: getfield dev/latvian/mods/kubejs/item/ItemTintFunction$Mapped.map Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;
      // 09b: iload 9
      // 09d: aload 10
      // 09f: invokeinterface it/unimi/dsi/fastutil/ints/Int2ObjectMap.put (ILjava/lang/Object;)Ljava/lang/Object; 3
      // 0a4: pop
      // 0a5: iinc 9 1
      // 0a8: goto 076
      // 0ab: aload 8
      // 0ad: goto 195
      // 0b0: aload 2
      // 0b1: checkcast java/lang/CharSequence
      // 0b4: astore 8
      // 0b6: aload 8
      // 0b8: invokeinterface java/lang/CharSequence.toString ()Ljava/lang/String; 1
      // 0bd: astore 9
      // 0bf: bipush -1
      // 0c0: istore 10
      // 0c2: aload 9
      // 0c4: invokevirtual java/lang/String.hashCode ()I
      // 0c7: lookupswitch 102 4 -982431341 57 107868 73 93832333 41 1020208935 89
      // 0f0: aload 9
      // 0f2: ldc "block"
      // 0f4: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0f7: ifeq 12d
      // 0fa: bipush 0
      // 0fb: istore 10
      // 0fd: goto 12d
      // 100: aload 9
      // 102: ldc "potion"
      // 104: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 107: ifeq 12d
      // 10a: bipush 1
      // 10b: istore 10
      // 10d: goto 12d
      // 110: aload 9
      // 112: ldc "map"
      // 114: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 117: ifeq 12d
      // 11a: bipush 2
      // 11b: istore 10
      // 11d: goto 12d
      // 120: aload 9
      // 122: ldc "display_color_nbt"
      // 124: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 127: ifeq 12d
      // 12a: bipush 3
      // 12b: istore 10
      // 12d: iload 10
      // 12f: tableswitch 53 0 3 29 35 41 47
      // 14c: getstatic dev/latvian/mods/kubejs/item/ItemTintFunction.BLOCK Ldev/latvian/mods/kubejs/item/ItemTintFunction;
      // 14f: goto 16f
      // 152: getstatic dev/latvian/mods/kubejs/item/ItemTintFunction.POTION Ldev/latvian/mods/kubejs/item/ItemTintFunction;
      // 155: goto 16f
      // 158: getstatic dev/latvian/mods/kubejs/item/ItemTintFunction.MAP Ldev/latvian/mods/kubejs/item/ItemTintFunction;
      // 15b: goto 16f
      // 15e: getstatic dev/latvian/mods/kubejs/item/ItemTintFunction.DISPLAY_COLOR_NBT Ldev/latvian/mods/kubejs/item/ItemTintFunction;
      // 161: goto 16f
      // 164: new dev/latvian/mods/kubejs/item/ItemTintFunction$Fixed
      // 167: dup
      // 168: aload 1
      // 169: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ColorWrapper.wrap (Ljava/lang/Object;)Ldev/latvian/mods/kubejs/color/KubeColor;
      // 16c: invokespecial dev/latvian/mods/kubejs/item/ItemTintFunction$Fixed.<init> (Ldev/latvian/mods/kubejs/color/KubeColor;)V
      // 16f: goto 195
      // 172: aload 2
      // 173: checkcast dev/latvian/mods/rhino/BaseFunction
      // 176: astore 9
      // 178: aload 0
      // 179: getstatic dev/latvian/mods/kubejs/item/ItemTintFunction.TYPE_INFO Ldev/latvian/mods/rhino/type/TypeInfo;
      // 17c: aload 9
      // 17e: invokevirtual dev/latvian/mods/rhino/Context.createInterfaceAdapter (Ldev/latvian/mods/rhino/type/TypeInfo;Ldev/latvian/mods/rhino/ScriptableObject;)Ljava/lang/Object;
      // 181: checkcast dev/latvian/mods/kubejs/item/ItemTintFunction
      // 184: goto 195
      // 187: new dev/latvian/mods/kubejs/item/ItemTintFunction$Fixed
      // 18a: dup
      // 18b: aload 1
      // 18c: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ColorWrapper.wrap (Ljava/lang/Object;)Ldev/latvian/mods/kubejs/color/KubeColor;
      // 18f: invokespecial dev/latvian/mods/kubejs/item/ItemTintFunction$Fixed.<init> (Ldev/latvian/mods/kubejs/color/KubeColor;)V
      // 192: goto 195
      // 195: areturn
   }

   public record Fixed(KubeColor color) implements ItemTintFunction {
      @Override
      public KubeColor getColor(ItemStack stack, int index) {
         return this.color;
      }
   }

   public static class Mapped implements ItemTintFunction {
      public final Int2ObjectMap<ItemTintFunction> map = new Int2ObjectArrayMap(1);

      @Override
      public KubeColor getColor(ItemStack stack, int index) {
         ItemTintFunction f = (ItemTintFunction)this.map.get(index);
         return f == null ? null : f.getColor(stack, index);
      }
   }
}
