package net.bettercombat.mixin.player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.Pose;
import net.bettercombat.logic.WeaponRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Player.class},
   priority = 899
)
public abstract class PlayerEntityMixin implements PlayerAttackProperties, EntityPlayer_BetterCombat {
   private int comboCount = 0;
   private static final EntityDataAccessor<String> BETTER_COMBAT_MAIN_IDLE_ANIMATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<String> BETTER_COMBAT_OFF_IDLE_ANIMATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Byte> BETTER_COMBAT_FLAGS = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
   private Multimap<Holder<Attribute>, AttributeModifier> dualWieldingAttributeMap;
   private static final ResourceLocation dualWieldingSpeedModifierId = ResourceLocation.fromNamespaceAndPath("bettercombat", "dual_wield");
   private AttackHand lastAttack;

   @Override
   public int getComboCount() {
      return this.comboCount;
   }

   @Override
   public void setComboCount(int comboCount) {
      this.comboCount = comboCount;
   }

   @Inject(
      method = {"defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V"},
      at = {@At("TAIL")}
   )
   private void initDataTracker_TAIL_SpellEngine_SyncEffects(Builder builder, CallbackInfo ci) {
      builder.define(BETTER_COMBAT_MAIN_IDLE_ANIMATION, "");
      builder.define(BETTER_COMBAT_OFF_IDLE_ANIMATION, "");
      builder.define(BETTER_COMBAT_FLAGS, (byte)0);
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("TAIL")}
   )
   public void post_Tick(CallbackInfo ci) {
      Player player = (Player)this;
      if (player.level().isClientSide()) {
         ((PlayerAttackAnimatable)this).updateAnimationsOnTick();
      } else {
         Pose pose = PlayerAttackHelper.poseForPlayer(player);
         player.getEntityData().set(BETTER_COMBAT_MAIN_IDLE_ANIMATION, pose.base());
         player.getEntityData().set(BETTER_COMBAT_OFF_IDLE_ANIMATION, pose.offHand());
         this.updateCombatFlagsFromCommandTags(player);
      }

      this.updateDualWieldingSpeedBoost();
   }

   private void updateCombatFlagsFromCommandTags(Player player) {
      boolean tagged = player.getTags().contains("bettercombat_disabled");
      byte flags = this.getCombatFlags();
      boolean mirrored = (flags & 1) != 0;
      if (tagged != mirrored) {
         this.setCombatFlags((byte)(tagged ? flags | 1 : flags & -2));
      }
   }

   @Override
   public byte getCombatFlags() {
      return (Byte)((Player)this).getEntityData().get(BETTER_COMBAT_FLAGS);
   }

   @Override
   public void setCombatFlags(byte flags) {
      ((Player)this).getEntityData().set(BETTER_COMBAT_FLAGS, flags);
   }

   @Override
   public String getMainHandIdleAnimation() {
      return (String)((Player)this).getEntityData().get(BETTER_COMBAT_MAIN_IDLE_ANIMATION);
   }

   @Override
   public String getOffHandIdleAnimation() {
      return (String)((Player)this).getEntityData().get(BETTER_COMBAT_OFF_IDLE_ANIMATION);
   }

   @ModifyVariable(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At("STORE"),
      ordinal = 3
   )
   private boolean disableSweeping(boolean value) {
      if (BetterCombatMod.config.allow_vanilla_sweeping) {
         return value;
      } else {
         Player player = (Player)this;
         AttackHand currentHand = PlayerAttackHelper.getCurrentAttack(player, this.comboCount);
         return currentHand != null ? false : value;
      }
   }

   @Inject(
      method = {"getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getEquippedStack_Pre(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
      boolean mainHandHasTwoHanded = false;
      ItemStack mainHandStack = ((PlayerEntityAccessor)this).getInventory().getSelected();
      WeaponAttributes mainHandAttributes = WeaponRegistry.getAttributes(mainHandStack);
      if (mainHandAttributes != null && mainHandAttributes.isTwoHanded()) {
         mainHandHasTwoHanded = true;
      }

      boolean offHandHasTwoHanded = false;
      ItemStack offHandStack = (ItemStack)((PlayerEntityAccessor)this).getInventory().offhand.get(0);
      WeaponAttributes offHandAttributes = WeaponRegistry.getAttributes(offHandStack);
      if (offHandAttributes != null && offHandAttributes.isTwoHanded()) {
         offHandHasTwoHanded = true;
      }

      if (slot == EquipmentSlot.OFFHAND && (mainHandHasTwoHanded || offHandHasTwoHanded)) {
         cir.setReturnValue(ItemStack.EMPTY);
         cir.cancel();
      }
   }

   private void updateDualWieldingSpeedBoost() {
      Player player = (Player)this;
      boolean newState = PlayerAttackHelper.isDualWielding(player);
      boolean currentState = this.dualWieldingAttributeMap != null;
      if (newState != currentState) {
         if (newState) {
            this.dualWieldingAttributeMap = HashMultimap.create();
            double multiplier = BetterCombatMod.config.dual_wielding_attack_speed_multiplier - 1.0F;
            this.dualWieldingAttributeMap
               .put(Attributes.ATTACK_SPEED, new AttributeModifier(dualWieldingSpeedModifierId, multiplier, Operation.ADD_MULTIPLIED_BASE));
            player.getAttributes().addTransientAttributeModifiers(this.dualWieldingAttributeMap);
         } else if (this.dualWieldingAttributeMap != null) {
            player.getAttributes().removeAttributeModifiers(this.dualWieldingAttributeMap);
            this.dualWieldingAttributeMap = null;
         }
      }
   }

   @ModifyArg(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
      ),
      index = 0
   )
   public InteractionHand getHand(InteractionHand hand) {
      Player player = (Player)this;
      AttackHand currentHand = PlayerAttackHelper.getCurrentAttack(player, this.comboCount);
      if (currentHand != null) {
         return currentHand.isOffHand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
      } else {
         return InteractionHand.MAIN_HAND;
      }
   }

   @Redirect(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"
      )
   )
   public ItemStack getMainHandStack_Redirect(Player instance) {
      if (this.comboCount < 0) {
         return instance.getMainHandItem();
      } else {
         AttackHand hand = PlayerAttackHelper.getCurrentAttack(instance, this.comboCount);
         if (hand == null) {
            boolean isOffHand = PlayerAttackHelper.shouldAttackWithOffHand(instance, this.comboCount);
            return isOffHand ? ItemStack.EMPTY : instance.getMainHandItem();
         } else {
            this.lastAttack = hand;
            return hand.itemStack();
         }
      }
   }

   @Redirect(
      method = {"attack(Lnet/minecraft/world/entity/Entity;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"
      )
   )
   public void setStackInHand_Redirect(Player instance, InteractionHand handArg, ItemStack itemStack) {
      if (this.comboCount < 0) {
         instance.setItemInHand(handArg, itemStack);
      }

      AttackHand hand = this.lastAttack;
      if (hand == null) {
         hand = PlayerAttackHelper.getCurrentAttack(instance, this.comboCount);
      }

      if (hand == null) {
         instance.setItemInHand(handArg, itemStack);
      } else {
         InteractionHand redirectedHand = hand.isOffHand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         instance.setItemInHand(redirectedHand, itemStack);
      }
   }

   @Nullable
   @Override
   public AttackHand getCurrentAttack() {
      if (this.comboCount < 0) {
         return null;
      } else {
         Player player = (Player)this;
         return PlayerAttackHelper.getCurrentAttack(player, this.comboCount);
      }
   }
}
