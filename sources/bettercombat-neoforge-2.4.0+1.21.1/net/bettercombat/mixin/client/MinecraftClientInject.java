package net.bettercombat.mixin.client;

import java.util.List;
import me.shedaniel.autoconfig.AutoConfig;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.PlatformClient;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.CombatFlags;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.api.fx.ParticlePlacement;
import net.bettercombat.api.fx.TrailAppearance;
import net.bettercombat.client.BetterCombatClientMod;
import net.bettercombat.client.Keybindings;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.client.collision.TargetFinder;
import net.bettercombat.client.particle.SlashParticleUtil;
import net.bettercombat.config.ClientConfigWrapper;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.WeaponRegistry;
import net.bettercombat.logic.WeaponSwing;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.PatternMatching;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Minecraft.class})
public abstract class MinecraftClientInject implements MinecraftClient_BetterCombat {
   @Shadow
   public ClientLevel level;
   @Shadow
   @Nullable
   public LocalPlayer player;
   @Shadow
   private int rightClickDelay;
   @Shadow
   @Final
   public Font font;
   @Shadow
   public int missTime;
   @Shadow
   @Final
   public Gui gui;
   @Shadow
   @Nullable
   public HitResult hitResult;
   private boolean isHoldingAttackInput = false;
   private boolean isHarvesting = false;
   private ItemStack upswingStack;
   private ItemStack lastAttacedWithItemStack;
   private WeaponSwing ongoingSwing;
   private int lastAttacked = 1000;
   private float lastSwingDuration = 0.0F;
   private int comboReset = 0;
   private List<Entity> targetsInReach = null;

   private Minecraft thisClient() {
      return (Minecraft)this;
   }

   @Inject(
      method = {"disconnect(Lnet/minecraft/client/gui/screens/Screen;)V"},
      at = {@At("TAIL")}
   )
   private void disconnect_TAIL(Screen screen, CallbackInfo ci) {
      BetterCombatClientMod.ENABLED = false;
   }

   @Inject(
      method = {"startAttack()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pre_doAttack(CallbackInfoReturnable<Boolean> info) {
      if (BetterCombatClientMod.ENABLED) {
         if (!CombatFlags.isAttackDisabled(this.player)) {
            Minecraft client = this.thisClient();
            WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandItem());
            if (attributes != null && attributes.attacks() != null) {
               if (this.isTargetingMineableBlock() || this.isHarvesting) {
                  this.isHarvesting = true;
                  return;
               }

               this.startUpswing(attributes);
               info.setReturnValue(false);
               info.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"continueAttack(Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pre_handleBlockBreaking(boolean bl, CallbackInfo ci) {
      if (BetterCombatClientMod.ENABLED) {
         if (!CombatFlags.isAttackDisabled(this.player)) {
            Minecraft client = this.thisClient();
            WeaponAttributes attributes = WeaponRegistry.getAttributes(client.player.getMainHandItem());
            if (attributes != null && attributes.attacks() != null) {
               boolean isPressed = client.options.keyAttack.isDown();
               if (isPressed && !this.isHoldingAttackInput) {
                  if (this.isTargetingMineableBlock() || this.isHarvesting) {
                     this.isHarvesting = true;
                     return;
                  }

                  ci.cancel();
               }

               if (BetterCombatClientMod.config.isHoldToAttackEnabled && isPressed) {
                  this.isHoldingAttackInput = true;
                  this.startUpswing(attributes);
                  ci.cancel();
               } else {
                  this.isHarvesting = false;
                  this.isHoldingAttackInput = false;
               }
            }
         }
      }
   }

   @Inject(
      method = {"startUseItem()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pre_doItemUse(CallbackInfo ci) {
      if (BetterCombatClientMod.ENABLED) {
         if (!CombatFlags.isAttackDisabled(this.player)) {
            AttackHand hand = this.getCurrentHand();
            if (hand != null) {
               double upswingRate = hand.upswingRate();
               if (this.currentUpswingTicks() > 0 || this.player.getAttackStrengthScale(0.0F) < 1.0 - upswingRate) {
                  ci.cancel();
               }
            }
         }
      }
   }

   private boolean isTargetingMineableBlock() {
      if (!BetterCombatClientMod.config.isMiningWithWeaponsEnabled) {
         String whitelist = BetterCombatClientMod.config.mineWithWeaponWhitelist;
         if (whitelist == null || whitelist.isEmpty()) {
            return false;
         }

         ItemStack itemStack = this.player.getMainHandItem();
         String id = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
         if (!PatternMatching.matches(id, whitelist)) {
            return false;
         }
      }

      String regex = BetterCombatClientMod.config.mineWithWeaponBlacklist;
      if (regex != null && !regex.isEmpty()) {
         ItemStack itemStack = this.player.getMainHandItem();
         String id = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
         if (PatternMatching.matches(id, regex)) {
            return false;
         }
      }

      if (BetterCombatClientMod.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled && this.hasTargetsInReach()) {
         return false;
      } else {
         Minecraft client = this.thisClient();
         HitResult crosshairTarget = client.hitResult;
         if (crosshairTarget != null && crosshairTarget.getType() == Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState clicked = this.level.getBlockState(pos);
            if (!this.shouldSwingThruGrass()) {
               return true;
            }

            if (!clicked.getCollisionShape(this.level, pos).isEmpty() || clicked.getDestroySpeed(this.level, pos) != 0.0F) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean shouldSwingThruGrass() {
      if (!BetterCombatClientMod.config.isSwingThruGrassEnabled) {
         return false;
      } else if (BetterCombatClientMod.config.isSwingThruGrassSmart && !this.hasTargetsInReach()) {
         return false;
      } else {
         String regex = BetterCombatClientMod.config.swingThruGrassBlacklist;
         if (regex != null && !regex.isEmpty()) {
            ItemStack itemStack = this.player.getMainHandItem();
            String id = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
            return !PatternMatching.matches(id, regex);
         } else {
            return true;
         }
      }
   }

   @Unique
   private int currentTime() {
      return this.player == null ? 0 : this.player.tickCount;
   }

   @Unique
   private int currentUpswingTicks() {
      return this.ongoingSwing == null ? 0 : this.ongoingSwing.upswingTicksLeft(this.currentTime());
   }

   private void startUpswing(WeaponAttributes attributes) {
      if (!this.player.isHandsBusy()) {
         AttackHand attackHand = this.getCurrentHand();
         if (attackHand != null) {
            float upswingRate = (float)attackHand.upswingRate();
            if (this.currentUpswingTicks() <= 0
               && this.missTime <= 0
               && !this.player.isUsingItem()
               && !(this.player.getAttackStrengthScale(0.0F) < 1.0 - upswingRate)) {
               this.player.releaseUsingItem();
               this.lastAttacked = 0;
               this.upswingStack = this.player.getMainHandItem();
               float attackCooldownTicksFloat = PlayerAttackHelper.getAttackCooldownTicksCapped(this.player);
               int attackCooldownTicks = Math.round(attackCooldownTicksFloat);
               this.comboReset = Math.round(attackCooldownTicksFloat * BetterCombatMod.config.combo_reset_rate);
               int upswingTicks = Math.max(Math.round(attackCooldownTicksFloat * upswingRate), 1);
               this.ongoingSwing = new WeaponSwing(attackHand, this.currentTime(), upswingTicks, attackCooldownTicksFloat);
               this.lastSwingDuration = attackCooldownTicksFloat;
               this.rightClickDelay = attackCooldownTicks;
               this.setMiningCooldown(attackCooldownTicks);
               String animationName = attackHand.attack().animation();
               boolean isOffHand = attackHand.isOffHand();
               AnimatedHand animatedHand = AnimatedHand.from(isOffHand, attributes.isTwoHanded());
               ((PlayerAttackAnimatable)this.player).playAttackAnimation(animationName, animatedHand, attackCooldownTicksFloat, upswingRate);
               List<ParticlePlacement> particles = SlashParticleUtil.trailParticlesFromAttack(attackHand);
               TrailAppearance appearance = SlashParticleUtil.appearanceFor(this.player, attackHand.itemStack());
               Packets.AttackAnimation packet = new Packets.AttackAnimation(
                  this.player.getId(),
                  animatedHand,
                  animationName,
                  attackCooldownTicksFloat,
                  upswingRate,
                  (float)PlayerAttackHelper.getStaticRange(this.player, attackHand.itemStack()),
                  upswingTicks,
                  new Packets.SwingParticles(particles, appearance)
               );
               Platform.networkC2S_Send(packet);
               BetterCombatClientEvents.ATTACK_START.invoke(handler -> handler.onPlayerAttackStart(this.player, attackHand));
            }
         }
      }
   }

   private void cancelSwingIfNeeded() {
      if (this.upswingStack != null && !areItemStackEqual(this.player.getMainHandItem(), this.upswingStack)) {
         this.cancelWeaponSwing();
      }
   }

   private void attackFromUpswingIfNeeded() {
      if (this.ongoingSwing != null && this.currentUpswingTicks() == 0) {
         this.performAttack();
         this.upswingStack = null;
      }
   }

   private void resetComboIfNeeded() {
      if (this.lastAttacked > this.comboReset && this.getComboCount() > 0) {
         this.setComboCount(0);
      }

      if (!PlayerAttackHelper.shouldAttackWithOffHand(this.player, this.getComboCount())
         && (
            this.player.getMainHandItem() == null
               || this.lastAttacedWithItemStack != null && !this.lastAttacedWithItemStack.getItem().equals(this.player.getMainHandItem().getItem())
         )) {
         this.setComboCount(0);
      }
   }

   private boolean shouldUpdateTargetsInReach() {
      return !BetterCombatClientMod.config.isHighlightCrosshairEnabled && !BetterCombatClientMod.config.isAttackInsteadOfMineWhenEnemiesCloseEnabled
         ? false
         : this.targetsInReach == null;
   }

   private void updateTargetsInReach(List<Entity> targets) {
      this.targetsInReach = targets;
   }

   private void updateTargetsIfNeeded() {
      if (this.shouldUpdateTargetsInReach()) {
         List<Entity> targets = List.of();
         AttackHand hand = PlayerAttackHelper.getCurrentAttack(this.player, this.getComboCount());
         if (hand != null) {
            WeaponAttributes attributes = WeaponRegistry.getAttributes(hand.itemStack());
            double range = PlayerAttackHelper.getRangeForItem(this.player, hand.itemStack());
            range *= hand.attack().rangeMultiplier();
            if (attributes != null && attributes.attacks() != null) {
               targets = TargetFinder.findAttackTargets(this.player, this.getCursorTarget(), hand.attack(), range);
            }
         }

         this.updateTargetsInReach(targets);
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   private void pre_Tick(CallbackInfo ci) {
      if (this.player != null) {
         this.targetsInReach = null;
         this.lastAttacked++;
         if (CombatFlags.isAttackDisabled(this.player)) {
            if (this.ongoingSwing != null) {
               this.cancelWeaponSwing();
            }
         } else {
            if (this.ongoingSwing != null) {
               int time = this.currentTime();
               WeaponSwing swing = this.ongoingSwing;
               if (swing.ticksLeft(time) <= 0) {
                  this.ongoingSwing = null;
               }

               if (!this.player.isAlive() || !swing.isValid(time)) {
                  this.cancelWeaponSwing();
               }
            }

            this.cancelSwingIfNeeded();
            this.attackFromUpswingIfNeeded();
            this.updateTargetsIfNeeded();
            this.resetComboIfNeeded();
         }
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("TAIL")}
   )
   private void post_Tick(CallbackInfo ci) {
      if (this.player != null) {
         if (Keybindings.toggleMineKeyBinding.consumeClick()) {
            BetterCombatClientMod.config.isMiningWithWeaponsEnabled = !BetterCombatClientMod.config.isMiningWithWeaponsEnabled;
            AutoConfig.getConfigHolder(ClientConfigWrapper.class).save();
            String message = I18n.get(
               BetterCombatClientMod.config.isMiningWithWeaponsEnabled ? "hud.bettercombat.mine_with_weapons_on" : "hud.bettercombat.mine_with_weapons_off",
               new Object[0]
            );
            this.gui.setOverlayMessage(Component.literal(message), false);
         }
      }
   }

   private void performAttack() {
      if (Keybindings.feintKeyBinding.isDown()) {
         this.player.resetAttackStrengthTicker();
         this.cancelWeaponSwing();
      } else {
         WeaponSwing weaponSwing = this.ongoingSwing;
         if (weaponSwing != null) {
            AttackHand hand = weaponSwing.attackHand();
            if (hand != null) {
               WeaponAttributes.Attack attack = hand.attack();
               double upswingRate = hand.upswingRate();
               if (!(this.player.getAttackStrengthScale(0.0F) < 1.0 - upswingRate)) {
                  Entity cursorTarget = this.getCursorTarget();
                  double range = PlayerAttackHelper.getRangeForItem(this.player, hand.itemStack());
                  range *= hand.attack().rangeMultiplier();
                  List<Entity> targets = TargetFinder.findAttackTargets(this.player, cursorTarget, attack, range);
                  this.updateTargetsInReach(targets);
                  if (targets.size() == 0) {
                     PlatformClient.onEmptyLeftClick(this.player);
                     if (this.hitResult.getType() == Type.BLOCK) {
                        BlockHitResult blockHitResult = (BlockHitResult)this.hitResult;
                        BlockPos pos = blockHitResult.getBlockPos();
                        Packets.C2S_BlockHit packet = new Packets.C2S_BlockHit(pos);
                        Platform.networkC2S_Send(packet);
                     }
                  }

                  Packets.C2S_AttackRequest packet = new Packets.C2S_AttackRequest(
                     this.getComboCount(), this.player.isShiftKeyDown(), this.player.getInventory().selected, cursorTarget, targets
                  );
                  Platform.networkC2S_Send(packet);

                  for (Entity target : targets) {
                     this.player.attack(target);
                  }

                  this.player.resetAttackStrengthTicker();
                  BetterCombatClientEvents.ATTACK_HIT.invoke(handler -> handler.onPlayerAttackStart(this.player, hand, targets, cursorTarget));
                  List<ParticlePlacement> particles = SlashParticleUtil.trailParticlesFromAttack(hand);
                  TrailAppearance appearance = SlashParticleUtil.appearanceFor(this.player, hand.itemStack());
                  SlashParticleUtil.spawnParticles(
                     this.player, hand.isOffHand(), (float)PlayerAttackHelper.getStaticRange(this.player, hand.itemStack()), particles, appearance
                  );
                  this.setComboCount(this.getComboCount() + 1);
                  if (!hand.isOffHand()) {
                     this.lastAttacedWithItemStack = hand.itemStack();
                  }
               }
            }
         }
      }
   }

   private AttackHand getCurrentHand() {
      return PlayerAttackHelper.getCurrentAttack(this.player, this.getComboCount());
   }

   private void setComboCount(int comboCount) {
      ((PlayerAttackProperties)this.player).setComboCount(comboCount);
   }

   private static boolean areItemStackEqual(ItemStack left, ItemStack right) {
      if (left == null && right == null) {
         return true;
      } else {
         return left != null && right != null ? ItemStack.matches(left, right) : false;
      }
   }

   private void setMiningCooldown(int ticks) {
      Minecraft client = this.thisClient();
      ((MinecraftClientAccessor)client).setAttackCooldown(ticks);
   }

   private void cancelWeaponSwing() {
      int downWind = (int)Math.round(PlayerAttackHelper.getAttackCooldownTicksCapped(this.player) * (1.0 - 0.5 * BetterCombatMod.config.upswing_multiplier));
      ((PlayerAttackAnimatable)this.player).stopAttackAnimation(downWind);
      Packets.AttackAnimation packet = Packets.AttackAnimation.stop(this.player.getId(), downWind);
      Platform.networkC2S_Send(packet);
      this.upswingStack = null;
      this.ongoingSwing = null;
      this.rightClickDelay = 0;
      this.setMiningCooldown(0);
   }

   @Override
   public int getComboCount() {
      return ((PlayerAttackProperties)this.player).getComboCount();
   }

   @Override
   public boolean hasTargetsInReach() {
      return this.targetsInReach != null && !this.targetsInReach.isEmpty();
   }

   @Override
   public float getSwingProgress() {
      return !(this.lastAttacked > this.lastSwingDuration) && !(this.lastSwingDuration <= 0.0F) ? this.lastAttacked / this.lastSwingDuration : 1.0F;
   }

   @Override
   public int getUpswingTicks() {
      return this.currentUpswingTicks();
   }

   @Override
   public void cancelUpswing() {
      if (this.currentUpswingTicks() > 0) {
         this.cancelWeaponSwing();
      }
   }

   @Override
   public AttackHand getCurrentAttackHand() {
      return this.ongoingSwing != null ? this.ongoingSwing.attackHand() : null;
   }
}
