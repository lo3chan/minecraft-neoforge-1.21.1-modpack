package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.plugin.builtin.event.RecipeViewerEvents;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ItemData(
   List<ItemStack> addedEntries,
   List<Ingredient> removedEntries,
   List<Ingredient> completelyRemovedEntries,
   List<ItemData.Group> groupedEntries,
   List<ItemData.Info> info,
   List<ItemData.DataComponentSubtypes> dataComponentSubtypes
) {
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemData> STREAM_CODEC = StreamCodec.composite(
      ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ItemData::addedEntries,
      Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
      ItemData::removedEntries,
      Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
      ItemData::completelyRemovedEntries,
      ItemData.Group.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ItemData::groupedEntries,
      ItemData.Info.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ItemData::info,
      ItemData.DataComponentSubtypes.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ItemData::dataComponentSubtypes,
      ItemData::new
   );

   public static ItemData collect() {
      ArrayList<ItemStack> addedEntries = new ArrayList<>();
      ArrayList<Ingredient> removedEntries = new ArrayList<>();
      ArrayList<Ingredient> completelyRemovedEntries = new ArrayList<>();
      ArrayList<ItemData.Group> groupedEntries = new ArrayList<>();
      ArrayList<ItemData.Info> info = new ArrayList<>();
      ArrayList<ItemData.DataComponentSubtypes> dataComponentSubtypes = new ArrayList<>();
      if (RecipeViewerEvents.ADD_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
         RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerAddItemEntriesKubeEvent(addedEntries));
      }

      if (RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
         RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerRemoveItemEntriesKubeEvent(removedEntries));
      }

      if (RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.hasListeners(RecipeViewerEntryType.ITEM)) {
         RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY
            .post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerRemoveItemEntriesKubeEvent(completelyRemovedEntries));
      }

      if (RecipeViewerEvents.GROUP_ENTRIES.hasListeners(RecipeViewerEntryType.ITEM)) {
         RecipeViewerEvents.GROUP_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerGroupItemEntriesKubeEvent(groupedEntries));
      }

      if (RecipeViewerEvents.ADD_INFORMATION.hasListeners(RecipeViewerEntryType.ITEM)) {
         RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerAddItemInformationKubeEvent(info));
      }

      if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.ITEM)) {
         RecipeViewerEvents.REGISTER_SUBTYPES
            .post(ScriptType.SERVER, RecipeViewerEntryType.ITEM, new ServerRegisterItemSubtypesKubeEvent(dataComponentSubtypes));
      }

      return new ItemData(
         List.copyOf(addedEntries),
         List.copyOf(removedEntries),
         List.copyOf(completelyRemovedEntries),
         List.copyOf(groupedEntries),
         List.copyOf(info),
         List.copyOf(dataComponentSubtypes)
      );
   }

   public boolean isEmpty() {
      return this.addedEntries.isEmpty()
         && this.removedEntries.isEmpty()
         && this.completelyRemovedEntries.isEmpty()
         && this.groupedEntries.isEmpty()
         && this.info.isEmpty()
         && this.dataComponentSubtypes.isEmpty();
   }

   public record DataComponentSubtypes(Ingredient filter, List<DataComponentType<?>> components) {
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemData.DataComponentSubtypes> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC,
         ItemData.DataComponentSubtypes::filter,
         DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()),
         ItemData.DataComponentSubtypes::components,
         ItemData.DataComponentSubtypes::new
      );
   }

   public record Group(Ingredient filter, ResourceLocation groupId, Component description) {
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemData.Group> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC,
         ItemData.Group::filter,
         ResourceLocation.STREAM_CODEC,
         ItemData.Group::groupId,
         ComponentSerialization.STREAM_CODEC,
         ItemData.Group::description,
         ItemData.Group::new
      );
   }

   public record Info(Ingredient filter, List<Component> info) {
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemData.Info> STREAM_CODEC = StreamCodec.composite(
         Ingredient.CONTENTS_STREAM_CODEC,
         ItemData.Info::filter,
         ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()),
         ItemData.Info::info,
         ItemData.Info::new
      );
   }
}
