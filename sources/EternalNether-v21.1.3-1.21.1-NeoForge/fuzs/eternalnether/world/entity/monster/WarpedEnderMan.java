package fuzs.eternalnether.world.entity.monster;

import com.google.common.collect.ImmutableMap;
import fuzs.eternalnether.init.ModSoundEvents;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import java.util.Map;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class WarpedEnderMan extends EnderMan {
   private static final Map<SoundEvent, SoundEvent> SOUND_EVENTS = ImmutableMap.of(
      SoundEvents.ENDERMAN_AMBIENT,
      (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_AMBIENT.value(),
      SoundEvents.ENDERMAN_DEATH,
      (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_DEATH.value(),
      SoundEvents.ENDERMAN_HURT,
      (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_HURT.value(),
      SoundEvents.ENDERMAN_SCREAM,
      (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_SCREAM.value(),
      SoundEvents.ENDERMAN_STARE,
      (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_STARE.value(),
      SoundEvents.ENDERMAN_TELEPORT,
      (SoundEvent)ModSoundEvents.WARPED_ENDERMAN_TELEPORT.value()
   );
   private static final int SHEAR_COOLDOWN = 20;
   private static final WarpedEnderMan.WarpedEnderManVariant[] VARIANTS = WarpedEnderMan.WarpedEnderManVariant.values();
   private static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(WarpedEnderMan.class, EntityDataSerializers.INT);
   private WarpedEnderMan.WarpedEnderManVariant variant;
   private int shearCooldownCounter = 0;
   private boolean toConvertToEnderman = false;

   public WarpedEnderMan(EntityType<? extends EnderMan> entityType, Level world) {
      super(entityType, world);
      this.setVariant(randomVariant(this.getRandom()));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Endermite.class, true, false));
      this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal(this, false));
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 55.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.ATTACK_DAMAGE, 8.5)
         .add(Attributes.FOLLOW_RANGE, 64.0);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         if (this.shearCooldownCounter > 0) {
            this.shearCooldownCounter--;
         } else if (this.shearCooldownCounter < 0) {
            this.shearCooldownCounter = 0;
         }

         if (this.toConvertToEnderman) {
            EnderMan enderman = (EnderMan)this.convertTo(EntityType.ENDERMAN, false);
            this.playShearSound(enderman);
         }
      }
   }

   public void playSound(SoundEvent event, float volume, float pitch) {
      super.playSound(SOUND_EVENTS.getOrDefault(event, event), volume, pitch);
   }

   private void playShearSound(EnderMan enderman) {
      this.level().playSound(null, enderman, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(VARIANT_ID, 2);
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("Variant", this.variant.ordinal());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.setVariant(VARIANTS[tag.getInt("Variant")]);
   }

   public WarpedEnderMan.WarpedEnderManVariant getVariant() {
      WarpedEnderMan.WarpedEnderManVariant ret = VARIANTS[this.entityData.get(VARIANT_ID)];
      this.variant = ret;
      return ret;
   }

   public void setVariant(WarpedEnderMan.WarpedEnderManVariant variant) {
      this.variant = variant;
      this.entityData.set(VARIANT_ID, variant.ordinal());
   }

   public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack itemInHand = player.getItemInHand(interactionHand);
      if (itemInHand.is(Items.SHEARS)) {
         if (this.isReadyForShearing() && !this.level().isClientSide) {
            boolean flag = this.toConvertToEnderman;
            this.shearWarp();
            this.gameEvent(GameEvent.SHEAR, player);
            ItemHelper.hurtAndBreak(itemInHand, 1, player, interactionHand);
            if (this.toConvertToEnderman && !flag && player instanceof ServerPlayer serverPlayer) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, this);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.CONSUME;
         }
      } else {
         return super.mobInteract(player, interactionHand);
      }
   }

   private boolean isReadyForShearing() {
      return this.shearCooldownCounter == 0;
   }

   private void shearWarp() {
      ItemStack itemstack = new ItemStack(Items.TWISTING_VINES, this.getRandom().nextInt(2) + 1);
      BehaviorUtils.throwItem(this, itemstack, Vec3.ZERO.add(0.0, 1.0, 0.0));
      this.shearCooldownCounter = 20;
      switch (this.variant) {
         case FRESH:
            this.toConvertToEnderman = true;
            break;
         case SHORT_VINE:
            this.setVariant(WarpedEnderMan.WarpedEnderManVariant.FRESH);
            this.playShearSound(this);
            break;
         case LONG_VINE:
            this.setVariant(WarpedEnderMan.WarpedEnderManVariant.SHORT_VINE);
            this.playShearSound(this);
      }
   }

   private static WarpedEnderMan.WarpedEnderManVariant randomVariant(RandomSource random) {
      return VARIANTS[random.nextInt(VARIANTS.length)];
   }

   public static enum WarpedEnderManVariant {
      FRESH,
      SHORT_VINE,
      LONG_VINE;
   }
}
