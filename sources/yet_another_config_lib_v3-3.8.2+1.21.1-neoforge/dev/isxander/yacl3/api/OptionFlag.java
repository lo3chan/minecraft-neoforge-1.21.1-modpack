package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.RequireRestartScreen;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

@FunctionalInterface
public interface OptionFlag extends Consumer<Minecraft> {
   OptionFlag GAME_RESTART = client -> client.setScreen(new RequireRestartScreen(client.screen));
   OptionFlag RELOAD_CHUNKS = client -> client.levelRenderer.allChanged();
   OptionFlag WORLD_RENDER_UPDATE = client -> client.levelRenderer.needsUpdate();
   OptionFlag ASSET_RELOAD = Minecraft::delayTextureReload;
}
