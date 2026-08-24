package at.petrak.hexcasting.common.msgs;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import at.petrak.hexcasting.common.lib.HexSounds;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record MsgNewSpellPatternS2C(ExecutionClientView info, int index) implements IMessage {
   public static final ResourceLocation ID = HexAPI.modLoc("pat_sc");

   @Override
   public ResourceLocation id() {
      return ID;
   }

   public static MsgNewSpellPatternS2C deserialize(ByteBuf buffer) {
      FriendlyByteBuf buf = new FriendlyByteBuf(buffer);
      boolean isStackEmpty = buf.readBoolean();
      ResolvedPatternType resolutionType = (ResolvedPatternType)buf.readEnum(ResolvedPatternType.class);
      int index = buf.readInt();
      List<CompoundTag> stack = buf.readList(fbb -> fbb.readNbt());
      CompoundTag raven = (CompoundTag)buf.readOptional(fbb -> fbb.readNbt()).orElse(null);
      return new MsgNewSpellPatternS2C(new ExecutionClientView(isStackEmpty, resolutionType, stack, raven), index);
   }

   @Override
   public void serialize(FriendlyByteBuf buf) {
      buf.writeBoolean(this.info.isStackClear());
      buf.writeEnum(this.info.getResolutionType());
      buf.writeInt(this.index);
      buf.writeCollection(this.info.getStackDescs(), (fbb, tag) -> fbb.writeNbt(tag));
      buf.writeOptional(Optional.ofNullable(this.info.getRavenmind()), (fbb, tag) -> fbb.writeNbt(tag));
   }

   public static void handle(final MsgNewSpellPatternS2C self) {
      Minecraft.getInstance().execute(new Runnable() {
         @Override
         public void run() {
            Minecraft mc = Minecraft.getInstance();
            if (self.info().isStackClear()) {
               mc.getSoundManager().stop(HexSounds.CASTING_AMBIANCE.getLocation(), null);
            }

            if (Minecraft.getInstance().screen instanceof GuiSpellcasting spellGui) {
               spellGui.recvServerUpdate(self.info(), self.index());
            }
         }
      });
   }
}
