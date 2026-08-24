package org.dimdev.limlib.client.specialmodels.mixin;

import java.util.List;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.random.WeightedEntry.Wrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({WeightedBakedModel.class})
public interface WeightedBakedModelAccessor {
   @Accessor("list")
   List<Wrapper<BakedModel>> corners$getList();

   @Accessor("totalWeight")
   int corners$getTotalWeight();
}
