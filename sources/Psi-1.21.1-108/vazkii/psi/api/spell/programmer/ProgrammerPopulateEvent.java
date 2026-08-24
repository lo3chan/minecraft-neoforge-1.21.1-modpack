package vazkii.psi.api.spell.programmer;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.SpellPiece;

public class ProgrammerPopulateEvent extends Event {
   @NotNull
   private final Player entity;
   @NotNull
   private Registry<Class<? extends SpellPiece>> spellPieceRegistry;

   public ProgrammerPopulateEvent(@NotNull Player entity, @NotNull Registry<Class<? extends SpellPiece>> registry) {
      this.entity = entity;
      this.spellPieceRegistry = registry;
   }

   @NotNull
   public Player getPlayer() {
      return this.entity;
   }

   @NotNull
   public Registry<Class<? extends SpellPiece>> getSpellPieceRegistry() {
      return this.spellPieceRegistry;
   }

   public void setSpellPieceRegistry(@NotNull Registry<Class<? extends SpellPiece>> registry) {
      this.spellPieceRegistry = registry;
   }
}
