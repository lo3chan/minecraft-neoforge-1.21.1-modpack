package net.joefoxe.hexerei.util.message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RecipeToServer extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipeToServer> CODEC = StreamCodec.ofMember(RecipeToServer::encode, RecipeToServer::new);
   public static final Type<RecipeToServer> TYPE = new Type(HexereiUtil.getResource("recipe_server"));
   private List<ItemStack> stacks;
   private BlockPos pos;
   private UUID uuid;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public RecipeToServer(List<ItemStack> stacks, BlockPos pos, UUID uuid) {
      this.stacks = stacks;
      this.pos = pos;
      this.uuid = uuid;
   }

   public RecipeToServer(RegistryFriendlyByteBuf buf) {
      int num = buf.readInt();
      List<ItemStack> stacks1 = new ArrayList<>();

      for (int i = 0; i < num; i++) {
         stacks1.add((ItemStack)ItemStack.STREAM_CODEC.decode(buf));
      }

      this.stacks = stacks1;
      if (buf.readBoolean()) {
         this.pos = buf.readBlockPos();
      } else {
         this.pos = null;
      }

      this.uuid = buf.readUUID();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      List<ItemStack> non_empty = this.stacks.stream().filter(stackx -> !stackx.isEmpty()).toList();
      buffer.writeInt(non_empty.size());

      for (ItemStack stack : non_empty) {
         ItemStack.STREAM_CODEC.encode(buffer, stack);
      }

      if (this.pos != null) {
         buffer.writeBoolean(true);
         buffer.writeBlockPos(this.pos);
      } else {
         buffer.writeBoolean(false);
      }

      buffer.writeUUID(this.uuid);
   }

   @Override
   public void onServerReceived(MinecraftServer server, ServerPlayer player) {
      if (player.level().getBlockEntity(this.pos) instanceof MixingCauldronTile cauldron) {
         cauldron.setContents(this.stacks, player.level().getPlayerByUUID(this.uuid));
      }
   }
}
