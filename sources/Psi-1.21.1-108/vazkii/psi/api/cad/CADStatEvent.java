package vazkii.psi.api.cad;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class CADStatEvent extends Event {
   private final EnumCADComponent statProvider;
   private final EnumCADStat stat;
   private final ItemStack cad;
   private final ItemStack component;
   private int statValue;

   public CADStatEvent(EnumCADStat stat, ItemStack cad, ItemStack component, int statValue) {
      this.statProvider = stat.getSourceType();
      this.stat = stat;
      this.cad = cad;
      this.component = component;
      this.statValue = statValue;
   }

   public EnumCADComponent getStatProvider() {
      return this.statProvider;
   }

   public EnumCADStat getStat() {
      return this.stat;
   }

   public ItemStack getCad() {
      return this.cad;
   }

   public ItemStack getComponent() {
      return this.component;
   }

   public int getStatValue() {
      return this.statValue;
   }

   public void setStatValue(int statValue) {
      this.statValue = statValue;
   }
}
