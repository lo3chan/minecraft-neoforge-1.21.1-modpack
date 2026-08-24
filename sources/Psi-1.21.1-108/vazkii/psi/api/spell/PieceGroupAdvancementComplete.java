package vazkii.psi.api.spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PieceGroupAdvancementComplete extends Event {
   @Nullable
   private final SpellPiece piece;
   @NotNull
   private final Player playerEntity;
   @NotNull
   private final ResourceLocation pieceGroup;

   public PieceGroupAdvancementComplete(@Nullable SpellPiece piece, @NotNull Player playerEntity, @NotNull ResourceLocation pieceGroup) {
      this.piece = piece;
      this.playerEntity = playerEntity;
      this.pieceGroup = pieceGroup;
   }

   @NotNull
   public ResourceLocation getPieceGroup() {
      return this.pieceGroup;
   }

   @Nullable
   public SpellPiece getPiece() {
      return this.piece;
   }

   @NotNull
   public Player getPlayerEntity() {
      return this.playerEntity;
   }
}
