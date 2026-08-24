package dev.kosmx.playerAnim.minecraftApi.codec;

import dev.kosmx.playerAnim.api.IPlayable;
import java.io.IOException;
import java.io.OutputStream;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface AnimationEncoder<T extends IPlayable> {
   void encode(@NotNull OutputStream var1, @NotNull ResourceLocation var2, @NotNull T var3) throws IOException;
}
