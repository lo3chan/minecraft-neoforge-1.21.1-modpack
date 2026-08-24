package fuzs.eternalnether.data.client;

import com.google.common.collect.ImmutableMap;
import fuzs.eternalnether.data.ModAdvancementProvider;
import fuzs.eternalnether.init.ModBlockFamilies;
import fuzs.eternalnether.init.ModBlocks;
import fuzs.eternalnether.init.ModEntityTypes;
import fuzs.eternalnether.init.ModItems;
import fuzs.eternalnether.init.ModSoundEvents;
import fuzs.eternalnether.init.ResourceKeyHelper;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider.TranslationBuilder;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.BlockFamily.Variant;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModLanguageProvider extends AbstractLanguageProvider {
   static final Map<Variant, BiFunction<ModLanguageProvider.BlockFamilyBuilder, Block, ModLanguageProvider.BlockFamilyBuilder>> VARIANT_FUNCTIONS = ImmutableMap.builder()
      .put(Variant.BUTTON, ModLanguageProvider.BlockFamilyBuilder::button)
      .put(Variant.CHISELED, ModLanguageProvider.BlockFamilyBuilder::chiseled)
      .put(Variant.CRACKED, ModLanguageProvider.BlockFamilyBuilder::cracked)
      .put(Variant.CUT, ModLanguageProvider.BlockFamilyBuilder::cut)
      .put(Variant.DOOR, ModLanguageProvider.BlockFamilyBuilder::door)
      .put(Variant.CUSTOM_FENCE, ModLanguageProvider.BlockFamilyBuilder::fence)
      .put(Variant.FENCE, ModLanguageProvider.BlockFamilyBuilder::fence)
      .put(Variant.CUSTOM_FENCE_GATE, ModLanguageProvider.BlockFamilyBuilder::fenceGate)
      .put(Variant.FENCE_GATE, ModLanguageProvider.BlockFamilyBuilder::fenceGate)
      .put(Variant.MOSAIC, ModLanguageProvider.BlockFamilyBuilder::mosaic)
      .put(Variant.SIGN, ModLanguageProvider.BlockFamilyBuilder::sign)
      .put(Variant.SLAB, ModLanguageProvider.BlockFamilyBuilder::slab)
      .put(Variant.STAIRS, ModLanguageProvider.BlockFamilyBuilder::stairs)
      .put(Variant.PRESSURE_PLATE, ModLanguageProvider.BlockFamilyBuilder::pressurePlate)
      .put(Variant.POLISHED, ModLanguageProvider.BlockFamilyBuilder::polished)
      .put(Variant.TRAPDOOR, ModLanguageProvider.BlockFamilyBuilder::trapdoor)
      .put(Variant.WALL, ModLanguageProvider.BlockFamilyBuilder::wall)
      .put(Variant.WALL_SIGN, ModLanguageProvider.BlockFamilyBuilder::wallSign)
      .build();

   public ModLanguageProvider(DataProviderContext context) {
      super(context);
   }

   public void addTranslations(TranslationBuilder builder) {
      builder.addCreativeModeTab("eternalnether", "Eternal Nether");
      builder.addBlock(ModBlocks.COBBLED_BLACKSTONE, "Cobbled Blackstone");
      blockFamily(builder, "Withered Blackstone")
         .baseBlock((Block)ModBlocks.WITHERED_BLACKSTONE.value())
         .slab((Block)ModBlocks.WITHERED_BLACKSTONE_SLAB.value())
         .stairs((Block)ModBlocks.WITHERED_BLACKSTONE_STAIRS.value())
         .wall((Block)ModBlocks.WITHERED_BLACKSTONE_WALL.value())
         .chiseled((Block)ModBlocks.CHISELED_WITHERED_BLACKSTONE.value());
      blockFamily(builder, "Cracked Withered Blackstone").generateFor(ModBlockFamilies.CRACKED_WITHERED_BLACKSTONE_FAMILY);
      blockFamily(builder, "Warped Nether Brick", "Warped Nether Bricks").generateFor(ModBlockFamilies.WARPED_NETHER_BRICKS_FAMILY);
      builder.add((Block)ModBlocks.WITHERED_BASALT.value(), "Withered Basalt");
      builder.add((Block)ModBlocks.WITHERED_COAL_BLOCK.value(), "Withered Coal Block");
      builder.add((Block)ModBlocks.WITHERED_QUARTZ_BLOCK.value(), "Withered Quartz Block");
      builder.add((Block)ModBlocks.WITHERED_DEBRIS.value(), "Withered Debris");
      builder.add((Block)ModBlocks.SOUL_STONE.value(), "Soul Stone");
      builder.add((Block)ModBlocks.WITHERED_BONE_BLOCK.value(), "Withered Bone Block");
      builder.add((Block)ModBlocks.NETHERITE_BELL.value(), "Netherite Bell");
      builder.addSpawnEgg((Item)ModItems.WEX_SPAWN_EGG.value(), "Wex");
      builder.addSpawnEgg((Item)ModItems.WARPED_ENDERMAN_SPAWN_EGG.value(), "Warped Enderman");
      builder.addSpawnEgg((Item)ModItems.PIGLIN_PRISONER_SPAWN_EGG.value(), "Piglin Prisoner");
      builder.addSpawnEgg((Item)ModItems.PIGLIN_HUNTER_SPAWN_EGG.value(), "Piglin Hunter");
      builder.addSpawnEgg((Item)ModItems.WRAITHER_SPAWN_EGG.value(), "Wraither");
      builder.addSpawnEgg((Item)ModItems.WITHER_SKELETON_KNIGHT_SPAWN_EGG.value(), "Wither Skeleton Knight");
      builder.addSpawnEgg((Item)ModItems.CORPOR_SPAWN_EGG.value(), "Corpor");
      builder.addSpawnEgg((Item)ModItems.WITHER_SKELETON_HORSE_SPAWN_EGG.value(), "Withered Skeleton Horse");
      builder.add(ResourceKeyHelper.getComponent(ModItems.WITHER_WALTZ_JUKEBOX_SONG), "Izofar - Wither Waltz");
      builder.add((Item)ModItems.WITHER_WALTZ_MUSIC_DISC.value(), "Music Disc");
      builder.add((Item)ModItems.WARPED_ENDER_PEARL.value(), "Warped Ender Pearl");
      builder.add((Item)ModItems.WITHERED_BONE.value(), "Withered Bone");
      builder.add((Item)ModItems.WITHERED_BONE_MEAL.value(), "Withered Bone Meal");
      builder.add((Item)ModItems.GILDED_NETHERITE_SHIELD.value(), "Gilded Netherite Shield");
      builder.add((Item)ModItems.CUTLASS.value(), "Cutlass");
      builder.add((EntityType)ModEntityTypes.WEX.value(), "Wex");
      builder.add((EntityType)ModEntityTypes.WARPED_ENDERMAN.value(), "Warped Enderman");
      builder.add((EntityType)ModEntityTypes.PIGLIN_PRISONER.value(), "Piglin Prisoner");
      builder.add((EntityType)ModEntityTypes.PIGLIN_HUNTER.value(), "Piglin Hunter");
      builder.add((EntityType)ModEntityTypes.WRAITHER.value(), "Wraither");
      builder.add((EntityType)ModEntityTypes.WITHER_SKELETON_KNIGHT.value(), "Wither Skeleton Knight");
      builder.add((EntityType)ModEntityTypes.CORPOR.value(), "Corpor");
      builder.add((EntityType)ModEntityTypes.WITHER_SKELETON_HORSE.value(), "Withered Skeleton Horse");
      builder.add((EntityType)ModEntityTypes.WARPED_ENDER_PEARL.value(), "Warped Ender Pearl");
      builder.add((SoundEvent)ModSoundEvents.WEX_CHARGE.value(), "Wex shrieks");
      builder.add((SoundEvent)ModSoundEvents.WEX_DEATH.value(), "Wex dies");
      builder.add((SoundEvent)ModSoundEvents.WEX_HURT.value(), "Wex hurts");
      builder.add((SoundEvent)ModSoundEvents.WEX_AMBIENT.value(), "Wex wexes");
      builder.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_DEATH.value(), "Warped Enderman dies");
      builder.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_HURT.value(), "Warped Enderman hurts");
      builder.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_AMBIENT.value(), "Warped Enderman vwoops");
      builder.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_TELEPORT.value(), "Warped Enderman teleports");
      builder.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_SCREAM.value(), "Warped Enderman screams");
      builder.add((SoundEvent)ModSoundEvents.WARPED_ENDERMAN_STARE.value(), "Warped Enderman cries out");
      builder.add(ModAdvancementProvider.ROOT_ADVANCEMENT.title(), "Eternal Nether");
      builder.add(ModAdvancementProvider.ROOT_ADVANCEMENT.description(), "Explore the Nether for new structures!");
      builder.add(ModAdvancementProvider.ACQUIRE_WITHER_WALTZ_ADVANCEMENT.title(), "Here I Waltz");
      builder.add(ModAdvancementProvider.ACQUIRE_WITHER_WALTZ_ADVANCEMENT.description(), "Acquire the Wither Waltz Music Disc");
      builder.add(ModAdvancementProvider.CATACOMB_ADVANCEMENT.title(), "To Wither Or Not To Wither");
      builder.add(ModAdvancementProvider.CATACOMB_ADVANCEMENT.description(), "Locate a Catacomb structure");
      builder.add(ModAdvancementProvider.CITADEL_ADVANCEMENT.title(), "The Warping Citadel");
      builder.add(ModAdvancementProvider.CITADEL_ADVANCEMENT.description(), "Locate a Citadel structure");
      builder.add(ModAdvancementProvider.EXPLORE_STRUCTURES_ADVANCEMENT.title(), "Hotter Tourist Destinations");
      builder.add(ModAdvancementProvider.EXPLORE_STRUCTURES_ADVANCEMENT.description(), "Locate all Eternal Nether structures");
      builder.add(ModAdvancementProvider.PIGLIN_MANOR_ADVANCEMENT.title(), "Mind Your Manors");
      builder.add(ModAdvancementProvider.PIGLIN_MANOR_ADVANCEMENT.description(), "Locate a Piglin Manor structure");
      builder.add(ModAdvancementProvider.RIDE_WITHER_SKELETON_HORSE_ADVANCEMENT.title(), "Dark Horse");
      builder.add(ModAdvancementProvider.RIDE_WITHER_SKELETON_HORSE_ADVANCEMENT.description(), "Ride a Wither Skeleton Horse");
      builder.add(ModAdvancementProvider.SUMMON_ENDERMAN_ADVANCEMENT.title(), "A Little Off The Top");
      builder.add(ModAdvancementProvider.SUMMON_ENDERMAN_ADVANCEMENT.description(), "Trim the Warp from a Warped Enderman");
      builder.add(ModAdvancementProvider.RESCUE_PIGLIN_PRISONER_ADVANCEMENT.title(), "Saving Private Swine");
      builder.add(ModAdvancementProvider.RESCUE_PIGLIN_PRISONER_ADVANCEMENT.description(), "Rescue a Piglin Prisoner");
   }

   public static ModLanguageProvider.BlockFamilyBuilder blockFamily(TranslationBuilder builder, String blockValue) {
      return new ModLanguageProvider.BlockFamilyBuilder(builder::add, blockValue);
   }

   public static ModLanguageProvider.BlockFamilyBuilder blockFamily(TranslationBuilder builder, String blockValue, String baseBlockValue) {
      return new ModLanguageProvider.BlockFamilyBuilder(builder::add, blockValue, baseBlockValue);
   }

   public static class BlockFamilyBuilder {
      private final BiConsumer<Block, String> valueConsumer;
      private final String blockValue;
      private final String baseBlockValue;

      public BlockFamilyBuilder(BiConsumer<Block, String> valueConsumer, String blockValue) {
         this(valueConsumer, blockValue, blockValue);
      }

      public BlockFamilyBuilder(BiConsumer<Block, String> valueConsumer, String blockValue, String baseBlockValue) {
         this.valueConsumer = valueConsumer;
         this.blockValue = blockValue;
         this.baseBlockValue = baseBlockValue;
      }

      public void generateFor(BlockFamily blockFamily) {
         this.baseBlock(blockFamily.getBaseBlock());
         blockFamily.getVariants()
            .forEach(
               (variant, block) -> {
                  BiFunction<ModLanguageProvider.BlockFamilyBuilder, Block, ModLanguageProvider.BlockFamilyBuilder> variantFunction = ModLanguageProvider.VARIANT_FUNCTIONS
                     .get(variant);
                  if (variantFunction != null) {
                     variantFunction.apply(this, block);
                  }
               }
            );
      }

      public ModLanguageProvider.BlockFamilyBuilder baseBlock(Block block) {
         this.valueConsumer.accept(block, this.baseBlockValue);
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder button(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Button");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder chiseled(Block block) {
         this.valueConsumer.accept(block, "Chiseled " + this.blockValue);
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder cracked(Block block) {
         this.valueConsumer.accept(block, "Cracked " + this.blockValue);
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder cut(Block block) {
         this.valueConsumer.accept(block, "Cut " + this.blockValue);
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder door(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Door");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder fence(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Fence");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder fenceGate(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Fence Gate");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder mosaic(Block block) {
         this.valueConsumer.accept(block, "Mosaic " + this.blockValue);
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder sign(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Sign");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder slab(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Slab");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder stairs(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Stairs");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder pressurePlate(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Pressure Plate");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder polished(Block block) {
         this.valueConsumer.accept(block, "Polished " + this.blockValue);
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder trapdoor(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Trapdoor");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder wall(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Wall");
         return this;
      }

      public ModLanguageProvider.BlockFamilyBuilder wallSign(Block block) {
         this.valueConsumer.accept(block, this.blockValue + " Wall Sign");
         return this;
      }
   }
}
