package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public record MsgNewSpellPatternC2S(InteractionHand handUsed, HexPattern pattern, List<ResolvedPattern> resolvedPatterns) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("pat_cs");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgNewSpellPatternC2S deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      InteractionHand hand = (InteractionHand)buf.readEnum(InteractionHand.class);
      HexPattern pattern = HexPattern.fromNBT(buf.readNbt());
      int resolvedPatternsLen = buf.readInt();
      ArrayList<ResolvedPattern> resolvedPatterns = new ArrayList<>(resolvedPatternsLen);

      for (int i = 0; i < resolvedPatternsLen; i++) {
         resolvedPatterns.add(ResolvedPattern.fromNBT(buf.readNbt()));
      }

      return new MsgNewSpellPatternC2S(hand, pattern, resolvedPatterns);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeEnum(this.handUsed);
      buf.writeNbt(this.pattern.serializeToNBT());
      buf.writeInt(this.resolvedPatterns.size());

      for (ResolvedPattern pat : this.resolvedPatterns) {
         buf.writeNbt(pat.serializeToNBT());
      }
   }

   public void handle(MinecraftServer server, ServerPlayer sender) {
      server.execute(() -> StaffCastEnv.handleNewPatternOnServer(sender, this));
   }
}
