package net.mehvahdjukaar.amendments.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LanternRegistry extends BlockTypeRegistry<LanternRegistry.LanternType> {
   public static final LanternRegistry INSTANCE = new LanternRegistry();
   private static final Map<ResourceLocation, Double> SPECIAL_OFFSETS = new HashMap<>();
   public static final LanternRegistry.LanternType VANILLA;
   public static final LanternRegistry.LanternType SOUL;
   private static final Set<String> BLACKLIST_MODS;
   private static final Set<ResourceLocation> WHITELIST;
   private static final Set<String> WHITELIST_NAMESPACES;

   private LanternRegistry() {
      super(LanternRegistry.LanternType.class, "lantern");
   }

   public LanternRegistry.LanternType getDefaultType() {
      return VANILLA;
   }

   public Optional<LanternRegistry.LanternType> detectTypeFromBlock(Block block, ResourceLocation blockId) {
      if (block.asItem() == Items.AIR) {
         return Optional.empty();
      } else if (WHITELIST.contains(blockId)) {
         return Optional.of(new LanternRegistry.LanternType(blockId, block));
      } else {
         String namespace = blockId.getNamespace();
         if (BLACKLIST_MODS.contains(namespace)) {
            return Optional.empty();
         } else if (WHITELIST_NAMESPACES.contains(namespace)) {
            return Optional.of(new LanternRegistry.LanternType(blockId, block));
         } else {
            String path = blockId.getPath();
            if (namespace.equals("twigs") && path.contains("paper_lantern")) {
               return Optional.of(new LanternRegistry.LanternType(blockId, block));
            } else if (namespace.equals("windswept") && path.equals("ice_lantern")) {
               return Optional.of(new LanternRegistry.LanternType(blockId, block));
            } else {
               return block instanceof LanternBlock && !block.defaultBlockState().hasBlockEntity()
                  ? Optional.of(new LanternRegistry.LanternType(blockId, block))
                  : Optional.empty();
            }
         }
      }
   }

   static {
      for (String path : List.of(
         "copper_lantern",
         "exposed_copper_lantern",
         "weathered_copper_lantern",
         "oxidized_copper_lantern",
         "waxed_copper_lantern",
         "waxed_exposed_copper_lantern",
         "waxed_weathered_copper_lantern",
         "waxed_oxidized_copper_lantern"
      )) {
         SPECIAL_OFFSETS.put(ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", path), -0.0625);
      }

      VANILLA = new LanternRegistry.LanternType(ResourceLocation.withDefaultNamespace("lantern"), Blocks.LANTERN);
      SOUL = new LanternRegistry.LanternType(ResourceLocation.withDefaultNamespace("soul_lantern"), Blocks.SOUL_LANTERN);
      BLACKLIST_MODS = Set.of("bbb", "extlights", "betterendforge", "spelunkery", "galosphere", "tconstruct", "enigmaticlegacy", "beautify");
      WHITELIST = Set.of(ResourceLocation.fromNamespaceAndPath("enlightened_end", "xenon_lantern"));
      WHITELIST_NAMESPACES = Set.of("skinnedlanterns");
   }

   public static class LanternType extends BlockType {
      public final Block lantern;
      public final double attachmentOffset;

      public LanternType(ResourceLocation name, Block lantern) {
         super(name);
         this.lantern = lantern;
         this.attachmentOffset = computeAttachmentOffset(name, lantern);
      }

      private static double computeAttachmentOffset(ResourceLocation id, Block lantern) {
         if (id.getNamespace().equals("twigs")) {
            return 0.0;
         } else {
            try {
               BlockState state = lantern.defaultBlockState();
               if (state.hasProperty(LanternBlock.HANGING)) {
                  state = (BlockState)state.setValue(LanternBlock.HANGING, false);
               }

               VoxelShape shape = state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
               double manual = LanternRegistry.SPECIAL_OFFSETS.getOrDefault(id, 0.0);
               return shape.isEmpty() ? manual : shape.bounds().maxY - 0.5625 + manual;
            } catch (Exception var6) {
               return LanternRegistry.SPECIAL_OFFSETS.getOrDefault(id, 0.0);
            }
         }
      }

      public String getTranslationKey() {
         return Utils.getID(this.lantern).getPath();
      }

      public ItemLike mainChild() {
         return this.lantern;
      }

      protected void initializeChildrenBlocks() {
         this.addChild("lantern", this.lantern);
      }

      protected void initializeChildrenItems() {
      }
   }
}
