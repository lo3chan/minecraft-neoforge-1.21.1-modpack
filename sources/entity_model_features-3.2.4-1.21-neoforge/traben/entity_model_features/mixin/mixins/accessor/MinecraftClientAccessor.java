package traben.entity_model_features.mixin.mixins.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Minecraft.class})
public interface MinecraftClientAccessor {
   @Accessor("timer")
   Timer getTimer();
}
