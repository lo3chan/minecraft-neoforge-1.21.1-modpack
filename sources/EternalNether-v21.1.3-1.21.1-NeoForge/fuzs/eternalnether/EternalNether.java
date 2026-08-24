package fuzs.eternalnether;

import fuzs.eternalnether.init.ModEntityTypes;
import fuzs.eternalnether.init.ModFeatures;
import fuzs.eternalnether.init.ModItems;
import fuzs.eternalnether.init.ModRegistry;
import fuzs.eternalnether.util.CreativeModeTabHelper;
import fuzs.eternalnether.world.entity.animal.horse.WitherSkeletonHorse;
import fuzs.eternalnether.world.entity.monster.Corpor;
import fuzs.eternalnether.world.entity.monster.WarpedEnderMan;
import fuzs.eternalnether.world.entity.monster.Wex;
import fuzs.eternalnether.world.entity.monster.WitherSkeletonKnight;
import fuzs.eternalnether.world.entity.monster.Wraither;
import fuzs.eternalnether.world.entity.monster.piglin.PiglinPrisoner;
import fuzs.eternalnether.world.entity.monster.piglin.PiglinPrisonerAi;
import fuzs.eternalnether.world.entity.projectile.EnderPearlTeleportCallback;
import fuzs.eternalnether.world.entity.projectile.ThrownWarpedEnderpearl;
import fuzs.eternalnether.world.item.CutlassItem;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.core.v1.context.EntityAttributesCreateContext;
import fuzs.puzzleslib.api.core.v1.context.SpawnPlacementsContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.entity.living.UseItemEvents;
import fuzs.puzzleslib.api.event.v1.level.BlockEvents;
import fuzs.puzzleslib.api.event.v1.level.BlockEvents.Break;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EternalNether implements ModConstructor {
   public static final String MOD_ID = "eternalnether";
   public static final String MOD_NAME = "Eternal Nether";
   public static final Logger LOGGER = LoggerFactory.getLogger("Eternal Nether");

   public void onConstructMod() {
      ModRegistry.boostrap();
      registerEventHandlers();
   }

   private static void registerEventHandlers() {
      BlockEvents.BREAK.register((Break)(serverLevel, blockPos, blockState, player, itemInHand) -> {
         if (blockState.is(Blocks.IRON_BARS)) {
            PiglinPrisonerAi.exciteNearbyPiglins(player, false);
         }

         return EventResult.PASS;
      });
      UseItemEvents.START.register(CutlassItem::onUseItemStart);
      EnderPearlTeleportCallback.EVENT.register(ThrownWarpedEnderpearl::onEnderPearlTeleport);
   }

   public void onCommonSetup() {
      ModFeatures.setBasaltFeatureRestrictions();
      ModEntityTypes.setPiglinBruteSensorsAndMemories();
   }

   public void onRegisterBiomeModifications(BiomeModificationsContext context) {
      context.register(
         BiomeLoadingPhase.ADDITIONS,
         biomeLoadingContext -> biomeLoadingContext.is(Biomes.SOUL_SAND_VALLEY),
         biomeModificationContext -> biomeModificationContext.generationSettings()
            .addFeature(Decoration.UNDERGROUND_DECORATION, ModRegistry.SOUL_STONE_BLOBS_PLACED_FEATURE)
      );
   }

   public void onEntityAttributeCreation(EntityAttributesCreateContext context) {
      context.registerEntityAttributes((EntityType)ModEntityTypes.PIGLIN_PRISONER.value(), PiglinPrisoner.createAttributes());
      context.registerEntityAttributes((EntityType)ModEntityTypes.PIGLIN_HUNTER.value(), PiglinBrute.createAttributes());
      context.registerEntityAttributes((EntityType)ModEntityTypes.WEX.value(), Wex.createAttributes());
      context.registerEntityAttributes((EntityType)ModEntityTypes.WARPED_ENDERMAN.value(), WarpedEnderMan.createAttributes());
      context.registerEntityAttributes((EntityType)ModEntityTypes.WRAITHER.value(), Wraither.createAttributes());
      context.registerEntityAttributes((EntityType)ModEntityTypes.WITHER_SKELETON_KNIGHT.value(), WitherSkeletonKnight.createAttributes());
      context.registerEntityAttributes((EntityType)ModEntityTypes.CORPOR.value(), Corpor.createAttributes());
      context.registerEntityAttributes((EntityType)ModEntityTypes.WITHER_SKELETON_HORSE.value(), WitherSkeletonHorse.createAttributes());
   }

   public void onRegisterSpawnPlacements(SpawnPlacementsContext context) {
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.PIGLIN_PRISONER.value(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         PiglinPrisoner::checkPiglinSpawnRules
      );
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.PIGLIN_HUNTER.value(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         PiglinPrisoner::checkPiglinSpawnRules
      );
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.WEX.value(), SpawnPlacementTypes.NO_RESTRICTIONS, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules
      );
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.WARPED_ENDERMAN.value(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules
      );
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.WRAITHER.value(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules
      );
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.WITHER_SKELETON_KNIGHT.value(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         Monster::checkMonsterSpawnRules
      );
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.CORPOR.value(), SpawnPlacementTypes.ON_GROUND, Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules
      );
      context.registerSpawnPlacement(
         (EntityType)ModEntityTypes.WITHER_SKELETON_HORSE.value(),
         SpawnPlacementTypes.ON_GROUND,
         Types.MOTION_BLOCKING_NO_LEAVES,
         SkeletonHorse::checkSkeletonHorseSpawnRules
      );
   }

   public void onRegisterCreativeModeTabs(CreativeModeTabContext context) {
      context.registerCreativeModeTab(
         CreativeModeTabConfigurator.from("eternalnether", ModItems.WITHERED_DEBRIS).displayItems(CreativeModeTabHelper.getDisplayItems("eternalnether"))
      );
   }

   public ContentRegistrationFlags[] getContentRegistrationFlags() {
      return new ContentRegistrationFlags[]{ContentRegistrationFlags.BIOME_MODIFICATIONS};
   }

   public static ResourceLocation id(String path) {
      return ResourceLocationHelper.fromNamespaceAndPath("eternalnether", path);
   }
}
