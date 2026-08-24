package jeresources.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public interface IDungeonRegistry {
   void registerCategory(@NotNull String var1, @NotNull String var2);

   void registerChest(@NotNull String var1, @NotNull ResourceKey<LootTable> var2);

   void registerChest(@NotNull String var1, @NotNull LootTable var2);
}
