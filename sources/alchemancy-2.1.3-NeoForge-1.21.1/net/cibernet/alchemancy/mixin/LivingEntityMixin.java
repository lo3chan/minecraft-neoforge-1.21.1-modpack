package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.events.handler.MobTemptHandler;
import net.cibernet.alchemancy.mixin.accessors.EntityAccessor;
import net.cibernet.alchemancy.network.S2CRidePlayerPayload;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin {
   @Shadow
   protected abstract float getRiddenSpeed(Player var1);

   @Shadow
   public abstract double getAttributeValue(Holder<Attribute> var1);

   @Shadow
   public abstract Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 var1, float var2);

   @Shadow
   public abstract void travel(Vec3 var1);

   @Inject(
      method = {"dismountVehicle"},
      at = {@At("HEAD")}
   )
   public void dismountVehicle(Entity vehicle, CallbackInfo ci) {
      LivingEntity self = (LivingEntity)this;
      if (vehicle instanceof ServerPlayer other) {
         PacketDistributor.sendToPlayer(other, new S2CRidePlayerPayload(self.getId(), true), new CustomPacketPayload[0]);
      }
   }

   @WrapOperation(
      method = {"createLivingAttributes"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;builder()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"
      )}
   )
   private static Builder createLivingAttributes(Operation<Builder> original) {
      Builder result = (Builder)original.call(new Object[0]);
      if (!result.hasAttribute(Attributes.ATTACK_DAMAGE)) {
         result.add(Attributes.ATTACK_DAMAGE, 1.0);
      }

      return result;
   }

   @Inject(
      method = {"getRiddenInput"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getRiddenInput(Player player, Vec3 travelVector, CallbackInfoReturnable<Vec3> cir) {
      MobTemptHandler.performIfTempted(
         this.alchemancy$self(), player, EquipmentSlotGroup.HAND, () -> cir.setReturnValue(new Vec3(0.0, -Mth.sin(player.getXRot() * 0.017453292F), 1.0))
      );
   }

   @Inject(
      method = {"tickRidden"},
      at = {@At("RETURN")}
   )
   public void riddenTick(Player player, Vec3 travelVector, CallbackInfo ci) {
      LivingEntity self = this.alchemancy$self();
      if (!(self instanceof ItemSteerable)) {
         MobTemptHandler.performIfTempted(
            self,
            player,
            EquipmentSlotGroup.HAND,
            () -> {
               self.setYRot(player.getYRot());
               self.setXRot(player.getXRot() * 0.5F);
               self.yRotO = self.yBodyRot = self.yHeadRot = self.getYRot();
               float f2 = self.level()
                  .getBlockState(self.getBlockPosBelowThatAffectsMyMovement())
                  .getFriction(self.level(), self.getBlockPosBelowThatAffectsMyMovement(), self);
               Vec3 frictionTravelVector = EntityAccessor.invokeGetInputVector(travelVector, f2, self.getYRot()).add(self.getDeltaMovement());
               Vec3 collidedTravelVector = ((EntityAccessor)self).invokeCollide(frictionTravelVector);
               if (!Mth.equal(frictionTravelVector.x, collidedTravelVector.x) || !Mth.equal(frictionTravelVector.z, collidedTravelVector.z)) {
                  self.horizontalCollision = true;
               }
            }
         );
      }
   }

   @Inject(
      method = {"getRiddenSpeed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getRiddenSpeed(Player player, CallbackInfoReturnable<Float> cir) {
      MobTemptHandler.performIfTempted(
         this.alchemancy$self(),
         player,
         EquipmentSlotGroup.HAND,
         () -> cir.setReturnValue((float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.22499999403953552))
      );
   }

   @Unique
   private LivingEntity alchemancy$self() {
      return (LivingEntity)this;
   }
}
