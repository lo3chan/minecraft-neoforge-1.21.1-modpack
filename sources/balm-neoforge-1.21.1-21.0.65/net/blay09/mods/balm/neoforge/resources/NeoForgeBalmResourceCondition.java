package net.blay09.mods.balm.neoforge.resources;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.blay09.mods.balm.api.resources.BalmResourceCondition;
import net.blay09.mods.balm.common.resources.ResourceConditionContextImpl;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;

public record NeoForgeBalmResourceCondition<T extends BalmResourceCondition>(
   ResourceLocation identifier, T delegate, Function<ResourceLocation, MapCodec<? extends ICondition>> codecResolver
) implements ICondition {
   public boolean test(IContext context) {
      return this.delegate.test(new ResourceConditionContextImpl(context));
   }

   public MapCodec<? extends ICondition> codec() {
      return this.codecResolver.apply(this.identifier);
   }
}
