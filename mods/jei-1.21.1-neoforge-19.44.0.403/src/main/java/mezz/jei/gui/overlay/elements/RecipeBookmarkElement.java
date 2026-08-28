/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  java.lang.MatchException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.overlay.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiKeyMapping;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.common.Internal;
import mezz.jei.common.config.BookmarkTooltipFeature;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.gui.IngredientsTooltipComponent;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.input.keys.IJeiKeyMappingInternal;
import mezz.jei.common.transfer.RecipeTransferUtil;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.bookmarks.RecipeBookmark;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.bookmarks.PreviewTooltipComponent;
import mezz.jei.gui.overlay.elements.IElement;
import mezz.jei.gui.overlay.ingredients.IngredientGridTooltipHelper;
import mezz.jei.gui.recipes.RecipeCategoryIconUtil;
import mezz.jei.gui.util.FocusUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class RecipeBookmarkElement<R, I>
implements IElement<I> {
    private final RecipeBookmark<R, I> recipeBookmark;
    private final IClientConfig clientConfig;
    @Nullable
    private PreviewTooltipComponent<R> previewTooltipComponent;
    @Nullable
    private IngredientsTooltipComponent ingredientsTooltipComponent;
    @Nullable
    private Optional<IRecipeLayoutDrawable<R>> cachedLayoutDrawable;

    public RecipeBookmarkElement(RecipeBookmark<R, I> recipeBookmark) {
        this.recipeBookmark = recipeBookmark;
        this.clientConfig = Internal.getJeiClientConfigs().getClientConfig();
    }

    @Override
    public ITypedIngredient<I> getTypedIngredient() {
        return this.recipeBookmark.getDisplayIngredient();
    }

    @Override
    public Optional<IBookmark> getBookmark() {
        return Optional.of(this.recipeBookmark);
    }

    @Override
    public IDrawable createRenderOverlay() {
        IRecipeCategory<R> recipeCategory = this.recipeBookmark.getRecipeCategory();
        return new RecipeBookmarkIcon(recipeCategory);
    }

    @Override
    public boolean handleClick(UserInput input, IInternalKeyMappings keyBindings) {
        boolean transferOnce = input.is(keyBindings.getTransferRecipeBookmark());
        boolean transferMax = input.is(keyBindings.getMaxTransferRecipeBookmark());
        if (transferOnce || transferMax) {
            Minecraft minecraft = Minecraft.getInstance();
            Screen screen = minecraft.screen;
            LocalPlayer player = minecraft.player;
            if (player != null && screen instanceof AbstractContainerScreen) {
                AbstractContainerScreen containerScreen = (AbstractContainerScreen)screen;
                IRecipeLayoutDrawable recipeLayout = this.getRecipeLayoutDrawable().orElse(null);
                if (recipeLayout == null) {
                    return false;
                }
                IRecipeTransferManager recipeTransferManager = Internal.getJeiRuntime().getRecipeTransferManager();
                AbstractContainerMenu container = containerScreen.getMenu();
                if (input.isSimulate()) {
                    IRecipeTransferError recipeTransferError = RecipeTransferUtil.getTransferRecipeError(recipeTransferManager, container, recipeLayout, (Player)player).orElse(null);
                    return recipeTransferError == null || recipeTransferError.getType().allowsTransfer;
                }
                return RecipeTransferUtil.transferRecipe(recipeTransferManager, container, recipeLayout, (Player)player, transferMax);
            }
        }
        return false;
    }

    @Override
    public void show(IRecipesGui recipesGui, FocusUtil focusUtil, List<RecipeIngredientRole> roles) {
        IRecipeCategory<R> recipeCategory = this.recipeBookmark.getRecipeCategory();
        R recipe = this.recipeBookmark.getRecipe();
        ITypedIngredient<I> ingredient = this.getTypedIngredient();
        List<IFocus<?>> focuses = focusUtil.createFocuses(ingredient, List.of(RecipeIngredientRole.OUTPUT));
        recipesGui.showRecipes(recipeCategory, List.of(recipe), focuses);
    }

    @Override
    public void getTooltip(JeiTooltip tooltip, IngredientGridTooltipHelper tooltipHelper, IIngredientRenderer<I> ingredientRenderer, IIngredientHelper<I> ingredientHelper) {
        ITypedIngredient<I> displayIngredient = this.recipeBookmark.getDisplayIngredient();
        R recipe = this.recipeBookmark.getRecipe();
        IRecipeCategory<R> recipeCategory = this.recipeBookmark.getRecipeCategory();
        tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.bookmarks.recipe", (Object[])new Object[]{recipeCategory.getTitle()}));
        this.addBookmarkTooltipFeaturesIfEnabled(tooltip);
        if (this.recipeBookmark.isDisplayIsOutput()) {
            ResourceLocation ingredientName;
            String ingredientModId;
            String recipeModId;
            IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
            IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
            IModIdHelper modIdHelper = jeiRuntime.getJeiHelpers().getModIdHelper();
            ResourceLocation recipeName = recipeCategory.getRegistryName(recipe);
            if (recipeName != null && !(recipeModId = recipeName.getNamespace()).equals(ingredientModId = (ingredientName = ingredientHelper.getResourceLocation(displayIngredient.getIngredient())).getNamespace())) {
                Component modName = modIdHelper.getFormattedModNameComponentForModId(recipeModId);
                MutableComponent recipeBy = Component.translatable((String)"jei.tooltip.recipe.by", (Object[])new Object[]{modName});
                tooltip.add((FormattedText)recipeBy.withStyle(ChatFormatting.GRAY));
            }
            tooltip.add((FormattedText)Component.empty());
            SafeIngredientUtil.getRichTooltip(tooltip, ingredientManager, ingredientRenderer, displayIngredient);
        }
    }

    private void addBookmarkTooltipFeaturesIfEnabled(JeiTooltip tooltip) {
        JeiTooltip transferComponents = this.createTransferComponents();
        List<BookmarkTooltipFeature> bookmarkTooltipFeatures = this.getBookmarkTooltipFeatures();
        if (bookmarkTooltipFeatures.isEmpty() && transferComponents.isEmpty()) {
            return;
        }
        if (this.clientConfig.holdShiftToShowBookmarkTooltipFeaturesEnabled().getValue().booleanValue()) {
            IJeiKeyMappingInternal showBookmarkTooltipFeatures = Internal.getKeyMappings().getShowBookmarkTooltipFeatures();
            if (showBookmarkTooltipFeatures.isDown()) {
                this.addBookmarkTooltipFeatures(tooltip, bookmarkTooltipFeatures);
                tooltip.addAll(transferComponents);
            } else {
                tooltip.addKeyUsageComponent("jei.tooltip.bookmarks.tooltips.usage", showBookmarkTooltipFeatures);
            }
        } else {
            this.addBookmarkTooltipFeatures(tooltip, bookmarkTooltipFeatures);
            tooltip.addAll(transferComponents);
        }
    }

    private List<BookmarkTooltipFeature> getBookmarkTooltipFeatures() {
        return this.clientConfig.bookmarkTooltipFeatures().getValue();
    }

    private void addBookmarkTooltipFeatures(JeiTooltip tooltip, List<BookmarkTooltipFeature> features) {
        BookmarkTooltipFeature feature;
        boolean added;
        Iterator<BookmarkTooltipFeature> iterator = features.iterator();
        while (iterator.hasNext() && (added = this.addBookmarkTooltipFeature(tooltip, feature = iterator.next()))) {
        }
    }

    private boolean addBookmarkTooltipFeature(JeiTooltip tooltip, BookmarkTooltipFeature feature) {
        return switch (feature) {
            default -> throw new MatchException(null, null);
            case BookmarkTooltipFeature.PREVIEW -> this.addPreviewTooltipComponent(tooltip);
            case BookmarkTooltipFeature.INGREDIENTS -> this.addIngredientsTooltipComponent(tooltip);
        };
    }

    private boolean addPreviewTooltipComponent(JeiTooltip tooltip) {
        PreviewTooltipComponent<R> component = this.previewTooltipComponent;
        if (component == null) {
            IRecipeLayoutDrawable recipeLayout = this.getRecipeLayoutDrawable().orElse(null);
            if (recipeLayout == null) {
                return false;
            }
            this.previewTooltipComponent = component = new PreviewTooltipComponent(recipeLayout);
        }
        tooltip.add(component);
        return true;
    }

    private boolean addIngredientsTooltipComponent(JeiTooltip tooltip) {
        IngredientsTooltipComponent component = this.ingredientsTooltipComponent;
        if (component == null) {
            IRecipeLayoutDrawable recipeLayout = this.getRecipeLayoutDrawable().orElse(null);
            if (recipeLayout == null) {
                return false;
            }
            this.ingredientsTooltipComponent = component = new IngredientsTooltipComponent(recipeLayout);
        }
        tooltip.add(component);
        return true;
    }

    private JeiTooltip createTransferComponents() {
        JeiTooltip results = new JeiTooltip();
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        LocalPlayer player = minecraft.player;
        if (player != null && screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen containerScreen = (AbstractContainerScreen)screen;
            IRecipeTransferError recipeTransferError = this.getRecipeLayoutDrawable().flatMap(arg_0 -> RecipeBookmarkElement.lambda$createTransferComponents$0(containerScreen, (Player)player, arg_0)).orElse(null);
            if (recipeTransferError == null || recipeTransferError.getType().allowsTransfer) {
                IJeiKeyMapping maxTransferRecipeBookmark;
                IInternalKeyMappings keyMappings = Internal.getKeyMappings();
                IJeiKeyMapping transferRecipeBookmark = keyMappings.getTransferRecipeBookmark();
                if (!transferRecipeBookmark.isUnbound()) {
                    results.addKeyUsageComponent("jei.tooltip.bookmarks.tooltips.transfer.usage", transferRecipeBookmark);
                }
                if (!(maxTransferRecipeBookmark = keyMappings.getMaxTransferRecipeBookmark()).isUnbound()) {
                    results.addKeyUsageComponent("jei.tooltip.bookmarks.tooltips.transfer.max.usage", maxTransferRecipeBookmark);
                }
            }
        }
        return results;
    }

    private Optional<IRecipeLayoutDrawable<R>> getRecipeLayoutDrawable() {
        if (this.cachedLayoutDrawable == null) {
            IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
            IRecipeManager recipeManager = jeiRuntime.getRecipeManager();
            IFocusFactory focusFactory = jeiRuntime.getJeiHelpers().getFocusFactory();
            ScalableDrawable recipePreviewBackground = Internal.getTextures().getRecipePreviewBackground();
            this.cachedLayoutDrawable = recipeManager.createRecipeLayoutDrawable(this.recipeBookmark.getRecipeCategory(), this.recipeBookmark.getRecipe(), focusFactory.getEmptyFocusGroup(), recipePreviewBackground, 4);
        }
        return this.cachedLayoutDrawable;
    }

    @Override
    public boolean isVisible() {
        return this.recipeBookmark.isVisible();
    }

    @Override
    public void tick() {
        PreviewTooltipComponent<R> component = this.previewTooltipComponent;
        if (component != null) {
            component.tick();
        }
    }

    private static /* synthetic */ Optional lambda$createTransferComponents$0(AbstractContainerScreen containerScreen, Player player, IRecipeLayoutDrawable recipeLayout) {
        IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
        IRecipeTransferManager recipeTransferManager = jeiRuntime.getRecipeTransferManager();
        AbstractContainerMenu container = containerScreen.getMenu();
        return RecipeTransferUtil.getTransferRecipeError(recipeTransferManager, container, recipeLayout, player);
    }

    private static class RecipeBookmarkIcon
    implements IDrawable {
        private final IDrawable icon;

        public RecipeBookmarkIcon(IRecipeCategory<?> recipeCategory) {
            IJeiRuntime jeiRuntime = Internal.getJeiRuntime();
            IRecipeManager recipeManager = jeiRuntime.getRecipeManager();
            IJeiHelpers jeiHelpers = jeiRuntime.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
            this.icon = RecipeCategoryIconUtil.create(recipeCategory, recipeManager, guiHelper);
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate((float)(8 + xOffset), (float)(8 + yOffset), 200.0f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            this.icon.draw(guiGraphics);
            poseStack.popPose();
        }
    }
}

