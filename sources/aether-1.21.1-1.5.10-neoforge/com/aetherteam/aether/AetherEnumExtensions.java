package com.aetherteam.aether;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.item.AetherItems;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class AetherEnumExtensions {
   public static final EnumProxy<Rarity> AETHER_LOOT_RARITY_PROXY = new EnumProxy(Rarity.class, new Object[]{-1, "aether:loot", ChatFormatting.GREEN});

   public static Object skyrootBoatType(int idx, Class<?> type) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: invalid constant type: Ljava/lang/Object; with value aether:skyroot
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ConstExprent.toJava(ConstExprent.java:356)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.SwitchExprent.toJava(SwitchExprent.java:152)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1014)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1153)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:904)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1014)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent.toJava(ExitExprent.java:86)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:891)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:91)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:829)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:829)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:258)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1306)
      //
      // Bytecode:
      // 00: iload 0
      // 01: bipush 5
      // 02: if_icmpne 0a
      // 05: bipush 0
      // 06: invokestatic java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;
      // 09: areturn
      // 0a: aload 1
      // 0b: iload 0
      // 0c: tableswitch 65 0 4 36 42 47 53 59
      // 30: getstatic com/aetherteam/aether/block/AetherBlocks.SKYROOT_PLANKS Lnet/neoforged/neoforge/registries/DeferredBlock;
      // 33: goto 5b
      // 36: ldc "aether:skyroot"
      // 38: goto 5b
      // 3b: getstatic com/aetherteam/aether/item/AetherItems.SKYROOT_BOAT Lnet/neoforged/neoforge/registries/DeferredItem;
      // 3e: goto 5b
      // 41: getstatic com/aetherteam/aether/item/AetherItems.SKYROOT_CHEST_BOAT Lnet/neoforged/neoforge/registries/DeferredItem;
      // 44: goto 5b
      // 47: getstatic com/aetherteam/aether/item/AetherItems.SKYROOT_STICK Lnet/neoforged/neoforge/registries/DeferredItem;
      // 4a: goto 5b
      // 4d: new java/lang/IllegalArgumentException
      // 50: dup
      // 51: iload 0
      // 52: invokedynamic makeConcatWithConstants (I)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "Unexpected parameter index: \u0001" ]
      // 57: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 5a: athrow
      // 5b: invokevirtual java/lang/Class.cast (Ljava/lang/Object;)Ljava/lang/Object;
      // 5e: areturn
   }

   public static Object enchantingSearchIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack(Items.COMPASS)));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object enchantingFoodIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)AetherItems.ENCHANTED_BERRY.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object enchantingBlocksIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)AetherBlocks.ENCHANTED_GRAVITITE.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object enchantingMiscIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)AetherItems.SKYROOT_REMEDY_BUCKET.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object enchantingRepairIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)AetherItems.ZANITE_PICKAXE.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object freezableSearchIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack(Items.COMPASS)));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object freezableBlocksIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)AetherBlocks.BLUE_AERCLOUD.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object freezableMiscIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)AetherItems.ICE_RING.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object incubationSearchIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack(Items.COMPASS)));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   public static Object incubationMiscIcon(int idx, Class<?> type) {
      switch (idx) {
         case 0:
            return type.cast((Supplier<List<ItemStack>>)() -> List.of(new ItemStack((ItemLike)AetherItems.BLUE_MOA_EGG.get())));
         default:
            throw new IllegalArgumentException("Unexpected parameter index: " + idx);
      }
   }

   private static String prefix(String id) {
      return "aether:" + id;
   }
}
