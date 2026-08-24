package at.petrak.hexcasting.api.casting.eval;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class MishapEnvironment {
   @Nullable
   protected final ServerPlayer caster;
   protected final ServerLevel world;

   protected MishapEnvironment(ServerLevel world, @Nullable ServerPlayer caster) {
      this.caster = caster;
      this.world = world;
   }

   public abstract void yeetHeldItemsTowards(Vec3 var1);

   public abstract void dropHeldItems();

   public abstract void drown();

   public abstract void damage(float var1);

   public abstract void removeXp(int var1);

   public abstract void blind(int var1);

   protected void yeetItem(ItemStack stack, Vec3 srcPos, Vec3 delta) {
      ItemEntity entity = new ItemEntity(
         this.world,
         srcPos.x,
         srcPos.y,
         srcPos.z,
         stack,
         delta.x + (Math.random() - 0.5) * 0.1,
         delta.y + (Math.random() - 0.5) * 0.1,
         delta.z + (Math.random() - 0.5) * 0.1
      );
      entity.setPickUpDelay(40);
      this.world.addWithUUID(entity);
   }
}
