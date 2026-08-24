package com.aetherteam.aether.entity.passive;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.ai.goal.FallingRandomStrollGoal;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FlyingCow extends WingedAnimal {
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.COW.getDimensions().scale(0.5F).withEyeHeight(0.665F);

   public FlyingCow(EntityType<? extends FlyingCow> type, Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(AetherTags.Items.FLYING_COW_TEMPTATION_ITEMS), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(5, new FallingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.2);
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AetherTags.Items.FLYING_COW_TEMPTATION_ITEMS);
   }

   @Override
   public InteractionResult mobInteract(Player playerEntity, InteractionHand hand) {
      ItemStack itemStack = playerEntity.getItemInHand(hand);
      if (itemStack.is(Items.BUCKET) && !this.isBaby()) {
         playerEntity.playSound((SoundEvent)AetherSoundEvents.ENTITY_FLYING_COW_MILK.get(), 1.0F, 1.0F);
         ItemStack itemStack1 = ItemUtils.createFilledResult(itemStack, playerEntity, Items.MILK_BUCKET.getDefaultInstance());
         playerEntity.setItemInHand(hand, itemStack1);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.mobInteract(playerEntity, hand);
      }
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_FLYING_COW_AMBIENT.get();
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_FLYING_COW_HURT.get();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_FLYING_COW_DEATH.get();
   }

   @Nullable
   @Override
   protected SoundEvent getSaddledSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_FLYING_COW_SADDLE.get();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound((SoundEvent)AetherSoundEvents.ENTITY_FLYING_COW_STEP.get(), 0.15F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   public float getSteeringSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.75F;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
      return (AgeableMob)((EntityType)AetherEntityTypes.FLYING_COW.get()).create(level);
   }

   public EntityDimensions getDefaultDimensions(Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }
}
