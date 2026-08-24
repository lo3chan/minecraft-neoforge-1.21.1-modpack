package net.joefoxe.hexerei.fluid;

import net.joefoxe.hexerei.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.LavaFluid.Flowing;
import net.minecraft.world.level.material.LavaFluid.Source;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
   public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, "hexerei");
   public static final DeferredHolder<Fluid, PotionFluid> POTION = FLUIDS.register("potion", PotionFluid::new);
   public static final DeferredHolder<Fluid, BloodFluid.Flowing> BLOOD_FLOWING = FLUIDS.register("blood_flowing", BloodFluid.Flowing::new);
   public static final DeferredHolder<Fluid, BloodFluid.Source> BLOOD_FLUID = FLUIDS.register("blood_fluid", BloodFluid.Source::new);
   public static final DeferredHolder<Block, LiquidBlock> BLOOD_BLOCK = ModBlocks.BLOCKS
      .register(
         "blood",
         () -> new LiquidBlock(
            (FlowingFluid)BLOOD_FLUID.value(),
            Properties.of()
               .mapColor(MapColor.WATER)
               .replaceable()
               .noCollission()
               .strength(100.0F)
               .pushReaction(PushReaction.DESTROY)
               .noLootTable()
               .liquid()
               .sound(SoundType.EMPTY)
         )
      );
   public static final DeferredHolder<Fluid, LavaFluid> QUICKSILVER_FLOWING = FLUIDS.register("quicksilver_flowing", () -> new Flowing() {
      public FluidType getFluidType() {
         return (FluidType)ModFluidTypes.QUICKSILVER_FLUID_TYPE.value();
      }
   });
   public static final DeferredHolder<Fluid, Source> QUICKSILVER_FLUID = FLUIDS.register("quicksilver_fluid", () -> new Source() {
      public FluidType getFluidType() {
         return (FluidType)ModFluidTypes.QUICKSILVER_FLUID_TYPE.value();
      }
   });
   public static final DeferredHolder<Block, LiquidBlock> QUICKSILVER_BLOCK = ModBlocks.BLOCKS
      .register("quicksilver", () -> new LiquidBlock((FlowingFluid)QUICKSILVER_FLUID.value(), Properties.ofFullCopy(Blocks.LAVA)));
   public static final DeferredHolder<Fluid, TallowFluid.Flowing> TALLOW_FLOWING = FLUIDS.register("tallow_flowing", TallowFluid.Flowing::new);
   public static final DeferredHolder<Fluid, TallowFluid.Source> TALLOW_FLUID = FLUIDS.register("tallow_fluid", TallowFluid.Source::new);
   public static final DeferredHolder<Block, LiquidBlock> TALLOW_BLOCK = ModBlocks.BLOCKS
      .register(
         "tallow",
         () -> new LiquidBlock(
            (FlowingFluid)TALLOW_FLUID.value(),
            Properties.of()
               .mapColor(MapColor.WATER)
               .replaceable()
               .noCollission()
               .strength(100.0F)
               .pushReaction(PushReaction.DESTROY)
               .noLootTable()
               .liquid()
               .sound(SoundType.EMPTY)
         )
      );

   public static void register(IEventBus eventBus) {
      FLUIDS.register(eventBus);
   }
}
