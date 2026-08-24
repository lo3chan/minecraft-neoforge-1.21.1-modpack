package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.WayfindingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class WayfindingProperty extends Property implements IDataHolder<Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData>> {
   private static final Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> DEFAULT = new Tuple(
      WayfindingProperty.WayfindData.DEFAULT, WayfindingProperty.RotationData.DEFAULT
   );

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      if (super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd, consumeItem)) {
         if (consumeItem && propertySource.has(DataComponents.LODESTONE_TRACKER)) {
            LodestoneTracker tracker = (LodestoneTracker)propertySource.get(DataComponents.LODESTONE_TRACKER);
            if (tracker.target().isPresent()) {
               this.setData(stack, ((WayfindingProperty.WayfindData)this.getDefaultData().getA()).withBlockPosition((GlobalPos)tracker.target().get()));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
      if (!level.isClientSide()) {
         WayfindingProperty.WayfindData data = (WayfindingProperty.WayfindData)this.getData(stack).getA();
         if (data.fallbackPos.isEmpty() || !data.fallbackPos.get().dimension().location().equals(level.dimension().location())) {
            GlobalPos fallback;
            if ((data.targetedPos.isEmpty() || data.targetedPos.get().dimension().location().equals(user.level().dimension().location()))
               && user instanceof ServerPlayer player
               && level.dimension().location().equals(player.getRespawnDimension().location())) {
               if (player.getRespawnPosition() == null) {
                  return;
               }

               fallback = new GlobalPos(level.dimension(), player.getRespawnPosition());
            } else if (level.dimensionTypeRegistration().is(AlchemancyTags.Dimensions.WAYFINDING_POINTS_TO_ORIGIN)) {
               fallback = new GlobalPos(level.dimension(), BlockPos.ZERO);
            } else {
               fallback = new GlobalPos(level.dimension(), user.blockPosition());
            }

            this.setData(stack, data.withFallback(fallback));
         }
      }
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      WayfindingProperty.WayfindData data = (WayfindingProperty.WayfindData)this.getData(event.getItemStack()).getA();
      if (!data.hasTarget() && event.getTarget() instanceof Player target) {
         this.setData(event.getItemStack(), data.withPlayer(target));
         playWayfindingSound(target);
         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      if (event.getLevel().getBlockState(event.getPos()).is(AlchemancyTags.Blocks.WAYFINDING_TARGETABLE)) {
         WayfindingProperty.WayfindData data = (WayfindingProperty.WayfindData)this.getData(event.getItemStack()).getA();
         if (!data.hasTarget()) {
            this.setData(event.getItemStack(), data.withBlockPosition(new GlobalPos(event.getLevel().dimension(), event.getPos())));
            playWayfindingSound(event.getLevel(), event.getPos());
            event.setCancellationResult(ItemInteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      }
   }

   public static void playWayfindingSound(Entity source) {
      playWayfindingSound(source.level(), source.position());
   }

   public static void playWayfindingSound(Level level, BlockPos pos) {
      playWayfindingSound(level, pos.getCenter());
   }

   public static void playWayfindingSound(Level level, Vec3 pos) {
      level.playSound(null, pos.x, pos.y, pos.z, (SoundEvent)AlchemancySoundEvents.WAYFINDING.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
   }

   @SubscribeEvent
   public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {
      Inventory inventory = event.getEntity().getInventory();
      if (event.getNewSpawn() != null) {
         for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.WAYFINDING)) {
               WayfindingProperty.WayfindData data = (WayfindingProperty.WayfindData)((WayfindingProperty)AlchemancyProperties.WAYFINDING.get())
                  .getData(stack)
                  .getA();
               if (data.fallbackPos().isPresent() && data.fallbackPos().get().dimension().equals(event.getSpawnLevel())) {
                  ((WayfindingProperty)AlchemancyProperties.WAYFINDING.get())
                     .setData(stack, data.withFallback(new GlobalPos(event.getSpawnLevel(), event.getNewSpawn())));
               }
            }
         }
      }
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      WayfindingProperty.WayfindData data = (WayfindingProperty.WayfindData)this.getData(stack).getA();
      if (!data.hasTarget()) {
         return super.getDisplayText(stack);
      } else {
         Component target = Component.empty();
         if (data.targetedPlayer.isPresent()) {
            Optional<Player> targetPlayer = CommonUtils.getPlayerByUUID((UUID)data.targetedPlayer.get().getA());
            target = Component.literal(targetPlayer.isPresent() ? targetPlayer.get().getGameProfile().getName() : (String)data.targetedPlayer.get().getB());
         } else if (data.targetedPos.isPresent()) {
            GlobalPos pos = data.targetedPos.get();
            if ((ServerLifecycleHooks.getCurrentServer() == null || !(ServerLifecycleHooks.getCurrentServer() instanceof DedicatedServer))
               && !ClientUtil.getCurrentLevel().dimension().equals(pos.dimension())) {
               target = Component.literal(pos.dimension().location().toString());
            } else {
               target = Component.translatable("property.detail.block_position", new Object[]{pos.pos().getX(), pos.pos().getY(), pos.pos().getZ()});
            }
         }

         return Component.translatable("property.detail", new Object[]{super.getDisplayText(stack), target}).withColor(this.getColor(stack));
      }
   }

   public void setData(ItemStack item, WayfindingProperty.WayfindData value) {
      IDataHolder.super.setData(
         item,
         new Tuple(
            value,
            ServerLifecycleHooks.getCurrentServer() != null && ServerLifecycleHooks.getCurrentServer() instanceof DedicatedServer
               ? WayfindingProperty.RotationData.DEFAULT
               : (WayfindingProperty.RotationData)this.getData(item).getB()
         )
      );
   }

   public void setData(ItemStack item, WayfindingProperty.RotationData value) {
      IDataHolder.super.setData(item, new Tuple((WayfindingProperty.WayfindData)this.getData(item).getA(), value));
   }

   @Override
   public int getColor(ItemStack stack) {
      return 3701941;
   }

   public Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> readData(CompoundTag tag) {
      return new Tuple(WayfindingProperty.WayfindData.fromNbt(tag), WayfindingProperty.RotationData.fromNbt(tag.getCompound("rotation_data")));
   }

   public CompoundTag writeData(Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> data) {
      CompoundTag tag = ((WayfindingProperty.WayfindData)data.getA()).toNbt();
      tag.put("rotation_data", ((WayfindingProperty.RotationData)data.getB()).toNbt());
      return tag;
   }

   public Tuple<WayfindingProperty.WayfindData, WayfindingProperty.RotationData> getDefaultData() {
      return DEFAULT;
   }

   public record RotationData(float rotation, float previousRotaion, long lastUpdateTick) {
      public static final WayfindingProperty.RotationData DEFAULT = new WayfindingProperty.RotationData(0.0F, 0.0F, 0L);

      public WayfindingProperty.RotationData step(float rotation, long currentTick) {
         return new WayfindingProperty.RotationData(rotation, this.rotation, currentTick);
      }

      public static WayfindingProperty.RotationData fromNbt(CompoundTag tag) {
         return new WayfindingProperty.RotationData(tag.getFloat("rotation"), tag.getFloat("previous_rotation"), tag.getLong("last_update_tick"));
      }

      public CompoundTag toNbt() {
         return new CompoundTag() {
            {
               this.putFloat("rotation", RotationData.this.rotation);
               this.putFloat("previous_rotation", RotationData.this.previousRotaion);
               this.putLong("last_update_tick", RotationData.this.lastUpdateTick);
            }
         };
      }

      public boolean shouldUpdate(long gameTime) {
         return this.lastUpdateTick != gameTime;
      }
   }

   public record WayfindData(Optional<Tuple<UUID, String>> targetedPlayer, Optional<GlobalPos> targetedPos, Optional<GlobalPos> fallbackPos) {
      public static final WayfindingProperty.WayfindData DEFAULT = new WayfindingProperty.WayfindData(Optional.empty(), Optional.empty(), Optional.empty());

      public static WayfindingProperty.WayfindData fromNbt(CompoundTag tag) {
         Optional<GlobalPos> targetedPos = Optional.empty();
         Optional<GlobalPos> fallbackPos = Optional.empty();
         if (tag.contains("target_position", 10)) {
            CompoundTag targetTag = tag.getCompound("target_position");
            targetedPos = Optional.of(
               new GlobalPos(
                  ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetTag.getString("dimension"))),
                  NbtUtils.readBlockPos(targetTag, "pos").orElse(BlockPos.ZERO)
               )
            );
         }

         if (tag.contains("fallback_position", 10)) {
            CompoundTag targetTag = tag.getCompound("fallback_position");
            fallbackPos = Optional.of(
               new GlobalPos(
                  ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(targetTag.getString("dimension"))),
                  NbtUtils.readBlockPos(targetTag, "pos").orElse(BlockPos.ZERO)
               )
            );
         }

         return new WayfindingProperty.WayfindData(
            Optional.ofNullable(
               tag.hasUUID("target_player")
                  ? new Tuple(tag.getUUID("target_player"), tag.contains("target_player_name", 8) ? tag.getString("target_player_name") : "???")
                  : null
            ),
            targetedPos,
            fallbackPos
         );
      }

      public WayfindingProperty.WayfindData withPlayer(Player targetedPlayer) {
         return new WayfindingProperty.WayfindData(
            Optional.of(new Tuple(targetedPlayer.getUUID(), targetedPlayer.getGameProfile().getName())), Optional.empty(), this.fallbackPos
         );
      }

      public WayfindingProperty.WayfindData withBlockPosition(GlobalPos targetedPos) {
         return new WayfindingProperty.WayfindData(Optional.empty(), Optional.of(targetedPos), this.fallbackPos);
      }

      public WayfindingProperty.WayfindData withFallback(GlobalPos fallbackPos) {
         return new WayfindingProperty.WayfindData(this.targetedPlayer, this.targetedPos, Optional.ofNullable(fallbackPos));
      }

      public float getRotation(Entity user) {
         Level level = user.level();
         Optional<BlockPos> target = this.getTargetPos(level);
         float result;
         if (this.targetedPlayer.isPresent()
            && (target.isEmpty() || user instanceof Player player && player.getUUID().equals(this.targetedPlayer.get().getA()))) {
            result = WayfindingUtil.getRandomlySpinningRotation(0, level.getGameTime());
         } else if (target.isPresent()) {
            result = WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), target.get());
         } else {
            if (!this.fallbackPos.isPresent() || !this.fallbackPos.get().dimension().location().equals(user.level().dimension().location())) {
               GlobalPos worldSpawn = CompassItem.getSpawnPosition(level);
               return worldSpawn != null
                  ? WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), worldSpawn.pos())
                  : WayfindingUtil.getRandomlySpinningRotation(0, level.getGameTime());
            }

            result = WayfindingUtil.getRotationTowardsCompassTarget(user, level.getGameTime(), this.fallbackPos.get().pos());
         }

         return result;
      }

      public Optional<BlockPos> getTargetPos(Level level) {
         if (this.targetedPlayer.isPresent()) {
            Player target = level.getPlayerByUUID((UUID)this.targetedPlayer.get().getA());
            if (target != null) {
               return Optional.of(target.blockPosition());
            }
         }

         return this.targetedPos.isPresent() && this.targetedPos.get().dimension().location().equals(level.dimension().location())
            ? Optional.of(this.targetedPos.get().pos())
            : Optional.empty();
      }

      public boolean hasTarget() {
         return this.targetedPos.isPresent() || this.targetedPlayer.isPresent();
      }

      public CompoundTag toNbt() {
         return new CompoundTag() {
            {
               WayfindData.this.targetedPlayer.ifPresent(player -> {
                  this.putUUID("target_player", (UUID)player.getA());
                  this.putString("target_player_name", (String)player.getB());
               });
               if (WayfindData.this.targetedPos.isPresent()) {
                  CompoundTag targetTag = new CompoundTag();
                  targetTag.putString("dimension", WayfindData.this.targetedPos.get().dimension().location().toString());
                  targetTag.put("pos", NbtUtils.writeBlockPos(WayfindData.this.targetedPos.get().pos()));
                  this.put("target_position", targetTag);
               }

               if (WayfindData.this.fallbackPos.isPresent()) {
                  CompoundTag targetTag = new CompoundTag();
                  targetTag.putString("dimension", WayfindData.this.fallbackPos.get().dimension().location().toString());
                  targetTag.put("pos", NbtUtils.writeBlockPos(WayfindData.this.fallbackPos.get().pos()));
                  this.put("fallback_position", targetTag);
               }
            }
         };
      }

      public Optional<ResourceKey<Level>> getTargetDimension(Level level) {
         if (this.targetedPlayer.isPresent()) {
            Player target = level.getPlayerByUUID((UUID)this.targetedPlayer.get().getA());
            if (target != null) {
               return Optional.of(target.level().dimension());
            }
         }

         return this.targetedPos.isPresent() ? Optional.of(this.targetedPos.get().dimension()) : Optional.empty();
      }
   }
}
