package dev.corgitaco.enhancedcelestials2core.api.lunarevent.modifier;

import com.mojang.serialization.MapCodec;

public interface LunarEventModifierType<T extends LunarEventModifier> {
   MapCodec<T> codec();
}
