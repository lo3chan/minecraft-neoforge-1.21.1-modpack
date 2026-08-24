package snownee.jade.gui.config.value;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationThunk;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.gui.config.OptionsList;

public abstract class OptionValue<T> extends OptionsList.Entry {
   private static final Component SERVER_FEATURE = Component.literal("*").withStyle(ChatFormatting.GRAY);
   protected final Supplier<T> getter;
   protected final Consumer<T> setter;
   private final Component title;
   protected ResourceLocation id;
   public boolean serverFeature;
   protected T value;
   protected int indent;
   private int x;

   public OptionValue(String optionName, Supplier<T> getter, Consumer<T> setter) {
      this.title = makeTitle(optionName);
      this.getter = getter;
      this.setter = setter;
      this.addMessage(this.title.getString());
      this.addMessageKey(optionName);
      String key = makeKey(optionName + "_desc");
      if (I18n.exists(key)) {
         this.appendDescription(Component.translatable(key));
      }
   }

   @Override
   public final void render(
      GuiGraphics guiGraphics, int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime
   ) {
      AbstractWidget widget = this.getFirstWidget();
      Component title0 = (Component)(widget.active
         ? this.title
         : this.title.copy().withStyle(new ChatFormatting[]{ChatFormatting.STRIKETHROUGH, ChatFormatting.GRAY}));
      int left = rowLeft + this.indent + 10;
      int top = rowTop + height / 2 - 9 / 2;
      guiGraphics.drawString(this.client.font, title0, left, top, 16777215);
      if (this.serverFeature) {
         guiGraphics.drawString(this.client.font, SERVER_FEATURE, left + this.getTextWidth() + 1, top, 16777215);
      }

      super.render(guiGraphics, index, rowTop, rowLeft, width, height, mouseX, mouseY, hovered, deltaTime);
      this.x = rowLeft;
   }

   public void save() {
      this.setter.accept(this.value);
   }

   public Component getTitle() {
      return this.title;
   }

   public void appendDescription(Component description) {
      if (this.description.isEmpty()) {
         this.description = Lists.newArrayList(new Component[]{description});
      } else {
         this.description.add(description);
      }

      this.addMessage(description.getString());
   }

   public int getX() {
      return this.x;
   }

   @Override
   public int getTextX(int width) {
      return this.getX() + this.indent + 10;
   }

   @Override
   public int getTextWidth() {
      return this.client.font.width(this.getTitle());
   }

   public void updateNarration(NarrationElementOutput output) {
      super.updateNarration(output);
      if (!this.description.isEmpty()) {
         output.add(NarratedElementType.HINT, NarrationThunk.from(this.description));
      }
   }

   public boolean isValidValue() {
      return true;
   }

   @Override
   public OptionsList.Entry parent(OptionsList.Entry parent) {
      super.parent(parent);
      if (parent instanceof OptionValue) {
         this.indent = ((OptionValue)parent).indent + 12;
      }

      return this;
   }

   public abstract void setValue(T var1);

   public abstract void updateValue();

   public void setId(ResourceLocation id) {
      this.id = id;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public List<Component> getDescriptionOnShift() {
      return this.id == null ? List.of() : List.of(Component.literal(this.id.toString()).withStyle(ChatFormatting.GRAY));
   }
}
