package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.blocks.blockentities.AlchemancyCatalystBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.EssenceExtractorBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.EssenceInjectorBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.blocks.blockentities.SculkBudBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlchemancyBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "alchemancy");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EssenceExtractorBlockEntity>> ESSENCE_EXTRACTOR = REGISTRY.register(
      "essence_extractor", () -> Builder.of(EssenceExtractorBlockEntity::new, new Block[0]).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EssenceInjectorBlockEntity>> ESSENCE_INJECTOR = REGISTRY.register(
      "essence_injector", () -> Builder.of(EssenceInjectorBlockEntity::new, new Block[0]).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemStackHolderBlockEntity>> ITEMSTACK_HOLDER = REGISTRY.register(
      "pedestal",
      () -> Builder.of(
            ItemStackHolderBlockEntity::new,
            new Block[]{
               (Block)AlchemancyBlocks.INFUSION_PEDESTAL.get(), (Block)AlchemancyBlocks.ALCHEMANCY_FORGE.get(), (Block)AlchemancyBlocks.FLATTENED_ITEM.get()
            }
         )
         .build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemancyCatalystBlockEntity>> ALCHEMANCY_CATALYST = REGISTRY.register(
      "alchemancy_catalyst", () -> Builder.of(AlchemancyCatalystBlockEntity::new, new Block[]{(Block)AlchemancyBlocks.ALCHEMANCY_CATALYST.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RootedItemBlockEntity>> ROOTED_ITEM = REGISTRY.register(
      "rooted_item", () -> Builder.of(RootedItemBlockEntity::new, new Block[]{(Block)AlchemancyBlocks.ROOTED_ITEM.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkBudBlockEntity>> SCULK_BUD = REGISTRY.register(
      "sculk_bud", () -> Builder.of(SculkBudBlockEntity::new, new Block[]{(Block)AlchemancyBlocks.SCULK_BUD.get()}).build(null)
   );
}
