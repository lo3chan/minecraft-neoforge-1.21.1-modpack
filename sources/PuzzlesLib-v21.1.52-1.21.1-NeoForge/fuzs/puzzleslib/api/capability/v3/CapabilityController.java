package fuzs.puzzleslib.api.capability.v3;

import fuzs.puzzleslib.api.capability.v3.data.BlockEntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.LevelCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.LevelChunkCapabilityKey;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import fuzs.puzzleslib.impl.core.ModContext;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

public interface CapabilityController {
   static CapabilityController from(String modId) {
      return ModContext.get(modId).getCapabilityController();
   }

   @NotNull
   static CapabilityKey<?, ?> get(ResourceLocation identifier) {
      return GlobalCapabilityRegister.get(identifier);
   }

   <T extends Entity, C extends CapabilityComponent<T>> EntityCapabilityKey.Mutable<T, C> registerEntityCapability(
      String var1, Class<C> var2, Supplier<C> var3, Class<T> var4
   );

   <T extends BlockEntity, C extends CapabilityComponent<T>> BlockEntityCapabilityKey<T, C> registerBlockEntityCapability(
      String var1, Class<C> var2, Supplier<C> var3, Class<T> var4
   );

   <C extends CapabilityComponent<LevelChunk>> LevelChunkCapabilityKey<C> registerLevelChunkCapability(String var1, Class<C> var2, Supplier<C> var3);

   <C extends CapabilityComponent<Level>> LevelCapabilityKey<C> registerLevelCapability(String var1, Class<C> var2, Supplier<C> var3);
}
