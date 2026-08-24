package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractArrow.class})
public abstract class AbstractArrowMixin extends Entity {
   @Shadow
   private ItemStack pickupItemStack;
   @Unique
   private static final EntityDataAccessor<ItemStack> ID_PICKUP_ITEM = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.ITEM_STACK);

   public AbstractArrowMixin(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(
      method = {"defineSynchedData"},
      at = {@At("RETURN")}
   )
   public void defineSynchedData(Builder builder, CallbackInfo ci) {
      builder.define(ID_PICKUP_ITEM, ItemStack.EMPTY);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>(Lnet/minecraft/world/entity/EntityType;DDDLnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"}
   )
   public void constructAbstractArrow(
      EntityType<? extends AbstractArrow> entityType,
      double x,
      double y,
      double z,
      Level level,
      ItemStack pickupItemStack,
      ItemStack firedFromWeapon,
      CallbackInfo ci
   ) {
      this.entityData.set(ID_PICKUP_ITEM, pickupItemStack);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setPickupItemStack"}
   )
   public void setPickupItemStack(ItemStack pickupItemStack, CallbackInfo ci) {
      this.entityData.set(ID_PICKUP_ITEM, pickupItemStack);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getPickupItem"},
      cancellable = true
   )
   public void getPickupItemStack(CallbackInfoReturnable<ItemStack> cir) {
      if (this.level().isClientSide) {
         cir.setReturnValue(((ItemStack)this.entityData.get(ID_PICKUP_ITEM)).copy());
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getPickupItemStackOrigin"},
      cancellable = true
   )
   public void getPickupItemStackOrigin(CallbackInfoReturnable<ItemStack> cir) {
      if (this.level().isClientSide) {
         cir.setReturnValue((ItemStack)this.entityData.get(ID_PICKUP_ITEM));
      }
   }

   @Shadow
   protected abstract ItemStack getPickupItem();

   @Shadow
   public abstract ItemStack getPickupItemStackOrigin();

   @Accessor("inGround")
   public abstract boolean getInGround();

   @Accessor("inGround")
   public abstract void setInGround(boolean var1);

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;isInWaterOrRain()Z",
         ordinal = 0
      )}
   )
   protected void preventInGround(CallbackInfo ci) {
      ItemStack stack = this.getPickupItemStackOrigin();
      AbstractArrow self = (AbstractArrow)this;
      boolean inGround = this.getInGround();
      InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
         TriState result = ((Property)propertyHolder.value()).allowArrowClipBlocks(self, stack);
         if (result != TriState.DEFAULT) {
            this.setInGround(result.isFalse() && inGround);
         }
      });
   }
}
