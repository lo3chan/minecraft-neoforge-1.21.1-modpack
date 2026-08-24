package fuzs.puzzleslib.impl.init.boat;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Boat.Type;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TypedBoat extends Boat {
   private final Supplier<Item> dropItem;

   public TypedBoat(EntityType<? extends Boat> entityType, Level level, Supplier<Item> dropItem) {
      super(entityType, level);
      this.dropItem = dropItem;
   }

   public Item getDropItem() {
      return this.dropItem.get();
   }

   protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
      this.lastYd = this.getDeltaMovement().y;
      if (!this.isPassenger()) {
         if (onGround) {
            this.resetFallDistance();
         } else if (!this.level().getFluidState(this.blockPosition().below()).is(FluidTags.WATER) && y < 0.0) {
            this.fallDistance -= (float)y;
         }
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.remove("Type");
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      compound.remove("Type");
      super.readAdditionalSaveData(compound);
   }

   public void setVariant(Type variant) {
   }

   public Type getVariant() {
      return Type.OAK;
   }
}
