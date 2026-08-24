package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record MsgNewWallScrollS2C(ClientboundAddEntityPacket inner, BlockPos pos, Direction dir, ItemStack scrollItem, boolean showsStrokeOrder, int blockSize)
   implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("wallscr");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      RegistryFriendlyByteBuf registryBuf = (RegistryFriendlyByteBuf)buf;
      ClientboundAddEntityPacket.STREAM_CODEC.encode(registryBuf, this.inner);
      buf.writeBlockPos(this.pos);
      buf.writeByte(this.dir.ordinal());
      ItemStack.OPTIONAL_STREAM_CODEC.encode(registryBuf, this.scrollItem);
      buf.writeBoolean(this.showsStrokeOrder);
      buf.writeVarInt(this.blockSize);
   }

   public static MsgNewWallScrollS2C deserialize(FriendlyByteBuf buf) {
      RegistryFriendlyByteBuf registryBuf = (RegistryFriendlyByteBuf)buf;
      ClientboundAddEntityPacket inner = (ClientboundAddEntityPacket)ClientboundAddEntityPacket.STREAM_CODEC.decode(registryBuf);
      BlockPos pos = buf.readBlockPos();
      Direction dir = HexUtils.getSafe(Direction.values(), buf.readByte());
      ItemStack scroll = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(registryBuf);
      boolean strokeOrder = buf.readBoolean();
      int blockSize = buf.readVarInt();
      return new MsgNewWallScrollS2C(inner, pos, dir, scroll, strokeOrder, blockSize);
   }

   public static void handle(final MsgNewWallScrollS2C self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
               player.connection.handleAddEntity(self.inner);
               if (player.level().getEntity(self.inner.getId()) instanceof EntityWallScroll scroll) {
                  scroll.readSpawnData(self.pos, self.dir, self.scrollItem, self.showsStrokeOrder, self.blockSize);
               }
            }
         }
      });
   }
}
