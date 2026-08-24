package net.cibernet.alchemancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.ArcaneProperty;
import net.cibernet.alchemancy.properties.BurningProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.ShockDamageProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.PropertyFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

public class InfusionCodexEntryScreen extends Screen {
   private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
   private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");
   private final Holder<Property> property;
   private final CodexEntryReloadListenener.CodexEntry entry;
   private final ItemStack[] dormantItems;
   private final int undiscoveredItems;
   private final boolean unobtainable;
   protected final Screen lastScreen;
   private HeaderAndFooterLayout layout;

   protected InfusionCodexEntryScreen(Holder<Property> property, CodexEntryReloadListenener.CodexEntry entry, Screen lastScreen) {
      super(((Property)property.value()).getName());
      this.property = property;
      this.entry = entry;
      this.lastScreen = lastScreen;
      AtomicInteger undiscovered = new AtomicInteger();
      Ingredient ingredient = Ingredient.of(((Property)property.value()).getDormantPropertyTag());
      this.dormantItems = ingredient.isEmpty()
         ? new ItemStack[0]
         : Arrays.stream(ingredient.getItems()).filter(stack -> !stack.is(Items.BARRIER)).filter(stack -> {
            if (InfusionCodexSaveData.isItemDiscovered(stack)) {
               return true;
            } else {
               undiscovered.getAndIncrement();
               return false;
            }
         }).toArray(ItemStack[]::new);
      this.undiscoveredItems = undiscovered.get();
      InfusionCodexSaveData.read(property);
      this.unobtainable = property.is(AlchemancyTags.Properties.CODEX_UNOBTAINABLE) && this.dormantItems.length == 0;
   }

   protected void init() {
      super.init();
      this.layout = new HeaderAndFooterLayout(this, 40, 32);
      LinearLayout footer = ((LinearLayout)this.layout.addToFooter(LinearLayout.vertical())).spacing(5);
      footer.defaultCellSetting().alignHorizontallyCenter();
      footer.addChild(Button.builder(CommonComponents.GUI_DONE, p_329727_ -> this.onClose()).width(200).build());
      LinearLayout header = ((LinearLayout)this.layout.addToHeader(LinearLayout.vertical())).spacing(5);
      header.addChild(new InfusionCodexEntryScreen.TitleWidget(this.width, 16, this.font, this.property).alignCenter());
      header.addChild(
         new StringWidget(this.width, 9, this.entry.flavor().copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY), this.font).alignCenter()
      );
      this.layout
         .addToContents(
            new InfusionCodexEntryScreen.EntryBox(0, 0, this.width, this.height - this.layout.getHeaderHeight() - this.layout.getFooterHeight(), 32, 8)
         );
      this.layout.visitWidgets(x$0 -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
      });
      this.layout.arrangeElements();
   }

   private MutableComponent translated(String key) {
      return Component.translatable("infusion_codex.%s.%s".formatted(this.property.getRegisteredName(), key));
   }

   private boolean hasTranslation(String key) {
      return I18n.exists("infusion_codex.%s.%s".formatted(this.property.getRegisteredName(), key));
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   class EntryBox extends AbstractWidget {
      final int xPadding;
      final int yPadding;
      private static final int itemPadding = 2;
      private static final int itemSize = 20;
      private static final int SCROLLBAR_WIDTH = 6;
      private float textYPointer;
      private float entryHeight;
      private float scrollAmount;
      private boolean scrolling;
      private static final String FORMAT_REGEX = "\\{([^\\}]*)\\}";

      public EntryBox(int x, int y, int width, int height, int xPadding, int yPadding) {
         super(x, y, width, height, Component.empty());
         this.xPadding = xPadding;
         this.yPadding = yPadding;
      }

      public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
         float oldAmount = this.scrollAmount;
         this.scrollAmount = Mth.clamp(this.scrollAmount - (float)(scrollY * 9.0), 0.0F, this.getMaxScroll());
         return oldAmount != this.scrollAmount;
      }

      public float getMaxScroll() {
         return Math.max(0.0F, this.entryHeight - this.getHeight());
      }

      public boolean scrollbarVisible() {
         return this.getMaxScroll() > 0.0F;
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         this.scrolling = button == 0 && mouseX >= this.getScrollbarPosition() && mouseX < this.getScrollbarPosition() + 6;
         return this.scrolling;
      }

      public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
         if (button == 0 && this.scrolling) {
            if (mouseY < this.getY()) {
               this.scrollAmount = 0.0F;
            } else if (mouseY > this.getBottom()) {
               this.scrollAmount = this.getMaxScroll();
            } else {
               float d0 = Math.max(1.0F, this.getMaxScroll());
               int i = this.height;
               int j = Mth.clamp((int)(i * i / (this.entryHeight - this.getHeight())), 32, i - 8);
               float d1 = Math.max(1.0F, d0 / (i - j));
               this.scrollAmount = (float)Math.clamp(this.scrollAmount + dragY * d1, 0.0, this.getMaxScroll());
            }

            return true;
         } else {
            return false;
         }
      }

      protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         this.renderListBackground(guiGraphics);
         guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
         this.textYPointer = this.yPadding - this.scrollAmount;
         if (InfusionCodexEntryScreen.this.unobtainable) {
            this.renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex.unobtainable"), 1.0F, 11141120);
            this.textYPointer += 10.0F;
         } else if (InfusionCodexEntryScreen.this.property.is(AlchemancyTags.Properties.DISABLED)) {
            this.renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex.disabled"), 1.0F, 11141120);
            this.textYPointer += 10.0F;
         }

         if (InfusionCodexEntryScreen.this.property.is(AlchemancyTags.Properties.SLOTLESS)) {
            this.renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex.slotless"), 1.0F, 16755200);
            this.textYPointer += 10.0F;
         }

         for (PropertyFunction function : InfusionCodexEntryScreen.this.entry.functions()) {
            this.renderFunctionParagraph(guiGraphics, function.localizationKey);
         }

         InfusionCodexEntryScreen.EntryBox.TooltipRendering tooltip = null;
         if (InfusionCodexEntryScreen.this.dormantItems.length > 0 || InfusionCodexEntryScreen.this.undiscoveredItems > 0) {
            this.renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex.dormant_properties"), 1.25F, 5592575);
            int itemsPerRow = (this.width - this.xPadding * 2) / 20;
            int itemCount = InfusionCodexEntryScreen.this.dormantItems.length;

            for (int i = 0; i < itemCount; i++) {
               ItemStack stack = InfusionCodexEntryScreen.this.dormantItems[i];
               int xx = this.getX() + this.xPadding + i % itemsPerRow * 20;
               int yy = this.getY() + (int)this.textYPointer + i / itemsPerRow * 20;
               guiGraphics.renderFakeItem(stack, xx, yy);
               if (mouseX >= xx - 2 && mouseX < xx - 2 + 20 && mouseY >= yy - 2 && mouseY < yy - 2 + 20) {
                  tooltip = new InfusionCodexEntryScreen.EntryBox.ItemTooltip(stack);
               }
            }

            if (InfusionCodexEntryScreen.this.undiscoveredItems > 0) {
               int xx = this.getX() + this.xPadding + itemCount % itemsPerRow * 20;
               int yy = this.getY() + (int)this.textYPointer + itemCount / itemsPerRow * 20;
               guiGraphics.drawString(
                  InfusionCodexEntryScreen.this.font,
                  Component.translatable("screen.infusion_codex.undiscovered_items", new Object[]{InfusionCodexEntryScreen.this.undiscoveredItems}),
                  xx,
                  yy + 10 - 9 / 2,
                  16777215
               );
               itemCount++;
               if (mouseX >= xx - 2 && mouseX < xx - 2 + 20 && mouseY >= yy - 2 && mouseY < yy - 2 + 20) {
                  tooltip = new InfusionCodexEntryScreen.EntryBox.TextTooltip(
                     List.of(
                        Component.translatable(
                           "screen.infusion_codex.undiscovered_items.tooltip", new Object[]{InfusionCodexEntryScreen.this.undiscoveredItems}
                        )
                     )
                  );
               }
            }

            this.textYPointer += (itemCount - 1) / itemsPerRow * 20 + 10;
         }

         if (InfusionCodexEntryScreen.this.unobtainable) {
            List<Holder<Item>> innates = InfusionCodexEntryScreen.this.entry.innates();
            if (!innates.isEmpty()) {
               this.renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex.innate_properties"), 1.25F, 5592575);
               int itemsPerRow = (this.width - this.xPadding * 2) / 20;
               int itemCount = innates.size();

               for (int ix = 0; ix < itemCount; ix++) {
                  ItemStack stack = ((Item)innates.get(ix).value()).getDefaultInstance();
                  int xx = this.getX() + this.xPadding + ix % itemsPerRow * 20;
                  int yy = this.getY() + (int)this.textYPointer + ix / itemsPerRow * 20;
                  guiGraphics.renderFakeItem(stack, xx, yy);
                  if (mouseX >= xx - 2 && mouseX < xx - 2 + 20 && mouseY >= yy - 2 && mouseY < yy - 2 + 20) {
                     tooltip = new InfusionCodexEntryScreen.EntryBox.ItemTooltip(stack);
                  }
               }

               this.textYPointer += (itemCount - 1) / itemsPerRow * 20 + 10;
            }
         }

         guiGraphics.disableScissor();
         this.renderListSeparators(guiGraphics);
         if (this.scrollbarVisible()) {
            int l = this.getScrollbarPosition();
            int i1 = (int)(this.height * this.height / this.getMaxScroll());
            i1 = Mth.clamp(i1, 32, this.height - 8);
            float k = (int)this.scrollAmount * (this.height - i1) / this.getMaxScroll() + this.getY();
            if (k < this.getY()) {
               k = this.getY();
            }

            RenderSystem.enableBlend();
            guiGraphics.blitSprite(InfusionCodexEntryScreen.SCROLLER_BACKGROUND_SPRITE, l, this.getY(), 6, this.getHeight());
            guiGraphics.blitSprite(InfusionCodexEntryScreen.SCROLLER_SPRITE, l, (int)k, 6, i1);
            RenderSystem.disableBlend();
         }

         if (tooltip != null && mouseX >= this.getX() && mouseX <= this.getRight() && mouseY >= this.getY() && mouseY <= this.getBottom()) {
            tooltip.apply(guiGraphics, InfusionCodexEntryScreen.this.font, mouseX, mouseY);
         }

         this.entryHeight = this.textYPointer + 10.0F + this.scrollAmount;
      }

      private int getScrollbarPosition() {
         return this.getWidth() - this.xPadding;
      }

      private void renderFunctionParagraph(GuiGraphics guiGraphics, String functionKey) {
         this.renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex." + functionKey), 1.25F, 5592575);
         this.renderTextLine(guiGraphics, InfusionCodexEntryScreen.this.translated(functionKey), 1.0F, 16777215);
         this.textYPointer += 10.0F;
      }

      public Component processFormatting(String formatType, String value) {
         String var3 = formatType.toLowerCase();

         return (Component)(switch (var3) {
            case "property" -> {
               Optional<Property> property = CommonUtils.registryAccessStatic()
                  .registryOrThrow(AlchemancyProperties.REGISTRY_KEY)
                  .getOptional(ResourceLocation.parse(value));
               yield property.<Component>map(Property::getName).orElse(Component.literal(value).withColor(16711680));
            }
            case "enchantment" -> {
               Optional<Enchantment> property = CommonUtils.registryAccessStatic()
                  .registryOrThrow(Registries.ENCHANTMENT)
                  .getOptional(ResourceLocation.parse(value));
               yield property.<MutableComponent>map(block -> block.description().copy().withStyle(ChatFormatting.LIGHT_PURPLE))
                  .orElse(Component.literal(value).withColor(16711680));
            }
            case "function" -> Component.translatable("screen.infusion_codex." + value).withStyle(ChatFormatting.BLUE);
            case "shock" -> Component.literal(value).withColor(((ShockDamageProperty)AlchemancyProperties.SHOCKING.get()).getColor(ItemStack.EMPTY));
            case "arcane" -> Component.literal(value).withColor(((ArcaneProperty)AlchemancyProperties.ARCANE.get()).getColor(ItemStack.EMPTY));
            case "fire" -> Component.literal(value).withColor(((BurningProperty)AlchemancyProperties.BURNING.get()).getColor(ItemStack.EMPTY));
            case "item" -> Component.literal(value).withStyle(ChatFormatting.GREEN);
            case "attribute" -> Component.literal(value).withStyle(ChatFormatting.DARK_AQUA);
            case "system" -> Component.literal(value).withStyle(ChatFormatting.AQUA);
            case "nether" -> Component.literal(value).withStyle(ChatFormatting.RED);
            case "end" -> Component.literal(value).withStyle(ChatFormatting.DARK_PURPLE);
            case "activate" -> Component.literal(value).withColor(16737126);
            case "hint" -> Component.literal(value).withColor(65535);
            case "property_list" -> {
               MutableComponent component = Component.empty();
               Iterable<Holder<Property>> properties = ((Registry)AlchemancyProperties.REGISTRY.getRegistry().get())
                  .getTagOrEmpty(TagKey.create(AlchemancyProperties.REGISTRY_KEY, ResourceLocation.parse(value)));
               int count = 0;

               for (Holder<Property> ignored : properties) {
                  count++;
               }

               int i = 0;

               for (Holder<Property> propertyHolder : properties) {
                  if (count == 1) {
                     component = component.append(((Property)propertyHolder.value()).getName());
                  } else {
                     i++;
                     component = component.append(
                        Component.translatable(
                           "screen.infusion_codex.inline_list_entry" + (i == count ? ".last" : ""), new Object[]{((Property)propertyHolder.value()).getName()}
                        )
                     );
                  }
               }

               yield component;
            }
            case "property_list_or" -> {
               MutableComponent component = Component.empty();
               Iterable<Holder<Property>> properties = ((Registry)AlchemancyProperties.REGISTRY.getRegistry().get())
                  .getTagOrEmpty(TagKey.create(AlchemancyProperties.REGISTRY_KEY, ResourceLocation.parse(value)));
               int count = 0;

               for (Holder<Property> ignored : properties) {
                  count++;
               }

               int i = 0;

               for (Holder<Property> propertyHolder : properties) {
                  if (count == 1) {
                     component = component.append(((Property)propertyHolder.value()).getName());
                  } else {
                     i++;
                     component = component.append(
                        Component.translatable(
                           "screen.infusion_codex.inline_list_entry" + (i == count ? ".last_or" : ""),
                           new Object[]{((Property)propertyHolder.value()).getName()}
                        )
                     );
                  }
               }

               yield component;
            }
            default -> Component.literal(value);
         });
      }

      public void renderTextLine(GuiGraphics guiGraphics, Component text, float scale, int color) {
         PoseStack poseStack = guiGraphics.pose();
         poseStack.pushPose();
         if (scale != 1.0F) {
            poseStack.scale(scale, scale, 1.0F);
         }

         ArrayList<Component> things = new ArrayList<>();
         String str = text.getString();
         str = str.replace("%s", "");
         str = Pattern.compile("\\{([^\\}]*)\\}").matcher(str).replaceAll(matchResult -> {
            String found = matchResult.group();
            found = found.substring(1, found.length() - 1);
            String[] params = found.split(" ", 2);
            things.add(this.processFormatting(params.length < 2 ? "" : params[0], params[params.length - 1]));
            return "%s";
         });
         MutableComponent newText = Component.empty();
         int i = 0;

         for (String s : str.split("%s")) {
            newText = newText.append(s);
            if (i < things.size()) {
               newText = newText.append(things.get(i));
            }

            i++;
         }

         for (FormattedCharSequence t : InfusionCodexEntryScreen.this.font.split(newText, this.width - this.xPadding * 2)) {
            guiGraphics.drawString(
               InfusionCodexEntryScreen.this.font, t, (this.getX() + this.xPadding) / scale, (this.getY() + this.textYPointer) / scale, color, true
            );
            this.textYPointer += 9.0F * scale;
         }

         this.textYPointer += 9.0F * 0.5F;
         poseStack.popPose();
      }

      protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
      }

      protected void renderListBackground(GuiGraphics guiGraphics) {
         RenderSystem.enableBlend();
         ResourceLocation resourcelocation = InfusionCodexEntryScreen.INWORLD_MENU_LIST_BACKGROUND;
         guiGraphics.blit(resourcelocation, this.getX(), this.getY(), this.getRight(), this.getBottom(), this.getWidth(), this.getHeight(), 32, 32);
         RenderSystem.disableBlend();
      }

      protected void renderListSeparators(GuiGraphics guiGraphics) {
         RenderSystem.enableBlend();
         guiGraphics.blit(Screen.INWORLD_HEADER_SEPARATOR, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
         guiGraphics.blit(Screen.INWORLD_FOOTER_SEPARATOR, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
         RenderSystem.disableBlend();
      }

      record ItemTooltip(ItemStack stack) implements InfusionCodexEntryScreen.EntryBox.TooltipRendering {
         @Override
         public void apply(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
            guiGraphics.renderTooltip(font, this.stack(), mouseX, mouseY);
         }
      }

      record TextTooltip(List<Component> lines) implements InfusionCodexEntryScreen.EntryBox.TooltipRendering {
         @Override
         public void apply(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
            guiGraphics.renderTooltip(font, this.lines().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
         }
      }

      interface TooltipRendering {
         void apply(GuiGraphics var1, Font var2, int var3, int var4);
      }
   }

   private static class TitleWidget extends StringWidget {
      private final ItemStack stack;
      private final Holder<Property> propertyHolder;
      private final float alignX = 0.5F;

      public TitleWidget(int width, int height, Font font, Holder<Property> propertyHolder) {
         super(width, height, Component.empty(), font);
         this.stack = InfusedPropertiesHelper.createPropertyCapsule(propertyHolder);
         this.propertyHolder = propertyHolder;
      }

      public Component getMessage() {
         return ((Property)this.propertyHolder.value()).getName(this.stack);
      }

      public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         PoseStack poseStack = guiGraphics.pose();
         poseStack.pushPose();
         int scale = 2;
         int yOff = 0;
         poseStack.scale(scale, scale, 1.0F);
         this.setX(this.getX() / scale);
         this.setY(this.getY() / scale + yOff);
         this.setWidth(this.getWidth() / scale);
         this.setHeight(this.getHeight() / scale);
         super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
         this.setX(this.getX() * scale);
         this.setY((this.getY() - yOff) * scale);
         this.setWidth(this.getWidth() * scale);
         this.setHeight(this.getHeight() * scale);
         poseStack.popPose();
         Component component = this.getMessage();
         Font font = this.getFont();
         int i = this.getWidth();
         int j = font.width(component) * scale;
         int k = this.getX() + Math.round(0.5F * (i - j));
         int l = this.getY() + (this.getHeight() - 9) / 2;
         guiGraphics.renderFakeItem(this.stack, k - 20, l - 4 + yOff * scale);
         guiGraphics.renderFakeItem(this.stack, k + j + 4, l - 4 + yOff * scale);
      }
   }
}
