package net.blay09.mods.balm.platform.attachment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

public interface DataAttachmentLookup<T> {
   @Nullable
   T get(Player var1);

   T getOrCreate(Player var1);

   boolean has(Player var1);

   @Nullable
   T remove(Player var1);

   @Nullable
   T update(Player var1, T var2);

   @Nullable
   T get(Level var1);

   T getOrCreate(Level var1);

   boolean has(Level var1);

   @Nullable
   T remove(Level var1);

   @Nullable
   T update(Level var1, T var2);

   @Nullable
   T get(Entity var1);

   T getOrCreate(Entity var1);

   boolean has(Entity var1);

   @Nullable
   T remove(Entity var1);

   @Nullable
   T update(Entity var1, T var2);

   @Nullable
   T get(BlockEntity var1);

   T getOrCreate(BlockEntity var1);

   boolean has(BlockEntity var1);

   @Nullable
   T remove(BlockEntity var1);

   @Nullable
   T update(BlockEntity var1, T var2);

   @Nullable
   T get(ChunkAccess var1);

   T getOrCreate(ChunkAccess var1);

   boolean has(ChunkAccess var1);

   @Nullable
   T remove(ChunkAccess var1);

   @Nullable
   T update(ChunkAccess var1, T var2);
}
