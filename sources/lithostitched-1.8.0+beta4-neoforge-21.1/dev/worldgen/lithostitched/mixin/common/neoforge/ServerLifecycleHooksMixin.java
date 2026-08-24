package dev.worldgen.lithostitched.mixin.common.neoforge;

import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.worldgen.modifier.ModifierManager;
import dev.worldgen.lithostitched.impl.worldgen.modifier.NeoforgeModifierHolder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(
   value = {ServerLifecycleHooks.class},
   remap = false
)
public class ServerLifecycleHooksMixin {
   @ModifyVariable(
      method = {"runModifiers"},
      at = @At("STORE"),
      ordinal = 0
   )
   private static List<BiomeModifier> lithostitched$injectBiomeModifers(List<BiomeModifier> biomeModifiers, MinecraftServer server) {
      List<BiomeModifier> allBiomeModifiers = new ArrayList<>(biomeModifiers);
      Map<ResourceLocation, WorldgenModifier> modifiers = ModifierManager.getAllModifiers(server.registryAccess());
      List<Entry<ResourceLocation, NeoforgeModifierHolder>> lithostitchedBiomeModifiers = modifiers.entrySet()
         .stream()
         .filter(entry -> entry.getValue() instanceof NeoforgeModifierHolder)
         .map(entry -> Map.entry(entry.getKey(), (NeoforgeModifierHolder)entry.getValue()))
         .toList();
      lithostitchedBiomeModifiers.forEach(entry -> allBiomeModifiers.add(entry.getValue().createNeoforgeModifier()));
      return allBiomeModifiers;
   }
}
