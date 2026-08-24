package org.dimdev.limlib.client.specialmodels.mixin;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MultiPartBakedModel.class})
public interface MultiPartBakedModelAccessor {
   @Accessor("selectors")
   List<Pair<Predicate<BlockState>, BakedModel>> corners$getSelectors();
}
