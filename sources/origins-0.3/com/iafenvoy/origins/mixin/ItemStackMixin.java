package com.iafenvoy.origins.mixin;

import com.iafenvoy.origins.accessor.EntityLinkedItemStack;
import com.iafenvoy.origins.data.power.builtin.regular.EdibleItemPower;
import com.iafenvoy.origins.data.power.builtin.regular.ItemOnItemPower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.lang.ref.WeakReference;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public abstract class ItemStackMixin implements EntityLinkedItemStack {
   @Unique
   @Nullable
   private WeakReference<Entity> origins$holdingEntity;

   @Shadow
   @Nullable
   public abstract Entity getEntityRepresentation();

   @Override
   public Entity origins$getEntity() {
      return this.origins$getEntity(true);
   }

   @Override
   public Entity origins$getEntity(boolean prioritiseVanillaHolder) {
      Entity vanillaHolder = this.getEntityRepresentation();
      if (prioritiseVanillaHolder && vanillaHolder != null) {
         return vanillaHolder;
      } else {
         return this.origins$holdingEntity != null ? this.origins$holdingEntity.get() : null;
      }
   }

   @Override
   public void origins$setEntity(Entity entity) {
      this.origins$holdingEntity = new WeakReference<>(entity);
   }

   @ModifyReturnValue(
      method = {"copy"},
      at = {@At("RETURN")}
   )
   private ItemStack origins$moveEntityToCopy(ItemStack copy) {
      if (this.origins$holdingEntity != null) {
         ((EntityLinkedItemStack)copy).origins$setEntity(this.origins$holdingEntity.get());
      }

      return copy;
   }

   @Inject(
      method = {"overrideOtherStackedOnMe"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void onItemOnItem(ItemStack other, Slot slot, ClickAction action, Player pPlayer, SlotAccess otherAccess, CallbackInfoReturnable<Boolean> cir) {
      if (!(Boolean)cir.getReturnValue()) {
         if (ItemOnItemPower.execute(pPlayer, slot, otherAccess, action)) {
            cir.setReturnValue(true);
         }
      }
   }

   @WrapOperation(
      method = {"use"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"
      )}
   )
   private InteractionResultHolder<ItemStack> origins$onItemUse(
      Item item, Level world, Player user, InteractionHand hand, Operation<InteractionResultHolder<ItemStack>> original
   ) {
      ItemStack useStack = (ItemStack)this;
      boolean canConsumeCustomFood = EdibleItemPower.get(useStack, user)
         .map(EdibleItemPower::getFoodProperties)
         .map(fc -> user.canEat(fc.canAlwaysEat()))
         .orElse(false);
      return canConsumeCustomFood
         ? ItemUtils.startUsingInstantly(world, user, hand)
         : (InteractionResultHolder)original.call(new Object[]{item, world, user, hand});
   }

   @WrapOperation(
      method = {"onUseTick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;I)V"
      )}
   )
   private void origins$onUsageTick(Item item, Level world, LivingEntity user, ItemStack stack, int remainingUseTicks, Operation<Void> original) {
      ItemStack usingStack = (ItemStack)this;
      if (EdibleItemPower.get(usingStack, user).isEmpty()) {
         original.call(new Object[]{item, world, user, usingStack, remainingUseTicks});
      }
   }

   @WrapOperation(
      method = {"releaseUsing"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)V"
      )}
   )
   private void origins$onStoppedUsing(Item item, ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, Operation<Void> original) {
      if (EdibleItemPower.get(stack, user).isEmpty()) {
         original.call(new Object[]{item, stack, world, user, remainingUseTicks});
      }
   }

   @WrapOperation(
      method = {"finishUsingItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"
      )}
   )
   private ItemStack origins$onFinishUsing(Item item, ItemStack stack, Level world, LivingEntity user, Operation<ItemStack> original) {
      return EdibleItemPower.get(stack, user)
         .map(p -> user.eat(world, stack, p.getFoodProperties()))
         .orElseGet(() -> (ItemStack)original.call(new Object[]{item, stack, world, user}));
   }

   @ModifyReturnValue(
      method = {"getUseAnimation"},
      at = {@At("RETURN")}
   )
   private UseAnim origins$replaceUseAnimation(UseAnim original) {
      return EdibleItemPower.get((ItemStack)this, null).map(EdibleItemPower::getConsumeAnimation).orElse(original);
   }

   @ModifyReturnValue(
      method = {"getEatingSound"},
      at = {@At("RETURN")}
   )
   private SoundEvent origins$replaceEatingSound(SoundEvent original) {
      return EdibleItemPower.get((ItemStack)this, null).map(EdibleItemPower::getConsumeSound).orElse(original);
   }

   @ModifyReturnValue(
      method = {"getDrinkingSound"},
      at = {@At("RETURN")}
   )
   private SoundEvent origins$replaceDrinkingSound(SoundEvent original) {
      return EdibleItemPower.get((ItemStack)this, null).map(EdibleItemPower::getConsumeSound).orElse(original);
   }

   @ModifyReturnValue(
      method = {"getUseDuration"},
      at = {@At("RETURN")}
   )
   private int origins$modifyUseDuration(int original, LivingEntity user) {
      return EdibleItemPower.get((ItemStack)this, user).map(p -> original).orElse(original);
   }

   @WrapOperation(
      method = {"useOnRelease"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;useOnRelease(Lnet/minecraft/world/item/ItemStack;)Z"
      )}
   )
   private boolean origins$useOnReleaseIfCustomFood(Item item, ItemStack stack, Operation<Boolean> original) {
      return EdibleItemPower.get(stack, null).isEmpty() ? (Boolean)original.call(new Object[]{item, stack}) : false;
   }
}
