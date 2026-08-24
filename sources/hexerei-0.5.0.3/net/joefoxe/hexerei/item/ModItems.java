package net.joefoxe.hexerei.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.block.custom.SageBurningPlate;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.HexereiPaintingEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModChestBoatEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomBrushBaseModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomLargeSatchelModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomMediumSatchelModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomNetheriteTipModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomRingsModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomSeatModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomSmallSatchelModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomStickBaseModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomThrusterBrushModel;
import net.joefoxe.hexerei.client.renderer.entity.model.BroomWaterproofTipModel;
import net.joefoxe.hexerei.client.renderer.entity.model.MoonDustBrushModel;
import net.joefoxe.hexerei.client.renderer.entity.model.WitchHazelBroomStickModel;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.data.loot.CopyCofferDataFunction;
import net.joefoxe.hexerei.data.loot.CopyCourierLetterDataFunction;
import net.joefoxe.hexerei.data.loot.CopyCourierPackageDataFunction;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.item.custom.BlendItem;
import net.joefoxe.hexerei.item.custom.BroomAttachmentItem;
import net.joefoxe.hexerei.item.custom.BroomBrushItem;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.joefoxe.hexerei.item.custom.BroomSeatItem;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.item.custom.CleaningClothItem;
import net.joefoxe.hexerei.item.custom.CofferItem;
import net.joefoxe.hexerei.item.custom.CourierLetterItem;
import net.joefoxe.hexerei.item.custom.CourierPackageItem;
import net.joefoxe.hexerei.item.custom.CrowAmuletItem;
import net.joefoxe.hexerei.item.custom.CrowFluteItem;
import net.joefoxe.hexerei.item.custom.DowsingRodItem;
import net.joefoxe.hexerei.item.custom.DyeableCarpetItem;
import net.joefoxe.hexerei.item.custom.FlowerOutputItem;
import net.joefoxe.hexerei.item.custom.FloweringLilyPadItem;
import net.joefoxe.hexerei.item.custom.GlassesItem;
import net.joefoxe.hexerei.item.custom.HerbJarItem;
import net.joefoxe.hexerei.item.custom.KeychainItem;
import net.joefoxe.hexerei.item.custom.MixingCauldronItem;
import net.joefoxe.hexerei.item.custom.ModBoatItem;
import net.joefoxe.hexerei.item.custom.ModChestBoatItem;
import net.joefoxe.hexerei.item.custom.ModChestItem;
import net.joefoxe.hexerei.item.custom.MushroomWitchArmorItem;
import net.joefoxe.hexerei.item.custom.SatchelItem;
import net.joefoxe.hexerei.item.custom.SeedMixtureItem;
import net.joefoxe.hexerei.item.custom.TallowImpurityItem;
import net.joefoxe.hexerei.item.custom.WaxBlendItem;
import net.joefoxe.hexerei.item.custom.WaxingKitItem;
import net.joefoxe.hexerei.item.custom.WhistleItem;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.item.custom.bottles.BottleBloodtem;
import net.joefoxe.hexerei.item.custom.bottles.BottleLavaItem;
import net.joefoxe.hexerei.item.custom.bottles.BottleMilkItem;
import net.joefoxe.hexerei.item.custom.bottles.BottleQuicksilverItem;
import net.joefoxe.hexerei.item.custom.bottles.BottleTallowItem;
import net.joefoxe.hexerei.item.data_components.BookColorData;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.BroomEnderSatchelBrushParticlePacket;
import net.joefoxe.hexerei.util.message.OpenOwlCourierDepotNameEditorPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, "hexerei");
   public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES = DeferredRegister.create(
      Registries.LOOT_FUNCTION_TYPE.location(), "hexerei"
   );
   public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyCofferDataFunction>> COPY_COFFER_DATA = LOOT_FUNCTION_TYPES.register(
      "copy_coffer_data", () -> new LootItemFunctionType(CopyCofferDataFunction.CODEC)
   );
   public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyCourierPackageDataFunction>> COPY_PACKAGE_DATA = LOOT_FUNCTION_TYPES.register(
      "copy_package_data", () -> new LootItemFunctionType(CopyCourierPackageDataFunction.CODEC)
   );
   public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CopyCourierLetterDataFunction>> COPY_LETTER_DATA = LOOT_FUNCTION_TYPES.register(
      "copy_letter_data", () -> new LootItemFunctionType(CopyCourierLetterDataFunction.CODEC)
   );
   public static final DeferredHolder<Item, Item> BOOK_OF_SHADOWS = ITEMS.register(
      "book_of_shadows",
      () -> new HexereiBookItem(
         new Properties().component(ModDataComponents.BOOK, BookData.EMPTY).component(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY).stacksTo(1)
      )
   );
   public static final DeferredHolder<Item, Item> NOTEBOOK = ITEMS.register(
      "notebook",
      () -> new HexereiBookItem(
         new Properties()
            .component(ModDataComponents.BOOK, BookData.EMPTY_NOTEBOOK)
            .component(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY_NOTEBOOK)
            .stacksTo(1)
      ) {
         @Override
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.notebook").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }
      }
   );
   public static final DeferredHolder<Item, Item> BOOK_OF_COLORS = ITEMS.register(
      "book_of_colors",
      () -> new HexereiBookItem(
         new Properties()
            .component(ModDataComponents.BOOK, BookData.EMPTY_AS.apply(HexereiUtil.getResource("book_of_colors")))
            .component(ModDataComponents.BOOK_COLORS, BookColorData.EMPTY_COLORS)
            .stacksTo(1)
      ) {
         @Override
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.book_of_colors").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }
      }
   );
   public static final DeferredHolder<Item, Item> BOOK_CANVAS = ITEMS.register(
      "book_canvas",
      () -> new HangingEntityItem((EntityType)ModEntityTypes.BOOK_CANVAS.get(), new Properties()) {
         public InteractionResult useOn(UseOnContext context) {
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockPos blockpos1 = blockpos.relative(direction);
            Player player = context.getPlayer();
            ItemStack itemstack = context.getItemInHand();
            if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
               return InteractionResult.FAIL;
            } else {
               Level level = context.getLevel();
               HexereiPaintingEntity painting = HexereiPaintingEntity.create(level, blockpos1, direction);
               painting.setDirection(direction);
               CustomData customdata = (CustomData)itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
               if (!customdata.isEmpty()) {
                  EntityType.updateCustomEntityTag(level, player, painting, customdata);
               }

               if (painting.survives()) {
                  if (!level.isClientSide) {
                     painting.playPlacementSound();
                     level.gameEvent(player, GameEvent.ENTITY_PLACE, painting.position());
                     level.addFreshEntity(painting);
                  }

                  itemstack.shrink(1);
                  return InteractionResult.sidedSuccess(level.isClientSide);
               } else {
                  return InteractionResult.CONSUME;
               }
            }
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.book_canvas").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.book_canvas2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_BROOM = ITEMS.register(
      "mahogany_broom", () -> new BroomItem("mahogany", new Properties().stacksTo(1).fireResistant()) {
         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.LAYER_LOCATION));
            this.outter_model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.POWER_LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/mahogany_broom.png");
            this.dye_texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/mahogany_broom.png");
         }
      }
   );
   public static final DeferredHolder<Item, Item> WILLOW_BROOM = ITEMS.register("willow_broom", () -> new BroomItem("willow", new Properties().stacksTo(1)) {
      @OnlyIn(Dist.CLIENT)
      @Override
      public void bakeModels() {
         EntityModelSet context = Minecraft.getInstance().getEntityModels();
         this.model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.LAYER_LOCATION));
         this.outter_model = new BroomStickBaseModel(context.bakeLayer(BroomStickBaseModel.POWER_LAYER_LOCATION));
         this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/willow_broom.png");
         this.dye_texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/willow_broom.png");
      }
   });
   public static final DeferredHolder<Item, Item> WITCH_HAZEL_BROOM = ITEMS.register(
      "witch_hazel_broom", () -> new BroomItem("witch_hazel", new Properties().stacksTo(1)) {
         @Override
         public Vec3 getBrushOffset() {
            return new Vec3(0.0, 0.0, 0.02500000037252903);
         }

         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new WitchHazelBroomStickModel(context.bakeLayer(WitchHazelBroomStickModel.LAYER_LOCATION));
            this.outter_model = new WitchHazelBroomStickModel(context.bakeLayer(WitchHazelBroomStickModel.POWER_LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/witch_hazel_broom.png");
            this.dye_texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/witch_hazel_broom.png");
         }
      }
   );
   public static final DeferredHolder<Item, Item> WHISTLE = ITEMS.register("broom_whistle", () -> new WhistleItem(new Properties().durability(100)));
   public static final DeferredHolder<Item, Item> WAX_BLEND = ITEMS.register("wax_blend", () -> new WaxBlendItem(new Properties()));
   public static final DeferredHolder<Item, Item> CLOTH = ITEMS.register("cloth", () -> new CleaningClothItem(new Properties()));
   public static final DeferredHolder<Item, Item> WAXING_KIT = ITEMS.register("waxing_kit", () -> new WaxingKitItem(new Properties().stacksTo(1), false));
   public static final DeferredHolder<Item, Item> CREATIVE_WAXING_KIT = ITEMS.register(
      "creative_waxing_kit", () -> new WaxingKitItem(new Properties().stacksTo(1).rarity(Rarity.EPIC), true)
   );
   public static final DeferredHolder<Item, Item> WILLOW_BOAT = ITEMS.register(
      "willow_boat", () -> new ModBoatItem(false, ModBoatEntity.Type.WILLOW, new Properties())
   );
   public static final DeferredHolder<Item, Item> POLISHED_WILLOW_BOAT = ITEMS.register(
      "polished_willow_boat", () -> new ModBoatItem(false, ModBoatEntity.Type.POLISHED_WILLOW, new Properties())
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_BOAT = ITEMS.register(
      "mahogany_boat", () -> new ModBoatItem(false, ModBoatEntity.Type.MAHOGANY, new Properties())
   );
   public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_BOAT = ITEMS.register(
      "polished_mahogany_boat", () -> new ModBoatItem(false, ModBoatEntity.Type.POLISHED_MAHOGANY, new Properties())
   );
   public static final DeferredHolder<Item, Item> WILLOW_CHEST_BOAT = ITEMS.register(
      "willow_chest_boat", () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.WILLOW, new Properties())
   );
   public static final DeferredHolder<Item, Item> POLISHED_WILLOW_CHEST_BOAT = ITEMS.register(
      "polished_willow_chest_boat", () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.POLISHED_WILLOW, new Properties())
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_CHEST_BOAT = ITEMS.register(
      "mahogany_chest_boat", () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.MAHOGANY, new Properties())
   );
   public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_CHEST_BOAT = ITEMS.register(
      "polished_mahogany_chest_boat", () -> new ModChestBoatItem(false, ModChestBoatEntity.Type.POLISHED_MAHOGANY, new Properties())
   );
   public static final DeferredHolder<Item, Item> SMALL_SATCHEL = ITEMS.register(
      "small_satchel",
      () -> new SatchelItem(new Properties()) {
         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomSmallSatchelModel(context.bakeLayer(BroomSmallSatchelModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_small_satchel.png");
            this.dye_texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_small_satchel_dye.png");
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.small_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> MEDIUM_SATCHEL = ITEMS.register(
      "medium_satchel",
      () -> new SatchelItem(new Properties()) {
         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_satchel.png");
            this.dye_texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_satchel_dye.png");
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.medium_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> LARGE_SATCHEL = ITEMS.register(
      "large_satchel",
      () -> new SatchelItem(new Properties()) {
         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomLargeSatchelModel(context.bakeLayer(BroomLargeSatchelModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_large_satchel.png");
            this.dye_texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_large_satchel_dye.png");
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.large_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> ENDER_SATCHEL = ITEMS.register(
      "ender_satchel",
      () -> new SatchelItem(new Properties()) {
         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_ender_satchel.png");
            this.dye_texture = null;
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.ender_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> REPLACER_SATCHEL = ITEMS.register(
      "replacer_satchel",
      () -> new SatchelItem(new Properties()) {
         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomMediumSatchelModel(context.bakeLayer(BroomMediumSatchelModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_replacer_satchel.png");
            this.dye_texture = null;
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.medium_satchel").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.replacer_satchel_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.replacer_satchel_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }

         @Override
         public void onBrushDamage(BroomEntity broom, RandomSource random) {
            if (broom.getModule(BroomEntity.BroomSlot.BRUSH).isEmpty()) {
               int extraBrushSlot = broom.getExtraBrush();
               if (extraBrushSlot != -1) {
                  broom.setModule(BroomEntity.BroomSlot.BRUSH, broom.itemHandler.getStackInSlot(extraBrushSlot).copy());
                  broom.itemHandler.setStackInSlot(extraBrushSlot, ItemStack.EMPTY);
                  broom.level().playSound(null, broom, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                  HexereiPacketHandler.sendToNearbyClient(broom.level(), broom, new BroomEnderSatchelBrushParticlePacket(broom.getId()));
                  broom.sync();
               } else {
                  broom.level().playSound(null, broom, SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
               }
            }
         }
      }
   );
   public static final DeferredHolder<Item, Item> BROOM_SEAT = ITEMS.register(
      "broom_seat",
      () -> new BroomSeatItem(new Properties()) {
         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomSeatModel(context.bakeLayer(BroomSeatModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_seat.png");
            this.dye_texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_seat_dye.png");
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_seat_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_seat_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> GOLD_RINGS = ITEMS.register("gold_rings", () -> new BroomAttachmentItem(new Properties()) {
      @OnlyIn(Dist.CLIENT)
      @Override
      public void bakeModels() {
         EntityModelSet context = Minecraft.getInstance().getEntityModels();
         this.model = new BroomRingsModel(context.bakeLayer(BroomRingsModel.LAYER_LOCATION));
         this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom.png");
         this.dye_texture = null;
      }

      public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
         tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      }
   });
   public static final DeferredHolder<Item, Item> BROOM_NETHERITE_TIP = ITEMS.register(
      "broom_netherite_tip",
      () -> new BroomAttachmentItem(new Properties().durability(200)) {
         public int getMaxDamage(ItemStack stack) {
            return (Integer)HexConfig.BROOM_NETHERITE_TIP_DURABILITY.get();
         }

         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomNetheriteTipModel(context.bakeLayer(BroomNetheriteTipModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_netherite_tip.png");
            this.dye_texture = null;
         }

         @Override
         public boolean shouldRenderParticles(BroomEntity broom, Level world, BroomEntity.Status status) {
            return status == BroomEntity.Status.UNDER_WATER || status == BroomEntity.Status.UNDER_FLOWING_WATER;
         }

         @Override
         public void renderParticles(BroomEntity broom, Level world, BroomEntity.Status status, RandomSource random) {
            if (random.nextInt(2) == 0) {
               float rotOffset = random.nextFloat() * 10.0F - 5.0F;
               world.addParticle(
                  ParticleTypes.SMALL_FLAME,
                  broom.getX()
                     - Math.sin((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  broom.getY() + broom.floatingOffset + 0.25F * random.nextFloat() - broom.getDeltaMovement().y(),
                  broom.getZ()
                     + Math.cos((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015
               );
            }

            if (random.nextInt(2) == 0) {
               float rotOffset = random.nextFloat() * 10.0F - 5.0F;
               world.addParticle(
                  ParticleTypes.SMOKE,
                  broom.getX()
                     - Math.sin((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  broom.getY() + broom.floatingOffset + 0.25F * random.nextFloat() - broom.getDeltaMovement().y(),
                  broom.getZ()
                     + Math.cos((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015
               );
            }
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(
                  Component.translatable("tooltip.hexerei.broom_netherite_tip").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> BROOM_WATERPROOF_TIP = ITEMS.register(
      "broom_waterproof_tip",
      () -> new BroomAttachmentItem(new Properties().durability(800)) {
         public int getMaxDamage(ItemStack stack) {
            return (Integer)HexConfig.BROOM_WATERPROOF_TIP_DURABILITY.get();
         }

         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomWaterproofTipModel(context.bakeLayer(BroomWaterproofTipModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom_waterproof_tip.png");
            this.dye_texture = null;
         }

         @Override
         public boolean shouldRenderParticles(BroomEntity broom, Level world, BroomEntity.Status status) {
            return status == BroomEntity.Status.UNDER_WATER || status == BroomEntity.Status.UNDER_FLOWING_WATER;
         }

         @Override
         public void renderParticles(BroomEntity broom, Level world, BroomEntity.Status status, RandomSource random) {
            if (random.nextInt(2) == 0) {
               float rotOffset = random.nextFloat() * 10.0F - 5.0F;
               world.addParticle(
                  ParticleTypes.BUBBLE,
                  broom.getX()
                     - Math.sin((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  broom.getY() + broom.floatingOffset + 0.25F * random.nextFloat() - broom.getDeltaMovement().y(),
                  broom.getZ()
                     + Math.cos((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015
               );
            }

            if (random.nextInt(2) == 0) {
               float rotOffset = random.nextFloat() * 10.0F - 5.0F;
               world.addParticle(
                  ParticleTypes.BUBBLE_POP,
                  broom.getX()
                     - Math.sin((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  broom.getY() + broom.floatingOffset + 0.25F * random.nextFloat() - broom.getDeltaMovement().y(),
                  broom.getZ()
                     + Math.cos((broom.getYRot() - 90.0F + broom.deltaRotation + rotOffset) / 180.0F * 3.141592653589793)
                        * (1.25 + broom.getDeltaMovement().length() / 4.0),
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015,
                  (random.nextDouble() - 0.5) * 0.015
               );
            }
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(
                  Component.translatable("tooltip.hexerei.broom_waterproof_tip").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> BROOM_KEYCHAIN = ITEMS.register("broom_keychain", () -> new KeychainItem(new Properties()));
   public static final DeferredHolder<Item, Item> BROOM_KEYCHAIN_BASE = ITEMS.register(
      "broom_keychain_base",
      () -> new Item(new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(
               Component.translatable("the base is not for use, see the broom keychain.").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WET_BROOM_BRUSH = ITEMS.register("wet_broom_brush", () -> new Item(new Properties()) {
      public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
         tooltipComponents.add(Component.translatable("tooltip.hexerei.wet_broom_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      }
   });
   public static final DeferredHolder<Item, Item> BROOM_BRUSH = ITEMS.register("broom_brush", () -> new BroomBrushItem(new Properties().durability(100)) {
      public int getMaxDamage(ItemStack stack) {
         return (Integer)HexConfig.BROOM_BRUSH_DURABILITY.get();
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void bakeModels() {
         EntityModelSet context = Minecraft.getInstance().getEntityModels();
         this.model = new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.LAYER_LOCATION));
         this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/broom.png");
         this.dye_texture = null;
         this.list = new ArrayList<>();
         this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM.get(), 5));
         this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_2.get(), 2));
         this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_3.get(), 8));
         this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_4.get(), 50));
         this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_5.get(), 50));
         this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_6.get(), 50));
      }

      public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
         tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      }
   });
   public static final DeferredHolder<Item, Item> BROOM_THRUSTER_BRUSH = ITEMS.register(
      "broom_thruster_brush", () -> new BroomBrushItem(new Properties().durability(400)) {
         public int getMaxDamage(ItemStack stack) {
            return (Integer)HexConfig.THRUSTER_BRUSH_DURABILITY.get();
         }

         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomThrusterBrushModel(context.bakeLayer(BroomThrusterBrushModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/thruster_brush.png");
            this.dye_texture = null;
            this.list = new ArrayList<>();
            this.list.add(new Tuple(ParticleTypes.SMALL_FLAME, 5));
            this.list.add(new Tuple(ParticleTypes.FLAME, 2));
            this.list.add(new Tuple(ParticleTypes.SMOKE, 8));
            this.list.add(new Tuple(ParticleTypes.LARGE_SMOKE, 50));
         }

         @Override
         public float getSpeedModifier() {
            return 1.5F;
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WET_MOON_DUST_BRUSH = ITEMS.register("wet_moon_dust_brush", () -> new Item(new Properties()) {
      public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
         tooltipComponents.add(Component.translatable("tooltip.hexerei.wet_broom_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      }
   });
   public static final DeferredHolder<Item, Item> MOON_DUST_BRUSH = ITEMS.register(
      "moon_dust_brush", () -> new BroomBrushItem(new Properties().durability(200)) {
         public int getMaxDamage(ItemStack stack) {
            return (Integer)HexConfig.MOON_DUST_BRUSH_DURABILITY.get();
         }

         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new MoonDustBrushModel(context.bakeLayer(MoonDustBrushModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/moon_dust_brush.png");
            this.dye_texture = null;
            this.list = new ArrayList<>();
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.STAR_BRUSH.get(), 5));
            this.list.add(new Tuple(ParticleTypes.ELECTRIC_SPARK, 2));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.MOON_BRUSH_1.get(), 8));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.MOON_BRUSH_2.get(), 50));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.MOON_BRUSH_3.get(), 50));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.MOON_BRUSH_4.get(), 50));
         }

         @Override
         public boolean shouldGlow(Level level, ItemStack brushStack) {
            float time = level.getTimeOfDay(0.0F);
            return time > 0.25F && time < 0.75F && level.getMoonPhase() == 0 && !level.dimensionType().hasFixedTime();
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.moon_dust_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }

         @Override
         public float getSpeedModifier(BroomEntity broom) {
            float time = broom.level().getTimeOfDay(0.0F);
            return time > 0.25F && time < 0.75F && broom.level().getMoonPhase() == 0 && !broom.level().dimensionType().hasFixedTime() ? 1.0F : 0.25F;
         }
      }
   );
   public static final DeferredHolder<Item, Item> WET_HERB_ENHANCED_BROOM_BRUSH = ITEMS.register(
      "wet_herb_enhanced_broom_brush", () -> new Item(new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.wet_broom_brush").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> HERB_ENHANCED_BROOM_BRUSH = ITEMS.register(
      "herb_enhanced_broom_brush", () -> new BroomBrushItem(new Properties().durability(200)) {
         public int getMaxDamage(ItemStack stack) {
            return (Integer)HexConfig.HERB_ENHANCED_BRUSH_DURABILITY.get();
         }

         @OnlyIn(Dist.CLIENT)
         @Override
         public void bakeModels() {
            EntityModelSet context = Minecraft.getInstance().getEntityModels();
            this.model = new BroomBrushBaseModel(context.bakeLayer(BroomBrushBaseModel.LAYER_LOCATION));
            this.texture = ResourceLocation.fromNamespaceAndPath("hexerei", "textures/entity/herb_enhanced_brush.png");
            this.dye_texture = null;
            this.list = new ArrayList<>();
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM.get(), 5));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_2.get(), 2));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_3.get(), 8));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_4.get(), 50));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_5.get(), 50));
            this.list.add(new Tuple((ParticleOptions)ModParticleTypes.BROOM_6.get(), 50));
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_attachments").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WARHAMMER = ITEMS.register(
      "warhammer", () -> new SwordItem(Tiers.NETHERITE, new Properties().attributes(SwordItem.createAttributes(Tiers.WOOD, 3, -2.4F)))
   );
   public static final DeferredHolder<Item, Item> BLOOD_BUCKET = ITEMS.register(
      "blood_bucket", () -> new BucketItem((Fluid)ModFluids.BLOOD_FLUID.value(), new Properties().stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> TALLOW_BUCKET = ITEMS.register(
      "tallow_bucket", () -> new BucketItem((Fluid)ModFluids.TALLOW_FLUID.value(), new Properties().stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> QUICKSILVER_BUCKET = ITEMS.register(
      "quicksilver_bucket", () -> new BucketItem((Fluid)ModFluids.QUICKSILVER_FLUID.value(), new Properties().stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> QUICKSILVER_BOTTLE = ITEMS.register("quicksilver_bottle", () -> new BottleQuicksilverItem(new Properties()));
   public static final DeferredHolder<Item, Item> BLOOD_BOTTLE = ITEMS.register("blood_bottle", () -> new BottleBloodtem(new Properties()));
   public static final DeferredHolder<Item, Item> TALLOW_BOTTLE = ITEMS.register("tallow_bottle", () -> new BottleTallowItem(new Properties()));
   public static final DeferredHolder<Item, Item> LAVA_BOTTLE = ITEMS.register("lava_bottle", () -> new BottleLavaItem(new Properties().durability(100)));
   public static final DeferredHolder<Item, Item> MILK_BOTTLE = ITEMS.register("milk_bottle", () -> new BottleMilkItem(new Properties()));
   public static final DeferredHolder<Item, Item> BLOOD_SIGIL = ITEMS.register("blood_sigil", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> ANIMAL_FAT = ITEMS.register("animal_fat", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> TALLOW_IMPURITY = ITEMS.register("tallow_impurity", () -> new TallowImpurityItem(new Properties()));
   public static final DeferredHolder<Item, Item> INFUSED_FABRIC = ITEMS.register("infused_fabric", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> SELENITE_SHARD = ITEMS.register(
      "selenite_shard",
      () -> new Item(new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.selenite_shard").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> SAGE = ITEMS.register("sage", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> SAGE_SEED = ITEMS.register(
      "sage_seed", () -> new ItemNameBlockItem((Block)ModBlocks.SAGE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.sage_seeds").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> SAGE_BUNDLE = ITEMS.register("sage_bundle", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> DRIED_SAGE_BUNDLE = ITEMS.register(
      "dried_sage_bundle",
      () -> new Item(new Properties().durability(3600)) {
         public int getMaxDamage(ItemStack stack) {
            return (Integer)HexConfig.SAGE_BUNDLE_DURATION.get();
         }

         public boolean isEnchantable(ItemStack p_41456_) {
            return false;
         }

         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               int duration = stack.getMaxDamage() - stack.getDamageValue();
               float percentDamaged = (float)stack.getDamageValue() / stack.getMaxDamage();
               int minutes = duration / 60;
               int seconds = duration % 60;
               ChatFormatting col = ChatFormatting.GREEN;
               MutableComponent component = Component.literal("");
               if (percentDamaged > 0.4F) {
                  col = ChatFormatting.DARK_GREEN;
               }

               if (percentDamaged > 0.6F) {
                  col = ChatFormatting.YELLOW;
               }

               if (percentDamaged > 0.7F) {
                  col = ChatFormatting.GOLD;
               }

               if (percentDamaged > 0.85F) {
                  col = ChatFormatting.RED;
               }

               if (percentDamaged > 0.95F) {
                  col = ChatFormatting.DARK_RED;
               }

               MutableComponent minutesText = Component.translatable("tooltip.hexerei.minutes").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
               MutableComponent minuteText = Component.translatable("tooltip.hexerei.minute").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
               MutableComponent secondsText = Component.translatable("tooltip.hexerei.seconds").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
               MutableComponent secondText = Component.translatable("tooltip.hexerei.second").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
               if (minutes > 1) {
                  component.append(String.valueOf(minutes)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(minutesText);
                  if (seconds >= 1) {
                     component.append(" ");
                     if (seconds > 1) {
                        component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondsText);
                     } else {
                        component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondText);
                     }
                  }
               } else if (minutes == 1) {
                  component.append(String.valueOf(minutes)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(minuteText);
                  if (seconds >= 1) {
                     component.append(" ");
                     if (seconds > 1) {
                        component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondsText);
                     } else {
                        component.append(String.valueOf(seconds)).withStyle(Style.EMPTY.withColor(col)).append(" ").append(secondText);
                     }
                  }
               }

               MutableComponent itemText = Component.translatable(((SageBurningPlate)ModBlocks.SAGE_BURNING_PLATE.get()).getDescriptionId())
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10061824)));
               tooltipComponents.add(
                  Component.translatable("tooltip.hexerei.dried_sage_bundle_shift_1", new Object[]{component})
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(
                  Component.translatable("tooltip.hexerei.dried_sage_bundle_shift_2", new Object[]{itemText})
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> LILY_PAD_ITEM = ITEMS.register(
      "flowering_lily_pad", () -> new FloweringLilyPadItem((Block)ModBlocks.LILY_PAD_BLOCK.get(), new Properties())
   );
   public static final DeferredHolder<Item, FlowerOutputItem> BELLADONNA_FLOWERS = ITEMS.register(
      "belladonna_flowers", () -> new FlowerOutputItem(new Properties())
   );
   public static final DeferredHolder<Item, FlowerOutputItem> BELLADONNA_BERRIES = ITEMS.register(
      "belladonna_berries",
      () -> new FlowerOutputItem(
         new Properties()
            .food(new Builder().nutrition(1).saturationModifier(0.1F).fast().effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 2), 100.0F).build())
      )
   );
   public static final DeferredHolder<Item, FlowerOutputItem> MANDRAKE_FLOWERS = ITEMS.register(
      "mandrake_flowers", () -> new FlowerOutputItem(new Properties())
   );
   public static final DeferredHolder<Item, FlowerOutputItem> MANDRAKE_ROOT = ITEMS.register("mandrake_root", () -> new FlowerOutputItem(new Properties()));
   public static final DeferredHolder<Item, FlowerOutputItem> MUGWORT_FLOWERS = ITEMS.register("mugwort_flowers", () -> new FlowerOutputItem(new Properties()));
   public static final DeferredHolder<Item, FlowerOutputItem> MUGWORT_LEAVES = ITEMS.register("mugwort_leaves", () -> new FlowerOutputItem(new Properties()));
   public static final DeferredHolder<Item, FlowerOutputItem> YELLOW_DOCK_FLOWERS = ITEMS.register(
      "yellow_dock_flowers", () -> new FlowerOutputItem(new Properties())
   );
   public static final DeferredHolder<Item, FlowerOutputItem> YELLOW_DOCK_LEAVES = ITEMS.register(
      "yellow_dock_leaves", () -> new FlowerOutputItem(new Properties())
   );
   public static final DeferredHolder<Item, Item> DRIED_SAGE = ITEMS.register("dried_sage", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> DRIED_BELLADONNA_FLOWERS = ITEMS.register("dried_belladonna_flowers", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> DRIED_MANDRAKE_FLOWERS = ITEMS.register("dried_mandrake_flowers", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> DRIED_MUGWORT_FLOWERS = ITEMS.register("dried_mugwort_flowers", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> DRIED_MUGWORT_LEAVES = ITEMS.register("dried_mugwort_leaves", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> DRIED_YELLOW_DOCK_FLOWERS = ITEMS.register("dried_yellow_dock_flowers", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> DRIED_YELLOW_DOCK_LEAVES = ITEMS.register("dried_yellow_dock_leaves", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, BlendItem> MINDFUL_TRANCE_BLEND = ITEMS.register("mindful_trance_blend", () -> new BlendItem(new Properties()));
   public static final DeferredHolder<Item, DowsingRodItem> DOWSING_ROD = ITEMS.register("dowsing_rod", () -> new DowsingRodItem(new Properties()));
   public static final DeferredHolder<Item, Item> SEED_MIXTURE = ITEMS.register("seed_mixture", () -> new SeedMixtureItem(new Properties()));
   public static final DeferredHolder<Item, Item> CROW_FLUTE = ITEMS.register(
      "crow_flute", () -> new CrowFluteItem(new Properties().component(ModDataComponents.FLUTE, FluteData.EMPTY))
   );
   public static final DeferredHolder<Item, DeferredSpawnEggItem> CROW_SPAWN_EGG = ITEMS.register(
      "crow_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.CROW, 1447446, 3355443, new Properties())
   );
   public static final DeferredHolder<Item, DeferredSpawnEggItem> OWL_SPAWN_EGG = ITEMS.register(
      "owl_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.OWL, 4929570, 13283727, new Properties())
   );
   public static final DeferredHolder<Item, Item> CROW_ANKH_AMULET = ITEMS.register(
      "crow_ankh_amulet",
      () -> new Item(new Properties().stacksTo(1)) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_ankh_amulet_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_ankh_amulet_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> CROW_BLANK_AMULET = ITEMS.register(
      "crow_blank_amulet", () -> new CrowAmuletItem(new Properties().stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> CROW_BLANK_AMULET_TRINKET = ITEMS.register("crow_blank_amulet_trinket", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> CROW_BLANK_AMULET_TRINKET_FRAME = ITEMS.register(
      "crow_blank_amulet_trinket_frame", () -> new Item(new Properties())
   );
   public static final DeferredHolder<Item, GlassesItem> READING_GLASSES = ITEMS.register("reading_glasses", () -> new GlassesItem(new Properties()));
   public static final DeferredHolder<Item, Item> MOON_DUST = ITEMS.register("moon_dust", () -> new Item(new Properties()));
   public static final DeferredHolder<Item, Item> WITCH_HELMET = ITEMS.register(
      "witch_helmet", () -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(33)))
   );
   public static final DeferredHolder<Item, Item> WITCH_CHESTPLATE = ITEMS.register(
      "witch_chestplate",
      () -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, Type.CHESTPLATE, new Properties().durability(Type.CHESTPLATE.getDurability(33)))
   );
   public static final DeferredHolder<Item, Item> WITCH_BOOTS = ITEMS.register(
      "witch_boots", () -> new WitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, Type.BOOTS, new Properties().durability(Type.BOOTS.getDurability(33)))
   );
   public static final DeferredHolder<Item, Item> MUSHROOM_WITCH_HAT = ITEMS.register(
      "mushroom_witch_hat",
      () -> new MushroomWitchArmorItem(ModArmorMaterial.INFUSED_FABRIC, Type.HELMET, new Properties().durability(Type.HELMET.getDurability(33)))
   );
   public static final DeferredHolder<Item, Item> WILLOW_SIGN = ITEMS.register(
      "willow_sign", () -> new SignItem(new Properties(), (Block)ModBlocks.WILLOW_SIGN.get(), (Block)ModBlocks.WILLOW_WALL_SIGN.get())
   );
   public static final DeferredHolder<Item, Item> WITCH_HAZEL_SIGN = ITEMS.register(
      "witch_hazel_sign", () -> new SignItem(new Properties(), (Block)ModBlocks.WITCH_HAZEL_SIGN.get(), (Block)ModBlocks.WITCH_HAZEL_WALL_SIGN.get())
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_SIGN = ITEMS.register(
      "mahogany_sign", () -> new SignItem(new Properties(), (Block)ModBlocks.MAHOGANY_SIGN.get(), (Block)ModBlocks.MAHOGANY_WALL_SIGN.get())
   );
   public static final DeferredHolder<Item, Item> POLISHED_WILLOW_SIGN = ITEMS.register(
      "polished_willow_sign",
      () -> new SignItem(new Properties(), (Block)ModBlocks.POLISHED_WILLOW_SIGN.get(), (Block)ModBlocks.POLISHED_WILLOW_WALL_SIGN.get())
   );
   public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_SIGN = ITEMS.register(
      "polished_witch_hazel_sign",
      () -> new SignItem(new Properties(), (Block)ModBlocks.POLISHED_WITCH_HAZEL_SIGN.get(), (Block)ModBlocks.POLISHED_WITCH_HAZEL_WALL_SIGN.get())
   );
   public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_SIGN = ITEMS.register(
      "polished_mahogany_sign",
      () -> new SignItem(new Properties(), (Block)ModBlocks.POLISHED_MAHOGANY_SIGN.get(), (Block)ModBlocks.POLISHED_MAHOGANY_WALL_SIGN.get())
   );
   public static final DeferredHolder<Item, Item> WILLOW_HANGING_SIGN = ITEMS.register(
      "willow_hanging_sign",
      () -> new HangingSignItem((Block)ModBlocks.WILLOW_HANGING_SIGN.get(), (Block)ModBlocks.WILLOW_WALL_HANGING_SIGN.get(), new Properties().stacksTo(16))
   );
   public static final DeferredHolder<Item, Item> WITCH_HAZEL_HANGING_SIGN = ITEMS.register(
      "witch_hazel_hanging_sign",
      () -> new HangingSignItem(
         (Block)ModBlocks.WITCH_HAZEL_HANGING_SIGN.get(), (Block)ModBlocks.WITCH_HAZEL_WALL_HANGING_SIGN.get(), new Properties().stacksTo(16)
      )
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_HANGING_SIGN = ITEMS.register(
      "mahogany_hanging_sign",
      () -> new HangingSignItem((Block)ModBlocks.MAHOGANY_HANGING_SIGN.get(), (Block)ModBlocks.MAHOGANY_WALL_HANGING_SIGN.get(), new Properties().stacksTo(16))
   );
   public static final DeferredHolder<Item, Item> WILLOW_CHEST = ITEMS.register(
      "willow_chest", () -> new ModChestItem((Block)ModBlocks.WILLOW_CHEST.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> WITCH_HAZEL_CHEST = ITEMS.register(
      "witch_hazel_chest", () -> new ModChestItem((Block)ModBlocks.WITCH_HAZEL_CHEST.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_CHEST = ITEMS.register(
      "mahogany_chest", () -> new ModChestItem((Block)ModBlocks.MAHOGANY_CHEST.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> MIXING_CAULDRON = ITEMS.register(
      "mixing_cauldron", () -> new MixingCauldronItem((Block)ModBlocks.MIXING_CAULDRON.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> COFFER = ITEMS.register("coffer", () -> new CofferItem((Block)ModBlocks.COFFER.get(), new Properties()));
   public static final DeferredHolder<Item, Item> ENTANGLED_COFFER = ITEMS.register(
      "entangled_coffer", () -> new CofferItem((Block)ModBlocks.ENTANGLED_COFFER.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> HERB_JAR = ITEMS.register(
      "herb_jar", () -> new HerbJarItem((Block)ModBlocks.HERB_JAR.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> CANDLE = ITEMS.register("candle", () -> new CandleItem((Block)ModBlocks.CANDLE.get(), new Properties()));
   public static final DeferredHolder<Item, Item> PACKING_PEANUT = ITEMS.register(
      "packing_peanut", () -> new Item(new Properties().food(new Builder().saturationModifier(1.0F).nutrition(1).alwaysEdible().build()))
   );
   public static final DeferredHolder<Item, Item> COURIER_PACKAGE = ITEMS.register(
      "courier_package", () -> new CourierPackageItem((Block)ModBlocks.COURIER_PACKAGE.get(), new Properties()) {}
   );
   public static final DeferredHolder<Item, Item> COURIER_LETTER = ITEMS.register(
      "courier_letter", () -> new CourierLetterItem((Block)ModBlocks.COURIER_LETTER.get(), new Properties()) {}
   );
   public static final DeferredHolder<Item, StandingAndWallBlockItem> WILLOW_COURIER_DEPOT = ITEMS.register(
      "willow_courier_depot",
      () -> new StandingAndWallBlockItem(
         (Block)ModBlocks.WILLOW_COURIER_DEPOT.get(), (Block)ModBlocks.WILLOW_COURIER_DEPOT_WALL.get(), new Properties(), Direction.DOWN
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_depot").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }

         protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
            boolean flag = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
            if (!pLevel.isClientSide && !flag && pPlayer != null) {
               BlockEntity blockentity = pLevel.getBlockEntity(pPos);
               if (blockentity instanceof OwlCourierDepotTile
                  && pLevel.getBlockState(pPos).getBlock() instanceof OwlCourierDepot depotBlock
                  && pPlayer instanceof ServerPlayer serverPlayer) {
                  HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(pPos), serverPlayer);
               }
            }

            return flag;
         }
      }
   );
   public static final DeferredHolder<Item, StandingAndWallBlockItem> MAHOGANY_COURIER_DEPOT = ITEMS.register(
      "mahogany_courier_depot",
      () -> new StandingAndWallBlockItem(
         (Block)ModBlocks.MAHOGANY_COURIER_DEPOT.get(), (Block)ModBlocks.MAHOGANY_COURIER_DEPOT_WALL.get(), new Properties(), Direction.DOWN
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_depot").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }

         protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
            boolean flag = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
            if (!pLevel.isClientSide && !flag && pPlayer != null) {
               BlockEntity blockentity = pLevel.getBlockEntity(pPos);
               if (blockentity instanceof OwlCourierDepotTile
                  && pLevel.getBlockState(pPos).getBlock() instanceof OwlCourierDepot depotBlock
                  && pPlayer instanceof ServerPlayer serverPlayer) {
                  HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(pPos), serverPlayer);
               }
            }

            return flag;
         }
      }
   );
   public static final DeferredHolder<Item, StandingAndWallBlockItem> WITCH_HAZEL_COURIER_DEPOT = ITEMS.register(
      "witch_hazel_courier_depot",
      () -> new StandingAndWallBlockItem(
         (Block)ModBlocks.WITCH_HAZEL_COURIER_DEPOT.get(), (Block)ModBlocks.WITCH_HAZEL_COURIER_DEPOT_WALL.get(), new Properties(), Direction.DOWN
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.courier_depot").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }

         protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
            boolean flag = super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
            if (!pLevel.isClientSide && !flag && pPlayer != null) {
               BlockEntity blockentity = pLevel.getBlockEntity(pPos);
               if (blockentity instanceof OwlCourierDepotTile
                  && pLevel.getBlockState(pPos).getBlock() instanceof OwlCourierDepot depotBlock
                  && pPlayer instanceof ServerPlayer serverPlayer) {
                  HexereiPacketHandler.sendToPlayerClient(new OpenOwlCourierDepotNameEditorPacket(pPos), serverPlayer);
               }
            }

            return flag;
         }
      }
   );
   public static final DeferredHolder<Item, StandingAndWallBlockItem> MAHOGANY_BROOM_STAND = ITEMS.register(
      "mahogany_broom_stand",
      () -> new StandingAndWallBlockItem(
         (Block)ModBlocks.MAHOGANY_BROOM_STAND.get(), (Block)ModBlocks.MAHOGANY_BROOM_STAND_WALL.get(), new Properties(), Direction.DOWN
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_stand").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }
      }
   );
   public static final DeferredHolder<Item, StandingAndWallBlockItem> WILLOW_BROOM_STAND = ITEMS.register(
      "willow_broom_stand",
      () -> new StandingAndWallBlockItem(
         (Block)ModBlocks.WILLOW_BROOM_STAND.get(), (Block)ModBlocks.WILLOW_BROOM_STAND_WALL.get(), new Properties(), Direction.DOWN
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_stand").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }
      }
   );
   public static final DeferredHolder<Item, StandingAndWallBlockItem> WITCH_HAZEL_BROOM_STAND = ITEMS.register(
      "witch_hazel_broom_stand",
      () -> new StandingAndWallBlockItem(
         (Block)ModBlocks.WITCH_HAZEL_BROOM_STAND.get(), (Block)ModBlocks.WITCH_HAZEL_BROOM_STAND_WALL.get(), new Properties(), Direction.DOWN
      ) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            if (Screen.hasShiftDown()) {
               tooltipComponents.add(
                  Component.translatable(
                        "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_stand").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable(
                        "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
                     )
                     .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
            }
         }
      }
   );
   public static final DeferredHolder<Item, Item> STONE_WINDOW_PANE = ITEMS.register(
      "stone_window_pane", () -> new BlockItem((Block)ModBlocks.STONE_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> STONE_WINDOW = ITEMS.register(
      "stone_window", () -> new BlockItem((Block)ModBlocks.STONE_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_STONE_WINDOW_PANE = ITEMS.register(
      "waxed_stone_window_pane",
      () -> new BlockItem((Block)ModBlocks.WAXED_STONE_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_STONE_WINDOW = ITEMS.register(
      "waxed_stone_window",
      () -> new BlockItem((Block)ModBlocks.WAXED_STONE_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_WINDOW_PANE = ITEMS.register(
      "mahogany_window_pane", () -> new BlockItem((Block)ModBlocks.MAHOGANY_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WILLOW_WINDOW_PANE = ITEMS.register(
      "willow_window_pane", () -> new BlockItem((Block)ModBlocks.WILLOW_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WITCH_HAZEL_WINDOW_PANE = ITEMS.register(
      "witch_hazel_window_pane", () -> new BlockItem((Block)ModBlocks.WITCH_HAZEL_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_MAHOGANY_WINDOW_PANE = ITEMS.register(
      "waxed_mahogany_window_pane",
      () -> new BlockItem((Block)ModBlocks.WAXED_MAHOGANY_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_WILLOW_WINDOW_PANE = ITEMS.register(
      "waxed_willow_window_pane",
      () -> new BlockItem((Block)ModBlocks.WAXED_WILLOW_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_WITCH_HAZEL_WINDOW_PANE = ITEMS.register(
      "waxed_witch_hazel_window_pane",
      () -> new BlockItem((Block)ModBlocks.WAXED_WITCH_HAZEL_WINDOW_PANE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_WINDOW = ITEMS.register(
      "mahogany_window", () -> new BlockItem((Block)ModBlocks.MAHOGANY_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WILLOW_WINDOW = ITEMS.register(
      "willow_window", () -> new BlockItem((Block)ModBlocks.WILLOW_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WITCH_HAZEL_WINDOW = ITEMS.register(
      "witch_hazel_window", () -> new BlockItem((Block)ModBlocks.WITCH_HAZEL_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_MAHOGANY_WINDOW = ITEMS.register(
      "waxed_mahogany_window",
      () -> new BlockItem((Block)ModBlocks.WAXED_MAHOGANY_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_WILLOW_WINDOW = ITEMS.register(
      "waxed_willow_window",
      () -> new BlockItem((Block)ModBlocks.WAXED_WILLOW_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_WITCH_HAZEL_WINDOW = ITEMS.register(
      "waxed_witch_hazel_window",
      () -> new BlockItem((Block)ModBlocks.WAXED_WITCH_HAZEL_WINDOW.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> INFUSED_FABRIC_CARPET_ORNATE = ITEMS.register(
      "infused_fabric_carpet_ornate", () -> new DyeableCarpetItem((Block)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.infused_fabric_ornate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_CARPET_ORNATE = ITEMS.register(
      "waxed_infused_fabric_carpet_ornate",
      () -> new DyeableCarpetItem((Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> INFUSED_FABRIC_BLOCK_ORNATE = ITEMS.register(
      "infused_fabric_block_ornate", () -> new BlockItem((Block)ModBlocks.INFUSED_FABRIC_BLOCK_ORNATE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.infused_fabric_ornate").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_BLOCK_ORNATE = ITEMS.register(
      "waxed_infused_fabric_block_ornate",
      () -> new BlockItem((Block)ModBlocks.WAXED_INFUSED_FABRIC_BLOCK_ORNATE.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> INFUSED_FABRIC_CARPET = ITEMS.register(
      "infused_fabric_carpet", () -> new DyeableCarpetItem((Block)ModBlocks.INFUSED_FABRIC_CARPET.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_CARPET = ITEMS.register(
      "waxed_infused_fabric_carpet",
      () -> new DyeableCarpetItem((Block)ModBlocks.WAXED_INFUSED_FABRIC_CARPET.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> INFUSED_FABRIC_BLOCK = ITEMS.register(
      "infused_fabric_block", () -> new DyeableCarpetItem((Block)ModBlocks.INFUSED_FABRIC_BLOCK.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_INFUSED_FABRIC_BLOCK = ITEMS.register(
      "waxed_infused_fabric_block",
      () -> new DyeableCarpetItem((Block)ModBlocks.WAXED_INFUSED_FABRIC_BLOCK.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            tooltipComponents.add(Component.translatable("tooltip.hexerei.can_be_dyed").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WILLOW_CONNECTED = ITEMS.register(
      "willow_connected", () -> new BlockItem((Block)ModBlocks.WILLOW_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_WILLOW_CONNECTED = ITEMS.register(
      "polished_willow_connected", () -> new BlockItem((Block)ModBlocks.POLISHED_WILLOW_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_WILLOW_PILLAR = ITEMS.register(
      "polished_willow_pillar", () -> new BlockItem((Block)ModBlocks.POLISHED_WILLOW_PILLAR.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_WILLOW_LAYERED = ITEMS.register(
      "polished_willow_layered", () -> new BlockItem((Block)ModBlocks.POLISHED_WILLOW_LAYERED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WITCH_HAZEL_CONNECTED = ITEMS.register(
      "witch_hazel_connected", () -> new BlockItem((Block)ModBlocks.WITCH_HAZEL_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_CONNECTED = ITEMS.register(
      "polished_witch_hazel_connected", () -> new BlockItem((Block)ModBlocks.POLISHED_WITCH_HAZEL_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_PILLAR = ITEMS.register(
      "polished_witch_hazel_pillar", () -> new BlockItem((Block)ModBlocks.POLISHED_WITCH_HAZEL_PILLAR.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_LAYERED = ITEMS.register(
      "polished_witch_hazel_layered", () -> new BlockItem((Block)ModBlocks.POLISHED_WITCH_HAZEL_LAYERED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_MAHOGANY_CONNECTED = ITEMS.register(
      "waxed_polished_mahogany_connected",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_MAHOGANY_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_MAHOGANY_PILLAR = ITEMS.register(
      "waxed_polished_mahogany_pillar",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_MAHOGANY_PILLAR.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_MAHOGANY_LAYERED = ITEMS.register(
      "waxed_polished_mahogany_layered",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_MAHOGANY_LAYERED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_MAHOGANY_CONNECTED = ITEMS.register(
      "waxed_mahogany_connected",
      () -> new BlockItem((Block)ModBlocks.WAXED_MAHOGANY_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_WILLOW_CONNECTED = ITEMS.register(
      "waxed_polished_willow_connected",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_WILLOW_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_WILLOW_PILLAR = ITEMS.register(
      "waxed_polished_willow_pillar",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_WILLOW_PILLAR.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_WILLOW_LAYERED = ITEMS.register(
      "waxed_polished_willow_layered",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_WILLOW_LAYERED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_WILLOW_CONNECTED = ITEMS.register(
      "waxed_willow_connected",
      () -> new BlockItem((Block)ModBlocks.WAXED_WILLOW_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_WITCH_HAZEL_CONNECTED = ITEMS.register(
      "waxed_polished_witch_hazel_connected",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_WITCH_HAZEL_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_WITCH_HAZEL_PILLAR = ITEMS.register(
      "waxed_polished_witch_hazel_pillar",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_WITCH_HAZEL_PILLAR.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_POLISHED_WITCH_HAZEL_LAYERED = ITEMS.register(
      "waxed_polished_witch_hazel_layered",
      () -> new BlockItem((Block)ModBlocks.WAXED_POLISHED_WITCH_HAZEL_LAYERED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> WAXED_WITCH_HAZEL_CONNECTED = ITEMS.register(
      "waxed_witch_hazel_connected",
      () -> new BlockItem((Block)ModBlocks.WAXED_WITCH_HAZEL_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            Component cloth = Component.translatable(((Item)ModItems.CLOTH.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            Component waxing_kit = Component.translatable(((Item)ModItems.WAXING_KIT.get()).getDescription().getString())
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(7035654)));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.waxed_connected_texture", new Object[]{cloth, waxing_kit})
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> MAHOGANY_CONNECTED = ITEMS.register(
      "mahogany_connected", () -> new BlockItem((Block)ModBlocks.MAHOGANY_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_CONNECTED = ITEMS.register(
      "polished_mahogany_connected", () -> new BlockItem((Block)ModBlocks.POLISHED_MAHOGANY_CONNECTED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_PILLAR = ITEMS.register(
      "polished_mahogany_pillar", () -> new BlockItem((Block)ModBlocks.POLISHED_MAHOGANY_PILLAR.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_LAYERED = ITEMS.register(
      "polished_mahogany_layered", () -> new BlockItem((Block)ModBlocks.POLISHED_MAHOGANY_LAYERED.get(), new Properties()) {
         public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
            tooltipComponents.add(Component.translatable("tooltip.hexerei.connected_texture").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
         }
      }
   );
   public static final DeferredHolder<Item, Item> POLISHED_WITCH_HAZEL_TRAPDOOR = ITEMS.register(
      "polished_witch_hazel_trapdoor", () -> new BlockItem((Block)ModBlocks.POLISHED_WITCH_HAZEL_TRAPDOOR.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> POLISHED_WILLOW_TRAPDOOR = ITEMS.register(
      "polished_willow_trapdoor", () -> new BlockItem((Block)ModBlocks.POLISHED_WILLOW_TRAPDOOR.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> POLISHED_MAHOGANY_TRAPDOOR = ITEMS.register(
      "polished_mahogany_trapdoor", () -> new BlockItem((Block)ModBlocks.POLISHED_MAHOGANY_TRAPDOOR.get(), new Properties())
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
      LOOT_FUNCTION_TYPES.register(eventBus);
   }
}
