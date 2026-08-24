package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockPredicate;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.registry.RegistryKubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.KubeResourceLocation;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.RecordTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

@Info("Various block related helper functions")
public class BlockWrapper {
   public static final TypeInfo TYPE_INFO = TypeInfo.of(Block.class);
   public static final TypeInfo STATE_TYPE_INFO = TypeInfo.of(BlockState.class);
   private static Collection<BlockState> ALL_STATE_CACHE = null;
   private static Map<String, Direction> facingMap;

   public static BlockIDPredicate id(ResourceLocation id) {
      return new BlockIDPredicate(id);
   }

   public static BlockIDPredicate id(ResourceLocation id, Map<String, Object> properties) {
      BlockIDPredicate b = id(id);

      for (Entry<String, Object> entry : properties.entrySet()) {
         b = b.with(entry.getKey(), entry.getValue().toString());
      }

      return b;
   }

   public static BlockEntityPredicate entity(ResourceLocation id) {
      return new BlockEntityPredicate(id);
   }

   public static BlockPredicate custom(BlockPredicate predicate) {
      return predicate;
   }

   @Info("Get a map of direction name to Direction. Functionally identical to Direction.ALL")
   public static Map<String, Direction> getFacing() {
      if (facingMap == null) {
         facingMap = new HashMap<>(6);

         for (Direction facing : DirectionWrapper.VALUES) {
            facingMap.put(facing.getSerializedName(), facing);
         }
      }

      return facingMap;
   }

   @Info("Gets a Block from a block id")
   public static Block getBlock(ResourceLocation id) {
      return (Block)BuiltInRegistries.BLOCK.get(id);
   }

   @Info("Gets a blocks id from the Block")
   @Nullable
   public static ResourceLocation getId(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block);
   }

   @Info("Gets a list of the classname of all registered blocks")
   public static List<String> getTypeList() {
      ArrayList<String> list = new ArrayList<>();

      for (Block block : BuiltInRegistries.BLOCK) {
         list.add(block.kjs$getId());
      }

      return list;
   }

   @Info("Gets a list of all blocks with tags")
   public static List<ResourceLocation> getTaggedIds(ResourceLocation tag) {
      return (List<ResourceLocation>)Util.make(new LinkedList(), list -> {
         for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(Tags.block(tag))) {
            ResourceKey<Block> l = holder.getKey();
            if (l != null) {
               list.add(l.location());
            }
         }
      });
   }

   public static Collection<BlockState> getAllBlockStates() {
      if (ALL_STATE_CACHE != null) {
         return ALL_STATE_CACHE;
      } else {
         HashSet<BlockState> states = new HashSet<>();

         for (Block block : BuiltInRegistries.BLOCK) {
            states.addAll(block.getStateDefinition().getPossibleStates());
         }

         ALL_STATE_CACHE = Collections.unmodifiableCollection(states);
         return ALL_STATE_CACHE;
      }
   }

   public static BlockState parseBlockState(RegistryAccessContainer registries, String string) {
      try {
         return BlockStateParser.parseForBlock(registries.access().lookupOrThrow(Registries.BLOCK), string, false).blockState();
      } catch (Exception var3) {
         throw new IllegalArgumentException("Invalid block state '%s'".formatted(string), var3);
      }
   }

   public static BlockSetType wrapSetType(Context cx, Object from, TypeInfo target) {
      return switch (from) {
         case null -> null;
         case BlockSetType type -> type;
         case CharSequence charSequence -> {
            String str = charSequence.toString();

            for (BlockSetType type : BlockSetType.values().toList()) {
               if (type.name().equalsIgnoreCase(str)) {
                  yield type;
               }
            }

            throw new KubeRuntimeException("Unknown BlockSetType named '%s'!".formatted(str)).source(SourceLine.of(cx));
         }
         default -> (BlockSetType)((RecordTypeInfo)target).wrap(cx, from, target);
      };
   }

   @Info("Parses a block state from the input string. May throw for invalid inputs!")
   public static BlockState wrapBlockState(RegistryAccessContainer registries, Object o) {
      return switch (o) {
         case null -> throw new KubeRuntimeException("BlockState cannot be null!");
         case BlockState bs -> bs;
         case Block block -> {
            BlockState var9 = block.defaultBlockState();
            yield var9;
         }
         default -> {
            BlockState var4;
            try {
               var4 = parseBlockState(registries, o.toString());
            } catch (IllegalArgumentException var8) {
               throw new KubeRuntimeException("Failed to read block state from %s: %s".formatted(o, var8.getMessage()));
            }

            yield var4;
         }
      };
   }

   public static BlockState withProperties(BlockState state, Map<?, ?> properties) {
      HashMap<String, Property<?>> pmap = new HashMap<>();

      for (Property<?> property : state.getProperties()) {
         pmap.put(property.getName(), property);
      }

      for (Entry<?, ?> entry : properties.entrySet()) {
         Property<? extends Comparable<?>> property = (Property<? extends Comparable<?>>)pmap.get(String.valueOf(entry.getKey()));
         if (property != null) {
            state = (BlockState)state.setValue(property, Cast.to(property.getValue(String.valueOf(entry.getValue())).orElseThrow()));
         }
      }

      return state;
   }

   public static void registerBuildingMaterial(Context cx, RegistryKubeEvent<Block> event, KubeResourceLocation id, BuildingMaterialProperties properties) {
      properties.register(cx, event, id);
   }

   public static void registerBuildingMaterial(Context cx, RegistryKubeEvent<Block> event, KubeResourceLocation id) {
      registerBuildingMaterial(cx, event, id, (BuildingMaterialProperties)cx.jsToJava(Map.of(), BuildingMaterialProperties.TYPE_INFO));
   }
}
