package dev.latvian.mods.kubejs.block.state;

import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.level.ruletest.AllMatchRuleTest;
import dev.latvian.mods.kubejs.level.ruletest.AlwaysFalseRuleTest;
import dev.latvian.mods.kubejs.level.ruletest.AnyMatchRuleTest;
import dev.latvian.mods.kubejs.level.ruletest.InvertRuleTest;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NBTWrapper;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatch;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.RegExpKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.Nullable;

public sealed interface BlockStatePredicate
   extends Predicate<BlockState>,
   ReplacementMatch
   permits BlockStatePredicate.Simple,
   BlockStatePredicate.BlockMatch,
   BlockStatePredicate.StateMatch,
   BlockStatePredicate.TagMatch,
   BlockStatePredicate.RegexMatch,
   BlockStatePredicate.OrMatch,
   BlockStatePredicate.NotMatch,
   BlockStatePredicate.AndMatch {
   boolean test(BlockState state);

   default boolean testBlock(Block block) {
      return this.test(block.defaultBlockState());
   }

   @Nullable
   default RuleTest asRuleTest() {
      return null;
   }

   static BlockStatePredicate fromString(Context cx, String s) {
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
      // 00: aload 1
      // 01: dup
      // 02: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 05: pop
      // 06: astore 2
      // 07: bipush 0
      // 08: istore 3
      // 09: aload 2
      // 0a: iload 3
      // 0b: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "*", "-", java/lang/String, java/lang/String ]
      // 10: tableswitch 143 0 3 32 38 44 84
      // 30: getstatic dev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple.ALL Ldev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple;
      // 33: goto ca
      // 36: getstatic dev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple.NONE Ldev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple;
      // 39: goto ca
      // 3c: aload 2
      // 3d: astore 4
      // 3f: aload 4
      // 41: ldc "#"
      // 43: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 46: ifne 4e
      // 49: bipush 3
      // 4a: istore 3
      // 4b: goto 09
      // 4e: new dev/latvian/mods/kubejs/block/state/BlockStatePredicate$TagMatch
      // 51: dup
      // 52: aload 4
      // 54: bipush 1
      // 55: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 58: invokestatic net/minecraft/resources/ResourceLocation.parse (Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;
      // 5b: invokestatic dev/latvian/mods/kubejs/util/Tags.block (Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey;
      // 5e: invokespecial dev/latvian/mods/kubejs/block/state/BlockStatePredicate$TagMatch.<init> (Lnet/minecraft/tags/TagKey;)V
      // 61: goto ca
      // 64: aload 2
      // 65: astore 5
      // 67: aload 5
      // 69: bipush 91
      // 6b: invokevirtual java/lang/String.indexOf (I)I
      // 6e: bipush -1
      // 6f: if_icmpne 77
      // 72: bipush 4
      // 73: istore 3
      // 74: goto 09
      // 77: aload 0
      // 78: invokestatic dev/latvian/mods/kubejs/util/RegistryAccessContainer.of (Ldev/latvian/mods/rhino/Context;)Ldev/latvian/mods/kubejs/util/RegistryAccessContainer;
      // 7b: aload 5
      // 7d: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/BlockWrapper.parseBlockState (Ldev/latvian/mods/kubejs/util/RegistryAccessContainer;Ljava/lang/String;)Lnet/minecraft/world/level/block/state/BlockState;
      // 80: astore 6
      // 82: aload 6
      // 84: getstatic net/minecraft/world/level/block/Blocks.AIR Lnet/minecraft/world/level/block/Block;
      // 87: invokevirtual net/minecraft/world/level/block/Block.defaultBlockState ()Lnet/minecraft/world/level/block/state/BlockState;
      // 8a: if_acmpeq 99
      // 8d: new dev/latvian/mods/kubejs/block/state/BlockStatePredicate$StateMatch
      // 90: dup
      // 91: aload 6
      // 93: invokespecial dev/latvian/mods/kubejs/block/state/BlockStatePredicate$StateMatch.<init> (Lnet/minecraft/world/level/block/state/BlockState;)V
      // 96: goto ca
      // 99: getstatic dev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple.NONE Ldev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple;
      // 9c: goto ca
      // 9f: getstatic net/minecraft/core/registries/BuiltInRegistries.BLOCK Lnet/minecraft/core/DefaultedRegistry;
      // a2: aload 1
      // a3: invokestatic net/minecraft/resources/ResourceLocation.parse (Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;
      // a6: invokeinterface net/minecraft/core/DefaultedRegistry.get (Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object; 2
      // ab: checkcast net/minecraft/world/level/block/Block
      // ae: astore 6
      // b0: aload 6
      // b2: getstatic net/minecraft/world/level/block/Blocks.AIR Lnet/minecraft/world/level/block/Block;
      // b5: if_acmpeq c4
      // b8: new dev/latvian/mods/kubejs/block/state/BlockStatePredicate$BlockMatch
      // bb: dup
      // bc: aload 6
      // be: invokespecial dev/latvian/mods/kubejs/block/state/BlockStatePredicate$BlockMatch.<init> (Lnet/minecraft/world/level/block/Block;)V
      // c1: goto ca
      // c4: getstatic dev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple.NONE Ldev/latvian/mods/kubejs/block/state/BlockStatePredicate$Simple;
      // c7: goto ca
      // ca: areturn
   }

   static BlockStatePredicate wrap(Context cx, Object o) {
      if (o == null || o == BlockStatePredicate.Simple.ALL) {
         return BlockStatePredicate.Simple.ALL;
      } else if (o == BlockStatePredicate.Simple.NONE) {
         return BlockStatePredicate.Simple.NONE;
      } else {
         List<?> list = ListJS.orSelf(o);
         if (list.isEmpty()) {
            return BlockStatePredicate.Simple.NONE;
         } else if (list.size() > 1) {
            ArrayList<BlockStatePredicate> predicates = new ArrayList<>();

            for (Object o1 : list) {
               BlockStatePredicate p = wrap(cx, o1);
               if (p == BlockStatePredicate.Simple.ALL) {
                  return BlockStatePredicate.Simple.ALL;
               }

               if (p != BlockStatePredicate.Simple.NONE) {
                  predicates.add(p);
               }
            }

            return (BlockStatePredicate)(predicates.isEmpty()
               ? BlockStatePredicate.Simple.NONE
               : (predicates.size() == 1 ? (BlockStatePredicate)predicates.getFirst() : new BlockStatePredicate.OrMatch(predicates)));
         } else {
            Object first = list.getFirst();
            Map<String, Object> map = cx.optionalMapOf(first);
            if (map == null) {
               return ofSingle(cx, first);
            } else if (map.isEmpty()) {
               return BlockStatePredicate.Simple.ALL;
            } else {
               ArrayList<BlockStatePredicate> predicates = new ArrayList<>();
               if (map.get("or") != null) {
                  predicates.add(wrap(cx, map.get("or")));
               }

               if (map.get("not") != null) {
                  predicates.add(new BlockStatePredicate.NotMatch(wrap(cx, map.get("not"))));
               }

               return new BlockStatePredicate.AndMatch(predicates);
            }
         }
      }
   }

   static RuleTest wrapRuleTest(Context cx, Object o) {
      RegistryOps<Tag> nbt = RegistryAccessContainer.of(cx).nbt();
      Objects.requireNonNull(o);

      return switch (o) {
         case RuleTest rule -> rule;
         case BlockStatePredicate bsp when bsp.asRuleTest() != null -> bsp.asRuleTest();
         default -> (RuleTest)Optional.ofNullable(NBTWrapper.wrapCompound(cx, o))
            .map(tag -> RuleTest.CODEC.parse(nbt, tag))
            .<RuleTest>flatMap(DataResult::result)
            .or(() -> Optional.ofNullable(wrap(cx, o).asRuleTest()))
            .orElseThrow(() -> new KubeRuntimeException("Could not parse valid rule test from %s!".formatted(o)).source(SourceLine.of(cx)));
      };
   }

   private static BlockStatePredicate ofSingle(Context cx, Object o) {
      return (BlockStatePredicate)(switch (o) {
         case BlockStatePredicate bsp -> bsp;
         case Block block -> new BlockStatePredicate.BlockMatch(block);
         case BlockState state -> new BlockStatePredicate.StateMatch(state);
         case TagKey tag -> new BlockStatePredicate.TagMatch(tag);
         default -> {
            Pattern pattern = RegExpKJS.wrap(o);
            yield pattern == null ? fromString(cx, o.toString()) : new BlockStatePredicate.RegexMatch(pattern);
         }
      });
   }

   default Collection<BlockState> getBlockStates() {
      HashSet<BlockState> states = new HashSet<>();

      for (BlockState state : BlockWrapper.getAllBlockStates()) {
         if (this.test(state)) {
            states.add(state);
         }
      }

      return states;
   }

   default Collection<Block> getBlocks() {
      HashSet<Block> blocks = new HashSet<>();

      for (BlockState state : this.getBlockStates()) {
         blocks.add(state.getBlock());
      }

      return blocks;
   }

   default Set<ResourceLocation> getBlockIds() {
      Set<ResourceLocation> set = new LinkedHashSet<>();

      for (Block block : this.getBlocks()) {
         set.add(block.kjs$getIdLocation());
      }

      return set;
   }

   default boolean check(List<TargetBlockState> targetStates) {
      for (TargetBlockState state : targetStates) {
         if (this.test(state.state)) {
            return true;
         }
      }

      return false;
   }

   public static final class AndMatch implements BlockStatePredicate {
      private final List<BlockStatePredicate> list;
      private final Collection<BlockState> cachedStates;

      public AndMatch(List<BlockStatePredicate> list) {
         this.list = list;
         this.cachedStates = new LinkedHashSet<>();

         for (Block block : BuiltInRegistries.BLOCK) {
            UnmodifiableIterator var4 = block.getStateDefinition().getPossibleStates().iterator();

            while (var4.hasNext()) {
               BlockState state = (BlockState)var4.next();
               boolean match = true;

               for (BlockStatePredicate predicate : list) {
                  if (!predicate.test(state)) {
                     match = false;
                     break;
                  }
               }

               if (match) {
                  this.cachedStates.add(state);
               }
            }
         }
      }

      @Override
      public boolean test(BlockState state) {
         for (BlockStatePredicate predicate : this.list) {
            if (!predicate.test(state)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean testBlock(Block block) {
         for (BlockStatePredicate predicate : this.list) {
            if (!predicate.testBlock(block)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public Collection<Block> getBlocks() {
         Set<Block> set = new HashSet<>();

         for (BlockState blockState : this.getBlockStates()) {
            set.add(blockState.getBlock());
         }

         return set;
      }

      @Override
      public Collection<BlockState> getBlockStates() {
         return this.cachedStates;
      }

      @Override
      public RuleTest asRuleTest() {
         AllMatchRuleTest test = new AllMatchRuleTest();

         for (BlockStatePredicate predicate : this.list) {
            test.rules.add(predicate.asRuleTest());
         }

         return test;
      }
   }

   public record BlockMatch(Block block) implements BlockStatePredicate {
      @Override
      public boolean test(BlockState state) {
         return state.is(this.block);
      }

      @Override
      public boolean testBlock(Block block) {
         return this.block == block;
      }

      @Override
      public Collection<Block> getBlocks() {
         return Collections.singleton(this.block);
      }

      @Override
      public Collection<BlockState> getBlockStates() {
         return this.block.getStateDefinition().getPossibleStates();
      }

      @Override
      public Set<ResourceLocation> getBlockIds() {
         return Set.of(this.block.kjs$getIdLocation());
      }

      @Override
      public RuleTest asRuleTest() {
         return new BlockMatchTest(this.block);
      }
   }

   public static final class NotMatch implements BlockStatePredicate {
      private final BlockStatePredicate predicate;
      private final Collection<BlockState> cachedStates;

      public NotMatch(BlockStatePredicate predicate) {
         this.predicate = predicate;
         this.cachedStates = new LinkedHashSet<>();

         for (Block block : BuiltInRegistries.BLOCK) {
            UnmodifiableIterator var4 = block.getStateDefinition().getPossibleStates().iterator();

            while (var4.hasNext()) {
               BlockState state = (BlockState)var4.next();
               if (!predicate.test(state)) {
                  this.cachedStates.add(state);
               }
            }
         }
      }

      @Override
      public boolean test(BlockState state) {
         return !this.predicate.test(state);
      }

      @Override
      public boolean testBlock(Block block) {
         return !this.predicate.testBlock(block);
      }

      @Override
      public Collection<Block> getBlocks() {
         Set<Block> set = new HashSet<>();

         for (BlockState blockState : this.getBlockStates()) {
            set.add(blockState.getBlock());
         }

         return set;
      }

      @Override
      public Collection<BlockState> getBlockStates() {
         return this.cachedStates;
      }

      @Override
      public Set<ResourceLocation> getBlockIds() {
         HashSet<ResourceLocation> set = new HashSet<>();

         for (Block block : this.getBlocks()) {
            set.add(block.kjs$getIdLocation());
         }

         return set;
      }

      @Override
      public RuleTest asRuleTest() {
         return new InvertRuleTest(this.predicate.asRuleTest());
      }
   }

   public record OrMatch(List<BlockStatePredicate> list) implements BlockStatePredicate {
      @Override
      public boolean test(BlockState state) {
         for (BlockStatePredicate predicate : this.list) {
            if (predicate.test(state)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean testBlock(Block block) {
         for (BlockStatePredicate predicate : this.list) {
            if (predicate.testBlock(block)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public Collection<Block> getBlocks() {
         HashSet<Block> set = new HashSet<>();

         for (BlockStatePredicate predicate : this.list) {
            set.addAll(predicate.getBlocks());
         }

         return set;
      }

      @Override
      public Collection<BlockState> getBlockStates() {
         HashSet<BlockState> set = new HashSet<>();

         for (BlockStatePredicate predicate : this.list) {
            set.addAll(predicate.getBlockStates());
         }

         return set;
      }

      @Override
      public Set<ResourceLocation> getBlockIds() {
         Set<ResourceLocation> set = new LinkedHashSet<>();

         for (BlockStatePredicate predicate : this.list) {
            set.addAll(predicate.getBlockIds());
         }

         return set;
      }

      @Override
      public RuleTest asRuleTest() {
         AnyMatchRuleTest test = new AnyMatchRuleTest();

         for (BlockStatePredicate predicate : this.list) {
            test.rules.add(predicate.asRuleTest());
         }

         return test;
      }
   }

   public static final class RegexMatch implements BlockStatePredicate {
      public final Pattern pattern;
      private final LinkedHashSet<Block> matchedBlocks;

      public RegexMatch(Pattern p) {
         this.pattern = p;
         this.matchedBlocks = new LinkedHashSet<>();

         for (Block block : BuiltInRegistries.BLOCK) {
            if (!this.matchedBlocks.contains(block) && this.pattern.matcher(block.kjs$getId()).find()) {
               this.matchedBlocks.add(block);
            }
         }
      }

      @Override
      public boolean test(BlockState state) {
         return this.matchedBlocks.contains(state.getBlock());
      }

      @Override
      public boolean testBlock(Block block) {
         return this.matchedBlocks.contains(block);
      }

      @Override
      public Collection<Block> getBlocks() {
         return this.matchedBlocks;
      }

      @Override
      public RuleTest asRuleTest() {
         AnyMatchRuleTest test = new AnyMatchRuleTest();

         for (Block block : this.matchedBlocks) {
            test.rules.add(new BlockMatchTest(block));
         }

         return test;
      }
   }

   public static enum Simple implements BlockStatePredicate {
      ALL(true),
      NONE(false);

      private final boolean match;

      private Simple(boolean match) {
         this.match = match;
      }

      @Override
      public boolean test(BlockState state) {
         return this.match;
      }

      @Override
      public boolean testBlock(Block block) {
         return this.match;
      }

      @Override
      public RuleTest asRuleTest() {
         return (RuleTest)(this.match ? AlwaysTrueTest.INSTANCE : AlwaysFalseRuleTest.INSTANCE);
      }

      @Override
      public Collection<BlockState> getBlockStates() {
         return (Collection<BlockState>)(this.match ? BlockWrapper.getAllBlockStates() : List.of());
      }
   }

   public record StateMatch(BlockState state) implements BlockStatePredicate {
      @Override
      public boolean test(BlockState s) {
         return this.state == s;
      }

      @Override
      public boolean testBlock(Block block) {
         return this.state.getBlock() == block;
      }

      @Override
      public Collection<Block> getBlocks() {
         return Collections.singleton(this.state.getBlock());
      }

      @Override
      public Collection<BlockState> getBlockStates() {
         return Collections.singleton(this.state);
      }

      @Override
      public Set<ResourceLocation> getBlockIds() {
         return Set.of(this.state.getBlock().kjs$getIdLocation());
      }

      @Override
      public RuleTest asRuleTest() {
         return new BlockStateMatchTest(this.state);
      }
   }

   public record TagMatch(TagKey<Block> tag) implements BlockStatePredicate {
      @Override
      public boolean test(BlockState state) {
         return state.is(this.tag);
      }

      @Override
      public boolean testBlock(Block block) {
         return block.builtInRegistryHolder().is(this.tag);
      }

      @Override
      public Collection<Block> getBlocks() {
         return (Collection<Block>)Util.make(new LinkedHashSet(), set -> {
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(this.tag)) {
               set.add((Block)holder.value());
            }
         });
      }

      @Override
      public RuleTest asRuleTest() {
         return new TagMatchTest(this.tag);
      }
   }
}
