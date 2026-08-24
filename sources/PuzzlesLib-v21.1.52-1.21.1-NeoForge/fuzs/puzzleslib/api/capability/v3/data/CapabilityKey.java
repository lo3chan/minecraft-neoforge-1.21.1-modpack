package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CapabilityKey<T, C extends CapabilityComponent<T>> {
   ResourceLocation identifier();

   C get(@NotNull T var1);

   boolean isProvidedBy(@Nullable Object var1);

   default Optional<C> getIfProvided(@Nullable Object holder) {
      return this.isProvidedBy(holder) ? Optional.of(this.get((T)holder)) : Optional.empty();
   }

   void setChanged(C var1, @Nullable PlayerSet var2);

   ClientboundMessage<?> toPacket(C var1);
}
