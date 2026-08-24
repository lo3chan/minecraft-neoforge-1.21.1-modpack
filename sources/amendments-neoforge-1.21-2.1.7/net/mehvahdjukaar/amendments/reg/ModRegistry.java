package net.mehvahdjukaar.amendments.reg;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.common.CakeRegistry;
import net.mehvahdjukaar.amendments.common.LanternRegistry;
import net.mehvahdjukaar.amendments.common.LecternEditMenu;
import net.mehvahdjukaar.amendments.common.block.CarpetSlabBlock;
import net.mehvahdjukaar.amendments.common.block.CarpetStairBlock;
import net.mehvahdjukaar.amendments.common.block.CeilingBannerBlock;
import net.mehvahdjukaar.amendments.common.block.DirectionalCakeBlock;
import net.mehvahdjukaar.amendments.common.block.DoubleCakeBlock;
import net.mehvahdjukaar.amendments.common.block.DoubleSkullBlock;
import net.mehvahdjukaar.amendments.common.block.DyeCauldronBlock;
import net.mehvahdjukaar.amendments.common.block.FloorCandleSkullBlock;
import net.mehvahdjukaar.amendments.common.block.HangingFlowerPotBlock;
import net.mehvahdjukaar.amendments.common.block.LiquidCauldronBlock;
import net.mehvahdjukaar.amendments.common.block.ToolHookBlock;
import net.mehvahdjukaar.amendments.common.block.WallCandleSkullBlock;
import net.mehvahdjukaar.amendments.common.block.WallLanternBlock;
import net.mehvahdjukaar.amendments.common.block.WaterloggedLilyBlock;
import net.mehvahdjukaar.amendments.common.block.WeatheringWallLanternBlock;
import net.mehvahdjukaar.amendments.common.entity.FallingLanternEntity;
import net.mehvahdjukaar.amendments.common.entity.MediumDragonFireball;
import net.mehvahdjukaar.amendments.common.entity.MediumFireball;
import net.mehvahdjukaar.amendments.common.item.DragonChargeItem;
import net.mehvahdjukaar.amendments.common.item.DyeBottleItem;
import net.mehvahdjukaar.amendments.common.item.placement.WallLanternPlacement;
import net.mehvahdjukaar.amendments.common.recipe.CauldronRecipe;
import net.mehvahdjukaar.amendments.common.recipe.DyeBottleRecipe;
import net.mehvahdjukaar.amendments.common.tile.CandleSkullBlockTile;
import net.mehvahdjukaar.amendments.common.tile.CarpetedBlockTile;
import net.mehvahdjukaar.amendments.common.tile.CeilingBannerBlockTile;
import net.mehvahdjukaar.amendments.common.tile.DoubleSkullBlockTile;
import net.mehvahdjukaar.amendments.common.tile.HangingFlowerPotBlockTile;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.common.tile.ToolHookBlockTile;
import net.mehvahdjukaar.amendments.common.tile.WallLanternBlockTile;
import net.mehvahdjukaar.amendments.common.tile.WaterloggedLilyBlockTile;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacementsAPI;
import net.mehvahdjukaar.moonlight.api.misc.HolderRef;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class ModRegistry {
   public static final HolderRef<DamageType> BOILING_DAMAGE = HolderRef.of(Amendments.res("boiling"), Registries.DAMAGE_TYPE);
   public static final HolderRef<SoftFluid> DYE_SOFT_FLUID = HolderRef.of(Amendments.res("dye"), SoftFluidRegistry.KEY);
   public static final RegSupplier<RecipeSerializer<DyeBottleRecipe>> DYE_BOTTLE_RECIPE = RegHelper.registerSpecialRecipe(
      Amendments.res("dye_bottle"), DyeBottleRecipe::new
   );
   public static final Supplier<MenuType<LecternEditMenu>> LECTERN_EDIT_MENU = RegHelper.registerMenuType(Amendments.res("lectern_edit"), LecternEditMenu::of);
   public static final RegSupplier<SimpleParticleType> BOILING_PARTICLE = RegHelper.registerParticle(Amendments.res("boiling_bubble"));
   public static final RegSupplier<SimpleParticleType> SPLASH_PARTICLE = RegHelper.registerParticle(Amendments.res("fluid_splash"));
   public static final Supplier<SimpleParticleType> DRAGON_FIREBALL_TRAIL_PARTICLE = RegHelper.registerParticle(Amendments.res("dragon_fireball_trail"));
   public static final Supplier<SimpleParticleType> FIREBALL_TRAIL_PARTICLE = RegHelper.registerParticle(Amendments.res("fireball_trail"));
   public static final Supplier<SimpleParticleType> FIREBALL_EMITTER_PARTICLE = RegHelper.registerParticle(Amendments.res("fireball_explosion_emitter"));
   public static final Supplier<SimpleParticleType> FIREBALL_EXPLOSION_PARTICLE = RegHelper.registerParticle(Amendments.res("fireball_explosion"));
   public static final RegSupplier<SoundEvent> FIREBALL_EXPLOSION_SOUND = RegHelper.registerSound(Amendments.res("explosion.fireball"));
   public static final Supplier<RecipeType<CauldronRecipe>> CAULDRON_RECIPE_TYPE = RegHelper.registerRecipeType(Amendments.res("cauldron_crafting"));
   public static final Supplier<RecipeSerializer<CauldronRecipe>> CAULDRON_RECIPE_SERIALIZER = RegHelper.registerRecipeSerializer(
      Amendments.res("cauldron_crafting"), CauldronRecipe.Serializer::new
   );
   public static final Supplier<EntityType<MediumDragonFireball>> MEDIUM_DRAGON_FIREBALL = regEntity(
      "medium_dragon_fireball", Builder.of(MediumDragonFireball::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(16).updateInterval(5)
   );
   public static final Supplier<EntityType<MediumFireball>> MEDIUM_FIREBALL = regEntity(
      "medium_fireball", Builder.of(MediumFireball::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(16).updateInterval(5)
   );
   public static final Supplier<Item> DRAGON_CHARGE = regItem("dragon_charge", () -> new DragonChargeItem(new Properties()));
   public static final Supplier<Item> DYE_BOTTLE_ITEM = regItem(
      "dye_bottle",
      () -> new DyeBottleItem(new Properties().component(DataComponents.DYED_COLOR, DyeBottleItem.RED_COLOR).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE))
   );
   public static final Supplier<Block> WATERLILY_BLOCK = regBlock(
      "water_lily_pad",
      () -> new WaterloggedLilyBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.LILY_PAD).instabreak().sound(SoundType.LILY_PAD).noOcclusion()
      )
   );
   public static final Supplier<BlockEntityType<WaterloggedLilyBlockTile>> WATERLILY_TILE = regTile(
      "water_lily_pad", () -> PlatHelper.newBlockEntityType(WaterloggedLilyBlockTile::new, new Block[]{WATERLILY_BLOCK.get()})
   );
   public static final Supplier<LiquidCauldronBlock> LIQUID_CAULDRON = regBlock(
      "liquid_cauldron", () -> new LiquidCauldronBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON))
   );
   public static final Supplier<Block> DYE_CAULDRON = regBlock(
      "dye_cauldron", () -> new DyeCauldronBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON))
   );
   public static final Supplier<BlockEntityType<LiquidCauldronBlockTile>> LIQUID_CAULDRON_TILE = regTile(
      "liquid_cauldron", () -> PlatHelper.newBlockEntityType(LiquidCauldronBlockTile::new, new Block[]{(Block)LIQUID_CAULDRON.get(), DYE_CAULDRON.get()})
   );
   public static final Supplier<Block> HANGING_FLOWER_POT = regBlock(
      "hanging_flower_pot", () -> new HangingFlowerPotBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT))
   );
   public static final Supplier<BlockEntityType<HangingFlowerPotBlockTile>> HANGING_FLOWER_POT_TILE = regTile(
      "hanging_flower_pot", () -> PlatHelper.newBlockEntityType(HangingFlowerPotBlockTile::new, new Block[]{HANGING_FLOWER_POT.get()})
   );
   public static final Map<DyeColor, Supplier<Block>> CEILING_BANNERS = (Map<DyeColor, Supplier<Block>>)Util.make(
      () -> {
         Map<DyeColor, Supplier<Block>> map = new Object2ObjectLinkedOpenHashMap();

         for (DyeColor color : BlocksColorAPI.SORTED_COLORS) {
            String name = "ceiling_banner_" + color.getName();
            map.put(
               color,
               regBlock(
                  name,
                  () -> new CeilingBannerBlock(
                     color,
                     net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                        .ignitedByLava()
                        .forceSolidOn()
                        .mapColor(color.getMapColor())
                        .strength(1.0F)
                        .noCollission()
                        .sound(SoundType.WOOD)
                  )
               )
            );
         }

         return Collections.unmodifiableMap(map);
      }
   );
   public static final Supplier<BlockEntityType<CeilingBannerBlockTile>> CEILING_BANNER_TILE = regTile(
      "ceiling_banner",
      () -> PlatHelper.newBlockEntityType(CeilingBannerBlockTile::new, CEILING_BANNERS.values().stream().map(Supplier::get).toArray(Block[]::new))
   );
   public static final Supplier<Block> CARPET_STAIRS = regBlock(
      "carpet_stairs",
      () -> new CarpetStairBlock(Blocks.OAK_STAIRS, net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS))
   );
   public static final Supplier<Block> CARPET_SLAB = regBlock(
      "carpet_slab", () -> new CarpetSlabBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))
   );
   public static final Supplier<BlockEntityType<CarpetedBlockTile>> CARPET_STAIRS_TILE = regTile(
      "carpeted_block", () -> PlatHelper.newBlockEntityType(CarpetedBlockTile::new, new Block[]{CARPET_STAIRS.get(), CARPET_SLAB.get()})
   );
   public static final Map<LanternRegistry.LanternType, WallLanternBlock> WALL_LANTERNS = new LinkedHashMap<>();
   public static final Map<ResourceLocation, WallLanternBlock> WALL_LANTERNS_BY_LANTERN = new HashMap<>();
   public static Supplier<BlockEntityType<WallLanternBlockTile>> WALL_LANTERN_TILE = RegHelper.registerBlockEntityType(
      Amendments.res("wall_lantern"), () -> PlatHelper.newBlockEntityType(WallLanternBlockTile::new, new Block[0])
   );
   public static Supplier<WallLanternBlock> WALL_LANTERN = () -> WALL_LANTERNS.get(LanternRegistry.VANILLA);
   public static final Supplier<EntityType<FallingLanternEntity>> FALLING_LANTERN = regEntity(
      "falling_lantern", Builder.of(FallingLanternEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20)
   );
   public static final Supplier<ToolHookBlock> TOOL_HOOK = regBlock(
      "tool_hook",
      () -> {
         net.minecraft.world.level.block.state.BlockBehaviour.Properties p = net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(
               Blocks.TRIPWIRE_HOOK
            )
            .dropsLike(Blocks.TRIPWIRE_HOOK);
         return new ToolHookBlock(p);
      }
   );
   public static final Supplier<BlockEntityType<ToolHookBlockTile>> TOOL_HOOK_TILE = regTile(
      "tool_hook", () -> PlatHelper.newBlockEntityType(ToolHookBlockTile::new, new Block[]{TOOL_HOOK.get()})
   );
   public static final Supplier<Block> SKULL_PILE = regBlock(
      "skull_pile",
      () -> {
         net.minecraft.world.level.block.state.BlockBehaviour.Properties p = net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(
               Blocks.SKELETON_SKULL
            )
            .sound(SoundType.BONE_BLOCK);
         return new DoubleSkullBlock(p);
      }
   );
   public static final Supplier<BlockEntityType<DoubleSkullBlockTile>> SKULL_PILE_TILE = regTile(
      "skull_pile", () -> PlatHelper.newBlockEntityType(DoubleSkullBlockTile::new, new Block[]{SKULL_PILE.get()})
   );
   public static final Supplier<Block> SKULL_CANDLE = regBlock(
      "skull_candle",
      () -> new FloorCandleSkullBlock(
         net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.SKELETON_SKULL).sound(SoundType.BONE_BLOCK)
      )
   );
   public static final Supplier<Block> SKULL_CANDLE_WALL = regBlock(
      "skull_candle_wall",
      () -> new WallCandleSkullBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)SKULL_CANDLE.get()))
   );
   public static final Supplier<Block> SKULL_CANDLE_SOUL = regBlock(
      "skull_candle_soul",
      () -> new FloorCandleSkullBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)SKULL_CANDLE.get()))
   );
   public static final Supplier<Block> SKULL_CANDLE_SOUL_WALL = regBlock(
      "skull_candle_soul_wall",
      () -> new WallCandleSkullBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)SKULL_CANDLE.get()))
   );
   public static final Supplier<BlockEntityType<CandleSkullBlockTile>> SKULL_CANDLE_TILE = regTile(
      "skull_candle",
      () -> PlatHelper.newBlockEntityType(
         CandleSkullBlockTile::new, new Block[]{SKULL_CANDLE.get(), SKULL_CANDLE_WALL.get(), SKULL_CANDLE_SOUL.get(), SKULL_CANDLE_SOUL_WALL.get()}
      )
   );
   public static final Supplier<Block> DIRECTIONAL_CAKE = regBlock("directional_cake", () -> new DirectionalCakeBlock(CakeRegistry.VANILLA));
   public static final Map<CakeRegistry.CakeType, DoubleCakeBlock> DOUBLE_CAKES = new LinkedHashMap<>();

   public static void init() {
      BlockSetAPI.registerBlockSetDefinition(CakeRegistry.INSTANCE);
      BlockSetAPI.registerBlockSetDefinition(LanternRegistry.INSTANCE);
      BlockSetAPI.addDynamicRegistration("amendments", ModRegistry::registerDoubleCakes, BuiltInRegistries.BLOCK);
      BlockSetAPI.addDynamicRegistration("amendments", ModRegistry::registerWallLanterns, BuiltInRegistries.BLOCK);
      RegHelper.addExtraBEBlockStatesRegistration(event -> event.addBlocks(WALL_LANTERN_TILE.get(), WALL_LANTERNS.values().toArray(new WallLanternBlock[0])));
   }

   public static void registerAdditionalPlacements() {
      if (CommonConfigs.WALL_LANTERN.get()) {
         for (LanternRegistry.LanternType type : LanternRegistry.INSTANCE) {
            WallLanternBlock wallBlock = WALL_LANTERNS.get(type);
            if (wallBlock != null) {
               AdditionalItemPlacementsAPI.registerPlacement(type.lantern.asItem(), new WallLanternPlacement(wallBlock));
            }
         }
      }

      if (CommonConfigs.HANGING_POT.get()) {
         AdditionalItemPlacementsAPI.registerSimplePlacement(Items.FLOWER_POT, HANGING_FLOWER_POT.get());
      }

      if (CommonConfigs.CEILING_BANNERS.get()) {
         for (Entry<DyeColor, Supplier<Block>> e : CEILING_BANNERS.entrySet()) {
            Item item = BannerBlock.byColor(e.getKey()).asItem();
            if (item == Items.AIR) {
               throw new IllegalStateException(
                  "Block "
                     + e.getValue().get()
                     + " has no corresponding item! How did this happen? Some OTHER mod must have screwed up the block to items map!"
               );
            }

            AdditionalItemPlacementsAPI.registerSimplePlacement((Item)Preconditions.checkNotNull(item), e.getValue().get());
         }
      }
   }

   private static void registerWallLanterns(Registrator<Block> event) {
      for (LanternRegistry.LanternType type : LanternRegistry.INSTANCE) {
         ResourceLocation id = Amendments.res(type.getVariantId("wall"));
         net.minecraft.world.level.block.state.BlockBehaviour.Properties p = net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(
               type.lantern
            )
            .pushReaction(PushReaction.DESTROY)
            .noLootTable();
         WallLanternBlock block = makeWallLantern(p, type);
         type.addChild("wall_lantern", block);
         event.register(id, block);
         WALL_LANTERNS.put(type, block);
         WALL_LANTERNS_BY_LANTERN.put(type.getId(), block);
      }
   }

   private static WallLanternBlock makeWallLantern(net.minecraft.world.level.block.state.BlockBehaviour.Properties p, LanternRegistry.LanternType type) {
      return (WallLanternBlock)(type.lantern instanceof WeatheringCopper ? new WeatheringWallLanternBlock(p, type) : new WallLanternBlock(p, type));
   }

   private static void registerDoubleCakes(Registrator<Block> event) {
      for (CakeRegistry.CakeType type : CakeRegistry.INSTANCE) {
         ResourceLocation id = Amendments.res(type.getVariantId("double"));
         DoubleCakeBlock block = new DoubleCakeBlock(type);
         type.addChild("double_cake", block);
         event.register(id, block);
         DOUBLE_CAKES.put(type, block);
      }
   }

   public static <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> regTile(String name, Supplier<T> sup) {
      return RegHelper.registerBlockEntityType(Amendments.res(name), sup);
   }

   public static <T extends Block> RegSupplier<T> regBlock(String name, Supplier<T> sup) {
      return RegHelper.registerBlock(Amendments.res(name), sup);
   }

   public static <T extends Item> RegSupplier<T> regItem(String name, Supplier<T> sup) {
      return RegHelper.registerItem(Amendments.res(name), sup);
   }

   public static <T extends Entity> Supplier<EntityType<T>> regEntity(String name, Builder<T> builder) {
      return RegHelper.registerEntityType(Amendments.res(name), builder);
   }
}
