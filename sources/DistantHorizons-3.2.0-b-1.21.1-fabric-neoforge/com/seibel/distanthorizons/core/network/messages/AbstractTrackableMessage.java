package com.seibel.distanthorizons.core.network.messages;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.network.messages.requests.ExceptionMessage;
import com.seibel.distanthorizons.core.network.session.NetworkSession;
import com.seibel.distanthorizons.core.util.LodUtil;
import com.seibel.distanthorizons.core.world.EWorldEnvironment;
import io.netty.buffer.ByteBuf;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractTrackableMessage extends AbstractNetworkMessage {
   private static final AtomicInteger LAST_MESSAGE_ID_REF = new AtomicInteger();
   public long futureId;

   public AbstractTrackableMessage() {
      EWorldEnvironment worldEnvironment = SharedApi.getEnvironment();
      LodUtil.assertTrue(worldEnvironment != null, "Message can't be created if no world is loaded.");
      long id = LAST_MESSAGE_ID_REF.getAndIncrement();
      id |= (worldEnvironment == EWorldEnvironment.SERVER_ONLY ? 1 : 0) << 31;
      this.futureId = id;
   }

   protected abstract void encodeInternal(ByteBuf byteBuf) throws Exception;

   protected abstract void decodeInternal(ByteBuf byteBuf) throws Exception;

   @Override
   public void setSession(NetworkSession networkSession) {
      super.setSession(networkSession);
      this.futureId = this.futureId | (long)networkSession.id << 32;
   }

   public void sendResponse(AbstractTrackableMessage responseMessage) {
      responseMessage.futureId = this.futureId;
      this.getSession().sendMessage(responseMessage);
   }

   public void sendResponse(Exception e) {
      this.sendResponse(new ExceptionMessage(e));
   }

   @Override
   public final void encode(ByteBuf out) {
      try {
         out.writeInt((int)this.futureId);
         this.encodeInternal(out);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   @Override
   public final void decode(ByteBuf in) {
      try {
         this.futureId = in.readInt();
         this.decodeInternal(in);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   @Override
   public ToStringHelper toStringHelper() {
      return super.toStringHelper().add("futureId", this.futureId);
   }
}
