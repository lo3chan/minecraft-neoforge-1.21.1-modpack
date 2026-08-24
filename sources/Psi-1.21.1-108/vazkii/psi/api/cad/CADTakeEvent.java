package vazkii.psi.api.cad;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public class CADTakeEvent extends Event implements ICancellableEvent {
   private final ItemStack cad;
   private final ITileCADAssembler assembler;
   private final Player player;
   @Nullable
   private String cancellationMessage = "psimisc.cancelled_cad_take";
   private float sound = 0.5F;

   public CADTakeEvent(ItemStack cad, ITileCADAssembler assembler, Player player) {
      this.cad = cad;
      this.assembler = assembler;
      this.player = player;
   }

   @Nullable
   public String getCancellationMessage() {
      return this.cancellationMessage;
   }

   public void setCancellationMessage(@Nullable String cancellationMessage) {
      this.cancellationMessage = cancellationMessage;
   }

   public float getSound() {
      return this.sound;
   }

   public void setSound(float sound) {
      this.sound = sound;
   }

   public ITileCADAssembler getAssembler() {
      return this.assembler;
   }

   public ItemStack getCad() {
      return this.cad;
   }

   public Player getPlayer() {
      return this.player;
   }
}
