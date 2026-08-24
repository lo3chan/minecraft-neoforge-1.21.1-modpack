package com.github.alexthe666.alexsmobs.misc;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidType;

public class AMPlatform {
   public static final Holder<GameEvent> ENTITY_ACTION = GameEvent.ENTITY_ACTION;
   private static final MapCodec<?> UNSUPPORTED_BLOCK_CODEC = MapCodec.unit(() -> {
      throw new UnsupportedOperationException("Alex's Mobs blocks are not codec-serializable");
   });

   public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity, ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(entity, serverEntity);
   }

   public static boolean postCancelled(Event event) {
      return NeoForge.EVENT_BUS.post(event) instanceof ICancellableEvent cancellable && cancellable.isCanceled();
   }

   public static double myRidingOffset(Entity passenger, Entity vehicle) {
      return -passenger.getVehicleAttachmentPoint(vehicle).y;
   }

   public static Block coloredSand(int rgb, Properties props) {
      return new ColoredFallingBlock(new ColorRGBA(rgb), props);
   }

   public static <B extends Block> MapCodec<B> unsupportedBlockCodec() {
      return (MapCodec<B>)UNSUPPORTED_BLOCK_CODEC;
   }

   public static FluidType waterType() {
      return (FluidType)NeoForgeMod.WATER_TYPE.value();
   }

   public static double fluidHeightWater(Entity entity) {
      return entity.getFluidTypeHeight(waterType());
   }

   public static double fluidHeightLava(Entity entity) {
      return entity.getFluidTypeHeight(lavaType());
   }

   public static boolean isInAnyFluid(Entity entity) {
      return entity.isInFluidType();
   }

   public static FluidType lavaType() {
      return (FluidType)NeoForgeMod.LAVA_TYPE.value();
   }

   public static Holder<Attribute> swimSpeed() {
      return NeoForgeMod.SWIM_SPEED;
   }

   public static Holder<Attribute> blockReach() {
      return Attributes.BLOCK_INTERACTION_RANGE;
   }

   public static Holder<Attribute> entityReach() {
      return Attributes.ENTITY_INTERACTION_RANGE;
   }

   public static boolean mobGriefing(Level level, Entity entity) {
      return EventHooks.canEntityGrief(level, entity);
   }

   public static double attackReachSqr(Mob mob, LivingEntity target) {
      return mob.getBbWidth() * 2.0F * mob.getBbWidth() * 2.0F + target.getBbWidth();
   }

   public static Properties copyProperties(BlockBehaviour from) {
      return Properties.ofFullCopy(from);
   }

   public static <T> Predicate<T> orConditions(Predicate<T>[] conditions) {
      return Util.anyOf(List.of(conditions));
   }

   public static ItemStack pickupBlock(BucketPickup block, LevelAccessor level, BlockPos pos, BlockState state) {
      return block.pickupBlock(null, level, pos, state);
   }

   public static LootTable lootTableById(MinecraftServer server, ResourceLocation id) {
      return server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
   }

   public static AABB encapsulating(BlockPos a, BlockPos b) {
      return AABB.encapsulatingFullBlocks(a, b);
   }
}
