package fuzs.puzzleslib.neoforge.mixin.accessor;

import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings.Builder;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Builder.class})
public interface MobSpawnSettingsBuilderNeoForgeAccessor {
   @Accessor("mobSpawnCosts")
   Map<EntityType<?>, MobSpawnCost> puzzleslib$getMobSpawnCosts();
}
