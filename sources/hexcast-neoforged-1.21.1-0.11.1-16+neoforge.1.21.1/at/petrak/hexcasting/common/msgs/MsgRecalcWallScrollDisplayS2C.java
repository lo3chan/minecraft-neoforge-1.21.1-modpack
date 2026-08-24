package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record MsgRecalcWallScrollDisplayS2C(int entityId, boolean showStrokeOrder) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("redoscroll");

   public static MsgRecalcWallScrollDisplayS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      int id = buf.readVarInt();
      boolean showStrokeOrder = buf.readBoolean();
      return new MsgRecalcWallScrollDisplayS2C(id, showStrokeOrder);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeVarInt(this.entityId);
      buf.writeBoolean(this.showStrokeOrder);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static void handle(final MsgRecalcWallScrollDisplayS2C msg) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level.getEntity(msg.entityId) instanceof EntityWallScroll scroll && scroll.getShowsStrokeOrder() != msg.showStrokeOrder) {
               scroll.recalculateDisplay();
            }
         }
      });
   }
}
