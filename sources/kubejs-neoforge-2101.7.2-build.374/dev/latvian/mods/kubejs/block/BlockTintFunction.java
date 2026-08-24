package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.color.KubeColor;
import dev.latvian.mods.kubejs.color.SimpleColor;
import dev.latvian.mods.kubejs.color.SimpleColorWithAlpha;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BlockTintFunction {
   TypeInfo TYPE_INFO = TypeInfo.of(BlockTintFunction.class);
   BlockTintFunction GRASS = (s, l, p, i) -> new SimpleColor(l != null && p != null ? BiomeColors.getAverageGrassColor(l, p) : GrassColor.get(0.5, 1.0));
   KubeColor DEFAULT_FOLIAGE_COLOR = new SimpleColor(FoliageColor.getDefaultColor());
   BlockTintFunction FOLIAGE = (s, l, p, i) -> (KubeColor)(l != null && p != null
      ? new SimpleColor(BiomeColors.getAverageFoliageColor(l, p))
      : DEFAULT_FOLIAGE_COLOR);
   BlockTintFunction.Fixed EVERGREEN_FOLIAGE = new BlockTintFunction.Fixed(new SimpleColor(FoliageColor.getEvergreenColor()));
   BlockTintFunction.Fixed BIRCH_FOLIAGE = new BlockTintFunction.Fixed(new SimpleColor(FoliageColor.getBirchColor()));
   BlockTintFunction.Fixed MANGROVE_FOLIAGE = new BlockTintFunction.Fixed(new SimpleColor(FoliageColor.getMangroveColor()));
   BlockTintFunction WATER = (s, l, p, i) -> l != null && p != null ? new SimpleColorWithAlpha(BiomeColors.getAverageWaterColor(l, p)) : null;
   KubeColor[] REDSTONE_COLORS = new KubeColor[16];
   BlockTintFunction REDSTONE = (state, level, pos, index) -> {
      if (REDSTONE_COLORS[0] == null) {
         for (int i = 0; i < REDSTONE_COLORS.length; i++) {
            REDSTONE_COLORS[i] = new SimpleColor(RedStoneWireBlock.getColorForPower(i));
         }
      }

      return REDSTONE_COLORS[state.getValue(BlockStateProperties.POWER)];
   };

   KubeColor getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index);

   @Nullable
   static BlockTintFunction wrap(Context cx, Object o) {
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
      // 006: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ dev/latvian/mods/rhino/Undefined, dev/latvian/mods/rhino/Scriptable, dev/latvian/mods/kubejs/block/BlockTintFunction, java/util/List, java/lang/CharSequence, dev/latvian/mods/rhino/BaseFunction ]
      // 00b: tableswitch 486 -1 5 41 45 55 78 89 165 465
      // 034: aconst_null
      // 035: goto 1ff
      // 038: aload 2
      // 039: checkcast dev/latvian/mods/rhino/Undefined
      // 03c: astore 4
      // 03e: aconst_null
      // 03f: goto 1ff
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
      // 056: goto 1ff
      // 059: aload 2
      // 05a: checkcast dev/latvian/mods/kubejs/block/BlockTintFunction
      // 05d: astore 6
      // 05f: aload 6
      // 061: goto 1ff
      // 064: aload 2
      // 065: checkcast java/util/List
      // 068: astore 7
      // 06a: new dev/latvian/mods/kubejs/block/BlockTintFunction$Mapped
      // 06d: dup
      // 06e: invokespecial dev/latvian/mods/kubejs/block/BlockTintFunction$Mapped.<init> ()V
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
      // 08c: invokestatic dev/latvian/mods/kubejs/block/BlockTintFunction.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Ldev/latvian/mods/kubejs/block/BlockTintFunction;
      // 08f: astore 10
      // 091: aload 10
      // 093: ifnull 0a5
      // 096: aload 8
      // 098: getfield dev/latvian/mods/kubejs/block/BlockTintFunction$Mapped.map Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;
      // 09b: iload 9
      // 09d: aload 10
      // 09f: invokeinterface it/unimi/dsi/fastutil/ints/Int2ObjectMap.put (ILjava/lang/Object;)Ljava/lang/Object; 3
      // 0a4: pop
      // 0a5: iinc 9 1
      // 0a8: goto 076
      // 0ab: aload 8
      // 0ad: goto 1ff
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
      // 0c7: lookupswitch 175 7 -1808114061 129 -766840204 161 -683104455 81 -605482838 113 98615734 65 112903447 145 1893530015 97
      // 108: aload 9
      // 10a: ldc "grass"
      // 10c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 10f: ifeq 176
      // 112: bipush 0
      // 113: istore 10
      // 115: goto 176
      // 118: aload 9
      // 11a: ldc "foliage"
      // 11c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 11f: ifeq 176
      // 122: bipush 1
      // 123: istore 10
      // 125: goto 176
      // 128: aload 9
      // 12a: ldc "evergreen_foliage"
      // 12c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 12f: ifeq 176
      // 132: bipush 2
      // 133: istore 10
      // 135: goto 176
      // 138: aload 9
      // 13a: ldc "birch_foliage"
      // 13c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 13f: ifeq 176
      // 142: bipush 3
      // 143: istore 10
      // 145: goto 176
      // 148: aload 9
      // 14a: ldc "mangrove_foliage"
      // 14c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 14f: ifeq 176
      // 152: bipush 4
      // 153: istore 10
      // 155: goto 176
      // 158: aload 9
      // 15a: ldc "water"
      // 15c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 15f: ifeq 176
      // 162: bipush 5
      // 163: istore 10
      // 165: goto 176
      // 168: aload 9
      // 16a: ldc "redstone"
      // 16c: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 16f: ifeq 176
      // 172: bipush 6
      // 174: istore 10
      // 176: iload 10
      // 178: tableswitch 86 0 6 44 50 56 62 68 74 80
      // 1a4: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.GRASS Ldev/latvian/mods/kubejs/block/BlockTintFunction;
      // 1a7: goto 1d9
      // 1aa: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.FOLIAGE Ldev/latvian/mods/kubejs/block/BlockTintFunction;
      // 1ad: goto 1d9
      // 1b0: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.EVERGREEN_FOLIAGE Ldev/latvian/mods/kubejs/block/BlockTintFunction$Fixed;
      // 1b3: goto 1d9
      // 1b6: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.BIRCH_FOLIAGE Ldev/latvian/mods/kubejs/block/BlockTintFunction$Fixed;
      // 1b9: goto 1d9
      // 1bc: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.MANGROVE_FOLIAGE Ldev/latvian/mods/kubejs/block/BlockTintFunction$Fixed;
      // 1bf: goto 1d9
      // 1c2: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.WATER Ldev/latvian/mods/kubejs/block/BlockTintFunction;
      // 1c5: goto 1d9
      // 1c8: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.REDSTONE Ldev/latvian/mods/kubejs/block/BlockTintFunction;
      // 1cb: goto 1d9
      // 1ce: new dev/latvian/mods/kubejs/block/BlockTintFunction$Fixed
      // 1d1: dup
      // 1d2: aload 1
      // 1d3: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ColorWrapper.wrap (Ljava/lang/Object;)Ldev/latvian/mods/kubejs/color/KubeColor;
      // 1d6: invokespecial dev/latvian/mods/kubejs/block/BlockTintFunction$Fixed.<init> (Ldev/latvian/mods/kubejs/color/KubeColor;)V
      // 1d9: goto 1ff
      // 1dc: aload 2
      // 1dd: checkcast dev/latvian/mods/rhino/BaseFunction
      // 1e0: astore 9
      // 1e2: aload 0
      // 1e3: getstatic dev/latvian/mods/kubejs/block/BlockTintFunction.TYPE_INFO Ldev/latvian/mods/rhino/type/TypeInfo;
      // 1e6: aload 9
      // 1e8: invokevirtual dev/latvian/mods/rhino/Context.createInterfaceAdapter (Ldev/latvian/mods/rhino/type/TypeInfo;Ldev/latvian/mods/rhino/ScriptableObject;)Ljava/lang/Object;
      // 1eb: checkcast dev/latvian/mods/kubejs/block/BlockTintFunction
      // 1ee: goto 1ff
      // 1f1: new dev/latvian/mods/kubejs/block/BlockTintFunction$Fixed
      // 1f4: dup
      // 1f5: aload 1
      // 1f6: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/ColorWrapper.wrap (Ljava/lang/Object;)Ldev/latvian/mods/kubejs/color/KubeColor;
      // 1f9: invokespecial dev/latvian/mods/kubejs/block/BlockTintFunction$Fixed.<init> (Ldev/latvian/mods/kubejs/color/KubeColor;)V
      // 1fc: goto 1ff
      // 1ff: areturn
   }

   public record Fixed(KubeColor color) implements BlockTintFunction {
      @Override
      public KubeColor getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
         return this.color;
      }
   }

   public static class Mapped implements BlockTintFunction {
      public final Int2ObjectMap<BlockTintFunction> map = new Int2ObjectArrayMap(1);

      @Override
      public KubeColor getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
         BlockTintFunction f = (BlockTintFunction)this.map.get(index);
         return f == null ? null : f.getColor(state, level, pos, index);
      }
   }
}
