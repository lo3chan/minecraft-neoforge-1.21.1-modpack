package vazkii.psi.api.spell;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import vazkii.psi.api.internal.IPlayerData;

public class LoopcastEndEvent extends Event {
   private final Player player;
   private final IPlayerData playerData;
   private final InteractionHand hand;
   private final int loopcastAmount;

   public LoopcastEndEvent(Player player, IPlayerData playerData, InteractionHand hand, int loopcastAmount) {
      this.player = player;
      this.playerData = playerData;
      this.hand = hand;
      this.loopcastAmount = loopcastAmount;
   }

   public Player getPlayer() {
      return this.player;
   }

   public IPlayerData getPlayerData() {
      return this.playerData;
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public int getLoopcastAmount() {
      return this.loopcastAmount;
   }
}
