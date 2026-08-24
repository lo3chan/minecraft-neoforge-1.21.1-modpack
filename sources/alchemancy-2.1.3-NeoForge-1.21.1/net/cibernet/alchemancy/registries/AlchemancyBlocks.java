package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.blocks.AlchemancyCatalystBlock;
import net.cibernet.alchemancy.blocks.AlchemancyForgeBlock;
import net.cibernet.alchemancy.blocks.BlazebloomBlock;
import net.cibernet.alchemancy.blocks.ChromachineBlock;
import net.cibernet.alchemancy.blocks.FlatHopperBlock;
import net.cibernet.alchemancy.blocks.FlattenedItemBlock;
import net.cibernet.alchemancy.blocks.GlowingOrbBlock;
import net.cibernet.alchemancy.blocks.GustBasketBlock;
import net.cibernet.alchemancy.blocks.InfusionPedestalBlock;
import net.cibernet.alchemancy.blocks.PhantomMembraneBlock;
import net.cibernet.alchemancy.blocks.PottedBlazebloomBlock;
import net.cibernet.alchemancy.blocks.RootedItemBlock;
import net.cibernet.alchemancy.blocks.SculkBudBlock;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class AlchemancyBlocks {
   public static final Blocks REGISTRY = DeferredRegister.createBlocks("alchemancy");
   public static final DeferredBlock<FlowerBlock> BLAZEBLOOM = REGISTRY.register(
      "blazebloom",
      () -> new BlazebloomBlock(
         MobEffects.FIRE_RESISTANCE,
         10.0F,
         Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .offsetType(OffsetType.XZ)
            .lightLevel(state -> 3)
            .pushReaction(PushReaction.DESTROY)
      ) {}
   );
   public static final DeferredBlock<PottedBlazebloomBlock> POTTED_BLAZEBLOOM = REGISTRY.register(
      "potted_blazebloom",
      () -> new PottedBlazebloomBlock(BLAZEBLOOM, Properties.of().instabreak().noOcclusion().lightLevel(state -> 3).pushReaction(PushReaction.DESTROY))
   );
   public static final DeferredBlock<InfusionPedestalBlock> INFUSION_PEDESTAL = REGISTRY.register(
      "infusion_pedestal", () -> new InfusionPedestalBlock(Properties.of().strength(1.5F))
   );
   public static final DeferredBlock<AlchemancyForgeBlock> ALCHEMANCY_FORGE = REGISTRY.register(
      "alchemancy_forge", () -> new AlchemancyForgeBlock(Properties.of().strength(1.5F))
   );
   public static final DeferredBlock<AlchemancyCatalystBlock> ALCHEMANCY_CATALYST = REGISTRY.register(
      "alchemancy_catalyst", () -> new AlchemancyCatalystBlock(Properties.of().strength(1.5F).sound(SoundType.GLASS).noOcclusion())
   );
   public static final DeferredBlock<RootedItemBlock> ROOTED_ITEM = REGISTRY.register(
      "rooted_item", () -> new RootedItemBlock(Properties.of().sound(SoundType.CROP).noOcclusion().mapColor(MapColor.PLANT))
   );
   public static final DeferredBlock<FlattenedItemBlock> FLATTENED_ITEM = REGISTRY.register(
      "flattened_item", () -> new FlattenedItemBlock(Properties.of().noOcclusion())
   );
   public static final DeferredBlock<SculkBudBlock> SCULK_BUD = REGISTRY.register(
      "sculk_bud", () -> new SculkBudBlock(Properties.of().sound(SoundType.SCULK).mapColor(MapColor.COLOR_BLACK).strength(0.2F).sound(SoundType.SCULK))
   );
   public static final DeferredBlock<GustBasketBlock> GUST_BASKET = REGISTRY.register(
      "gust_basket", () -> new GustBasketBlock(Properties.of().randomTicks().sound(SoundType.METAL).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1.5F))
   );
   public static final DeferredBlock<FlatHopperBlock> FLAT_HOPPER = REGISTRY.register(
      "flat_hopper", () -> new FlatHopperBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.HOPPER))
   );
   public static final DeferredBlock<ChromachineBlock> CHROMACHINE = REGISTRY.register(
      "chromachine",
      () -> new ChromachineBlock(Properties.of().randomTicks().sound(SoundType.METAL).mapColor(MapColor.METAL).strength(1.5F).lightLevel(state -> 7))
   );
   public static final DeferredBlock<PhantomMembraneBlock> PHANTOM_MEMBRANE_BLOCK = REGISTRY.register(
      "phantom_membrane_block", () -> new PhantomMembraneBlock(Properties.of().noOcclusion().instabreak().sound(SoundType.SCULK))
   );
   public static final DeferredBlock<GlowingOrbBlock> GLOWING_ORB = REGISTRY.register(
      "glowing_orb",
      () -> new GlowingOrbBlock(
         Properties.of()
            .noCollission()
            .instabreak()
            .pushReaction(PushReaction.DESTROY)
            .replaceable()
            .sound(AlchemancySoundEvents.GLOWING_ORB)
            .lightLevel(state -> 15)
      )
   );
}
