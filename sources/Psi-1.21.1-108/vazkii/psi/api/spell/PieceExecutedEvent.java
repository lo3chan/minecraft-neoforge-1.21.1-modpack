package vazkii.psi.api.spell;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;

public class PieceExecutedEvent extends Event {
   @NotNull
   private final SpellPiece piece;
   @NotNull
   private final Player playerEntity;

   public PieceExecutedEvent(@NotNull SpellPiece piece, @NotNull Player playerEntity) {
      this.piece = piece;
      this.playerEntity = playerEntity;
   }

   @NotNull
   public SpellPiece getPiece() {
      return this.piece;
   }

   @NotNull
   public Player getPlayerEntity() {
      return this.playerEntity;
   }
}
