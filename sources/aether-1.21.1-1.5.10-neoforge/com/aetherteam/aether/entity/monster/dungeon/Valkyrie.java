package com.aetherteam.aether.entity.monster.dungeon;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.item.AetherItems;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Valkyrie extends AbstractValkyrie implements NeutralMob {
   private static final int PERSISTENT_ANGER_TIME = 2147483647;
   private int remainingPersistentAngerTime;
   @Nullable
   private UUID persistentAngerTarget;
   private int chatTimer;

   public Valkyrie(EntityType<? extends Valkyrie> type, Level worldIn) {
      super(type, worldIn);
   }

   @Override
   public void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(3, new AbstractValkyrie.LungeGoal(this, 0.65, 30));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, 10, false, false, this::isAngryAt));
      this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal(this, false));
   }

   public static Builder createMobAttributes() {
      return createAttributes()
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.25)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 50.0);
   }

   @Override
   public void tick() {
      super.tick();
      double motionY = this.getDeltaMovement().y();
      if (!this.onGround() && Math.abs(motionY - this.lastMotionY) > 0.07 && Math.abs(motionY - this.lastMotionY) < 0.09) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.0225, 0.0));
      }
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide()) {
         this.updatePersistentAnger((ServerLevel)this.level(), true);
      }
   }

   public void customServerAiStep() {
      super.customServerAiStep();
      if (this.chatTimer > 0) {
         this.chatTimer--;
      }
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack item = player.getItemInHand(hand);
      if (hand == InteractionHand.MAIN_HAND && this.getTarget() == null) {
         this.lookAt(player, 180.0F, 180.0F);
         if (!this.level().isClientSide() && this.chatTimer <= 0) {
            String translationId;
            if (item.getItem() == AetherItems.VICTORY_MEDAL.get()) {
               if (item.getCount() >= 10) {
                  translationId = "gui.aether.valkyrie.dialog.medal.1";
               } else if (item.getCount() >= 5) {
                  translationId = "gui.aether.valkyrie.dialog.medal.2";
               } else {
                  translationId = "gui.aether.valkyrie.dialog.medal.3";
               }
            } else {
               translationId = "gui.aether.valkyrie.dialog." + (char)(this.getRandom().nextInt(3) + 49);
            }

            this.chat(player, Component.translatable(translationId), false);
            this.playSound(this.getInteractSound(), 1.0F, this.getVoicePitch());
            this.chatTimer = 60;
         }
      }

      return InteractionResult.PASS;
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      boolean result = super.hurt(source, amount);
      if (!this.level().isClientSide()
         && source.getEntity() instanceof Player player
         && this.getTarget() == null
         && this.level().getDifficulty() != Difficulty.PEACEFUL
         && this.getHealth() > 0.0F) {
         this.chat(player, Component.translatable("gui.aether.valkyrie.dialog.attack." + (char)(this.getRandom().nextInt(3) + 49)), false);
      }

      return result;
   }

   public boolean doHurtTarget(Entity entity) {
      boolean result = super.doHurtTarget(entity);
      if (entity instanceof ServerPlayer player && player.getHealth() <= 0.0F) {
         this.chat(
            player,
            Component.translatable("gui.aether.valkyrie.dialog.playerdeath." + (char)(this.getRandom().nextInt(3) + 49), new Object[]{player.getDisplayName()}),
            false
         );
      }

      return result;
   }

   public void die(DamageSource source) {
      if (source.getEntity() instanceof Player player) {
         this.chat(player, Component.translatable("gui.aether.valkyrie.dialog.defeated." + (char)(this.getRandom().nextInt(3) + 49)), false);
      }

      this.spawnExplosionParticles();
      super.die(source);
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(2147483647);
   }

   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   public void setRemainingPersistentAngerTime(int time) {
      this.remainingPersistentAngerTime = time;
   }

   @Nullable
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID target) {
      this.persistentAngerTarget = target;
   }

   protected SoundEvent getInteractSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_VALKYRIE_INTERACT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)AetherSoundEvents.ENTITY_VALKYRIE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_VALKYRIE_DEATH.get();
   }
}
