package net.mehvahdjukaar.moonlight.api.set.leaves;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Supplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeavesType extends BlockType {
   public static Codec<LeavesType> CODEC;
   public static StreamCodec<ByteBuf, LeavesType> STREAM_CODEC;
   public final Block leaves;

   protected LeavesType(ResourceLocation id, Block leaves) {
      super(id);
      this.leaves = leaves;
   }

   @Deprecated(
      forRemoval = true
   )
   @NotNull
   public WoodType getWoodType() {
      WoodType w = this.getAssociatedWoodType();
      return w == null ? VanillaWoodTypes.OAK : w;
   }

   @Nullable
   public WoodType getAssociatedWoodType() {
      return LeavesTypeRegistry.INSTANCE.getEquivalentWoodType(this);
   }

   @Override
   public ItemLike mainChild() {
      return this.leaves;
   }

   @Override
   public String getTranslationKey() {
      return "leaves_type." + this.getNamespace() + "." + this.getTypeName();
   }

   @Override
   public void initializeChildrenBlocks() {
      this.addChild("leaves", this.leaves);
      WoodType equivalentWood = this.getAssociatedWoodType();
      Block log;
      if (equivalentWood != null) {
         log = equivalentWood.log;
      } else {
         log = this.findRelatedEntry("log", BuiltInRegistries.BLOCK);
      }

      if (log != null) {
         this.addChild("log", log);
      }

      this.addChild("sapling", this.findRelatedEntry("sapling", BuiltInRegistries.BLOCK));
   }

   @Override
   public void initializeChildrenItems() {
   }

   static {
      LeavesTypeRegistry.touch();
   }

   public static class Finder extends BlockType.SetFinderBuilder<LeavesType> {
      private Supplier<Block> leavesFinder;

      public Finder(ResourceLocation id) {
         super(id, LeavesTypeRegistry.INSTANCE);
         this.leavesSuffix("_leaves");
      }

      public LeavesType.Finder leaves(Supplier<Block> planksFinder) {
         this.leavesFinder = planksFinder;
         return this;
      }

      public LeavesType.Finder leaves(ResourceLocation id) {
         return this.leaves(
            (Supplier<Block>)(() -> (Block)BuiltInRegistries.BLOCK
               .getOptional(id)
               .orElseThrow(() -> new IllegalStateException("Failed to find leaves block: " + id)))
         );
      }

      public LeavesType.Finder leaves(String leavesName) {
         return this.leaves(Utils.idWithOptionalNamespace(leavesName, this.id.getNamespace()));
      }

      public LeavesType.Finder leavesAffix(String prefix, String suffix) {
         return this.leaves(prefix + this.id.getPath() + suffix);
      }

      public LeavesType.Finder leavesSuffix(String suffix) {
         return this.leaves(this.id.getPath() + suffix);
      }

      public LeavesType.Finder equivalentWood(String id) {
         LeavesTypeRegistry.INSTANCE.addLeavesToWoodMapping(this.id, ResourceLocation.parse(id));
         return this;
      }

      @Override
      public Optional<LeavesType> get() {
         if (PlatHelper.isModLoaded(this.id.getNamespace())) {
            try {
               Block leaves = (Block)Preconditions.checkNotNull(this.leavesFinder.get(), "Manual Finder - failed to find a leaf block for {}", this.id);
               LeavesType w = new LeavesType(this.id, leaves);
               this.childNames.forEach((key, value) -> {
                  try {
                     ItemLike obj = (ItemLike)Preconditions.checkNotNull(value.get());
                     w.addChild(key, obj);
                  } catch (Exception var5) {
                     Moonlight.LOGGER.warn("Failed to find child for WoodType: {} - {}. Ignored! ERROR: {}", this.id, key, var5.getMessage());
                  }
               });
               return Optional.of(w);
            } catch (Exception var3) {
               Moonlight.LOGGER.warn("Failed to find custom WoodType:  {} - ", this.id, var3);
            }
         }

         return Optional.empty();
      }

      @Deprecated(
         forRemoval = true
      )
      public Finder(ResourceLocation id, Supplier<Block> leaves, @Nullable Supplier<WoodType> wood) {
         this(id, leaves);
      }

      @Deprecated(
         forRemoval = true
      )
      public Finder(ResourceLocation id, Supplier<Block> leaves) {
         super(id, LeavesTypeRegistry.INSTANCE);
         this.leavesFinder = leaves;
      }

      @Deprecated(
         forRemoval = true
      )
      public static LeavesType.Finder simple(String modId, String leavesTypeName, String leavesName) {
         return new LeavesType.Finder(
            ResourceLocation.fromNamespaceAndPath(modId, leavesTypeName),
            () -> (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modId, leavesName)),
            null
         );
      }

      @Deprecated(
         forRemoval = true
      )
      public static LeavesType.Finder simple(String modId, String leavesTypeName, String leavesName, String woodTypeID) {
         ResourceLocation leavesId = ResourceLocation.fromNamespaceAndPath(modId, leavesName);
         LeavesTypeRegistry.INSTANCE.addLeavesToWoodMapping(leavesId, ResourceLocation.parse(woodTypeID));
         return new LeavesType.Finder(ResourceLocation.fromNamespaceAndPath(modId, leavesTypeName), () -> (Block)BuiltInRegistries.BLOCK.get(leavesId));
      }

      @Deprecated(
         forRemoval = true
      )
      public void addChild(String childType, String childName) {
         this.addChild(childType, this.id.withPath(childName));
      }

      @Deprecated(
         forRemoval = true
      )
      public void addChild(String childType, ResourceLocation childName) {
         this.childBlock(childType, childName);
      }
   }
}
