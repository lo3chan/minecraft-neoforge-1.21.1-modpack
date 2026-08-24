package net.joefoxe.hexerei.block.connected;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConnectedTextureTransparentLayer {
   @Nullable
   CTSpriteShiftEntry getTransparentShift(BlockState var1, Direction var2, @NotNull TextureAtlasSprite var3);
}
