package net.mehvahdjukaar.moonlight.api.set.wood;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoodTypeRegistry extends BlockTypeRegistry<WoodType> {
   public static final WoodTypeRegistry INSTANCE = new WoodTypeRegistry();
   @Deprecated(
      forRemoval = true
   )
   public static final WoodType OAK_TYPE = VanillaWoodTypes.OAK;
   private final Map<net.minecraft.world.level.block.state.properties.WoodType, WoodType> fromVanilla = new IdentityHashMap<>();
   public static final Set<String> IGNORED_MODS = new HashSet<>(
      Set.of("chipped", "compressedblocks", "securitycraft", "absentbydesign", "immersive_weathering", "dynamictrees", "dt")
   );

   @Deprecated(
      forRemoval = true
   )
   public static Collection<WoodType> getTypes() {
      return INSTANCE.getValues();
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static WoodType getValue(ResourceLocation woodTypeId) {
      return INSTANCE.get(woodTypeId);
   }

   @Deprecated(
      forRemoval = true
   )
   @Nullable
   public static WoodType getValue(String woodTypeId) {
      return INSTANCE.get(ResourceLocation.parse(woodTypeId));
   }

   @Deprecated(
      forRemoval = true
   )
   public static WoodType fromNBT(String name) {
      return INSTANCE.getFromNBT(name);
   }

   @Deprecated(
      forRemoval = true
   )
   public static WoodType fromVanilla(net.minecraft.world.level.block.state.properties.WoodType vanillaType) {
      return INSTANCE.getFromVanilla(vanillaType);
   }

   public WoodTypeRegistry() {
      super(WoodType.class, "wood_type");
      this.addFinder(() -> {
         WoodType b = new WoodType(ResourceLocation.parse("bamboo"), Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK);
         b.addChild("stripped_log", Blocks.STRIPPED_BAMBOO_BLOCK);
         return Optional.of(b);
      });
      WoodType.CODEC = this.getCodec();
      WoodType.STREAM_CODEC = this.getStreamCodecExplicit();
      WoodType.ENTITY_SERIALIZER = RegHelper.registerEntityDataSerializer(
         Moonlight.res("wood_type"), () -> EntityDataSerializer.forValueType(this.getStreamCodecExplicit())
      );
   }

   static void touch() {
   }

   protected WoodType register(WoodType vanillaType) {
      return super.register(vanillaType);
   }

   public WoodType getDefaultType() {
      return VanillaWoodTypes.OAK;
   }

   @Override
   public Optional<WoodType> detectTypeFromBlock(Block baseBlock, ResourceLocation baseId) {
      String name = null;
      String path = baseId.getPath();
      if (!baseId.getNamespace().equals("tfc") && !baseId.getNamespace().equals("afc")) {
         if (path.endsWith("_planks")) {
            name = path.substring(0, path.length() - "_planks".length());
         } else if (path.startsWith("planks_")) {
            name = path.substring("planks_".length());
         } else if (path.endsWith("_plank")) {
            name = path.substring(0, path.length() - "_plank".length());
         } else if (path.startsWith("plank_")) {
            name = path.substring("plank_".length());
         }

         String namespace = baseId.getNamespace();
         if (name != null && !IGNORED_MODS.contains(namespace)) {
            BlockState state = baseBlock.defaultBlockState();
            if (state.getProperties().size() <= 2 && !(baseBlock instanceof SlabBlock)) {
               name = name.replace("/", "_");
               ResourceLocation id = baseId.withPath(name);
               Block logBlock = WoodType.findLog(id);
               if (logBlock != null && !this.valuesReg.containsKey(id)) {
                  return Optional.of(new WoodType(id, baseBlock, logBlock));
               }
            }
         }

         return Optional.empty();
      } else {
         if (path.contains("wood/planks/")) {
            Optional<Block> log = BuiltInRegistries.BLOCK.getOptional(baseId.withPath(path.replace("planks", "log")));
            if (log.isPresent()) {
               ResourceLocation id = baseId.withPath(path.replace("wood/planks/", ""));
               return Optional.of(new WoodType(id, baseBlock, log.get()));
            }
         }

         return Optional.empty();
      }
   }

   @Nullable
   public WoodType getFromVanilla(net.minecraft.world.level.block.state.properties.WoodType woodType) {
      if (this.fromVanilla.isEmpty()) {
         for (WoodType w : this.getValues()) {
            net.minecraft.world.level.block.state.properties.WoodType vanilla = w.toVanilla();
            if (vanilla != null) {
               this.fromVanilla.put(vanilla, w);
            }
         }
      }

      return this.fromVanilla.get(woodType);
   }

   public WoodType.Finder addSimpleFinder(ResourceLocation woodTypeId) {
      WoodType.Finder finder = new WoodType.Finder(woodTypeId);
      this.addFinder(finder);
      return finder;
   }

   public WoodType.Finder addSimpleFinder(String nameWoodType) {
      return this.addSimpleFinder(ResourceLocation.parse(nameWoodType));
   }

   public WoodType.Finder addSimpleFinder(String namespace, String nameWoodType) {
      return this.addSimpleFinder(ResourceLocation.fromNamespaceAndPath(namespace, nameWoodType));
   }
}
