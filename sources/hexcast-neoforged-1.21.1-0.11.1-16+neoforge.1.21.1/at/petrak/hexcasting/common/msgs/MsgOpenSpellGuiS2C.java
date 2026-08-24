package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public record MsgOpenSpellGuiS2C(InteractionHand hand, List<ResolvedPattern> patterns, List<CompoundTag> stack, CompoundTag ravenmind, int parenCount)
   implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("cgui");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgOpenSpellGuiS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      InteractionHand hand = (InteractionHand)buf.readEnum(InteractionHand.class);
      List<ResolvedPattern> patterns = buf.readList(fbb -> ResolvedPattern.fromNBT(fbb.readNbt()));
      List<CompoundTag> stack = buf.readList(fbb -> fbb.readNbt());
      CompoundTag raven = buf.readNbt();
      int parenCount = buf.readVarInt();
      return new MsgOpenSpellGuiS2C(hand, patterns, stack, raven, parenCount);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeEnum(this.hand);
      buf.writeCollection(this.patterns, (fbb, pat) -> fbb.writeNbt(pat.serializeToNBT()));
      buf.writeCollection(this.stack, (fbb, tag) -> fbb.writeNbt(tag));
      buf.writeNbt(this.ravenmind);
      buf.writeVarInt(this.parenCount);
   }

   public static void handle(final MsgOpenSpellGuiS2C msg) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new GuiSpellcasting(msg.hand(), msg.patterns(), msg.stack, msg.ravenmind, msg.parenCount));
         }
      });
   }
}
