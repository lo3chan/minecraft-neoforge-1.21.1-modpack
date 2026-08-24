package net.mehvahdjukaar.amendments.integration.platform;

import java.lang.reflect.Method;
import net.mehvahdjukaar.amendments.common.block.CommonCauldronCode;
import net.mehvahdjukaar.amendments.common.block.LiquidCauldronBlock;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluid;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidRegistry;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import net.mehvahdjukaar.moonlight.api.misc.HolderRef;
import net.mehvahdjukaar.moonlight.api.misc.OptRegSupplier;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class AlexCavesCompatImpl {
   public static HolderRef<SoftFluid> ACID_SF = HolderRef.of(ResourceLocation.fromNamespaceAndPath("alexscaves", "acid"), SoftFluidRegistry.KEY);
   public static HolderRef<FluidType> ACID_FLUID_TYPE = HolderRef.of(ResourceLocation.fromNamespaceAndPath("alexscaves", "acid"), Keys.FLUID_TYPES);
   public static RegSupplier<Block> ACID_BLOCK = OptRegSupplier.of(ResourceLocation.fromNamespaceAndPath("alexscaves", "acid"), Registries.BLOCK);
   public static RegSupplier<ParticleType<?>> ACID_BUBBLE = OptRegSupplier.of(
      ResourceLocation.fromNamespaceAndPath("alexscaves", "acid_bubble"), Registries.PARTICLE_TYPE
   );
   public static RegSupplier<SoundEvent> ACID_IDLE = OptRegSupplier.of(ResourceLocation.fromNamespaceAndPath("alexscaves", "acid_idle"), Registries.SOUND_EVENT);
   public static final Method SET_H;

   public static void acidDamage(SoftFluidStack fluid, Level level, BlockPos pos, BlockState state, Entity entity) {
      if (fluid.is(ACID_SF)) {
         try {
            FluidType acidFluid = (FluidType)ACID_FLUID_TYPE.get(level);
            double oldH = entity.getFluidTypeHeight(acidFluid);
            double stateH = ((Integer)state.getValue(LiquidCauldronBlock.LEVEL)).intValue() * 0.25;
            SET_H.invoke(entity, acidFluid, stateH);
            Block acid = (Block)ACID_BLOCK.get();
            acid.defaultBlockState().entityInside(level, pos, entity);
            SET_H.invoke(entity, acidFluid, oldH);
         } catch (Exception var11) {
         }
      }
   }

   public static void acidParticles(SoftFluidStack fluid, Level level, BlockPos pos, RandomSource rand, double height) {
      if (fluid.is(ACID_SF)) {
         if (rand.nextInt(400) == 0) {
            level.playLocalSound(
               pos.getX() + 0.5,
               pos.getY() + 0.5,
               pos.getZ() + 0.5,
               (SoundEvent)ACID_IDLE.get(),
               SoundSource.BLOCKS,
               0.5F,
               rand.nextFloat() * 0.4F + 0.8F,
               false
            );
         }

         CommonCauldronCode.addSurfaceParticles(
            (ParticleOptions)ACID_BUBBLE.get(),
            level,
            pos,
            1,
            height,
            rand,
            (rand.nextFloat() - 0.5F) * 0.1F,
            0.05F + rand.nextFloat() * 0.1F,
            (rand.nextFloat() - 0.5F) * 0.1F
         );
      }
   }

   static {
      try {
         SET_H = Entity.class.getDeclaredMethod("setFluidTypeHeight", FluidType.class, double.class);
      } catch (NoSuchMethodException var1) {
         throw new RuntimeException(var1);
      }

      SET_H.setAccessible(true);
   }
}
