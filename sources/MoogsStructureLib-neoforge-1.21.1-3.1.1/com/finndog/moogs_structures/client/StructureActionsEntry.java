package com.finndog.moogs_structures.client;

import com.finndog.moogs_structures.config.MslConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

public class StructureActionsEntry extends TooltipListEntry<Object> {
   private static final int GAP = 2;
   private final Button previewButton;
   private final Button disableButton;
   private final String structureId;
   private boolean savedDisabled;
   private boolean pendingDisabled;

   public StructureActionsEntry(Component fieldName, String previewUrl, String structureId) {
      super(fieldName, () -> Optional.empty());
      this.structureId = structureId;
      this.savedDisabled = MslConfig.get().isDisabledForScreen(structureId);
      this.pendingDisabled = this.savedDisabled;
      this.previewButton = previewUrl != null ? ConfigButtons.preview(previewUrl) : null;
      this.disableButton = ConfigButtons.disable(this.savedDisabled, v -> this.pendingDisabled = v);
   }

   public void save() {
      if (this.pendingDisabled != this.savedDisabled) {
         MslConfig.get().setStructureDisabledAndSave(this.structureId, this.pendingDisabled);
         this.savedDisabled = this.pendingDisabled;
      }
   }

   public boolean isEdited() {
      return this.pendingDisabled != this.savedDisabled;
   }

   public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float delta) {
      super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, delta);
      Minecraft mc = Minecraft.getInstance();
      graphics.drawString(mc.font, this.getFieldName(), x, y + entryHeight / 2 - 9 / 2, this.getPreferredTextColor());
      int reserved = 62 + (this.previewButton != null ? 57 : 0);
      int stripX = x + entryWidth - reserved;
      if (this.previewButton != null) {
         this.previewButton.setX(stripX);
         this.previewButton.setY(y);
         this.previewButton.render(graphics, mouseX, mouseY, delta);
         stripX += 57;
      }

      this.disableButton.setX(stripX);
      this.disableButton.setY(y);
      this.disableButton.render(graphics, mouseX, mouseY, delta);
   }

   public Object getValue() {
      return null;
   }

   public Optional<Object> getDefaultValue() {
      return Optional.empty();
   }

   public List<? extends GuiEventListener> children() {
      List<GuiEventListener> list = new ArrayList<>();
      if (this.previewButton != null) {
         list.add(this.previewButton);
      }

      list.add(this.disableButton);
      return list;
   }

   public List<? extends NarratableEntry> narratables() {
      List<NarratableEntry> list = new ArrayList<>();
      if (this.previewButton != null) {
         list.add(this.previewButton);
      }

      list.add(this.disableButton);
      return list;
   }
}
