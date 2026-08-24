package com.teamresourceful.resourcefulconfig.client.components.options.types;

import com.teamresourceful.resourcefulconfig.api.annotations.ConfigOption;
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryData;
import com.teamresourceful.resourcefulconfig.api.types.options.Option;
import com.teamresourceful.resourcefulconfig.client.UIConstants;
import com.teamresourceful.resourcefulconfig.client.components.ModSprites;
import com.teamresourceful.resourcefulconfig.client.components.base.BaseWidget;
import com.teamresourceful.resourcefulconfig.client.components.options.misc.draggable.DraggableList;
import com.teamresourceful.resourcefulconfig.client.screens.base.ModalOverlay;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

public class DraggableListOptionWidget extends BaseWidget {
   private static final int WIDTH = 80;
   private static final int SIZE = 12;
   private static final int SPACING = 4;
   private static final int PADDING = 2;
   private final Component title;
   private final Enum<?>[] options;
   private final Set<Enum<?>> duplicatables;
   private final Supplier<List<Enum<?>>> getter;
   private final Consumer<List<Enum<?>>> setter;
   private final ConfigOption.Range range;

   public DraggableListOptionWidget(
      Component title, Enum<?>[] options, Set<Enum<?>> duplicatables, Supplier<List<Enum<?>>> getter, Consumer<List<Enum<?>>> setter, ConfigOption.Range range
   ) {
      super(80, 16);
      this.title = title;
      this.options = options;
      this.duplicatables = duplicatables;
      this.getter = getter;
      this.setter = setter;
      this.range = range;
   }

   public static DraggableListOptionWidget of(ResourcefulConfigValueEntry entry, EntryData data) {
      return new DraggableListOptionWidget(
         entry.options().title().toComponent(),
         (Enum<?>[])entry.objectType().getEnumConstants(),
         Set.of(data.getOrDefaultOption(Option.DRAGGABLE, new Enum[0])),
         () -> Arrays.asList((Enum<?>[])entry.getArray()),
         value -> {
            Enum<?>[] array = (Enum<?>[])Array.newInstance(entry.objectType(), value.size());

            for (int i = 0; i < value.size(); i++) {
               array[i] = value.get(i);
            }

            entry.setArray(array);
         },
         data.getOption(Option.RANGE)
      );
   }

   @Override
   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      graphics.blitSprite(ModSprites.ofButton(this.isHovered()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      int contentWidth = this.font.width(UIConstants.EDIT) + 4 + 12;
      graphics.blitSprite(ModSprites.EDIT, this.getX() + (this.getWidth() - contentWidth) / 2, this.getY() + 2, 12, 12);
      graphics.drawString(
         this.font, UIConstants.EDIT, this.getX() + (this.getWidth() - contentWidth) / 2 + 12 + 4, this.getY() + (this.getHeight() - 9) / 2 + 1, -329226
      );
   }

   public void onClick(double mouseX, double mouseY) {
      new DraggableListOptionWidget.DraggableListOverlay(this).open();
   }

   private static class DraggableListOverlay extends ModalOverlay {
      private final DraggableListOptionWidget widget;

      protected DraggableListOverlay(DraggableListOptionWidget widget) {
         this.widget = widget;
         this.title = UIConstants.EDIT_LIST;
      }

      @Override
      protected void init() {
         super.init();
         LinearLayout layout = LinearLayout.horizontal();
         StringWidget title = (StringWidget)layout.addChild(new StringWidget(this.contentWidth - 84, 16, Component.empty(), this.font).alignLeft());
         Runnable updateTitle = () -> {
            if (this.widget.range != null) {
               String count = "%d/%d".formatted(this.widget.getter.get().size(), (int)this.widget.range.max());
               title.setMessage(Component.empty().append(this.widget.title).append(" ").append(count));
            } else {
               title.setMessage(this.widget.title);
            }
         };
         updateTitle.run();
         Set<Enum<?>> options = new LinkedHashSet<>(Arrays.asList(this.widget.options));
         this.widget.getter.get().forEach(item -> {
            if (!this.widget.duplicatables.contains(item)) {
               options.remove(item);
            }
         });
         DropdownWidget dropdown = (DropdownWidget)layout.addChild(new DropdownWidget(options.toArray(new Enum[0]), () -> null, value -> {
            List<Enum<?>> listx = new ArrayList<>(this.widget.getter.get());
            listx.addFirst(value);
            this.widget.setter.accept(listx);
         }).setTitle(UIConstants.ADD_ITEM));
         dropdown.active = this.widget.range == null || this.widget.getter.get().size() < this.widget.range.max();
         layout.setPosition(this.left + 4, this.top + 1);
         layout.arrangeElements();
         layout.visitWidgets(x$0 -> {
            AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
         });
         int heading = layout.getHeight() + 4;
         DraggableList<Enum<?>> list = (DraggableList<Enum<?>>)this.addRenderableWidget(
            new DraggableList(this.left + 1, this.top + heading, this.contentWidth - 2, this.contentHeight - heading)
         );
         list.addAll(this.widget.getter.get());
         list.setOnUpdate(value -> {
            this.widget.setter.accept(value);
            list.setCanDelete(this.widget.range == null || this.widget.getter.get().size() > this.widget.range.min());
            dropdown.active = this.widget.range == null || this.widget.getter.get().size() < this.widget.range.max();
            updateTitle.run();
         });
         list.setCanDelete(this.widget.range == null || this.widget.getter.get().size() > this.widget.range.min());
      }

      @Override
      public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
         super.renderBackground(graphics, mouseX, mouseY, partialTicks);
         graphics.blitSprite(ModSprites.BUTTON, this.left, this.top + 20, this.contentWidth, this.contentHeight - 20);
      }
   }
}
