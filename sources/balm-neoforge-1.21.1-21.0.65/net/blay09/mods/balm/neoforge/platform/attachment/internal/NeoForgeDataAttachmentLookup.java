package net.blay09.mods.balm.neoforge.platform.attachment.internal;

import net.blay09.mods.balm.platform.attachment.DataAttachmentLookup;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

public class NeoForgeDataAttachmentLookup<T> implements DataAttachmentLookup<T> {
   private final Holder<AttachmentType<T>> type;

   public NeoForgeDataAttachmentLookup(Holder<?> type) {
      this.type = (Holder<AttachmentType<T>>)type;
   }

   @Nullable
   @Override
   public T get(Player player) {
      return (T)player.getExistingData(this.type::value).orElse(null);
   }

   @Override
   public T getOrCreate(Player player) {
      return (T)player.getData(this.type::value);
   }

   @Override
   public boolean has(Player player) {
      return player.hasData(this.type::value);
   }

   @Nullable
   @Override
   public T remove(Player player) {
      return (T)player.removeData(this.type::value);
   }

   @Nullable
   @Override
   public T update(Player player, T value) {
      return (T)player.setData(this.type::value, value);
   }

   @Nullable
   @Override
   public T get(Level level) {
      return (T)level.getExistingData(this.type::value).orElse(null);
   }

   @Override
   public T getOrCreate(Level level) {
      return (T)level.getData(this.type::value);
   }

   @Override
   public boolean has(Level level) {
      return level.hasData(this.type::value);
   }

   @Nullable
   @Override
   public T remove(Level level) {
      return (T)level.removeData(this.type::value);
   }

   @Nullable
   @Override
   public T update(Level level, T value) {
      return (T)level.setData(this.type::value, value);
   }

   @Nullable
   @Override
   public T get(Entity entity) {
      return (T)entity.getExistingData(this.type::value).orElse(null);
   }

   @Override
   public T getOrCreate(Entity entity) {
      return (T)entity.getData(this.type::value);
   }

   @Override
   public boolean has(Entity entity) {
      return entity.hasData(this.type::value);
   }

   @Nullable
   @Override
   public T remove(Entity entity) {
      return (T)entity.removeData(this.type::value);
   }

   @Nullable
   @Override
   public T update(Entity entity, T value) {
      return (T)entity.setData(this.type::value, value);
   }

   @Nullable
   @Override
   public T get(BlockEntity blockEntity) {
      return (T)blockEntity.getExistingData(this.type::value).orElse(null);
   }

   @Override
   public T getOrCreate(BlockEntity blockEntity) {
      return (T)blockEntity.getData(this.type::value);
   }

   @Override
   public boolean has(BlockEntity blockEntity) {
      return blockEntity.hasData(this.type::value);
   }

   @Nullable
   @Override
   public T remove(BlockEntity blockEntity) {
      return (T)blockEntity.removeData(this.type::value);
   }

   @Nullable
   @Override
   public T update(BlockEntity blockEntity, T value) {
      return (T)blockEntity.setData(this.type::value, value);
   }

   @Nullable
   @Override
   public T get(ChunkAccess chunk) {
      return (T)chunk.getExistingData(this.type::value).orElse(null);
   }

   @Override
   public T getOrCreate(ChunkAccess chunk) {
      return (T)chunk.getData(this.type::value);
   }

   @Override
   public boolean has(ChunkAccess chunk) {
      return chunk.hasData(this.type::value);
   }

   @Nullable
   @Override
   public T remove(ChunkAccess chunk) {
      return (T)chunk.removeData(this.type::value);
   }

   @Nullable
   @Override
   public T update(ChunkAccess chunk, T value) {
      return (T)chunk.setData(this.type::value, value);
   }
}
