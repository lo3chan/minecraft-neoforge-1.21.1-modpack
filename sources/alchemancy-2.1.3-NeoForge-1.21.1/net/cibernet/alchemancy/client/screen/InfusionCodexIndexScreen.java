package net.cibernet.alchemancy.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.Map;
import net.cibernet.alchemancy.AlchemancyConfig;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.special.InfusionCodexProperty;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.SortOrder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class InfusionCodexIndexScreen extends Screen {
   private static final ResourceLocation NEW_ENTRY_ICON = ResourceLocation.fromNamespaceAndPath("alchemancy", "infusion_codex/new_entry_icon");
   private InfusionCodexIndexScreen.PropertyList propertyList;
   private EditBox searchBar;
   private HeaderAndFooterLayout layout;
   private final ItemStack inspectedItem;
   private final Screen previousScreen;
   private SortOrder sortOrder;
   private int unlockedEntryCount = 0;
   private ObjectArrayList<java.util.Map.Entry<Holder<Property>, CodexEntryReloadListenener.CodexEntry>> entries;
   private Component tooltip = null;

   public InfusionCodexIndexScreen(Component title) {
      super(title);
      this.inspectedItem = ItemStack.EMPTY;
      this.previousScreen = Minecraft.getInstance().screen;
      this.sortOrder = InfusionCodexSaveData.getSortOrder();
   }

   public InfusionCodexIndexScreen(ItemStack inspectedItem) {
      super(inspectedItem.getDisplayName());
      this.inspectedItem = inspectedItem;
      this.previousScreen = Minecraft.getInstance().screen;
      this.sortOrder = InfusionCodexSaveData.getSortOrder();
   }

   public void onClose() {
      this.minecraft.setScreen(this.previousScreen);
   }

   protected void init() {
      this.generateEntries();
      this.layout = new HeaderAndFooterLayout(this, 48, 32);
      LinearLayout footer = ((LinearLayout)this.layout.addToFooter(LinearLayout.vertical())).spacing(5);
      footer.defaultCellSetting().alignHorizontallyCenter();
      footer.addChild(Button.builder(CommonComponents.GUI_DONE, p_329727_ -> this.onClose()).width(200).build());
      LinearLayout header = ((LinearLayout)this.layout.addToHeader(LinearLayout.vertical())).spacing(2);
      if (!this.inspectedItem.isEmpty()) {
         header.addChild(
            new StringWidget(200, 9, Component.translatable("screen.infusion_codex.inspecting").withStyle(ChatFormatting.GRAY), this.font).alignCenter()
         );
      }

      MutableComponent title = Component.empty().append(this.title);
      if (this.inspectedItem.isEmpty() && !InfusionCodexSaveData.bypassesUnlocks()) {
         title = title.append(
            Component.translatable("screen.infusion_codex.unlocked_counter", new Object[]{this.unlockedEntryCount, this.entries.size()})
               .withStyle(this.unlockedEntryCount == this.entries.size() ? ChatFormatting.GOLD : ChatFormatting.WHITE)
         );
      }

      header.addChild(new StringWidget(200, !this.inspectedItem.isEmpty() ? 18 : 9, title, this.font).alignCenter());
      LinearLayout searchDiv = (LinearLayout)header.addChild(LinearLayout.horizontal());
      if (this.searchBar == null) {
         this.searchBar = new EditBox(this.font, 200, 16, Component.translatable("narrator.infusion_codex.search_bar")) {
            public boolean charTyped(char codePoint, int modifiers) {
               if (super.charTyped(codePoint, modifiers)) {
                  InfusionCodexIndexScreen.this.updatePropertyList();
                  return true;
               } else {
                  return false;
               }
            }

            public void deleteChars(int num) {
               super.deleteChars(num);
               if (num != 0) {
                  InfusionCodexIndexScreen.this.updatePropertyList();
               }
            }
         };
         this.searchBar.setHint(Component.translatable("screen.infusion_codex.search_bar").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
      }

      searchDiv.addChild(this.searchBar);
      searchDiv.addChild(Button.builder(this.sortOrder.buttonLabel, button -> {
         this.sortOrder = SortOrder.values()[(this.sortOrder.ordinal() + 1) % SortOrder.values().length];
         InfusionCodexSaveData.setSortOrder(this.sortOrder);
         button.setMessage(this.sortOrder.buttonLabel);
         button.setTooltip(this.sortOrder.tooltip);
         this.updatePropertyList();
      }).tooltip(this.sortOrder.tooltip).size(24, 16).build());
      this.layout.visitWidgets(x$0 -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
      });
      this.updatePropertyList();
      this.layout.arrangeElements();
   }

   private void generateEntries() {
      Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry> entrySet = (Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry>)(this.inspectedItem
            .isEmpty()
         ? CodexEntryReloadListenener.getEntries()
         : InfusionCodexProperty.inspectItem(this.minecraft.player, this.inspectedItem));
      ObjectArrayList<java.util.Map.Entry<Holder<Property>, CodexEntryReloadListenener.CodexEntry>> objectarraylist = new ObjectArrayList(entrySet.entrySet());
      objectarraylist.removeIf(
         entry -> !InfusionCodexSaveData.isUnlocked((Holder<Property>)entry.getKey()) && ((Holder)entry.getKey()).is(AlchemancyTags.Properties.CODEX_HIDDEN)
      );
      this.unlockedEntryCount = (int)objectarraylist.stream().filter(entry -> InfusionCodexSaveData.isUnlocked((Holder<Property>)entry.getKey())).count();
      this.entries = objectarraylist;
      this.sortEntries();
   }

   private void sortEntries() {
      this.entries.sort(Comparator.comparing(entry -> ((Holder)entry.getKey()).getKey()));
      this.entries.sort((o1, o2) -> this.sortOrder.sortFunction.compare((Holder<Property>)o1.getKey(), (Holder<Property>)o2.getKey()));
      this.entries.sort(Comparator.comparing(entry -> !InfusionCodexSaveData.isUnlocked((Holder<Property>)entry.getKey())));
   }

   private void updatePropertyList() {
      this.sortEntries();
      double scroll = this.propertyList == null ? 0.0 : this.propertyList.getScrollAmount();
      if (this.propertyList != null) {
         this.removeWidget(this.propertyList);
      }

      this.propertyList = new InfusionCodexIndexScreen.PropertyList(this.minecraft);
      this.propertyList.setScrollAmount(scroll);
      this.addRenderableWidget(this.propertyList);
      this.propertyList.updateSize(this.width, this.layout);
      this.layout.arrangeElements();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.render(guiGraphics, mouseX, mouseY, partialTick);
      if (this.tooltip != null) {
         guiGraphics.renderTooltip(this.font, this.tooltip, mouseX, mouseY);
         this.tooltip = null;
      }
   }

   class PropertyList extends ObjectSelectionList<InfusionCodexIndexScreen.PropertyList.LockedEntry> {
      public PropertyList(Minecraft minecraft) {
         super(minecraft, InfusionCodexIndexScreen.this.width, InfusionCodexIndexScreen.this.height - 33 - 58, 33, 16);
         InfusionCodexIndexScreen.this.entries
            .stream()
            .filter(
               propertyHolder -> ((Property)((Holder)propertyHolder.getKey()).value())
                  .getName()
                  .getString()
                  .toLowerCase()
                  .contains(InfusionCodexIndexScreen.this.searchBar.getValue().toLowerCase())
            )
            .forEach(
               propertyHolder -> this.addEntry(
                  (net.minecraft.client.gui.components.AbstractSelectionList.Entry)(InfusionCodexSaveData.isUnlocked((Holder<Property>)propertyHolder.getKey())
                     ? new InfusionCodexIndexScreen.PropertyList.Entry(
                        (Holder<Property>)propertyHolder.getKey(), (CodexEntryReloadListenener.CodexEntry)propertyHolder.getValue()
                     )
                     : new InfusionCodexIndexScreen.PropertyList.LockedEntry((Holder<Property>)propertyHolder.getKey()))
               )
            );
      }

      public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         this.setSelected((InfusionCodexIndexScreen.PropertyList.LockedEntry)this.getHovered());
         super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
      }

      public int getRowWidth() {
         return 280;
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends InfusionCodexIndexScreen.PropertyList.LockedEntry {
         private final CodexEntryReloadListenener.CodexEntry entry;
         private final boolean read;
         private final Component textNarration;
         private final ItemStack propertyCapsule;

         Entry(Holder<Property> property, CodexEntryReloadListenener.CodexEntry entry) {
            super(property);
            this.textNarration = ((Property)property.value()).getName();
            this.entry = entry;
            this.propertyCapsule = InfusedPropertiesHelper.createPropertyCapsule(this.property);
            this.read = InfusionCodexSaveData.isRead(property);
         }

         @Override
         public void render(
            GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick
         ) {
            int i = top + height / 2 - 4;
            int j = index % 2 == 0 ? -1 : -4539718;
            PoseStack poseStack = guiGraphics.pose();
            Component name = ((Property)this.property.value()).getName(this.propertyCapsule);
            if (this.equals(PropertyList.this.getSelected())) {
               name = Component.literal(name.getString()).withColor(16777215);
            }

            guiGraphics.renderFakeItem(this.propertyCapsule, left - 2, i - 4);
            if (!this.read) {
               poseStack.pushPose();
               poseStack.translate(0.0F, 0.0F, 200.0F);
               guiGraphics.blitSprite(InfusionCodexIndexScreen.NEW_ENTRY_ICON, 16, 16, 0, 0, left - 2, i - 4, 16, 16);
               poseStack.popPose();
            }

            guiGraphics.drawString(InfusionCodexIndexScreen.this.font, name, left + 18, i, j);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.equals(PropertyList.this.getSelected())) {
               PropertyList.this.minecraft.setScreen(new InfusionCodexEntryScreen(this.property, this.entry, InfusionCodexIndexScreen.this));
               PropertyList.this.playDownSound(Minecraft.getInstance().getSoundManager());
            }

            return true;
         }

         @Override
         public Component getNarration() {
            return this.textNarration;
         }
      }

      @OnlyIn(Dist.CLIENT)
      class LockedEntry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<InfusionCodexIndexScreen.PropertyList.LockedEntry> {
         protected final Holder<Property> property;
         private static final Component NARRATOR_COMPONENT = Component.translatable("narrator.infusion_codex.locked_entry");
         private final Component textComponent;
         private static final Component TOOLTIP_TEXT_COMPONENT = Component.translatable("screen.infusion_codex.locked_entry.tooltip");
         private final boolean spoilerMode = AlchemancyConfig.Client.codexDisplayMode().equals(InfusionCodexSaveData.DisplayMode.SPOILER);

         public Component getNarration() {
            return NARRATOR_COMPONENT;
         }

         LockedEntry(Holder<Property> property) {
            this.property = property;
            this.textComponent = this.spoilerMode
               ? Component.literal(((Property)property.value()).getName().getString())
               : Component.translatable("screen.infusion_codex.locked_entry");
         }

         public void render(
            GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick
         ) {
            int i = top + height / 2 - 4;
            int j = this.spoilerMode ? 11184810 : (index % 2 == 0 ? -1 : -4539718);
            guiGraphics.drawString(InfusionCodexIndexScreen.this.font, this.textComponent, left + 18, i, j);
            if (hovering) {
               InfusionCodexIndexScreen.this.tooltip = TOOLTIP_TEXT_COMPONENT;
            }
         }
      }
   }
}
