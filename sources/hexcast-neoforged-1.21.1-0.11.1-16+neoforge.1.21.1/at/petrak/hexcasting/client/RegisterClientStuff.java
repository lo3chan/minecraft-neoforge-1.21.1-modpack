package at.petrak.hexcasting.client;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.item.VariantItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.entity.WallScrollRenderer;
import at.petrak.hexcasting.client.render.GaslightingTracker;
import at.petrak.hexcasting.client.render.ScryingLensOverlays;
import at.petrak.hexcasting.client.render.be.BlockEntityAkashicBookshelfRenderer;
import at.petrak.hexcasting.client.render.be.BlockEntityQuenchedAllayRenderer;
import at.petrak.hexcasting.client.render.be.BlockEntitySlateRenderer;
import at.petrak.hexcasting.common.blocks.BlockQuenchedAllay;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import at.petrak.hexcasting.common.entities.HexEntities;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.items.magic.ItemMediaBattery;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import at.petrak.hexcasting.common.items.storage.ItemScroll;
import at.petrak.hexcasting.common.items.storage.ItemSlate;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import at.petrak.hexcasting.common.items.storage.ItemThoughtKnot;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public class RegisterClientStuff {
   public static Map<ResourceLocation, List<BakedModel>> QUENCHED_ALLAY_VARIANTS = new HashMap<>();
   private static final Map<BlockQuenchedAllay, Boolean> QUENCHED_ALLAY_TYPES = Map.of(
      HexBlocks.QUENCHED_ALLAY, false, HexBlocks.QUENCHED_ALLAY_TILES, true, HexBlocks.QUENCHED_ALLAY_BRICKS, true, HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL, true
   );

   public static void init() {
      registerSealableDataHolderOverrides(HexItems.FOCUS, stack -> HexItems.FOCUS.readIotaTag(stack) != null, ItemFocus::isSealed);
      registerSealableDataHolderOverrides(HexItems.SPELLBOOK, stack -> HexItems.SPELLBOOK.readIotaTag(stack) != null, ItemSpellbook::isSealed);
      registerVariantOverrides(HexItems.FOCUS, HexItems.FOCUS::getVariant);
      registerVariantOverrides(HexItems.SPELLBOOK, HexItems.SPELLBOOK::getVariant);
      registerVariantOverrides(HexItems.CYPHER, HexItems.CYPHER::getVariant);
      registerVariantOverrides(HexItems.TRINKET, HexItems.TRINKET::getVariant);
      registerVariantOverrides(HexItems.ARTIFACT, HexItems.ARTIFACT::getVariant);
      IClientXplatAbstractions.INSTANCE
         .registerItemProperty(
            HexItems.THOUGHT_KNOT, ItemThoughtKnot.WRITTEN_PRED, (stack, level, holder, holderID) -> NBTHelper.contains(stack, "data") ? 1.0F : 0.0F
         );
      registerPackagedSpellOverrides(HexItems.CYPHER);
      registerPackagedSpellOverrides(HexItems.TRINKET);
      registerPackagedSpellOverrides(HexItems.ARTIFACT);
      IClientXplatAbstractions x = IClientXplatAbstractions.INSTANCE;
      x.registerItemProperty(HexItems.BATTERY, ItemMediaBattery.MEDIA_PREDICATE, (stack, level, holder, holderID) -> {
         MediaHolderItem item = (MediaHolderItem)stack.getItem();
         return item.getMediaFullness(stack);
      });
      x.registerItemProperty(HexItems.BATTERY, ItemMediaBattery.MAX_MEDIA_PREDICATE, (stack, level, holder, holderID) -> {
         ItemMediaBattery item = (ItemMediaBattery)stack.getItem();
         long max = item.getMaxMedia(stack);
         return 1.049658F * (float)Math.log((float)max / 100000.0F + 9.06152F) - 2.1436F;
      });
      registerScrollOverrides(HexItems.SCROLL_SMOL);
      registerScrollOverrides(HexItems.SCROLL_MEDIUM);
      registerScrollOverrides(HexItems.SCROLL_LARGE);
      x.registerItemProperty(HexItems.SLATE, ItemSlate.WRITTEN_PRED, (stack, level, holder, holderID) -> ItemSlate.hasPattern(stack) ? 1.0F : 0.0F);
      registerWandOverrides(HexItems.STAFF_OAK);
      registerWandOverrides(HexItems.STAFF_BIRCH);
      registerWandOverrides(HexItems.STAFF_SPRUCE);
      registerWandOverrides(HexItems.STAFF_JUNGLE);
      registerWandOverrides(HexItems.STAFF_DARK_OAK);
      registerWandOverrides(HexItems.STAFF_ACACIA);
      registerWandOverrides(HexItems.STAFF_EDIFIED);
      registerWandOverrides(HexItems.STAFF_MINDSPLICE);
      registerGaslight4(HexItems.STAFF_QUENCHED);
      registerGaslight4(HexBlocks.QUENCHED_ALLAY.asItem());
      registerGaslight4(HexBlocks.QUENCHED_ALLAY_TILES.asItem());
      registerGaslight4(HexBlocks.QUENCHED_ALLAY_BRICKS.asItem());
      registerGaslight4(HexBlocks.QUENCHED_ALLAY_BRICKS_SMALL.asItem());
      registerGaslight4(HexItems.QUENCHED_SHARD);
      x.setRenderLayer(HexBlocks.CONJURED_LIGHT, RenderType.cutout());
      x.setRenderLayer(HexBlocks.CONJURED_BLOCK, RenderType.cutout());
      x.setRenderLayer(HexBlocks.EDIFIED_DOOR, RenderType.cutout());
      x.setRenderLayer(HexBlocks.EDIFIED_TRAPDOOR, RenderType.cutout());
      x.setRenderLayer(HexBlocks.AKASHIC_BOOKSHELF, RenderType.cutout());
      x.setRenderLayer(HexBlocks.SCONCE, RenderType.cutout());
      x.setRenderLayer(HexBlocks.AMETHYST_EDIFIED_LEAVES, RenderType.cutoutMipped());
      x.setRenderLayer(HexBlocks.AVENTURINE_EDIFIED_LEAVES, RenderType.cutoutMipped());
      x.setRenderLayer(HexBlocks.CITRINE_EDIFIED_LEAVES, RenderType.cutoutMipped());
      x.setRenderLayer(HexBlocks.AKASHIC_RECORD, RenderType.translucent());
      x.setRenderLayer(HexBlocks.QUENCHED_ALLAY, RenderType.translucent());
      x.registerEntityRenderer(HexEntities.WALL_SCROLL, WallScrollRenderer::new);
      ScryingLensOverlays.addScryingLensStuff();
   }

   private static void registerGaslight4(Item item) {
      IClientXplatAbstractions.INSTANCE
         .registerItemProperty(
            item, GaslightingTracker.GASLIGHTING_PRED, (stack, level, holder, holderID) -> Math.abs(GaslightingTracker.getGaslightingAmount() % 4)
         );
   }

   public static void registerColorProviders(BiConsumer<ItemColor, Item> itemColorRegistry, BiConsumer<BlockColor, Block> blockColorRegistry) {
      itemColorRegistry.accept(makeIotaStorageColorizer(HexItems.FOCUS::getColor), HexItems.FOCUS);
      itemColorRegistry.accept(makeIotaStorageColorizer(HexItems.SPELLBOOK::getColor), HexItems.SPELLBOOK);
      itemColorRegistry.accept(makeIotaStorageColorizer(HexItems.THOUGHT_KNOT::getColor), HexItems.THOUGHT_KNOT);
      blockColorRegistry.accept((bs, level, pos, idx) -> {
         if ((Boolean)bs.getValue(BlockAkashicBookshelf.HAS_BOOKS) && level != null && pos != null) {
            if (level.getBlockEntity(pos) instanceof BlockEntityAkashicBookshelf beas) {
               CompoundTag iotaTag = beas.getIotaTag();
               return iotaTag == null ? -1 : IotaType.getColor(iotaTag);
            } else {
               return -1;
            }
         } else {
            return -1;
         }
      }, HexBlocks.AKASHIC_BOOKSHELF);
   }

   public static ItemColor makeIotaStorageColorizer(ToIntFunction<ItemStack> getColor) {
      return (stack, idx) -> idx == 1 ? getColor.applyAsInt(stack) : -1;
   }

   private static void registerSealableDataHolderOverrides(IotaHolderItem item, Predicate<ItemStack> hasIota, Predicate<ItemStack> isSealed) {
      IClientXplatAbstractions.INSTANCE.registerItemProperty((Item)item, ItemFocus.OVERLAY_PRED, (stack, level, holder, holderID) -> {
         if (!hasIota.test(stack) && !NBTHelper.hasString(stack, "VisualOverride")) {
            return 0.0F;
         } else {
            return !isSealed.test(stack) ? 1.0F : 2.0F;
         }
      });
   }

   private static void registerVariantOverrides(VariantItem item, Function<ItemStack, Integer> variant) {
      IClientXplatAbstractions.INSTANCE
         .registerItemProperty((Item)item, ItemFocus.VARIANT_PRED, (stack, level, holder, holderID) -> variant.apply(stack).intValue());
   }

   private static void registerScrollOverrides(ItemScroll scroll) {
      IClientXplatAbstractions.INSTANCE
         .registerItemProperty(scroll, ItemScroll.ANCIENT_PREDICATE, (stack, level, holder, holderID) -> NBTHelper.hasString(stack, "op_id") ? 1.0F : 0.0F);
   }

   private static void registerPackagedSpellOverrides(ItemPackagedHex item) {
      IClientXplatAbstractions.INSTANCE
         .registerItemProperty(item, ItemPackagedHex.HAS_PATTERNS_PRED, (stack, level, holder, holderID) -> item.hasHex(stack) ? 1.0F : 0.0F);
   }

   private static void registerWandOverrides(ItemStaff item) {
      IClientXplatAbstractions.INSTANCE.registerItemProperty(item, ItemStaff.FUNNY_LEVEL_PREDICATE, (stack, level, holder, holderID) -> {
         if (!stack.has(DataComponents.CUSTOM_NAME)) {
            return 0.0F;
         } else {
            String name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
            if (name.contains("old")) {
               return 1.0F;
            } else {
               return name.contains("cherry") ? 2.0F : 0.0F;
            }
         }
      });
   }

   public static void registerBlockEntityRenderers(@NotNull RegisterClientStuff.BlockEntityRendererRegisterererer registerer) {
      registerer.registerBlockEntityRenderer(HexBlockEntities.SLATE_TILE, BlockEntitySlateRenderer::new);
      registerer.registerBlockEntityRenderer(HexBlockEntities.AKASHIC_BOOKSHELF_TILE, BlockEntityAkashicBookshelfRenderer::new);
      registerer.registerBlockEntityRenderer(HexBlockEntities.QUENCHED_ALLAY_TILE, BlockEntityQuenchedAllayRenderer::new);
      registerer.registerBlockEntityRenderer(HexBlockEntities.QUENCHED_ALLAY_TILES_TILE, BlockEntityQuenchedAllayRenderer::new);
      registerer.registerBlockEntityRenderer(HexBlockEntities.QUENCHED_ALLAY_BRICKS_TILE, BlockEntityQuenchedAllayRenderer::new);
      registerer.registerBlockEntityRenderer(HexBlockEntities.QUENCHED_ALLAY_BRICKS_SMALL_TILE, BlockEntityQuenchedAllayRenderer::new);
   }

   public static void onModelRegister(ResourceManager recMan, Consumer<ResourceLocation> extraModels) {
      for (Entry<BlockQuenchedAllay, Boolean> type : QUENCHED_ALLAY_TYPES.entrySet()) {
         ResourceLocation blockLoc = BuiltInRegistries.BLOCK.getKey(type.getKey());
         String locStart = "block/";
         if (type.getValue()) {
            locStart = locStart + "deco/";
         }

         for (int i = 0; i < 4; i++) {
            extraModels.accept(HexAPI.modLoc(locStart + blockLoc.getPath() + "_" + i));
         }
      }
   }

   public static void onModelBake(ModelBakery loader, Map<ResourceLocation, BakedModel> map) {
      for (Entry<BlockQuenchedAllay, Boolean> type : QUENCHED_ALLAY_TYPES.entrySet()) {
         ResourceLocation blockLoc = BuiltInRegistries.BLOCK.getKey(type.getKey());
         String locStart = "block/";
         if (type.getValue()) {
            locStart = locStart + "deco/";
         }

         ArrayList<BakedModel> list = new ArrayList<>();

         for (int i = 0; i < 4; i++) {
            ResourceLocation variantLoc = HexAPI.modLoc(locStart + blockLoc.getPath() + "_" + i);
            BakedModel model = map.get(variantLoc);
            list.add(model);
         }

         QUENCHED_ALLAY_VARIANTS.put(blockLoc, list);
      }
   }

   @FunctionalInterface
   public interface BlockEntityRendererRegisterererer {
      <T extends BlockEntity> void registerBlockEntityRenderer(BlockEntityType<T> var1, BlockEntityRendererProvider<? super T> var2);
   }
}
