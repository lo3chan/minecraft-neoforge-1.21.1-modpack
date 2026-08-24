package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.core.BlockProviderKJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.BlockWrapper;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface LevelBlock extends BlockProviderKJS {
   Level getLevel();

   BlockPos getPos();

   @Override
   default Block kjs$getBlock() {
      return this.getBlockState().getBlock();
   }

   @HideFromJS
   default LevelBlock cache(BlockState state) {
      return this;
   }

   @HideFromJS
   default LevelBlock cache(BlockEntity entity) {
      return this;
   }

   default ResourceKey<Level> getDimensionKey() {
      return this.getLevel().dimension();
   }

   default ResourceLocation getDimension() {
      return this.getDimensionKey().location();
   }

   default int getX() {
      return this.getPos().getX();
   }

   default int getY() {
      return this.getPos().getY();
   }

   default int getZ() {
      return this.getPos().getZ();
   }

   default double getCenterX() {
      return this.getX() + 0.5;
   }

   default double getCenterY() {
      return this.getY() + 0.5;
   }

   default double getCenterZ() {
      return this.getZ() + 0.5;
   }

   default LevelBlock offset(Direction f, int d) {
      return this.getLevel().kjs$getBlock(this.getPos().relative(f, d));
   }

   default LevelBlock offset(Direction f) {
      return this.offset(f, 1);
   }

   default LevelBlock offset(int x, int y, int z) {
      return this.getLevel().kjs$getBlock(this.getPos().offset(x, y, z));
   }

   default LevelBlock getDown() {
      return this.offset(Direction.DOWN);
   }

   default LevelBlock getUp() {
      return this.offset(Direction.UP);
   }

   default LevelBlock getNorth() {
      return this.offset(Direction.NORTH);
   }

   default LevelBlock getSouth() {
      return this.offset(Direction.SOUTH);
   }

   default LevelBlock getWest() {
      return this.offset(Direction.WEST);
   }

   default LevelBlock getEast() {
      return this.offset(Direction.EAST);
   }

   default BlockState getBlockState() {
      return this.getLevel().getBlockState(this.getPos());
   }

   default void setBlockState(BlockState state, int flags) {
      this.getLevel().setBlock(this.getPos(), state, flags);
   }

   default void setBlockState(BlockState state) {
      this.setBlockState(state, 3);
   }

   default void set(Block block, Map<?, ?> properties, int flags) {
      BlockState state = block.defaultBlockState();
      if (!properties.isEmpty() && state.getBlock() != Blocks.AIR) {
         state = BlockWrapper.withProperties(state, properties);
         HashMap<String, Property<?>> pmap = new HashMap<>();

         for (Property<?> property : state.getProperties()) {
            pmap.put(property.getName(), property);
         }

         for (Entry<?, ?> entry : properties.entrySet()) {
            Property<? extends Comparable<?>> property = (Property<? extends Comparable<?>>)pmap.get(String.valueOf(entry.getKey()));
            if (property != null) {
               state = (BlockState)state.setValue(property, Cast.to(property.getValue(String.valueOf(entry.getValue())).orElseThrow()));
            }
         }
      }

      this.setBlockState(state, flags);
   }

   default void set(Block block, Map<?, ?> properties) {
      this.set(block, properties, 3);
   }

   default void set(Block block) {
      this.set(block, Collections.emptyMap());
   }

   default Map<String, String> getProperties() {
      Map<String, String> map = new HashMap<>();
      BlockState state = this.getBlockState();

      for (Property property : state.getProperties()) {
         map.put(property.getName(), property.getName(state.getValue(property)));
      }

      return map;
   }

   @Nullable
   default BlockEntity getEntity() {
      return this.getLevel().getBlockEntity(this.getPos());
   }

   default String getEntityId() {
      BlockEntity entity = this.getEntity();
      return entity == null ? "minecraft:air" : BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(entity.getType()).toString();
   }

   @Nullable
   default CompoundTag getEntityData() {
      BlockEntity entity = this.getEntity();
      return entity != null ? entity.saveWithoutMetadata(this.getLevel().registryAccess()) : null;
   }

   default void setEntityData(@Nullable CompoundTag tag) {
      if (tag != null) {
         BlockEntity entity = this.getEntity();
         if (entity != null) {
            entity.loadWithComponents(tag, this.getLevel().registryAccess());
         }
      }
   }

   default void mergeEntityData(@Nullable CompoundTag tag) {
      CompoundTag t = this.getEntityData();
      if (t == null) {
         this.setEntityData(tag);
      } else if (tag != null && !tag.isEmpty()) {
         for (String s : tag.getAllKeys()) {
            t.put(s, tag.get(s));
         }
      }

      this.setEntityData(t);
   }

   default int getLight() {
      return this.getLevel().getMaxLocalRawBrightness(this.getPos());
   }

   default int getSkyLight() {
      return this.getLevel().getBrightness(LightLayer.SKY, this.getPos()) - this.getLevel().getSkyDarken();
   }

   default int getBlockLight() {
      return this.getLevel().getBrightness(LightLayer.BLOCK, this.getPos());
   }

   default boolean getCanSeeSky() {
      return this.getLevel().canSeeSky(this.getPos());
   }

   default boolean canSeeSkyFromBelowWater() {
      return this.getLevel().canSeeSkyFromBelowWater(this.getPos());
   }

   default Explosion explode(ExplosionProperties properties) {
      return this.getLevel().kjs$explode(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5, properties);
   }

   @Nullable
   default Entity createEntity(EntityType<?> type) {
      Entity entity = this.getLevel().kjs$createEntity(type);
      if (entity != null) {
         entity.kjs$setPosition(this);
      }

      return entity;
   }

   default void spawnLightning(boolean effectOnly, @Nullable ServerPlayer player) {
      this.getLevel().kjs$spawnLightning(this.getCenterX(), this.getCenterY(), this.getCenterZ(), effectOnly, player);
   }

   default void spawnLightning(boolean effectOnly) {
      this.spawnLightning(effectOnly, null);
   }

   default void spawnLightning() {
      this.spawnLightning(false);
   }

   default void spawnFireworks(Fireworks fireworks, int lifetime) {
      this.getLevel().kjs$spawnFireworks(this.getCenterX(), this.getCenterY(), this.getCenterZ(), fireworks, lifetime);
   }

   @Nullable
   default InventoryKJS getInventory() {
      return this.getInventory(Direction.UP);
   }

   @Nullable
   default InventoryKJS getInventory(Direction facing) {
      BlockEntity entity = this.getEntity();
      if (entity != null) {
         IItemHandler c = (IItemHandler)this.getLevel().getCapability(ItemHandler.BLOCK, this.getPos(), this.getBlockState(), this.getEntity(), facing);
         if (c instanceof InventoryKJS inv) {
            return inv;
         }

         if (entity instanceof InventoryKJS inv) {
            return inv;
         }
      }

      return null;
   }

   default ItemStack getItem() {
      BlockState state = this.getBlockState();
      return state.getBlock().getCloneItemStack(this.getLevel(), this.getPos(), state);
   }

   default List<ItemStack> getDrops() {
      return this.getDrops(null, ItemStack.EMPTY);
   }

   default List<ItemStack> getDrops(@Nullable Entity entity, ItemStack heldItem) {
      return this.getLevel() instanceof ServerLevel s ? Block.getDrops(this.getBlockState(), s, this.getPos(), this.getEntity(), entity, heldItem) : List.of();
   }

   default void popItem(ItemStack item) {
      Block.popResource(this.getLevel(), this.getPos(), item);
   }

   default void popItemFromFace(ItemStack item, Direction dir) {
      Block.popResourceFromFace(this.getLevel(), this.getPos(), dir, item);
   }

   default EntityArrayList getPlayersInRadius(double radius) {
      EntityArrayList list = new EntityArrayList(1);
      double cx = this.getCenterX();
      double cy = this.getCenterY();
      double cz = this.getCenterZ();

      for (Entity entity : this.getLevel()
         .getEntities(
            (Entity)null,
            new AABB(cx - 0.5 - radius, cy - 0.5 - radius, cz - 0.5 - radius, cx + 0.5 + radius, cy + 0.5 + radius, cz + 0.5 + radius),
            EntityArrayList.ALWAYS_TRUE_PREDICATE
         )) {
         if (entity.distanceToSqr(cx, cy, cz) <= radius * radius && entity instanceof Player p && !p.isFakePlayer()) {
            list.add(p);
         }
      }

      return list;
   }

   default EntityArrayList getPlayersInRadius() {
      return this.getPlayersInRadius(8.0);
   }

   default ResourceLocation getBiomeId() {
      ResourceKey<Biome> k = this.getLevel().getBiome(this.getPos()).getKey();
      return k == null ? Biomes.PLAINS.location() : k.location();
   }

   default String toBlockStateString() {
      String id = this.kjs$getId();
      Map<String, String> properties = this.getProperties();
      if (properties.isEmpty()) {
         return id;
      } else {
         StringBuilder builder = new StringBuilder(id);
         builder.append('[');
         boolean first = true;

         for (Entry<String, String> entry : properties.entrySet()) {
            if (first) {
               first = false;
            } else {
               builder.append(',');
            }

            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
         }

         builder.append(']');
         return builder.toString();
      }
   }
}
