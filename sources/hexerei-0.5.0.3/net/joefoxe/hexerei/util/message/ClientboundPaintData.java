package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.data.books.BookManager;
import net.joefoxe.hexerei.data.books.BookPage;
import net.joefoxe.hexerei.data.books.BookPaintElement;
import net.joefoxe.hexerei.data.books.ClientPaintDataCache;
import net.joefoxe.hexerei.data.books.PaintData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ClientboundPaintData extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPaintData> CODEC = StreamCodec.ofMember(
      ClientboundPaintData::encode, ClientboundPaintData::new
   );
   public static final Type<ClientboundPaintData> TYPE = new Type(HexereiUtil.getResource("clientbound_paint_data"));
   PaintData paintData;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public ClientboundPaintData(PaintData paintData) {
      this.paintData = paintData;
   }

   public ClientboundPaintData(RegistryFriendlyByteBuf buf) {
      this.paintData = (PaintData)PaintData.STREAM_CODEC.decode(buf);
   }

   @Override
   public void encode(RegistryFriendlyByteBuf buffer) {
      PaintData.STREAM_CODEC.encode(buffer, this.paintData);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      ClientPaintDataCache.store(this.paintData.page, this.paintData.uuid, this.paintData);
      ResourceLocation bookLoc = ResourceLocation.parse(this.paintData.page.toString().split("/")[0]);
      BookPage bookPage = BookManager.getBookPages(bookLoc, this.paintData.page);
      if (bookPage != null) {
         for (BookPaintElement paintElement : bookPage.paintElements) {
            paintElement.client.getPaintSystem(this.paintData.uuid).fromPaintData(this.paintData);
         }
      }
   }
}
