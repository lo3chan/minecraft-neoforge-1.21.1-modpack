package mezz.jei.common.gui;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.Error;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiKeyMapping;
import mezz.jei.common.Internal;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class JeiTooltip implements ITooltipBuilder {
   private final List<Either<FormattedText, TooltipComponent>> lines = new ArrayList<>();
   @Nullable
   private ITypedIngredient<?> typedIngredient;

   @Override
   public void add(@Nullable FormattedText formattedText) {
      if (formattedText == null) {
         if (Services.PLATFORM.getModHelper().isInDev()) {
            throw new NullPointerException("Tried to add null tooltip text");
         }
      } else {
         this.lines.add(Either.left(formattedText));
      }
   }

   @Override
   public void add(@Nullable TooltipComponent component) {
      if (component == null) {
         if (Services.PLATFORM.getModHelper().isInDev()) {
            throw new NullPointerException("Tried to add null tooltip component");
         }
      } else {
         this.lines.add(Either.right(component));
      }
   }

   @Override
   public void setIngredient(ITypedIngredient<?> typedIngredient) {
      this.typedIngredient = typedIngredient;
   }

   @Override
   public void addKeyUsageComponent(String translationKey, IJeiKeyMapping keyMapping) {
      MutableComponent translatedKeyMessage = keyMapping.getTranslatedKeyMessage().copy();
      this.addKeyUsageComponent(translationKey, translatedKeyMessage);
   }

   public void addKeyUsageComponent(String translationKey, MutableComponent keyMapping) {
      Component boldKeyMapping = keyMapping.withStyle(ChatFormatting.BOLD);
      MutableComponent component = Component.translatable(translationKey, new Object[]{boldKeyMapping})
         .withStyle(ChatFormatting.ITALIC)
         .withStyle(ChatFormatting.GRAY);
      this.add(component);
   }

   @Override
   public void addAll(Collection<? extends FormattedText> components) {
      for (FormattedText component : components) {
         this.add(component);
      }
   }

   @Override
   public void clearIngredient() {
      this.typedIngredient = null;
   }

   @Override
   public List<Either<FormattedText, TooltipComponent>> getLines() {
      return this.lines;
   }

   public void addAll(JeiTooltip tooltip) {
      this.lines.addAll(tooltip.lines);
   }

   public boolean isEmpty() {
      return this.lines.isEmpty() && this.typedIngredient == null;
   }

   @Deprecated
   public List<Component> getLegacyComponents() {
      return this.lines.stream().<Component>mapMulti((e, consumer) -> e.left().ifPresent(f -> {
         if (f instanceof Component c) {
            consumer.accept(c);
         }
      })).collect(Collectors.toCollection(ArrayList::new));
   }

   @Deprecated
   @Override
   public List<Component> toLegacyToComponents() {
      return this.getLegacyComponents();
   }

   @Deprecated
   @Override
   public void removeAll(List<Component> components) {
      for (Component component : components) {
         this.lines.remove(Either.left(component));
      }
   }

   @Override
   public String toString() {
      return this.lines.stream().map(e -> (String)e.map(FormattedText::getString, Object::toString)).collect(Collectors.joining("\n", "[\n", "\n]"));
   }

   public void draw(GuiGraphics guiGraphics, int x, int y) {
      if (this.typedIngredient != null) {
         this.draw(guiGraphics, x, y, this.typedIngredient);
      } else if (!this.isEmpty()) {
         Minecraft minecraft = Minecraft.getInstance();
         Font font = minecraft.font;
         IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();

         try {
            renderHelper.renderTooltip(guiGraphics, this.lines, x, y, font, ItemStack.EMPTY);
         } catch (RuntimeException var8) {
            throw new RuntimeException("Crashed when rendering tooltip:\n" + this, var8);
         }
      }
   }

   private <T> void draw(GuiGraphics guiGraphics, int x, int y, ITypedIngredient<T> typedIngredient) {
      IIngredientType<T> ingredientType = typedIngredient.getType();
      IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
      IIngredientRenderer<T> ingredientRenderer = ingredientManager.getIngredientRenderer(ingredientType);
      this.draw(guiGraphics, x, y, typedIngredient, ingredientRenderer, ingredientManager);
   }

   public <T> void draw(
      GuiGraphics guiGraphics,
      int x,
      int y,
      ITypedIngredient<T> typedIngredient,
      IIngredientRenderer<T> ingredientRenderer,
      IIngredientManager ingredientManager
   ) {
      JeiTooltip.TooltipRenderData renderData = this.prepareForIngredientTooltip(typedIngredient, ingredientRenderer, ingredientManager);
      if (!this.isEmpty()) {
         try {
            IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
            renderHelper.renderTooltip(guiGraphics, this.lines, x, y, renderData.font(), renderData.itemStack());
         } catch (RuntimeException var10) {
            CrashReport crashReport = ErrorUtil.createIngredientCrashReport(var10, "Rendering ingredient tooltip", ingredientManager, typedIngredient);
            crashReport.addCategory("tooltip").setDetail("value", this);
            throw new ReportedException(crashReport);
         }
      }
   }

   public <T> JeiTooltip.TooltipRenderData prepareForIngredientTooltip(
      ITypedIngredient<T> typedIngredient, IIngredientRenderer<T> ingredientRenderer, IIngredientManager ingredientManager
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      T ingredient = typedIngredient.getIngredient();
      Font font = ingredientRenderer.getFontRenderer(minecraft, ingredient);
      ItemStack itemStack = typedIngredient.getItemStack().orElse(ItemStack.EMPTY);
      itemStack.getTooltipImage().ifPresent(c -> {
         int index = Math.min(1, this.lines.size());
         this.lines.add(index, Either.right(c));
      });
      this.addDebugInfo(ingredientManager, typedIngredient);
      IJeiHelpers jeiHelpers = Internal.getJeiRuntime().getJeiHelpers();
      IModIdHelper modIdHelper = jeiHelpers.getModIdHelper();
      modIdHelper.getModNameForTooltip(typedIngredient).ifPresent(this::add);
      return new JeiTooltip.TooltipRenderData(font, itemStack);
   }

   private <T> void addDebugInfo(IIngredientManager ingredientManager, ITypedIngredient<T> typedIngredient) {
      if (DebugConfig.isDebugInfoTooltipsEnabled() && Minecraft.getInstance().options.advancedItemTooltips) {
         T ingredient = typedIngredient.getIngredient();
         IIngredientType<T> type = typedIngredient.getType();
         IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(type);
         Codec<T> ingredientCodec = ingredientManager.getIngredientCodec(type);
         this.add(Component.empty());
         this.add(Component.literal("JEI Debug:").withStyle(ChatFormatting.DARK_GRAY));
         this.add(Component.literal("• type: " + ingredientHelper.getIngredientType().getUid()).withStyle(ChatFormatting.DARK_GRAY));
         this.add(Component.literal("• has subtypes: " + (ingredientHelper.hasSubtypes(ingredient) ? "true" : "false")).withStyle(ChatFormatting.DARK_GRAY));
         this.add(Component.literal("• uid: " + ingredientHelper.getUid(ingredient, UidContext.Ingredient)).withStyle(ChatFormatting.DARK_GRAY));

         try {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;

            assert level != null;

            RegistryAccess registryAccess = level.registryAccess();
            RegistryOps<JsonElement> registryOps = registryAccess.createSerializationContext(JsonOps.INSTANCE);
            String jsonResult = (String)ingredientCodec.encodeStart(registryOps, ingredient).mapOrElse(JsonElement::toString, Error::message);
            this.add(Component.literal("• json: " + jsonResult).withStyle(ChatFormatting.DARK_GRAY));
         } catch (RuntimeException var12) {
            this.add(Component.literal("• json crashed: " + var12.getMessage()).withStyle(ChatFormatting.DARK_RED));
         }

         this.add(Component.literal("• extra info: " + ingredientHelper.getErrorInfo(ingredient)).withStyle(ChatFormatting.DARK_GRAY));
         this.add(Component.empty());
      }
   }

   public record TooltipRenderData(Font font, ItemStack itemStack) {
   }
}
