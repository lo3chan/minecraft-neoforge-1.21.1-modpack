package com.aetherteam.aether.entity.passive;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.ai.controller.FallingMoveControl;
import com.aetherteam.aether.entity.ai.goal.EatAetherGrassGoal;
import com.aetherteam.aether.entity.ai.goal.FallingRandomStrollGoal;
import com.aetherteam.aether.entity.ai.navigator.FallPathNavigation;
import com.aetherteam.aether.loot.AetherLoot;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.IShearable;

public class Sheepuff extends AetherAnimal implements Shearable, IShearable {
   private static final EntityDataAccessor<Byte> DATA_WOOL_COLOR_ID = SynchedEntityData.defineId(Sheepuff.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Boolean> DATA_PUFFED_ID = SynchedEntityData.defineId(Sheepuff.class, EntityDataSerializers.BOOLEAN);
   private static final Map<DyeColor, ItemLike> ITEM_BY_DYE = (Map<DyeColor, ItemLike>)Util.make(new EnumMap(DyeColor.class), map -> {
      map.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
      map.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
      map.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      map.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
      map.put(DyeColor.LIME, Blocks.LIME_WOOL);
      map.put(DyeColor.PINK, Blocks.PINK_WOOL);
      map.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
      map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      map.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
      map.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
      map.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
      map.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
      map.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
      map.put(DyeColor.RED, Blocks.RED_WOOL);
      map.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
   });
   private static final Map<DyeColor, Integer> COLOR_BY_DYE = Maps.newEnumMap(
      Arrays.stream(DyeColor.values()).collect(Collectors.toMap(p_29868_ -> (DyeColor)p_29868_, Sheepuff::createSheepColor))
   );
   private int eatAnimationTick;
   private int amountEaten;
   private EatAetherGrassGoal eatBlockGoal;
   private final FallPathNavigation fallNavigation;
   private final GroundPathNavigation groundNavigation;

   private static int createSheepColor(DyeColor dyeColor) {
      if (dyeColor == DyeColor.WHITE) {
         return -1644826;
      } else {
         int i = dyeColor.getTextureDiffuseColor();
         float f = 0.75F;
         return ARGB32.color(255, Mth.floor(ARGB32.red(i) * f), Mth.floor(ARGB32.green(i) * f), Mth.floor(ARGB32.blue(i) * f));
      }
   }

   public static int getColor(DyeColor dyeColor) {
      return COLOR_BY_DYE.get(dyeColor);
   }

   public Sheepuff(EntityType<? extends Sheepuff> type, Level level) {
      super(type, level);
      this.moveControl = new FallingMoveControl(this);
      this.fallNavigation = new FallPathNavigation(this, level);
      this.groundNavigation = new GroundPathNavigation(this, level);
   }

   protected void registerGoals() {
      this.eatBlockGoal = new EatAetherGrassGoal(this);
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, Ingredient.of(AetherTags.Items.SHEEPUFF_TEMPTATION_ITEMS), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, this.eatBlockGoal);
      this.goalSelector.addGoal(6, new FallingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   public static Builder createMobAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.23);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_WOOL_COLOR_ID, (byte)0);
      builder.define(DATA_PUFFED_ID, false);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
      this.setColor(getRandomSheepuffColor(level.getRandom()));
      return super.finalizeSpawn(level, difficulty, reason, spawnData);
   }

   protected void customServerAiStep() {
      this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
      super.customServerAiStep();
   }

   public void aiStep() {
      if (this.level().isClientSide()) {
         this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      }

      super.aiStep();
   }

   public void tick() {
      super.tick();
      if (this.getPuffed()) {
         this.checkSlowFallDistance();
         AttributeInstance gravity = this.getAttribute(Attributes.GRAVITY);
         if (gravity != null) {
            double fallSpeed = Math.max(gravity.getValue() * -0.625, -0.05);
            if (this.getDeltaMovement().y() < fallSpeed) {
               this.setDeltaMovement(this.getDeltaMovement().x, fallSpeed, this.getDeltaMovement().z);
            }
         }

         this.navigation = this.fallNavigation;
      } else {
         this.navigation = this.groundNavigation;
      }
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      if (this.getPuffed()) {
         this.push(0.0, 1.8, 0.0);
      }
   }

   public void ate() {
      this.amountEaten++;
      if (!this.isSheared()) {
         if (this.amountEaten >= 2) {
            this.setPuffed(true);
            this.amountEaten = 0;
         }
      } else if (this.amountEaten == 1) {
         this.setSheared(false);
         this.amountEaten = 0;
      }

      if (this.isBaby()) {
         this.ageUp(60);
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (itemstack.getItem() instanceof DyeItem dyeItem && !this.isSheared()) {
         DyeColor color = dyeItem.getDyeColor();
         if (this.getColor() != color) {
            if (this.getPuffed() && itemstack.getCount() >= 2) {
               player.swing(hand);
               if (!player.level().isClientSide()) {
                  this.setColor(color);
                  if (!player.getAbilities().instabuild) {
                     itemstack.shrink(2);
                  }
               }
            } else if (!this.getPuffed()) {
               player.swing(hand);
               if (!player.level().isClientSide()) {
                  this.setColor(color);
                  if (!player.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }
               }
            }
         }
      }

      return super.mobInteract(player, hand);
   }

   public void shear(SoundSource source) {
      this.level().playSound(null, this, (SoundEvent)AetherSoundEvents.ENTITY_SHEEPUFF_SHEAR.get(), source, 1.0F, 1.0F);
      this.amountEaten = 0;
      int i;
      if (this.getPuffed()) {
         this.setPuffed(false);
         i = 2;
      } else {
         this.setSheared(true);
         i = 1;
      }

      i += this.getRandom().nextInt(3);

      for (int j = 0; j < i; j++) {
         ItemEntity itementity = this.spawnAtLocation(ITEM_BY_DYE.get(this.getColor()), 1);
         if (itementity != null) {
            itementity.setDeltaMovement(
               itementity.getDeltaMovement()
                  .add(
                     (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.1F,
                     this.getRandom().nextFloat() * 0.05F,
                     (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.1F
                  )
            );
         }
      }
   }

   public boolean readyForShearing() {
      return this.isAlive() && !this.isSheared() && !this.isBaby();
   }

   public boolean isSheared() {
      return ((Byte)this.getEntityData().get(DATA_WOOL_COLOR_ID) & 16) != 0;
   }

   public void setSheared(boolean sheared) {
      byte b0 = (Byte)this.getEntityData().get(DATA_WOOL_COLOR_ID);
      if (sheared) {
         this.getEntityData().set(DATA_WOOL_COLOR_ID, (byte)(b0 | 16));
      } else {
         this.getEntityData().set(DATA_WOOL_COLOR_ID, (byte)(b0 & -17));
      }
   }

   public boolean getPuffed() {
      return (Boolean)this.getEntityData().get(DATA_PUFFED_ID);
   }

   public void setPuffed(boolean flag) {
      this.getEntityData().set(DATA_PUFFED_ID, flag);
   }

   public DyeColor getColor() {
      return DyeColor.byId((Byte)this.getEntityData().get(DATA_WOOL_COLOR_ID) & 15);
   }

   public void setColor(DyeColor dyeColor) {
      byte b0 = (Byte)this.getEntityData().get(DATA_WOOL_COLOR_ID);
      this.getEntityData().set(DATA_WOOL_COLOR_ID, (byte)(b0 & 240 | dyeColor.getId() & 15));
   }

   public static DyeColor getRandomSheepuffColor(RandomSource random) {
      int i = random.nextInt(100);
      if (i < 5) {
         return DyeColor.LIGHT_BLUE;
      } else if (i < 10) {
         return DyeColor.CYAN;
      } else if (i < 15) {
         return DyeColor.LIME;
      } else if (i < 18) {
         return DyeColor.PINK;
      } else {
         return random.nextInt(500) == 0 ? DyeColor.PURPLE : DyeColor.WHITE;
      }
   }

   public boolean isFood(ItemStack stack) {
      return stack.is(AetherTags.Items.SHEEPUFF_TEMPTATION_ITEMS);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SHEEPUFF_AMBIENT.get();
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return (SoundEvent)AetherSoundEvents.ENTITY_SHEEPUFF_HURT.get();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return (SoundEvent)AetherSoundEvents.ENTITY_SHEEPUFF_DEATH.get();
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)AetherSoundEvents.ENTITY_SHEEPUFF_STEP.get(), SoundSource.NEUTRAL, 0.15F, 1.0F);
   }

   public ResourceKey<LootTable> getDefaultLootTable() {
      if (this.isSheared()) {
         return this.getType().getDefaultLootTable();
      } else {
         return switch (this.getColor()) {
            case WHITE -> AetherLoot.ENTITIES_SHEEPUFF_WHITE;
            case ORANGE -> AetherLoot.ENTITIES_SHEEPUFF_ORANGE;
            case MAGENTA -> AetherLoot.ENTITIES_SHEEPUFF_MAGENTA;
            case LIGHT_BLUE -> AetherLoot.ENTITIES_SHEEPUFF_LIGHT_BLUE;
            case YELLOW -> AetherLoot.ENTITIES_SHEEPUFF_YELLOW;
            case LIME -> AetherLoot.ENTITIES_SHEEPUFF_LIME;
            case PINK -> AetherLoot.ENTITIES_SHEEPUFF_PINK;
            case GRAY -> AetherLoot.ENTITIES_SHEEPUFF_GRAY;
            case LIGHT_GRAY -> AetherLoot.ENTITIES_SHEEPUFF_LIGHT_GRAY;
            case CYAN -> AetherLoot.ENTITIES_SHEEPUFF_CYAN;
            case PURPLE -> AetherLoot.ENTITIES_SHEEPUFF_PURPLE;
            case BLUE -> AetherLoot.ENTITIES_SHEEPUFF_BLUE;
            case BROWN -> AetherLoot.ENTITIES_SHEEPUFF_BROWN;
            case GREEN -> AetherLoot.ENTITIES_SHEEPUFF_GREEN;
            case RED -> AetherLoot.ENTITIES_SHEEPUFF_RED;
            case BLACK -> AetherLoot.ENTITIES_SHEEPUFF_BLACK;
            default -> throw new MatchException(null, null);
         };
      }
   }

   protected int calculateFallDamage(float distance, float damageMultiplier) {
      return this.getPuffed() ? 0 : super.calculateFallDamage(distance, damageMultiplier);
   }

   public int getMaxFallDistance() {
      return !this.onGround() && this.getPuffed() ? 20 : super.getMaxFallDistance();
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob entity) {
      Sheepuff parent = (Sheepuff)entity;
      Sheepuff baby = (Sheepuff)((EntityType)AetherEntityTypes.SHEEPUFF.get()).create(level);
      if (baby != null) {
         baby.setColor(this.getOffspringColor(this, parent));
      }

      return baby;
   }

   private DyeColor getOffspringColor(Animal parent1, Animal parent2) {
      DyeColor dyeColor1 = ((Sheepuff)parent1).getColor();
      DyeColor dyeColor2 = ((Sheepuff)parent2).getColor();
      CraftingInput craftingInput = makeCraftInput(dyeColor1, dyeColor2);
      return this.level()
         .getRecipeManager()
         .getRecipeFor(RecipeType.CRAFTING, craftingInput, this.level())
         .map(recipeHolder -> ((CraftingRecipe)recipeHolder.value()).assemble(craftingInput, this.level().registryAccess()))
         .<Item>map(ItemStack::getItem)
         .filter(DyeItem.class::isInstance)
         .map(DyeItem.class::cast)
         .<DyeColor>map(DyeItem::getDyeColor)
         .orElseGet(() -> this.level().random.nextBoolean() ? dyeColor1 : dyeColor2);
   }

   private static CraftingInput makeCraftInput(DyeColor color1, DyeColor color2) {
      return CraftingInput.of(2, 1, List.of(new ItemStack(DyeItem.byColor(color1)), new ItemStack(DyeItem.byColor(color2))));
   }

   public float getHeadEatPositionScale(float pos) {
      if (this.eatAnimationTick <= 0) {
         return 0.0F;
      } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
         return 1.0F;
      } else {
         return this.eatAnimationTick < 4 ? (this.eatAnimationTick - pos) / 4.0F : -(this.eatAnimationTick - 40 - pos) / 4.0F;
      }
   }

   public float getHeadEatAngleScale(float angle) {
      if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
         float f = (this.eatAnimationTick - 4 - angle) / 32.0F;
         return 0.62831855F + 0.21991149F * Mth.sin(f * 28.7F);
      } else {
         return this.eatAnimationTick > 0 ? 0.62831855F : this.getXRot() * 0.017453292F;
      }
   }

   public void handleEntityEvent(byte id) {
      if (id == 10) {
         this.eatAnimationTick = 40;
      } else {
         super.handleEntityEvent(id);
      }
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Sheared", this.isSheared());
      tag.putBoolean("Puffed", this.getPuffed());
      tag.putByte("Color", (byte)this.getColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("Sheared")) {
         this.setSheared(tag.getBoolean("Sheared"));
      }

      if (tag.contains("Puffed")) {
         this.setPuffed(tag.getBoolean("Puffed"));
      }

      if (tag.contains("Color")) {
         this.setColor(DyeColor.byId(tag.getByte("Color")));
      }
   }
}
