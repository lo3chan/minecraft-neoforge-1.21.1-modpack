/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.tags.TagKey
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Unmodifiable
 */
package mezz.jei.library.gui.ingredients;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.gui.builder.IIngredientConsumer;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientVisibility;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.gui.elements.OffsetDrawable;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.library.gui.ingredients.ICycler;
import mezz.jei.library.gui.ingredients.RendererOverrides;
import mezz.jei.library.gui.ingredients.TagContentTooltipComponent;
import mezz.jei.library.gui.recipes.layout.builder.LegacyTooltipCallbackAdapter;
import mezz.jei.library.ingredients.DisplayIngredientAcceptor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public class RecipeSlot
implements IRecipeSlotView,
IRecipeSlotDrawable {
    private static final int MAX_DISPLAYED_INGREDIENTS = 100;
    private final RecipeIngredientRole role;
    private final ICycler cycler;
    private final List<IRecipeSlotRichTooltipCallback> tooltipCallbacks;
    @Nullable
    private final RendererOverrides rendererOverrides;
    @Nullable
    private final OffsetDrawable background;
    @Nullable
    private final IDrawable overlay;
    @Nullable
    private final String slotName;
    private ImmutableRect2i rect;
    private final @Unmodifiable List<@Nullable ITypedIngredient<?>> allIngredients;
    @Nullable
    private @Unmodifiable @Nullable List<@Nullable ITypedIngredient<?>> displayIngredients;
    @Nullable
    private DisplayIngredientAcceptor displayOverrides;

    public RecipeSlot(RecipeIngredientRole role, ImmutableRect2i rect, ICycler cycler, List<IRecipeSlotRichTooltipCallback> tooltipCallbacks, List<@Nullable ITypedIngredient<?>> allIngredients, @Nullable @Nullable List<@Nullable ITypedIngredient<?>> focusedIngredients, @Nullable OffsetDrawable background, @Nullable IDrawable overlay, @Nullable String slotName, @Nullable RendererOverrides rendererOverrides) {
        this.allIngredients = Collections.unmodifiableList(allIngredients);
        this.background = background;
        this.overlay = overlay;
        this.slotName = slotName;
        this.rendererOverrides = rendererOverrides;
        this.role = role;
        this.rect = rect;
        this.cycler = cycler;
        this.displayIngredients = focusedIngredients;
        this.tooltipCallbacks = tooltipCallbacks;
    }

    @Override
    public Stream<ITypedIngredient<?>> getAllIngredients() {
        return this.allIngredients.stream().filter(Objects::nonNull);
    }

    @Override
    public @Unmodifiable List<@Nullable ITypedIngredient<?>> getAllIngredientsList() {
        return this.allIngredients;
    }

    @Override
    public boolean isEmpty() {
        return this.allIngredients.isEmpty() || this.allIngredients.stream().allMatch(Objects::isNull);
    }

    @Override
    public Optional<ITypedIngredient<?>> getDisplayedIngredient() {
        if (this.displayOverrides != null) {
            List<@Nullable ITypedIngredient<?>> overrides = this.displayOverrides.getAllIngredients();
            return this.cycler.getCycled(overrides);
        }
        if (this.displayIngredients == null) {
            this.displayIngredients = RecipeSlot.calculateDisplayIngredients(this.allIngredients);
        }
        return this.cycler.getCycled(this.displayIngredients);
    }

    private static List<@Nullable ITypedIngredient<?>> calculateDisplayIngredients(List<@Nullable ITypedIngredient<?>> allIngredients) {
        if (allIngredients.isEmpty()) {
            return List.of();
        }
        List<@Nullable ITypedIngredient<?>> visibleIngredients = List.of();
        boolean hasInvisibleIngredients = false;
        IIngredientVisibility ingredientVisibility = Internal.getJeiRuntime().getJeiHelpers().getIngredientVisibility();
        for (int i = 0; i < allIngredients.size() && visibleIngredients.size() < 100; ++i) {
            boolean visible;
            @Nullable ITypedIngredient<?> ingredient = allIngredients.get(i);
            boolean bl = visible = ingredient == null || ingredientVisibility.isIngredientVisible(ingredient);
            if (visible) {
                if (!hasInvisibleIngredients) continue;
                visibleIngredients.add(ingredient);
                continue;
            }
            if (hasInvisibleIngredients) continue;
            hasInvisibleIngredients = true;
            visibleIngredients = new ArrayList(allIngredients.subList(0, i));
        }
        if (!visibleIngredients.isEmpty()) {
            return visibleIngredients;
        }
        if (allIngredients.size() < 100) {
            return allIngredients;
        }
        return allIngredients.subList(0, 100);
    }

    @Override
    public Optional<String> getSlotName() {
        return Optional.ofNullable(this.slotName);
    }

    @Override
    public RecipeIngredientRole getRole() {
        return this.role;
    }

    @Override
    public void drawHighlight(GuiGraphics guiGraphics, int color) {
        int x = this.rect.getX();
        int y = this.rect.getY();
        int width = this.rect.getWidth();
        int height = this.rect.getHeight();
        guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + width, y + height, color, color, 0);
    }

    private <T> void getTooltip(ITooltipBuilder tooltip, ITypedIngredient<T> typedIngredient) {
        IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
        IIngredientType<T> ingredientType = typedIngredient.getType();
        IIngredientRenderer<T> ingredientRenderer = this.getIngredientRenderer(ingredientType);
        SafeIngredientUtil.getRichTooltip(tooltip, ingredientManager, ingredientRenderer, typedIngredient);
        this.addTagNameTooltip(tooltip, ingredientManager, typedIngredient);
        this.addIngredientsToTooltip(tooltip, typedIngredient);
        for (IRecipeSlotRichTooltipCallback tooltipCallback : this.tooltipCallbacks) {
            tooltipCallback.onRichTooltip(this, tooltip);
        }
    }

    @Deprecated
    private <T> List<Component> getLegacyTooltip(ITypedIngredient<T> typedIngredient) {
        IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
        IIngredientType<T> ingredientType = typedIngredient.getType();
        IIngredientRenderer<T> ingredientRenderer = this.getIngredientRenderer(ingredientType);
        JeiTooltip tooltip = new JeiTooltip();
        SafeIngredientUtil.getRichTooltip(tooltip, ingredientManager, ingredientRenderer, typedIngredient);
        this.addTagNameTooltip(tooltip, ingredientManager, typedIngredient);
        for (IRecipeSlotRichTooltipCallback tooltipCallback : this.tooltipCallbacks) {
            tooltipCallback.onRichTooltip(this, tooltip);
        }
        return tooltip.getLegacyComponents();
    }

    private <T> void addTagNameTooltip(ITooltipBuilder tooltip, IIngredientManager ingredientManager, ITypedIngredient<T> ingredient) {
        IIngredientType<T> ingredientType = ingredient.getType();
        List<T> ingredients = this.getIngredients(ingredientType).toList();
        if (ingredients.isEmpty()) {
            return;
        }
        IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
        if (clientConfig.hideSingleTagContentTooltipEnabled().getValue().booleanValue() && ingredients.size() == 1) {
            return;
        }
        IIngredientHelper<T> ingredientHelper = ingredientManager.getIngredientHelper(ingredientType);
        ingredientHelper.getTagKeyEquivalent(ingredients).ifPresent(tagKeyEquivalent -> {
            tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.recipe.tag", (Object[])new Object[]{""}).withStyle(ChatFormatting.GRAY));
            IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
            Component tagName = renderHelper.getName((TagKey<?>)tagKeyEquivalent);
            tooltip.add((FormattedText)tagName.copy().withStyle(ChatFormatting.GRAY));
        });
    }

    private <T> void addIngredientsToTooltip(ITooltipBuilder tooltip, ITypedIngredient<T> displayed) {
        IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
        if (clientConfig.tagContentTooltipEnabled().getValue().booleanValue()) {
            IIngredientType<T> type = displayed.getType();
            IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
            IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
            IIngredientRenderer<T> renderer = ingredientManager.getIngredientRenderer(type);
            List<T> ingredients = this.getVisibleIngredients(type);
            if (ingredients.size() > 1) {
                tooltip.add(new TagContentTooltipComponent<T>(renderer, ingredients));
            }
        }
    }

    private <T> List<T> getVisibleIngredients(IIngredientType<T> ingredientType) {
        IIngredientVisibility ingredientVisibility = Internal.getJeiRuntime().getJeiHelpers().getIngredientVisibility();
        return this.getAllIngredients().filter(ingredientVisibility::isIngredientVisible).map(i -> i.getIngredient(ingredientType)).flatMap(Optional::stream).toList();
    }

    @Override
    public void addTooltipCallback(IRecipeSlotTooltipCallback tooltipCallback) {
        this.tooltipCallbacks.add(new LegacyTooltipCallbackAdapter(tooltipCallback));
    }

    private <T> IIngredientRenderer<T> getIngredientRenderer(IIngredientType<T> ingredientType) {
        return Optional.ofNullable(this.rendererOverrides).flatMap(r -> r.getIngredientRenderer(ingredientType)).orElseGet(() -> {
            IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
            return ingredientManager.getIngredientRenderer(ingredientType);
        });
    }

    @Override
    @Deprecated(since="19.34.0", forRemoval=true)
    public void draw(GuiGraphics guiGraphics) {
        this.draw(guiGraphics, false);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, boolean hovered) {
        int x = this.rect.getX();
        int y = this.rect.getY();
        if (this.background != null) {
            this.background.draw(guiGraphics, x, y);
        }
        RenderSystem.enableBlend();
        this.getDisplayedIngredient().ifPresent(ingredient -> this.drawIngredient(guiGraphics, (ITypedIngredient)ingredient, x, y));
        if (this.overlay != null) {
            RenderSystem.enableBlend();
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(0.0f, 0.0f, 200.0f);
            this.overlay.draw(guiGraphics, x, y);
            poseStack.popPose();
        }
        if (hovered) {
            this.drawHighlight(guiGraphics, -2130706433);
        }
        RenderSystem.disableBlend();
    }

    private <T> void drawIngredient(GuiGraphics guiGraphics, ITypedIngredient<T> typedIngredient, int xPos, int yPos) {
        IIngredientType<T> ingredientType = typedIngredient.getType();
        IIngredientRenderer<T> ingredientRenderer = this.getIngredientRenderer(ingredientType);
        SafeIngredientUtil.render(guiGraphics, ingredientRenderer, typedIngredient, xPos, yPos);
    }

    @Override
    @Deprecated(since="19.34.0", forRemoval=true)
    public void drawHoverOverlays(GuiGraphics guiGraphics) {
        this.drawHighlight(guiGraphics, -2130706433);
    }

    @Override
    @Deprecated
    public List<Component> getTooltip() {
        return this.getDisplayedIngredient().map(this::getLegacyTooltip).orElseGet(List::of);
    }

    @Override
    @Deprecated
    public void getTooltip(ITooltipBuilder tooltipBuilder) {
        this.getDisplayedIngredient().ifPresent(ingredient -> this.getTooltip(tooltipBuilder, (ITypedIngredient)ingredient));
    }

    @Override
    public void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        this.getDisplayedIngredient().ifPresent(ingredient -> {
            JeiTooltip tooltip = new JeiTooltip();
            this.getTooltip(tooltip, (ITypedIngredient)ingredient);
            tooltip.draw(guiGraphics, mouseX, mouseY);
        });
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.rect.contains(mouseX, mouseY);
    }

    @Override
    public void setPosition(int x, int y) {
        this.rect = this.rect.setPosition(x, y);
    }

    @Override
    public void clearDisplayOverrides() {
        this.displayOverrides = null;
    }

    @Override
    public IIngredientConsumer createDisplayOverrides() {
        if (this.displayOverrides == null) {
            IIngredientManager ingredientManager = Internal.getJeiRuntime().getIngredientManager();
            this.displayOverrides = new DisplayIngredientAcceptor(ingredientManager);
        }
        return this.displayOverrides;
    }

    @Override
    public Rect2i getRect() {
        return this.rect.toMutable();
    }

    @Override
    public Rect2i getAreaIncludingBackground() {
        if (this.background == null) {
            return this.rect.toMutable();
        }
        return MathUtil.union(this.rect, this.background.getArea()).toMutable();
    }

    public String toString() {
        return "RecipeSlot{rect=" + String.valueOf(this.rect) + "}";
    }
}

