package net.mehvahdjukaar.amendments.common.block;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.common.item.DyeBottleItem;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.util.PotionBottleType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class StructureCauldronHack extends Block implements EntityBlock {
   private static final Supplier<StructureCauldronHack> BLOCK = RegHelper.registerBlock(Amendments.res("cauldron_hack"), StructureCauldronHack::new);
   private static final Supplier<BlockEntityType<StructureCauldronHack.Tile>> TILE = RegHelper.registerBlockEntityType(
      Amendments.res("cauldron_hack"), () -> PlatHelper.newBlockEntityType(StructureCauldronHack.Tile::new, new Block[]{BLOCK.get()})
   );
   private static final BooleanProperty POTION = BooleanProperty.create("potion");
   private static final Supplier<List<Potion>> HARMFUL_POTS = Suppliers.memoize(
      () -> BuiltInRegistries.POTION.stream().filter(p -> p.getEffects().stream().noneMatch(e -> ((MobEffect)e.getEffect().value()).isBeneficial())).toList()
   );

   public static void register() {
   }

   public StructureCauldronHack() {
      super(Properties.of().dropsLike(Blocks.CAULDRON));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{POTION});
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new StructureCauldronHack.Tile(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return Utils.getTicker(pBlockEntityType, TILE.get(), StructureCauldronHack.Tile::tick);
   }

   private static class Tile extends BlockEntity {
      public Tile(BlockPos pos, BlockState blockState) {
         super(StructureCauldronHack.TILE.get(), pos, blockState);
      }

      public static <E extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, E e) {
         level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
         if ((Boolean)state.getValue(StructureCauldronHack.POTION)) {
            level.setBlockAndUpdate(pos, ModRegistry.LIQUID_CAULDRON.get().defaultBlockState());
            if (level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
               List<Potion> list = StructureCauldronHack.HARMFUL_POTS.get();
               Potion pot = list.get(level.random.nextInt(list.size()));
               SoftFluidStack fluidStack = SoftFluidStack.of(MLBuiltinSoftFluids.POTION.getHolder(level), level.random.nextIntBetweenInclusive(1, 4));
               fluidStack.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(pot)));
               if (level.random.nextFloat() < 0.4) {
                  fluidStack.set((DataComponentType)MoonlightRegistry.BOTTLE_TYPE.get(), PotionBottleType.SPLASH);
               }

               te.getSoftFluidTank().setFluid(fluidStack);
               te.setChanged();
            }
         } else {
            level.setBlockAndUpdate(pos, ModRegistry.DYE_CAULDRON.get().defaultBlockState());
            if (level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
               DyeColor dye = DyeColor.byId(level.random.nextInt(DyeColor.values().length));
               SoftFluidStack fluid = DyeBottleItem.createFluidStack(dye, 3, level);
               te.getSoftFluidTank().setFluid(fluid);
               te.setChanged();
            }
         }
      }
   }
}
