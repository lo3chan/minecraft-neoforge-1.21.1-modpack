package dev.latvian.mods.kubejs.client.highlight;

import dev.latvian.mods.kubejs.component.DataComponentWrapper;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.net.WebServerUpdateNBTPayload;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.OrderedCompoundTag;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;

public class KubedexPayloadHandler {
   private static ListTag sortedTagList(Stream<? extends TagKey<?>> stream) {
      return stream.map(TagKey::location)
         .sorted(ResourceLocation::compareNamespaced)
         .map(ResourceLocation::toString)
         .map(StringTag::valueOf)
         .collect(ListTag::new, AbstractList::add, AbstractCollection::addAll);
   }

   private static CompoundTag flags(int flags) {
      OrderedCompoundTag tag = new OrderedCompoundTag();
      tag.putBoolean("shift", (flags & 1) != 0);
      tag.putBoolean("ctrl", (flags & 2) != 0);
      tag.putBoolean("alt", (flags & 4) != 0);
      return tag;
   }

   public static void block(ServerPlayer player, BlockPos pos, int flags) {
      Frozen registries = player.server.registryAccess();
      BlockState blockState = player.level().getBlockState(pos);
      if (!blockState.isAir()) {
         OrderedCompoundTag payload = new OrderedCompoundTag();
         payload.put("flags", flags(flags));
         OrderedCompoundTag payloadBlock = new OrderedCompoundTag();
         payloadBlock.putString("id", blockState.getBlock().kjs$getId());
         payloadBlock.putString("dimension", player.level().dimension().location().toString());
         OrderedCompoundTag jpos = new OrderedCompoundTag();
         payloadBlock.put("pos", jpos);
         jpos.putInt("x", pos.getX());
         jpos.putInt("y", pos.getY());
         jpos.putInt("z", pos.getZ());
         CompoundTag p = new CompoundTag();
         payloadBlock.put("properties", p);

         for (Property<?> pk : blockState.getBlock().getStateDefinition().getProperties()) {
            p.putString(pk.getName(), pk.getName(Cast.to(blockState.getValue(pk))));
         }

         BlockEntity blockEntity = player.level().getBlockEntity(pos);
         if (blockEntity != null) {
            CompoundTag ejson = new CompoundTag();
            payloadBlock.put("block_entity", ejson);
            ejson.putString("id", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString());

            try {
               ejson.put(
                  "components",
                  (Tag)DataComponentMap.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), blockEntity.components()).result().get()
               );
            } catch (Exception var13) {
               ejson.put("components", new CompoundTag());
            }

            try {
               ejson.put("data", blockEntity.saveCustomOnly(registries));
            } catch (Exception var12) {
               ejson.put("data", new CompoundTag());
            }
         }

         payload.put("block", payloadBlock);
         KubeJSNet.safeSendToPlayer(player, new WebServerUpdateNBTPayload("highlight/block", "highlight", Optional.of(payload)));
      }
   }

   public static void entity(ServerPlayer player, int entityId, int flags) {
      Entity entity = player.level().getEntity(entityId);
      if (entity != null) {
         OrderedCompoundTag payload = new OrderedCompoundTag();
         payload.put("flags", flags(flags));
         OrderedCompoundTag payloadEntity = new OrderedCompoundTag();
         payloadEntity.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
         payloadEntity.putInt("network_id", entityId);
         payloadEntity.putString("unique_id", entity.getUUID().toString());
         payloadEntity.putString("dimension", player.level().dimension().location().toString());
         OrderedCompoundTag jpos = new OrderedCompoundTag();
         payloadEntity.put("pos", jpos);
         jpos.putDouble("x", entity.position().x);
         jpos.putDouble("y", entity.position().y);
         jpos.putDouble("z", entity.position().z);

         try {
            payloadEntity.put("data", entity.saveWithoutId(new CompoundTag()));
         } catch (Exception var8) {
            payloadEntity.put("data", new CompoundTag());
         }

         payload.put("entity", payloadEntity);
         KubeJSNet.safeSendToPlayer(player, new WebServerUpdateNBTPayload("highlight/entity", "highlight", Optional.of(payload)));
      }
   }

   public static void inventory(ServerPlayer player, List<Integer> slots, List<ItemStack> stacks, int flags) {
      LinkedHashSet<KubedexPayloadHandler.SlotItem> allStacks = new LinkedHashSet<>();

      for (ItemStack s : stacks) {
         if (!s.isEmpty()) {
            allStacks.add(new KubedexPayloadHandler.SlotItem(s, -1));
         }
      }

      for (int sx : slots) {
         if (sx >= 0 && sx < player.getInventory().getContainerSize()) {
            ItemStack item = player.getInventory().getItem(sx);
            if (!item.isEmpty()) {
               allStacks.add(new KubedexPayloadHandler.SlotItem(item, sx));
            }
         }
      }

      itemStacks(player, allStacks, flags);
   }

   public static void itemStacks(ServerPlayer player, Collection<KubedexPayloadHandler.SlotItem> stacks, int flags) {
      RegistryOps<Tag> ops = player.server.registryAccess().createSerializationContext(NbtOps.INSTANCE);
      CompoundTag payload = new CompoundTag();
      payload.put("flags", flags(flags));
      ListTag payloadItems = new ListTag();

      for (KubedexPayloadHandler.SlotItem slotStack : stacks) {
         ItemStack stack = slotStack.item;
         OrderedCompoundTag tag = new OrderedCompoundTag();
         tag.putString("string", stack.kjs$toItemString0(ops));
         tag.put("item", (Tag)ItemStack.CODEC.encodeStart(ops, stack).result().get());
         tag.put("name", (Tag)ComponentSerialization.FLAT_CODEC.encodeStart(ops, stack.getHoverName()).getOrThrow());
         tag.putString("icon", stack.kjs$getWebIconURL(ops, 64).toString());
         tag.putInt("slot", slotStack.slot);
         DataComponentPatch patch = stack.getComponentsPatch();
         if (!patch.isEmpty()) {
            tag.putString("component_string", DataComponentWrapper.patchToString(new StringBuilder(), ops, patch).toString());
         }

         ListTag itemTagList = sortedTagList(stack.getItemHolder().tags());
         if (!itemTagList.isEmpty()) {
            tag.put("tags", itemTagList);
         }

         if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() != Blocks.AIR) {
            ListTag blockTagList = sortedTagList(blockItem.getBlock().builtInRegistryHolder().tags());
            if (!blockTagList.isEmpty()) {
               tag.put("block_tags", blockTagList);
            }
         }

         if (stack.getItem() instanceof BucketItem bucket && bucket.content != Fluids.EMPTY) {
            ListTag fluidTagList = sortedTagList(bucket.content.builtInRegistryHolder().tags());
            if (!fluidTagList.isEmpty()) {
               tag.put("fluid_tags", fluidTagList);
            }
         }

         if (stack.getItem() instanceof SpawnEggItem egg) {
            EntityType<?> entityType = egg.getType(stack);
            if (entityType != null) {
               ListTag entityTagList = sortedTagList(entityType.builtInRegistryHolder().tags());
               if (!entityTagList.isEmpty()) {
                  tag.put("entity_tags", entityTagList);
               }
            }
         }

         payloadItems.add(tag);
      }

      payload.put("items", payloadItems);
      KubeJSNet.safeSendToPlayer(player, new WebServerUpdateNBTPayload("highlight/items", "highlight", Optional.of(payload)));
   }

   public record SlotItem(ItemStack item, int slot) {
   }
}
