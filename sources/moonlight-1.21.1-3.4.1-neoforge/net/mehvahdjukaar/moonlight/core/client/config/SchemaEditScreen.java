package net.mehvahdjukaar.moonlight.core.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.mehvahdjukaar.codecui.SchemaCodec;
import net.mehvahdjukaar.moonlight.api.client.gui.ConfigEditSession;
import net.mehvahdjukaar.moonlight.api.client.gui.GuiHelper;
import net.mehvahdjukaar.moonlight.api.client.gui.misc.ConfigGuiColors;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigCategory;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigNode;
import net.mehvahdjukaar.moonlight.api.platform.configs.options.ConfigOption;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class SchemaEditScreen extends ConfigPageScreen {
   private final SchemaEditScreen.State state;
   private final ConfigCategory category;
   @Nullable
   private final SchemaEditScreen parentPage;
   @Nullable
   private Button addButton;
   @Nullable
   private Component error;

   public static <T> Screen create(ConfigOption.SchemaValue<T> option, ConfigEditSession outerSession, Runnable onChange) {
      Screen parent = Minecraft.getInstance().screen;
      SchemaCodec<T> codec = option.codec;
      T current = outerSession.current(option);
      JsonElement currentJson = encode(codec, current);

      JsonElement defaultJson;
      try {
         defaultJson = encode(codec, option.defaultValue());
      } catch (Exception var11) {
         defaultJson = currentJson;
      }

      SchemaForm form = SchemaForm.build(option.title(), codec.schema(), currentJson, defaultJson);
      Consumer<Object> onDone = decoded -> {
         outerSession.put(option, decoded);
         onChange.run();
      };
      SchemaEditScreen.State state = new SchemaEditScreen.State(ConfigEditSession.scratch(parent), form.reader, codec, onDone);
      return new SchemaEditScreen(form.root, null, state, option.title());
   }

   private SchemaEditScreen(ConfigCategory category, @Nullable SchemaEditScreen parentPage, SchemaEditScreen.State state, Component title) {
      super(title);
      this.category = category;
      this.parentPage = parentPage;
      this.state = state;
   }

   private static <T> JsonElement encode(Codec<T> codec, @Nullable T value) {
      return (JsonElement)(value == null ? new JsonObject() : (JsonElement)codec.encodeStart(JsonOps.INSTANCE, value).result().orElseGet(JsonObject::new));
   }

   private boolean isRoot() {
      return this.parentPage == null;
   }

   @Override
   public ConfigEditSession session() {
      return this.state.session;
   }

   @Override
   public void openCategory(ConfigCategory cat) {
      this.minecraft.setScreen(new SchemaEditScreen(cat, this, this.state, cat.title()));
   }

   @Override
   public void onValueEdited() {
      this.error = null;
   }

   protected void init() {
      this.overlay.clear();
      SchemaForm.ListCategory listCategory = this.listCategory();
      int footer = listCategory != null ? 60 : 36;
      this.list = new ConfigRowList(this.minecraft, this.width, this.height - 44 - footer, 44, 24);
      this.populate();
      this.addRenderableWidget(this.list);
      int y = this.height - 28;
      int cx = this.width / 2;
      if (listCategory != null) {
         Component label = Component.literal("+ ")
            .withStyle(ChatFormatting.AQUA)
            .append(Component.translatable("gui.moonlight.config.list_add").withStyle(ChatFormatting.RESET));
         this.addButton = Button.builder(label, b -> this.addEntry(listCategory)).bounds(cx - 100, y - 24, 200, 20).build();
         this.addButton.active = listCategory.canAdd();
         this.addRenderableWidget(this.addButton);
      }

      if (this.isRoot()) {
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.commit()).bounds(cx - 100, y, 96, 20).build());
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(cx + 4, y, 96, 20).build());
      } else {
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> this.onClose()).bounds(cx - 50, y, 100, 20).build());
      }
   }

   @Nullable
   private SchemaForm.ListCategory listCategory() {
      return this.category instanceof SchemaForm.ListCategory lc ? lc : null;
   }

   @Override
   protected void populate() {
      SchemaForm.ListCategory listCategory = this.listCategory();
      List<ConfigListRow> rows = new ArrayList<>();
      List<ConfigNode> entries = this.category.entries();

      for (int i = 0; i < entries.size(); i++) {
         ConfigNode e = entries.get(i);
         ConfigListRow row;
         if (e instanceof ConfigCategory cat) {
            row = new CategoryRow(this, cat);
         } else {
            if (!(e instanceof ConfigOption<?> v)) {
               continue;
            }

            row = new OptionRow(this, v);
         }

         if (listCategory != null) {
            int index = i;
            rows.add(new ListEntryRow(row, listCategory.canRemove(), () -> this.removeEntry(listCategory, index)));
         } else {
            rows.add(row);
            if (e instanceof ConfigOption<?> v) {
               this.addDescriptionRows(rows, v);
            }
         }
      }

      this.list.setRows(rows);
   }

   private void addEntry(SchemaForm.ListCategory cat) {
      List<JsonElement> values = cat.snapshot(this.state.session);
      values.add(cat.newEntry());
      this.rebuild(cat, values);
      this.list.setScrollAmount(this.list.getMaxScroll());
   }

   private void removeEntry(SchemaForm.ListCategory cat, int index) {
      List<JsonElement> values = cat.snapshot(this.state.session);
      if (index < values.size()) {
         values.remove(index);
         this.rebuild(cat, values);
      }
   }

   private void rebuild(SchemaForm.ListCategory cat, List<JsonElement> values) {
      double scroll = this.list.getScrollAmount();
      cat.setEntries(values);
      this.overlay.clear();
      this.populate();
      this.list.setScrollAmount(scroll);
      if (this.addButton != null) {
         this.addButton.active = cat.canAdd();
      }

      this.onValueEdited();
   }

   private void commit() {
      JsonElement json = this.state.reader.read(this.state.session);
      DataResult<?> result = this.state.codec.parse(JsonOps.INSTANCE, json);
      Optional<?> value = result.result();
      if (value.isPresent()) {
         this.state.onDone.accept(value.get());
         this.minecraft.setScreen(this.state.session.returnScreen());
      } else {
         this.error = Component.translatable("gui.moonlight.config.schema_invalid", new Object[]{result.error().<String>map(Error::message).orElse("")});
      }
   }

   public void onClose() {
      this.minecraft.setScreen((Screen)(this.isRoot() ? this.state.session.returnScreen() : this.parentPage));
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      GuiHelper.renderHeaderBar(graphics, this.font, this.title, this.width, 44);
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      if (!this.renderOverlayOrTooltip(graphics, mouseX, mouseY)) {
         if (this.error != null) {
            int y = this.height - (this.listCategory() != null ? 66 : 42);
            graphics.drawCenteredString(this.font, this.error, this.width / 2, y, ConfigGuiColors.ERROR);
         }
      }
   }

   private record State(ConfigEditSession session, SchemaForm.Reader reader, Codec<?> codec, Consumer<Object> onDone) {
   }
}
