package net.joefoxe.hexerei.util.message;

import java.util.Collection;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.MapPatch;

public class MapDataPacket extends AbstractPacket {
   public static final StreamCodec<RegistryFriendlyByteBuf, AskForMapDataPacket> CODEC = StreamCodec.ofMember(
      AskForMapDataPacket::encode, AskForMapDataPacket::new
   );
   public static final Type<AskForMapDataPacket> TYPE = new Type(HexereiUtil.getResource("map_data"));
   private final MapId mapId;
   private final byte scale;
   private final boolean locked;

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public MapDataPacket(MapId pMapId, byte pScale, boolean pLocked, @Nullable Collection<MapDecoration> pDecorations, @Nullable MapPatch pColorPatch) {
      this.mapId = pMapId;
      this.scale = pScale;
      this.locked = pLocked;
   }

   public MapDataPacket(RegistryFriendlyByteBuf pBuffer) {
      this.mapId = (MapId)MapId.STREAM_CODEC.decode(pBuffer);
      this.scale = pBuffer.readByte();
      this.locked = pBuffer.readBoolean();
   }

   @Override
   public void encode(RegistryFriendlyByteBuf pBuffer) {
      MapId.STREAM_CODEC.encode(pBuffer, this.mapId);
      pBuffer.writeByte(this.scale);
      pBuffer.writeBoolean(this.locked);
   }

   @Override
   public void onClientReceived(Minecraft minecraft, Player player) {
      MapRenderer maprenderer = minecraft.gameRenderer.getMapRenderer();
      MapItemSavedData mapitemsaveddata = minecraft.level.getMapData(this.mapId);
      if (mapitemsaveddata == null) {
         mapitemsaveddata = MapItemSavedData.createForClient(this.getScale(), this.isLocked(), minecraft.level.dimension());
         minecraft.level.setMapData(this.mapId, mapitemsaveddata);
      }

      maprenderer.update(this.mapId, mapitemsaveddata);
   }

   public byte getScale() {
      return this.scale;
   }

   public boolean isLocked() {
      return this.locked;
   }
}
