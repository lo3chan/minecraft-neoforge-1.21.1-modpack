package net.astralya.hexalia.block.custom;

import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class RabbageCropBlock extends CropBlock {
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

   public RabbageCropBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected ItemLike getBaseSeedId() {
      return (ItemLike)ModItems.RABBAGE_SEEDS.get();
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity instanceof LivingEntity && !entity.getType().is(ModTags.EntityTypes.RABBAGE_IMMUNE)) {
         if (!level.isClientSide() && (Integer)state.getValue(AGE) >= 2) {
            double deltaX = Math.abs(entity.getX() - entity.xOld);
            double deltaZ = Math.abs(entity.getZ() - entity.zOld);
            if (deltaX >= 0.003000000026077032 || deltaZ >= 0.003000000026077032) {
               entity.hurt(level.damageSources().cactus(), 0.5F);
            }
         }
      }
   }
}
