package vazkii.psi.api.cad;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class PostCADCraftEvent extends Event {
   private final ItemStack cad;
   private final ITileCADAssembler assembler;

   public PostCADCraftEvent(ItemStack cad, ITileCADAssembler assembler) {
      this.cad = cad;
      this.assembler = assembler;
   }

   public ITileCADAssembler getAssembler() {
      return this.assembler;
   }

   public ItemStack getCad() {
      return this.cad;
   }
}
