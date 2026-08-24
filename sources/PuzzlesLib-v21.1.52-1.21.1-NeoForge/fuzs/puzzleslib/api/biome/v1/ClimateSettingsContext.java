package fuzs.puzzleslib.api.biome.v1;

import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import org.jetbrains.annotations.NotNull;

public interface ClimateSettingsContext {
   void hasPrecipitation(boolean var1);

   boolean hasPrecipitation();

   void setTemperature(float var1);

   float getTemperature();

   void setTemperatureModifier(@NotNull TemperatureModifier var1);

   void setDownfall(float var1);
}
