package dev.corgitaco.enhancedcelestials2core.api.lunarevent;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;

@FunctionalInterface
public interface LunarEventChangeListener {
   void onLunarEventChanged(ServerLevel var1, Holder<LunarEvent> var2, Holder<LunarEvent> var3);
}
