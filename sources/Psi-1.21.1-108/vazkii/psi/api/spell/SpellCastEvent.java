package vazkii.psi.api.spell;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import vazkii.psi.api.internal.IPlayerData;

public class SpellCastEvent extends Event {
   public final Spell spell;
   public final SpellContext context;
   public final Player player;
   public final IPlayerData playerData;
   public final ItemStack cad;
   public final ItemStack bullet;

   public SpellCastEvent(Spell spell, SpellContext context, Player player, IPlayerData playerData, ItemStack cad, ItemStack bullet) {
      this.spell = spell;
      this.context = context;
      this.player = player;
      this.playerData = playerData;
      this.cad = cad;
      this.bullet = bullet;
   }
}
