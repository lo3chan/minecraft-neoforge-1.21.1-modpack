package com.iafenvoy.origins.mixin.loot;

import com.iafenvoy.origins.accessor.KeyableLootTable;
import com.iafenvoy.origins.data.power.builtin.regular.ReplaceLootTablePower;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries.Holder;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Holder.class})
public abstract class ReloadableServerRegistries$HolderMixin {
   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void setupLootTables(Frozen registryManager, CallbackInfo ci) {
      registryManager.registryOrThrow(Registries.LOOT_TABLE).holders().forEach(reference -> {
         ResourceKey<LootTable> key = reference.getKey();
         if (reference.value() instanceof KeyableLootTable keyable) {
            keyable.origins$setup(key, (Holder)this);
         }
      });
   }

   @ModifyReturnValue(
      method = {"getLootTable"},
      at = {@At("RETURN")}
   )
   private LootTable getReplacedOrNormalTable(LootTable original, ResourceKey<LootTable> key) {
      return key.equals(ReplaceLootTablePower.REPLACED_TABLE_KEY) ? ReplaceLootTablePower.peek() : original;
   }
}
