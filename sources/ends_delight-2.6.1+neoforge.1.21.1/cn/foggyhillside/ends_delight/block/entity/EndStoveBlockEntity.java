package cn.foggyhillside.ends_delight.block.entity;

import cn.foggyhillside.ends_delight.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.block.AbstractStoveBlock;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;

public class EndStoveBlockEntity extends AbstractStoveBlockEntity {
   public EndStoveBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntityTypes.END_STOVE.get(), pos, state, RecipeType.CAMPFIRE_COOKING);
   }

   public static void particleTick(Level level, BlockPos pos, BlockState state, EndStoveBlockEntity stove) {
      if (!stove.isEmpty()) {
         stove.addSmokeParticles();
      }
   }

   public void addSmokeParticles() {
      assert this.level != null;

      ItemStackHandler items = this.getItems();

      for (int i = 0; i < items.getSlots(); i++) {
         if (!items.getStackInSlot(i).isEmpty() && !(this.level.random.nextFloat() >= 0.2F)) {
            Vec2 itemOffset = this.getStoveItemOffset(i);
            Direction direction = (Direction)this.getBlockState().getValue(AbstractStoveBlock.FACING);
            if (direction.get2DDataValue() % 2 != 0) {
               itemOffset = new Vec2(itemOffset.y, itemOffset.x);
            }

            double x = this.worldPosition.getX() + 0.5 - direction.getStepX() * itemOffset.x + direction.getClockWise().getStepX() * itemOffset.x;
            double y = this.worldPosition.getY() + 1.0;
            double z = this.worldPosition.getZ() + 0.5 - direction.getStepZ() * itemOffset.y + direction.getClockWise().getStepZ() * itemOffset.y;

            for (int k = 0; k < 3; k++) {
               this.level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
            }
         }
      }
   }

   protected int getInventorySlotCount() {
      return 6;
   }

   public Vec2 getStoveItemOffset(int index) {
      float X_OFFSET = 0.3F;
      float Y_OFFSET = 0.2F;
      Vec2[] OFFSETS = new Vec2[]{
         new Vec2(0.3F, 0.2F), new Vec2(0.0F, 0.2F), new Vec2(-0.3F, 0.2F), new Vec2(0.3F, -0.2F), new Vec2(0.0F, -0.2F), new Vec2(-0.3F, -0.2F)
      };
      return OFFSETS[index];
   }
}
