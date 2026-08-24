package net.blay09.mods.balm.api.provider;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class ProviderUtils {
   @Deprecated
   @Nullable
   public static <T> T getProvider(BlockEntity blockEntity, Class<T> clazz) {
      return Balm.getProviders().getProvider(blockEntity, clazz);
   }
}
