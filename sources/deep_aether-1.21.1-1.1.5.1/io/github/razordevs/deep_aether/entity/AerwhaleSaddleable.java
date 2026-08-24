package io.github.razordevs.deep_aether.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundSource;

public interface AerwhaleSaddleable {
   boolean isSaddleable();

   void equipSaddle(@Nullable SoundSource var1);

   boolean isSaddled();
}
