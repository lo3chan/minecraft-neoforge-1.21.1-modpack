package top.theillusivec4.curios.api.extensions;

import javax.annotation.Nonnull;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class RegisterCuriosExtensionsEvent extends Event implements IModBusEvent {
   public void registerSlotExtension(@Nonnull ICurioSlotExtension extension, String... slotIds) {
      CuriosExtensions.register(extension, slotIds);
   }

   public boolean isSlotExtensionRegistered(String slotId) {
      return CuriosExtensions.SLOT_EXTENSIONS.containsKey(slotId);
   }
}
