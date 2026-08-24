package snownee.jade.gui.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OptionButton extends OptionsList.Entry {
   protected Component title;

   public OptionButton(String titleKey, Button button) {
      this(makeTitle(titleKey), button);
   }

   public OptionButton(Component title, Button button) {
      this.title = title;
      this.addMessage(title.getString());
      if (button != null) {
         if (button.getMessage().getString().isEmpty()) {
            button.setMessage(title);
         } else {
            this.addMessage(button.getMessage().getString());
         }

         this.addWidget(button, 0);
      }
   }

   public OptionButton(Component title, Builder builder) {
      this(title, builder.createNarration($ -> CommonComponents.joinForNarration(new Component[]{title, (Component)$.get()})).build());
   }

   @Override
   public void render(
      GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime
   ) {
      guiGraphics.drawString(this.client.font, this.title, rowLeft + 10, rowTop + height / 2 - 9 / 2, 16777215);
      super.render(guiGraphics, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);
   }
}
