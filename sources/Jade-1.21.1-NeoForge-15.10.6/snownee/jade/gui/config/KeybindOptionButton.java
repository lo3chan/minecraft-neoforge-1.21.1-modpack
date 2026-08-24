package snownee.jade.gui.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class KeybindOptionButton extends OptionButton {
   private final KeyMapping keybind;

   public KeybindOptionButton(OptionsList owner, KeyMapping keybind) {
      super(Component.translatable(keybind.getName()), (Button)null);
      this.keybind = keybind;
      Button button = Button.builder(keybind.getTranslatedKeyMessage(), b -> {
            owner.selectedKey = this.keybind;
            owner.resetMappingAndUpdateButtons();
         })
         .size(100, 20)
         .createNarration(
            supplier -> this.keybind.isUnbound()
               ? Component.translatable("narrator.controls.unbound", new Object[]{this.title})
               : Component.translatable("narrator.controls.bound", new Object[]{this.title, supplier.get()})
         )
         .build();
      this.addWidget(button, 0);
   }

   public void refresh(KeyMapping selectedKey) {
      AbstractWidget button = this.getFirstWidget();
      if (selectedKey == this.keybind) {
         button.setMessage(
            Component.literal("> ")
               .append(button.getMessage().copy().withStyle(new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.UNDERLINE}))
               .append(" <")
               .withStyle(ChatFormatting.YELLOW)
         );
      } else {
         button.setMessage(this.keybind.getTranslatedKeyMessage());
      }
   }
}
