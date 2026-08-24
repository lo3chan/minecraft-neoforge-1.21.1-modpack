package vazkii.psi.common.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.internal.VanillaPacketDispatcher;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.tile.TileProgrammer;

public record MessageSpellModified(BlockPos pos, Spell spell) implements CustomPacketPayload {
   public static final ResourceLocation ID = Psi.location("message_spell_modified");
   public static final Type<MessageSpellModified> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, MessageSpellModified> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, MessageSpellModified::pos, Spell.STREAM_CODEC, MessageSpellModified::spell, MessageSpellModified::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(
         () -> {
            if (ctx.player().level().getBlockEntity(this.pos) instanceof TileProgrammer tile
               && (tile.playerLock == null || tile.playerLock.isEmpty() || tile.playerLock.equals(ctx.player().getName().getString()))) {
               tile.spell = this.spell;
               tile.onSpellChanged();
               VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
            }
         }
      );
   }
}
