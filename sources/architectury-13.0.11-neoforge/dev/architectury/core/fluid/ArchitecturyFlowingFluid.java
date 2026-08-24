package dev.architectury.core.fluid;

import com.google.common.base.MoreObjects;
import com.google.common.base.Suppliers;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties;
import org.jetbrains.annotations.NotNull;

public abstract class ArchitecturyFlowingFluid extends BaseFlowingFluid {
   private static final Map<ArchitecturyFluidAttributes, FluidType> FLUID_TYPE_MAP = new IdentityHashMap<>();
   private final ArchitecturyFluidAttributes attributes;

   ArchitecturyFlowingFluid(ArchitecturyFluidAttributes attributes) {
      super(toForgeProperties(attributes));
      this.attributes = attributes;
   }

   private static Properties toForgeProperties(ArchitecturyFluidAttributes attributes) {
      Properties forge = new Properties(
         Suppliers.memoize(
            () -> FLUID_TYPE_MAP.computeIfAbsent(
               attributes,
               attr -> new ArchitecturyFluidAttributesForge(net.neoforged.neoforge.fluids.FluidType.Properties.create(), attr.getSourceFluid(), attr)
            )
         ),
         attributes::getSourceFluid,
         attributes::getFlowingFluid
      );
      forge.slopeFindDistance(attributes.getSlopeFindDistance());
      forge.levelDecreasePerBlock(attributes.getDropOff());
      forge.bucket(() -> (Item)MoreObjects.firstNonNull(attributes.getBucketItem(), Items.AIR));
      forge.tickRate(attributes.getTickDelay());
      forge.explosionResistance(attributes.getExplosionResistance());
      forge.block(() -> (LiquidBlock)MoreObjects.firstNonNull(attributes.getBlock(), (LiquidBlock)Blocks.WATER));
      return forge;
   }

   public Fluid getFlowing() {
      return this.attributes.getFlowingFluid();
   }

   public Fluid getSource() {
      return this.attributes.getSourceFluid();
   }

   protected boolean canConvertToSource(Level level) {
      return this.attributes.canConvertToSource();
   }

   protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
      BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
      Block.dropResources(state, level, pos, blockEntity);
   }

   protected int getSlopeFindDistance(LevelReader level) {
      return this.attributes.getSlopeFindDistance(level);
   }

   protected int getDropOff(LevelReader level) {
      return this.attributes.getDropOff(level);
   }

   public Item getBucket() {
      Item item = this.attributes.getBucketItem();
      return item == null ? Items.AIR : item;
   }

   protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
      return direction == Direction.DOWN && !this.isSame(fluid);
   }

   public int getTickDelay(LevelReader level) {
      return this.attributes.getTickDelay(level);
   }

   protected float getExplosionResistance() {
      return this.attributes.getExplosionResistance();
   }

   protected BlockState createLegacyBlock(FluidState state) {
      LiquidBlock block = this.attributes.getBlock();
      return block == null ? Blocks.AIR.defaultBlockState() : (BlockState)block.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
   }

   @NotNull
   public Optional<SoundEvent> getPickupSound() {
      return Optional.ofNullable(this.attributes.getFillSound());
   }

   public boolean isSame(Fluid fluid) {
      return fluid == this.getSource() || fluid == this.getFlowing();
   }

   public static class Flowing extends ArchitecturyFlowingFluid {
      public Flowing(ArchitecturyFluidAttributes attributes) {
         super(attributes);
         this.registerDefaultState((FluidState)((FluidState)this.getStateDefinition().any()).setValue(LEVEL, 7));
      }

      protected void createFluidStateDefinition(Builder<Fluid, FluidState> builder) {
         super.createFluidStateDefinition(builder);
         builder.add(new Property[]{LEVEL});
      }

      public int getAmount(FluidState state) {
         return (Integer)state.getValue(LEVEL);
      }

      public boolean isSource(FluidState state) {
         return false;
      }
   }

   public static class Source extends ArchitecturyFlowingFluid {
      public Source(ArchitecturyFluidAttributes attributes) {
         super(attributes);
      }

      public int getAmount(FluidState state) {
         return 8;
      }

      public boolean isSource(FluidState state) {
         return true;
      }
   }
}
