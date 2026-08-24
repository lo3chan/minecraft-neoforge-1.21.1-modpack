package net.blay09.mods.balm.api.provider;

import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.Direction;

@Deprecated
public interface BalmProviderHolder {
   @Deprecated
   default List<BalmProvider<?>> getProviders() {
      return Collections.emptyList();
   }

   @Deprecated
   default List<Pair<Direction, BalmProvider<?>>> getSidedProviders() {
      return Collections.emptyList();
   }
}
