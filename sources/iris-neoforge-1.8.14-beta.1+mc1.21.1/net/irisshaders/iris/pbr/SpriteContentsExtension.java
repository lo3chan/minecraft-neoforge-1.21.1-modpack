package net.irisshaders.iris.pbr;

import net.minecraft.client.renderer.texture.SpriteContents.Ticker;
import org.jetbrains.annotations.Nullable;

public interface SpriteContentsExtension {
   @Nullable
   Ticker getCreatedTicker();
}
