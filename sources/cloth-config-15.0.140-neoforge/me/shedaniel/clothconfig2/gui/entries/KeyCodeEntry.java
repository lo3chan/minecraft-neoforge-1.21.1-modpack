package me.shedaniel.clothconfig2.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.api.ModifierKeyCode;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

@OnlyIn(Dist.CLIENT)
public class KeyCodeEntry extends TooltipListEntry<ModifierKeyCode> {
   private ModifierKeyCode value;
   private final ModifierKeyCode original;
   private final Button buttonWidget;
   private final Button resetButton;
   private final Supplier<ModifierKeyCode> defaultValue;
   private final List<AbstractWidget> widgets;
   private boolean allowMouse = true;
   private boolean allowKey = true;
   private boolean allowModifiers = true;

   @Deprecated
   @Internal
   public KeyCodeEntry(
      Component fieldName,
      ModifierKeyCode value,
      Component resetButtonKey,
      Supplier<ModifierKeyCode> defaultValue,
      Consumer<ModifierKeyCode> saveConsumer,
      Supplier<Optional<Component[]>> tooltipSupplier,
      boolean requiresRestart
   ) {
      super(fieldName, tooltipSupplier, requiresRestart);
      this.defaultValue = defaultValue;
      this.value = value.copy();
      this.original = value.copy();
      this.buttonWidget = Button.builder(Component.empty(), widget -> this.getConfigScreen().setFocusedBinding(this)).bounds(0, 0, 150, 20).build();
      this.resetButton = Button.builder(resetButtonKey, widget -> {
         this.value = this.getDefaultValue().orElse(null).copy();
         this.getConfigScreen().setFocusedBinding(null);
      }).bounds(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20).build();
      this.saveCallback = saveConsumer;
      this.widgets = Lists.newArrayList(new AbstractWidget[]{this.buttonWidget, this.resetButton});
   }

   @Override
   public boolean isEdited() {
      return super.isEdited() || !this.original.equals(this.getValue());
   }

   public boolean isAllowModifiers() {
      return this.allowModifiers;
   }

   public void setAllowModifiers(boolean allowModifiers) {
      this.allowModifiers = allowModifiers;
   }

   public boolean isAllowKey() {
      return this.allowKey;
   }

   public void setAllowKey(boolean allowKey) {
      this.allowKey = allowKey;
   }

   public boolean isAllowMouse() {
      return this.allowMouse;
   }

   public void setAllowMouse(boolean allowMouse) {
      this.allowMouse = allowMouse;
   }

   public ModifierKeyCode getValue() {
      return this.value;
   }

   public void setValue(ModifierKeyCode value) {
      this.value = value;
   }

   @Override
   public Optional<ModifierKeyCode> getDefaultValue() {
      return Optional.ofNullable(this.defaultValue).map(Supplier::get).map(ModifierKeyCode::copy);
   }

   private Component getLocalizedName() {
      return this.value.getLocalizedName();
   }

   @Override
   public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
      super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
      Window window = Minecraft.getInstance().getWindow();
      this.resetButton.active = this.isEditable() && this.getDefaultValue().isPresent() && !this.getDefaultValue().get().equals(this.getValue());
      this.resetButton.setY(y);
      this.buttonWidget.active = this.isEditable();
      this.buttonWidget.setY(y);
      this.buttonWidget.setMessage(this.getLocalizedName());
      if (this.getConfigScreen().getFocusedBinding() == this) {
         this.buttonWidget
            .setMessage(
               Component.literal("> ")
                  .withStyle(ChatFormatting.WHITE)
                  .append(this.buttonWidget.getMessage().plainCopy().withStyle(ChatFormatting.YELLOW))
                  .append(Component.literal(" <").withStyle(ChatFormatting.WHITE))
            );
      }

      Component displayedFieldName = this.getDisplayedFieldName();
      if (Minecraft.getInstance().font.isBidirectional()) {
         graphics.drawString(
            Minecraft.getInstance().font,
            displayedFieldName.getVisualOrderText(),
            window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName),
            y + 6,
            16777215
         );
         this.resetButton.setX(x);
         this.buttonWidget.setX(x + this.resetButton.getWidth() + 2);
      } else {
         graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, this.getPreferredTextColor());
         this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
         this.buttonWidget.setX(x + entryWidth - 150);
      }

      this.buttonWidget.setWidth(150 - this.resetButton.getWidth() - 2);
      this.resetButton.render(graphics, mouseX, mouseY, delta);
      this.buttonWidget.render(graphics, mouseX, mouseY, delta);
   }

   public List<? extends GuiEventListener> children() {
      return this.widgets;
   }

   @Override
   public List<? extends NarratableEntry> narratables() {
      return this.widgets;
   }
}
