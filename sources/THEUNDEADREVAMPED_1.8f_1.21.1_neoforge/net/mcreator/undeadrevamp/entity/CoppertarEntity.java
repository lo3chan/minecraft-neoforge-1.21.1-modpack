package net.mcreator.undeadrevamp.entity;

import net.mcreator.undeadrevamp.procedures.CoppertarOnEntityTickUpdateProcedure;
import net.mcreator.undeadrevamp.procedures.CoppertarRightClickedOnEntityProcedure;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class CoppertarEntity extends PathfinderMob {
   public CoppertarEntity(EntityType<CoppertarEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
      this.setPersistenceRequired();
   }

   protected void registerGoals() {
      super.registerGoals();
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
      ItemStack itemstack = sourceentity.getItemInHand(hand);
      InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
      super.mobInteract(sourceentity, hand);
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      Level world = this.level();
      CoppertarRightClickedOnEntityProcedure.execute(world, x, y, z, this);
      return retval;
   }

   public void baseTick() {
      super.baseTick();
      CoppertarOnEntityTickUpdateProcedure.execute(this);
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity entityIn) {
   }

   protected void pushEntities() {
   }

   public static void init(RegisterSpawnPlacementsEvent event) {
   }

   public static Builder createAttributes() {
      Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.0);
      builder = builder.add(Attributes.MAX_HEALTH, 10.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 3.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 16.0);
      return builder.add(Attributes.STEP_HEIGHT, 0.6);
   }
}
