package me.flashyreese.mods.sodiumextra.mixin.panini_projection;

import java.util.List;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PostChain.class})
public interface AccessorPostChain {
   @Accessor("passes")
   List<PostPass> sodiumExtra$getPasses();
}
