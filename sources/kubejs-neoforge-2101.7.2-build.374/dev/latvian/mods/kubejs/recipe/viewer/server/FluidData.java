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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public record FluidData(
   List<FluidStack> addedEntries,
   List<FluidIngredient> removedEntries,
   List<FluidIngredient> completelyRemovedEntries,
   List<FluidData.Group> groupedEntries,
   List<FluidData.Info> info,
   List<FluidData.DataComponentSubtypes> dataComponentSubtypes
) {
   public static final StreamCodec<RegistryFriendlyByteBuf, FluidData> STREAM_CODEC = StreamCodec.composite(
      FluidStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FluidData::addedEntries,
      FluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FluidData::removedEntries,
      FluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FluidData::completelyRemovedEntries,
      FluidData.Group.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FluidData::groupedEntries,
      FluidData.Info.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FluidData::info,
      FluidData.DataComponentSubtypes.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FluidData::dataComponentSubtypes,
      FluidData::new
   );

   public static FluidData collect() {
      ArrayList<FluidStack> addedEntries = new ArrayList<>();
      ArrayList<FluidIngredient> removedEntries = new ArrayList<>();
      ArrayList<FluidIngredient> completelyRemovedEntries = new ArrayList<>();
      ArrayList<FluidData.Group> groupedEntries = new ArrayList<>();
      ArrayList<FluidData.Info> info = new ArrayList<>();
      ArrayList<FluidData.DataComponentSubtypes> dataComponentSubtypes = new ArrayList<>();
      if (RecipeViewerEvents.ADD_ENTRIES.hasListeners(RecipeViewerEntryType.FLUID)) {
         RecipeViewerEvents.ADD_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.FLUID, new ServerAddFluidEntriesKubeEvent(addedEntries));
      }

      if (RecipeViewerEvents.REMOVE_ENTRIES.hasListeners(RecipeViewerEntryType.FLUID)) {
         RecipeViewerEvents.REMOVE_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.FLUID, new ServerRemoveFluidEntriesKubeEvent(removedEntries));
      }

      if (RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY.hasListeners(RecipeViewerEntryType.FLUID)) {
         RecipeViewerEvents.REMOVE_ENTRIES_COMPLETELY
            .post(ScriptType.SERVER, RecipeViewerEntryType.FLUID, new ServerRemoveFluidEntriesKubeEvent(completelyRemovedEntries));
      }

      if (RecipeViewerEvents.GROUP_ENTRIES.hasListeners(RecipeViewerEntryType.FLUID)) {
         RecipeViewerEvents.GROUP_ENTRIES.post(ScriptType.SERVER, RecipeViewerEntryType.FLUID, new ServerGroupFluidEntriesKubeEvent(groupedEntries));
      }

      if (RecipeViewerEvents.ADD_INFORMATION.hasListeners(RecipeViewerEntryType.FLUID)) {
         RecipeViewerEvents.ADD_INFORMATION.post(ScriptType.SERVER, RecipeViewerEntryType.FLUID, new ServerAddFluidInformationKubeEvent(info));
      }

      if (RecipeViewerEvents.REGISTER_SUBTYPES.hasListeners(RecipeViewerEntryType.FLUID)) {
         RecipeViewerEvents.REGISTER_SUBTYPES
            .post(ScriptType.SERVER, RecipeViewerEntryType.FLUID, new ServerRegisterFluidSubtypesKubeEvent(dataComponentSubtypes));
      }

      return new FluidData(
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

   public record DataComponentSubtypes(FluidIngredient filter, List<DataComponentType<?>> components) {
      public static final StreamCodec<RegistryFriendlyByteBuf, FluidData.DataComponentSubtypes> STREAM_CODEC = StreamCodec.composite(
         FluidIngredient.STREAM_CODEC,
         FluidData.DataComponentSubtypes::filter,
         DataComponentType.STREAM_CODEC.apply(ByteBufCodecs.list()),
         FluidData.DataComponentSubtypes::components,
         FluidData.DataComponentSubtypes::new
      );
   }

   public record Group(FluidIngredient filter, ResourceLocation groupId, Component description) {
      public static final StreamCodec<RegistryFriendlyByteBuf, FluidData.Group> STREAM_CODEC = StreamCodec.composite(
         FluidIngredient.STREAM_CODEC,
         FluidData.Group::filter,
         ResourceLocation.STREAM_CODEC,
         FluidData.Group::groupId,
         ComponentSerialization.STREAM_CODEC,
         FluidData.Group::description,
         FluidData.Group::new
      );
   }

   public record Info(FluidIngredient filter, List<Component> info) {
      public static final StreamCodec<RegistryFriendlyByteBuf, FluidData.Info> STREAM_CODEC = StreamCodec.composite(
         FluidIngredient.STREAM_CODEC,
         FluidData.Info::filter,
         ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()),
         FluidData.Info::info,
         FluidData.Info::new
      );
   }
}
