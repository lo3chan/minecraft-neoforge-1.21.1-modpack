package dev.corgitaco.enhancedcelestials2core.lunarevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.corgitaco.enhancedcelestials2core.api.EnhancedCelestialsRegistry;
import dev.corgitaco.enhancedcelestials2core.api.lunarevent.LunarEvent;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFixedCodec;

public record LunarEventInstance(Holder<LunarEvent> lunarEvent, long startTime, long endTime, boolean setByCommand) {
   public static final Codec<LunarEventInstance> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            RegistryFixedCodec.create(EnhancedCelestialsRegistry.LUNAR_EVENT_KEY).fieldOf("lunar_event").forGetter(LunarEventInstance::lunarEvent),
            Codec.LONG.fieldOf("start_time").forGetter(LunarEventInstance::startTime),
            Codec.LONG.fieldOf("end_time").forGetter(LunarEventInstance::endTime),
            Codec.BOOL.optionalFieldOf("set_by_command", false).forGetter(LunarEventInstance::setByCommand)
         )
         .apply(builder, LunarEventInstance::new)
   );

   public boolean isActive(long currentTime) {
      return currentTime >= this.startTime && currentTime <= this.endTime;
   }

   public float getProgress(long currentTime) {
      return !this.isActive(currentTime) ? 0.0F : (float)(currentTime - this.startTime) / (float)(this.endTime - this.startTime);
   }

   public float getFadeInProgress(long fadeTicks, long currentTime) {
      if (!this.isActive(currentTime)) {
         throw new IllegalArgumentException("Current time is not within the active period of the lunar event.");
      } else {
         long fadeLength = Math.min(fadeTicks, this.endTime - this.startTime);
         return fadeLength <= 0L ? 1.0F : Math.min(1.0F, (float)(currentTime - this.startTime) / (float)fadeLength);
      }
   }

   public float getFadeOutProgress(long fadeTicks, long currentTime) {
      if (!this.isActive(currentTime)) {
         throw new IllegalArgumentException("Current time is not within the active period of the lunar event.");
      } else {
         long fadeLength = Math.min(fadeTicks, this.endTime - this.startTime);
         return fadeLength <= 0L ? 1.0F : Math.min(1.0F, (float)(this.endTime - currentTime) / (float)fadeLength);
      }
   }

   public float getPreStartFadeProgress(long fadeTicks, long currentTime) {
      if (currentTime <= this.startTime - fadeTicks) {
         return 0.0F;
      } else {
         return currentTime >= this.startTime ? 1.0F : (float)(currentTime - (this.startTime - fadeTicks)) / (float)fadeTicks;
      }
   }

   public float getPostEndFadeProgress(long fadeTicks, long currentTime) {
      if (currentTime <= this.endTime) {
         return 1.0F;
      } else {
         return currentTime >= this.endTime + fadeTicks ? 0.0F : 1.0F - (float)(currentTime - this.endTime) / (float)fadeTicks;
      }
   }
}
