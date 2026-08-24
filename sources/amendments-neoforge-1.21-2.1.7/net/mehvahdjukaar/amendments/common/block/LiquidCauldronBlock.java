package net.mehvahdjukaar.amendments.common.block;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import net.mehvahdjukaar.amendments.common.tile.LiquidCauldronBlockTile;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.integration.AlexCavesCompat;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.amendments.reg.ModTags;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.MLBuiltinSoftFluids;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.PotionBottleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LiquidCauldronBlock extends ModCauldronBlock {
   public static final MapCodec<LiquidCauldronBlock> CODEC = simpleCodec(LiquidCauldronBlock::new);
   public static final IntegerProperty LEVEL = PlatHelper.getPlatform().isFabric() ? BlockStateProperties.LEVEL_CAULDRON : ModBlockProperties.LEVEL_1_4;
   public static final IntegerProperty LIGHT_LEVEL = ModBlockProperties.LIGHT_LEVEL;

   public LiquidCauldronBlock(Properties properties) {
      super(properties.lightLevel(value -> (Integer)value.getValue(LIGHT_LEVEL)));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(LEVEL, 1)).setValue(LIGHT_LEVEL, 0)).setValue(BOILING, false)
      );
   }

   protected MapCodec<? extends LiquidCauldronBlock> codec() {
      return CODEC;
   }

   @Override
   public IntegerProperty getLevelProperty() {
      return LEVEL;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LEVEL, LIGHT_LEVEL});
   }

   protected boolean canReceiveStalactiteDrip(Fluid fluid) {
      return true;
   }

   public void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
      if (!this.isFull(state) && level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
         int amount = SoftFluid.BOTTLE_COUNT;
         if (!CommonConfigs.LAVA_LAYERS.get() && fluid == Fluids.LAVA) {
            amount = SoftFluid.BUCKET_COUNT;
         }

         SoftFluidStack sf = SoftFluidStack.fromFluid(fluid, amount, DataComponentPatch.EMPTY, level.registryAccess());
         if (!sf.isEmpty() && te.getSoftFluidTank().addFluid(sf, false) != 0) {
            te.setChanged();
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(state));
            if (fluid == Fluids.LAVA) {
               level.levelEvent(1046, pos, 0);
            } else {
               level.levelEvent(1047, pos, 0);
            }
         }
      }
   }

   @Override
   public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Precipitation precipitation) {
      super.handlePrecipitation(state, level, pos, precipitation);
      if (!this.isFull(state) && level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
         SoftFluidTank softFluidTank = te.getSoftFluidTank();
         SoftFluidStack sf = softFluidTank.getFluid();
         if (precipitation == Precipitation.RAIN
            && sf.is(MLBuiltinSoftFluids.WATER)
            && softFluidTank.addFluid(SoftFluidStack.fromFluid(Fluids.WATER, 1, DataComponentPatch.EMPTY, level.registryAccess()), false) > 0) {
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(state));
            te.setChanged();
         }
      }
   }

   @Override
   protected void handleEntityInsideFluidSpecial(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (entity.mayInteract(level, pos) && level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile tile) {
         SoftFluidStack fluid = tile.getSoftFluidTank().getFluid();
         PotionBottleType potType = this.getPotType(fluid);
         if (entity instanceof LivingEntity living) {
            if (potType != null && potType != PotionBottleType.REGULAR && this.applyPotionFluidEffects(level, pos, living, fluid)) {
               tile.consumeOneLayer();
               level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }

            if (CompatHandler.ALEX_CAVES) {
               AlexCavesCompat.acidDamage(fluid, level, pos, state, entity);
            }
         }

         if (!tile.isGlowing()
            && fluid.is(ModTags.CAN_GLOW)
            && entity instanceof ItemEntity ie
            && ie.getItem().is(Items.GLOW_INK_SAC)
            && this.isEntityInsideContent(state, pos, entity)) {
            CommonCauldronCode.playSplashEffects(entity, this.getContentHeight(state));
            tile.setGlowing(true);
            level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            ie.getItem().shrink(1);
            if (ie.getItem().isEmpty()) {
               ie.discard();
            }
         }
      }
   }

   private boolean applyPotionFluidEffects(Level level, BlockPos pos, LivingEntity living, SoftFluidStack stack) {
      List<MobEffectInstance> effects = this.getPotionEffects(stack);
      boolean success = false;

      for (MobEffectInstance effect : effects) {
         Holder<MobEffect> ef = effect.getEffect();
         if (!living.hasEffect(ef)) {
            if (((MobEffect)ef.value()).isInstantenous()) {
               ((MobEffect)ef.value()).applyInstantenousEffect(null, null, living, effect.getAmplifier(), 1.0);
            } else {
               living.addEffect(new MobEffectInstance(effect));
            }

            success = true;
         }
      }

      if (success) {
         level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      return success;
   }

   @Override
   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
      super.animateTick(state, level, pos, rand);
      if (level.getBlockEntity(pos) instanceof LiquidCauldronBlockTile te) {
         SoftFluidTank tank = te.getSoftFluidTank();
         if (level.random.nextInt(4) == 0) {
            SoftFluidStack fluid = tank.getFluid();
            PotionBottleType type = this.getPotType(fluid);
            double height = this.getContentHeight(state);
            if (type != null) {
               if (getAllPotionEffects(fluid).size() >= CommonConfigs.POTION_MIXING_LIMIT.get()) {
                  CommonCauldronCode.addSurfaceParticles(ParticleTypes.SMOKE, level, pos, 2, height, rand, 0.0F, 0.0F, 0.0F);
               }

               if (type != PotionBottleType.REGULAR) {
                  int color = tank.getCachedParticleColor(level, pos);
                  int alpha = type == PotionBottleType.SPLASH ? Mth.floor(38.25F) : 255;
                  ParticleOptions particle = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB32.color(alpha, color));
                  this.addPotionParticles(particle, level, pos, 1, height, rand, color);
               }
            }

            if (CompatHandler.ALEX_CAVES) {
               AlexCavesCompat.acidParticles(fluid, level, pos, rand, height);
            }

            BlockPos blockPos = pos.above();
            if (fluid.is(MLBuiltinSoftFluids.LAVA) && level.getBlockState(blockPos).isAir() && !level.getBlockState(blockPos).isSolidRender(level, blockPos)) {
               Vec3 c = pos.getCenter();
               if (rand.nextInt(20) == 0) {
                  CommonCauldronCode.addSurfaceParticles(ParticleTypes.LAVA, level, pos, 1, height, rand, 0.0F, 0.0F, 0.0F);
                  level.playLocalSound(
                     c.x, height, c.z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false
                  );
               }

               if (rand.nextInt(40) == 0) {
                  level.playLocalSound(
                     c.x, height, c.z, SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, false
                  );
               }
            }
         }
      }
   }

   @NotNull
   public static ArrayList<MobEffectInstance> getAllPotionEffects(SoftFluidStack fluid) {
      return Lists.newArrayList(((PotionContents)fluid.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getAllEffects());
   }

   @Nullable
   private PotionBottleType getPotType(SoftFluidStack stack) {
      return stack.is(MLBuiltinSoftFluids.POTION)
         ? (PotionBottleType)stack.getOrDefault((DataComponentType)MoonlightRegistry.BOTTLE_TYPE.get(), PotionBottleType.REGULAR)
         : null;
   }

   private List<MobEffectInstance> getPotionEffects(SoftFluidStack stack) {
      return StreamSupport.<MobEffectInstance>stream(
            ((PotionContents)stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getAllEffects().spliterator(), false
         )
         .toList();
   }

   private void addPotionParticles(ParticleOptions type, Level level, BlockPos pos, int count, double surface, RandomSource rand, int color) {
      float r = ARGB32.red(color) / 255.0F;
      float g = ARGB32.green(color) / 255.0F;
      float b = ARGB32.blue(color) / 255.0F;
      CommonCauldronCode.addSurfaceParticles(type, level, pos, count, surface, rand, r, g, b);
   }

   @Override
   public BlockState updateStateOnFluidChange(BlockState state, Level level, BlockPos pos, SoftFluidStack fluid) {
      BlockState exploded = this.maybeExplode(state, level, pos, fluid);
      if (exploded != null) {
         return exploded;
      } else {
         int light = fluid.fluid().getLuminosity();
         if (light != (Integer)state.getValue(ModBlockProperties.LIGHT_LEVEL)) {
            state = (BlockState)state.setValue(ModBlockProperties.LIGHT_LEVEL, light);
         }

         return super.updateStateOnFluidChange(state, level, pos, fluid);
      }
   }

   @Nullable
   private BlockState maybeExplode(BlockState state, Level level, BlockPos pos, SoftFluidStack fluid) {
      List<MobEffectInstance> potionEffects = getAllPotionEffects(fluid);
      int potionEffectAmount = potionEffects.size();
      if (potionEffectAmount >= CommonConfigs.POTION_MIXING_LIMIT.get()) {
         if (potionEffectAmount > CommonConfigs.POTION_MIXING_LIMIT.get()) {
            level.destroyBlock(pos, true);
            Vec3 vec3 = pos.getCenter();
            level.explode(null, level.damageSources().badRespawnPointExplosion(vec3), null, vec3.x, vec3.y, vec3.z, 1.4F, false, ExplosionInteraction.NONE);
            return state;
         } else {
            if (level.isClientSide) {
               CommonCauldronCode.addSurfaceParticles(ParticleTypes.SMOKE, level, pos, 12, this.getContentHeight(state), level.random, 0.0F, 0.0F, 0.0F);
            }

            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return null;
         }
      } else {
         Map<MobEffect, MobEffect> inverse = CommonConfigs.INVERSE_POTIONS.get();
         List<MobEffect> effects = potionEffects.stream().map(e -> (MobEffect)e.getEffect().value()).toList();

         for (MobEffect effect : effects) {
            MobEffect inv = inverse.get(effect);
            if (inv != null && effects.contains(inv)) {
               if (level.isClientSide) {
                  CommonCauldronCode.addSurfaceParticles(
                     ParticleTypes.POOF, level, pos, 8, this.getContentHeight(state), level.random, 0.0F, 0.01F + level.random.nextFloat() * 0.1F, 0.0F
                  );
               }

               level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
               return Blocks.CAULDRON.defaultBlockState();
            }
         }

         return null;
      }
   }
}
