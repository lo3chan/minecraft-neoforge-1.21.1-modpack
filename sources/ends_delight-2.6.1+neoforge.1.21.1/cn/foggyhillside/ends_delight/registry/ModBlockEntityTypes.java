package cn.foggyhillside.ends_delight.registry;

import cn.foggyhillside.ends_delight.block.entity.EndStoveBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntityTypes {
   public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "ends_delight");
   public static final Supplier<BlockEntityType<EndStoveBlockEntity>> END_STOVE = TILES.register(
      "end_stove", () -> Builder.of(EndStoveBlockEntity::new, new Block[]{(Block)ModBlocks.END_STOVE.get()}).build(null)
   );
}
