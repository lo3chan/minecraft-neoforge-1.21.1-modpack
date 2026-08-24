package snownee.jade.addon.vanilla;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

public enum CommandBlockProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, String> {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
      String command = this.decodeFromData(accessor).orElse("");
      if (!command.isBlank()) {
         tooltip.add(Component.literal("> " + command));
      }
   }

   @Nullable
   public String streamData(BlockAccessor accessor) {
      if (!accessor.getPlayer().canUseGameMasterBlocks()) {
         return null;
      } else {
         String command = ((CommandBlockEntity)accessor.getBlockEntity()).getCommandBlock().getCommand();
         if (command.length() > 40) {
            command = command.substring(0, 37) + "...";
         }

         return command;
      }
   }

   @Override
   public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
      return ByteBufCodecs.STRING_UTF8.cast();
   }

   public boolean shouldRequestData(BlockAccessor accessor) {
      return accessor.getPlayer().canUseGameMasterBlocks();
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_COMMAND_BLOCK;
   }
}
