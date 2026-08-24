package io.wispforest.owo.ui.component;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIModelParsingException;
import io.wispforest.owo.ui.parsing.UIParsing;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.commands.arguments.item.ItemParser.ItemResult;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.w3c.dom.Element;

public class ItemComponent extends BaseComponent {
   protected static final Matrix4f ITEM_SCALING = new Matrix4f().scaling(16.0F, -16.0F, 16.0F);
   protected final BufferSource entityBuffers;
   protected final ItemRenderer itemRenderer;
   protected ItemStack stack;
   protected boolean showOverlay = false;
   protected boolean setTooltipFromStack = false;

   protected ItemComponent(ItemStack stack) {
      this.entityBuffers = Minecraft.getInstance().renderBuffers().bufferSource();
      this.itemRenderer = Minecraft.getInstance().getItemRenderer();
      this.stack = stack;
   }

   @Override
   protected int determineHorizontalContentSize(Sizing sizing) {
      return 16;
   }

   @Override
   protected int determineVerticalContentSize(Sizing sizing) {
      return 16;
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      boolean notSideLit = true;
      Lighting.setupForFlatItems();
      PoseStack matrices = context.pose();
      matrices.pushPose();
      matrices.translate(this.x, this.y, 100.0F);
      matrices.scale(this.width / 16.0F, this.height / 16.0F, 1.0F);
      matrices.translate(8.0, 8.0, 0.0);
      matrices.scale(16.0F, -16.0F, 16.0F);
      Minecraft client = Minecraft.getInstance();
      this.itemRenderer.renderStatic(this.stack, ItemDisplayContext.GUI, 15728880, OverlayTexture.NO_OVERLAY, matrices, this.entityBuffers, client.level, 0);
      this.entityBuffers.endBatch();
      matrices.popPose();
      if (this.showOverlay) {
         context.renderItemDecorations(client.font, this.stack, this.x, this.y);
      }

      Lighting.setupFor3DItems();
   }

   protected void updateTooltipForStack() {
      if (this.setTooltipFromStack) {
         if (!this.stack.isEmpty()) {
            Minecraft client = Minecraft.getInstance();
            this.tooltip(tooltipFromItem(this.stack, TooltipContext.of(client.level), client.player, null));
         } else {
            this.tooltip((List<ClientTooltipComponent>)null);
         }
      }
   }

   public ItemComponent setTooltipFromStack(boolean setTooltipFromStack) {
      this.setTooltipFromStack = setTooltipFromStack;
      this.updateTooltipForStack();
      return this;
   }

   public boolean setTooltipFromStack() {
      return this.setTooltipFromStack;
   }

   public ItemComponent stack(ItemStack stack) {
      this.stack = stack;
      this.updateTooltipForStack();
      return this;
   }

   public ItemStack stack() {
      return this.stack;
   }

   public ItemComponent showOverlay(boolean drawOverlay) {
      this.showOverlay = drawOverlay;
      return this;
   }

   public boolean showOverlay() {
      return this.showOverlay;
   }

   public static List<ClientTooltipComponent> tooltipFromItem(ItemStack stack, TooltipContext context, @Nullable Player player, @Nullable TooltipFlag type) {
      if (type == null) {
         type = Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL;
      }

      ArrayList<ClientTooltipComponent> tooltip = new ArrayList<>();
      stack.getTooltipLines(context, player, type).stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).forEach(tooltip::add);

      try {
         stack.getTooltipImage().ifPresent(data -> tooltip.add(1, ClientTooltipComponent.create(data)));
      } catch (IllegalArgumentException var6) {
      }

      return tooltip;
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "show-overlay", UIParsing::parseBool, this::showOverlay);
      UIParsing.apply(children, "set-tooltip-from-stack", UIParsing::parseBool, this::setTooltipFromStack);
      UIParsing.apply(children, "item", UIParsing::parseIdentifier, itemId -> {
         Owo.debugWarn(Owo.LOGGER, "Deprecated <item> property populated on item component - migrate to <stack> instead");
         Item item = (Item)BuiltInRegistries.ITEM.getOptional(itemId).orElseThrow(() -> new UIModelParsingException("Unknown item " + itemId));
         this.stack(item.getDefaultInstance());
      });
      UIParsing.apply(children, "stack", $ -> $.getTextContent().strip(), stackString -> {
         try {
            ItemResult result = new ItemParser(Provider.create(Stream.of(BuiltInRegistries.ITEM.asLookup()))).parse(new StringReader(stackString));
            ItemStack stack = new ItemStack(result.item());
            stack.applyComponentsAndValidate(result.components());
            this.stack(stack);
         } catch (CommandSyntaxException var4) {
            throw new UIModelParsingException("Invalid item stack", var4);
         }
      });
   }
}
