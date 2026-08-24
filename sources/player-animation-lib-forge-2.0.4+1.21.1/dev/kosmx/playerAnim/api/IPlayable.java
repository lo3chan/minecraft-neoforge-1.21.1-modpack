package dev.kosmx.playerAnim.api;

import dev.kosmx.playerAnim.api.layered.IActualAnimation;
import java.util.UUID;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public interface IPlayable extends Supplier<UUID> {
   @NotNull
   IActualAnimation<?> playAnimation();

   @NotNull
   String getName();
}
