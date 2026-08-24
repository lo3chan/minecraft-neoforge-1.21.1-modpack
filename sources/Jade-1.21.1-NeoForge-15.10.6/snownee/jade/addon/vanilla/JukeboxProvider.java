package snownee.jade.addon.vanilla;

import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;

public enum JukeboxProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, ItemStack> {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      Optional<ItemStack> result = INSTANCE.decodeFromData(accessor);
      if (!result.isEmpty()) {
         ItemStack stack = result.get();
         if (stack.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.jade.empty"));
         } else {
            JukeboxPlayable playable = (JukeboxPlayable)stack.get(DataComponents.JUKEBOX_PLAYABLE);
            Component name;
            if (playable != null) {
               name = playable.song()
                  .unwrap(accessor.getLevel().registryAccess())
                  .map($ -> ((JukeboxSong)$.value()).description())
                  .orElse(stack.getHoverName());
            } else {
               name = stack.getHoverName();
            }

            tooltip.add(Component.translatable("record.nowPlaying", new Object[]{IDisplayHelper.get().stripColor(name)}));
         }
      }
   }

   public boolean shouldRequestData(BlockAccessor accessor) {
      return (Boolean)accessor.getBlockState().getValue(JukeboxBlock.HAS_RECORD);
   }

   public ItemStack streamData(BlockAccessor accessor) {
      return ((JukeboxBlockEntity)accessor.getBlockEntity()).getTheItem();
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, ItemStack> streamCodec() {
      return ItemStack.OPTIONAL_STREAM_CODEC;
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_JUKEBOX;
   }
}
