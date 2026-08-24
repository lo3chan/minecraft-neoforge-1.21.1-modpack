package com.iafenvoy.origins.registry;

import com.iafenvoy.origins.content.TemporaryCobwebBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public final class OriginsBlocks {
   public static final Blocks REGISTRY = DeferredRegister.createBlocks("origins");
   public static final DeferredBlock<TemporaryCobwebBlock> TEMPORARY_COBWEB = REGISTRY.register("temporary_cobweb", TemporaryCobwebBlock::new);
}
