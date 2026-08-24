package vazkii.psi.api.spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.internal.IPlayerData;

public class PieceKnowledgeEvent extends Event implements ICancellableEvent {
   @NotNull
   private final ResourceLocation pieceGroup;
   @Nullable
   private final ResourceLocation pieceName;
   @NotNull
   private final Player player;
   @NotNull
   private final IPlayerData data;
   private final boolean isUnlocked;

   public PieceKnowledgeEvent(
      @NotNull ResourceLocation pieceGroup, @Nullable ResourceLocation pieceName, @NotNull Player player, @NotNull IPlayerData data, boolean isUnlocked
   ) {
      this.pieceGroup = pieceGroup;
      this.pieceName = pieceName;
      this.player = player;
      this.data = data;
      this.isUnlocked = isUnlocked;
   }

   @NotNull
   public ResourceLocation getPieceGroup() {
      return this.pieceGroup;
   }

   @Nullable
   public ResourceLocation getPieceName() {
      return this.pieceName;
   }

   @NotNull
   public Player getPlayer() {
      return this.player;
   }

   @NotNull
   public IPlayerData getData() {
      return this.data;
   }

   public boolean isUnlocked() {
      return this.isUnlocked;
   }
}
