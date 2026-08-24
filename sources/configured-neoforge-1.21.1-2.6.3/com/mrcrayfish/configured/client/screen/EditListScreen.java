package com.mrcrayfish.configured.client.screen;

import com.google.common.collect.ImmutableList;
import com.mrcrayfish.configured.api.IConfigValue;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.screen.list.IListConfigValue;
import com.mrcrayfish.configured.client.screen.list.IListType;
import com.mrcrayfish.configured.client.screen.list.ListTypes;
import com.mrcrayfish.configured.client.screen.widget.ConfiguredButton;
import com.mrcrayfish.configured.client.screen.widget.IconButton;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

public class EditListScreen<T> extends Screen implements IEditing {
   private final Screen parent;
   private final IModConfig config;
   private final List<EditListScreen.StringHolder> initialValues = new ArrayList<>();
   private final List<EditListScreen.StringHolder> values = new ArrayList<>();
   private final IConfigValue<List<T>> holder;
   private final IListType<T> listType;
   private EditListScreen<T>.ObjectList list;

   public EditListScreen(Screen parent, IModConfig config, Component titleIn, IConfigValue<List<T>> holder) {
      super(titleIn);
      this.parent = parent;
      this.config = config;
      this.holder = holder;
      this.listType = ListTypes.getType(holder);
      this.initialValues.addAll(holder.get().stream().map(o -> new EditListScreen.StringHolder(this.listType.getStringParser().apply((T)o))).toList());
      this.values.addAll(this.initialValues);
   }

   protected void init() {
      this.list = new EditListScreen.ObjectList();
      this.addWidget(this.list);
      if (!this.config.isReadOnly()) {
         this.addRenderableWidget(
            new IconButton(
               this.width / 2 - 140,
               this.height - 29,
               0,
               44,
               90,
               Component.translatable("configured.gui.apply"),
               button -> {
                  List<T> newValues = this.values
                     .stream()
                     .map(EditListScreen.StringHolder::getValue)
                     .map(s -> this.listType.getValueParser().apply(s))
                     .collect(Collectors.toList());
                  this.holder.set(newValues);
                  this.minecraft.setScreen(this.parent);
               }
            )
         );
         this.addRenderableWidget(
            new IconButton(
               this.width / 2 - 45,
               this.height - 29,
               22,
               33,
               90,
               Component.translatable("configured.gui.add_value"),
               button -> {
                  String newValue = this.holder instanceof IListConfigValue<T> listValue ? listValue.createPropertyValue() : "";
                  this.minecraft
                     .setScreen(
                        new EditStringScreen(
                           this,
                           this.config,
                           Component.translatable("configured.gui.edit_value"),
                           newValue,
                           s -> {
                              T value = this.listType.getValueParser().apply(s);
                              if (value != null) {
                                 return this.holder.isValid(Collections.singletonList(value))
                                    ? Pair.of(true, CommonComponents.EMPTY)
                                    : Pair.of(false, this.holder.getValidationHint());
                              } else {
                                 return Pair.of(false, this.listType.getHint());
                              }
                           },
                           s -> {
                              EditListScreen.StringHolder holder = new EditListScreen.StringHolder(s);
                              this.values.add(holder);
                              this.list.addEntry(new EditListScreen.StringEntry(this.list, holder));
                           }
                        )
                     );
               }
            )
         );
      }

      boolean readOnly = this.config.isReadOnly();
      int cancelWidth = readOnly ? 150 : 90;
      int cancelOffset = readOnly ? -75 : 50;
      Component cancelLabel = (Component)(readOnly ? Component.translatable("configured.gui.close") : CommonComponents.GUI_CANCEL);
      this.addRenderableWidget(
         ScreenUtil.button(
            this.width / 2 + cancelOffset,
            this.height - 29,
            cancelWidth,
            20,
            cancelLabel,
            button -> {
               if (this.isModified()) {
                  ConfirmationScreen confirmScreen = new ActiveConfirmationScreen(
                     this, this.config, Component.translatable("configured.gui.list_changed"), ConfirmationScreen.Icon.WARNING, result -> {
                        if (!result) {
                           return true;
                        } else {
                           this.minecraft.setScreen(this.parent);
                           return false;
                        }
                     }
                  );
                  this.minecraft.setScreen(confirmScreen);
               } else {
                  this.minecraft.setScreen(this.parent);
               }
            }
         )
      );
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      super.render(graphics, mouseX, mouseY, partialTicks);
      this.list.render(graphics, mouseX, mouseY, partialTicks);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 14, 16777215);
   }

   @Override
   public IModConfig getActiveConfig() {
      return this.config;
   }

   public boolean isModified() {
      if (this.initialValues.size() != this.values.size()) {
         return true;
      } else {
         for (int i = 0; i < this.initialValues.size(); i++) {
            String s1 = this.initialValues.get(i).getValue();
            String s2 = this.values.get(i).getValue();
            if (!s1.equals(s2)) {
               return true;
            }
         }

         return false;
      }
   }

   public class ObjectList extends ContainerObjectSelectionList<EditListScreen<T>.StringEntry> {
      public ObjectList() {
         super(EditListScreen.this.minecraft, EditListScreen.this.width, EditListScreen.this.height - 36 - 36, 36, 24);
         EditListScreen.this.values.forEach(value -> this.addEntry(EditListScreen.this.new StringEntry(this, value)));
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 144;
      }

      public int getRowWidth() {
         return 260;
      }

      public int addEntry(EditListScreen<T>.StringEntry entry) {
         return super.addEntry(entry);
      }

      public boolean removeEntry(EditListScreen<T>.StringEntry entry) {
         return super.removeEntry(entry);
      }
   }

   public class StringEntry extends Entry<EditListScreen<T>.StringEntry> {
      private final EditListScreen.StringHolder holder;
      private final EditListScreen<T>.ObjectList list;
      private final ConfiguredButton editButton;
      private final ConfiguredButton deleteButton;

      public StringEntry(EditListScreen<T>.ObjectList list, EditListScreen.StringHolder holder) {
         this.list = list;
         this.holder = holder;
         this.editButton = new IconButton(
            0,
            0,
            1,
            22,
            20,
            CommonComponents.EMPTY,
            onPress -> EditListScreen.this.minecraft
               .setScreen(
                  new EditStringScreen(
                     EditListScreen.this,
                     EditListScreen.this.config,
                     Component.translatable("configured.gui.edit_value"),
                     this.holder.getValue(),
                     s -> {
                        T value = EditListScreen.this.listType.getValueParser().apply(s);
                        if (value != null) {
                           return EditListScreen.this.holder.isValid(Collections.singletonList(value))
                              ? Pair.of(true, CommonComponents.EMPTY)
                              : Pair.of(false, EditListScreen.this.holder.getValidationHint());
                        } else {
                           return Pair.of(false, EditListScreen.this.listType.getHint());
                        }
                     },
                     this.holder::setValue
                  )
               )
         );
         this.editButton.setTooltip(Tooltip.create(Component.translatable("configured.gui.edit")), btn -> btn.isActive() && btn.isHoveredOrFocused());
         this.editButton.active = !EditListScreen.this.config.isReadOnly();
         this.deleteButton = new IconButton(0, 0, 11, 0, onPress -> {
            EditListScreen.this.values.remove(this.holder);
            this.list.removeEntry(this);
         });
         this.deleteButton.setTooltip(Tooltip.create(Component.translatable("configured.gui.remove")), btn -> btn.isActive() && btn.isHoveredOrFocused());
         this.deleteButton.active = !EditListScreen.this.config.isReadOnly();
      }

      public void render(
         GuiGraphics graphics, int x, int top, int left, int width, int p_230432_6_, int mouseX, int mouseY, boolean selected, float partialTicks
      ) {
         if (x % 2 != 0) {
            graphics.fill(left, top, left + width, top + 24, 1426063360);
         }

         graphics.drawString(EditListScreen.this.minecraft.font, Component.literal(this.holder.getValue()), left + 5, top + 8, 16777215);
         this.editButton.visible = true;
         this.editButton.setX(left + width - 44);
         this.editButton.setY(top + 2);
         this.editButton.render(graphics, mouseX, mouseY, partialTicks);
         this.deleteButton.visible = true;
         this.deleteButton.setX(left + width - 22);
         this.deleteButton.setY(top + 2);
         this.deleteButton.render(graphics, mouseX, mouseY, partialTicks);
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.editButton, this.deleteButton);
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(new NarratableEntry() {
            public NarrationPriority narrationPriority() {
               return NarrationPriority.HOVERED;
            }

            public void updateNarration(NarrationElementOutput output) {
               output.add(NarratedElementType.TITLE, StringEntry.this.holder.getValue());
            }
         }, this.editButton, this.deleteButton);
      }
   }

   public static class StringHolder {
      private String value;

      public StringHolder(String value) {
         this.value = value;
      }

      public String getValue() {
         return this.value;
      }

      public void setValue(String value) {
         this.value = value;
      }
   }
}
