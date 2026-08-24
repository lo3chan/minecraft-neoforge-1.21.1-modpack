package vazkii.psi.api.cad;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class AssembleCADEvent extends Event implements ICancellableEvent {
   private final ITileCADAssembler assembler;
   private final Player player;
   private ItemStack cad;

   public AssembleCADEvent(ItemStack cad, ITileCADAssembler assembler, Player player) {
      this.cad = cad;
      this.assembler = assembler;
      this.player = player;
   }

   public ITileCADAssembler getAssembler() {
      return this.assembler;
   }

   public ItemStack getCad() {
      return this.cad;
   }

   public void setCad(ItemStack cad) {
      if (!cad.isEmpty() && !(cad.getItem() instanceof ICAD)) {
         throw new IllegalStateException("Only a CAD can be crafted by the CAD Assembler!");
      } else {
         this.cad = cad;
      }
   }

   public Player getPlayer() {
      return this.player;
   }
}
