/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  java.lang.MatchException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.metadata.gui.GuiSpriteScaling$Tile
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 */
package mezz.jei.library.render;

import com.google.common.base.Preconditions;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.drawable.TilingDirection;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidTankRenderer<T>
implements IIngredientRenderer<T> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    private static final int MIN_FLUID_HEIGHT = 1;
    private final IPlatformFluidHelperInternal<T> fluidHelper;
    private final IIngredientTypeWithSubtypes<Fluid, T> type;
    private final long capacity;
    private final TooltipMode tooltipMode;
    private final int width;
    private final int height;
    private final TilingDirection tilingDirection;

    public FluidTankRenderer(IPlatformFluidHelperInternal<T> fluidHelper) {
        this(fluidHelper, fluidHelper.getFluidIngredientType(), fluidHelper.bucketVolume(), TooltipMode.ITEM_LIST, 16, 16, TilingDirection.UP_RIGHT);
    }

    public FluidTankRenderer(IPlatformFluidHelperInternal<T> fluidHelper, IIngredientTypeWithSubtypes<Fluid, T> type, long capacity, boolean showCapacity, int width, int height, TilingDirection tilingDirection) {
        this(fluidHelper, type, capacity, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height, tilingDirection);
    }

    private FluidTankRenderer(IPlatformFluidHelperInternal<T> fluidHelper, IIngredientTypeWithSubtypes<Fluid, T> type, long capacity, TooltipMode tooltipMode, int width, int height, TilingDirection tilingDirection) {
        Preconditions.checkArgument((capacity > 0L ? 1 : 0) != 0, (Object)"capacity must be > 0");
        Preconditions.checkArgument((width > 0 ? 1 : 0) != 0, (Object)"width must be > 0");
        Preconditions.checkArgument((height > 0 ? 1 : 0) != 0, (Object)"height must be > 0");
        Preconditions.checkNotNull(type, (Object)"type");
        Preconditions.checkNotNull((Object)((Object)tilingDirection), (Object)"tilingDirection");
        this.fluidHelper = fluidHelper;
        this.type = type;
        this.capacity = capacity;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
        this.tilingDirection = tilingDirection;
    }

    @Override
    public void render(GuiGraphics guiGraphics, T fluidStack) {
        this.render(guiGraphics, fluidStack, 0, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, T ingredient, int posX, int posY) {
        Fluid fluid = this.type.getBase(ingredient);
        if (fluid.isSame(Fluids.EMPTY)) {
            return;
        }
        this.fluidHelper.getStillFluidSprite(ingredient).ifPresent(fluidStillSprite -> {
            int fluidColor = this.fluidHelper.getColorTint(ingredient);
            long amount = this.fluidHelper.getAmount(ingredient);
            if (amount > 0L) {
                long longScaledAmount = amount * (long)this.height / this.capacity;
                int scaledAmount = Math.clamp((long)longScaledAmount, (int)1, (int)this.height);
                FluidTankRenderer.drawTiledSprite(guiGraphics, this.width, this.height, fluidColor, scaledAmount, fluidStillSprite, this.tilingDirection, posX, posY);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drawTiledSprite(GuiGraphics guiGraphics, int tiledWidth, int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite, TilingDirection tilingDirection, int posX, int posY) {
        SpriteContents spriteContents = sprite.contents();
        int spriteWidth = spriteContents.width();
        int spriteHeight = spriteContents.height();
        GuiSpriteScaling.Tile tileScaling = new GuiSpriteScaling.Tile(spriteWidth, spriteHeight);
        posY = posY + tiledHeight - scaledAmount;
        int xShift = FluidTankRenderer.getXShift(tilingDirection, tiledWidth, spriteWidth);
        int yShift = FluidTankRenderer.getYShift(tilingDirection, scaledAmount, spriteHeight);
        ImmutableRect2i scissorRect = new ImmutableRect2i(posX, posY, tiledWidth, scaledAmount);
        ScreenRectangle scissorArea = MathUtil.transform(scissorRect, guiGraphics.pose().last().pose());
        guiGraphics.enableScissor(scissorArea.left(), scissorArea.top(), scissorArea.right(), scissorArea.bottom());
        try {
            ScalableDrawable.blitTiledSpriteWithColor(guiGraphics, sprite, tileScaling, posX - xShift, posY - yShift, tiledWidth + xShift, scaledAmount + yShift, color);
        }
        finally {
            guiGraphics.disableScissor();
        }
    }

    private static int getXShift(TilingDirection tilingDirection, int desiredWidth, int spriteWidth) {
        return switch (tilingDirection) {
            default -> throw new MatchException(null, null);
            case TilingDirection.DOWN_RIGHT, TilingDirection.UP_RIGHT -> 0;
            case TilingDirection.DOWN_LEFT, TilingDirection.UP_LEFT -> FluidTankRenderer.getShift(desiredWidth, spriteWidth);
        };
    }

    private static int getYShift(TilingDirection tilingDirection, int desiredHeight, int spriteHeight) {
        return switch (tilingDirection) {
            default -> throw new MatchException(null, null);
            case TilingDirection.DOWN_RIGHT, TilingDirection.DOWN_LEFT -> 0;
            case TilingDirection.UP_RIGHT, TilingDirection.UP_LEFT -> FluidTankRenderer.getShift(desiredHeight, spriteHeight);
        };
    }

    private static int getShift(int desired, int sprite) {
        int remainder = desired % sprite;
        if (remainder == 0) {
            return 0;
        }
        return sprite - remainder;
    }

    @Override
    public List<Component> getTooltip(T fluidStack, TooltipFlag tooltipFlag) {
        ArrayList<Component> tooltip = new ArrayList<Component>();
        Fluid fluidType = this.type.getBase(fluidStack);
        if (fluidType.isSame(Fluids.EMPTY)) {
            return tooltip;
        }
        this.fluidHelper.getTooltip(tooltip, fluidStack, tooltipFlag);
        long amount = this.fluidHelper.getAmount(fluidStack);
        long milliBuckets = amount * 1000L / this.fluidHelper.bucketVolume();
        if (this.tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
            MutableComponent amountString = Component.translatable((String)"jei.tooltip.liquid.amount.with.capacity", (Object[])new Object[]{nf.format(milliBuckets), nf.format(this.capacity)});
            tooltip.add((Component)amountString.withStyle(ChatFormatting.GRAY));
        } else if (this.tooltipMode == TooltipMode.SHOW_AMOUNT) {
            MutableComponent amountString = Component.translatable((String)"jei.tooltip.liquid.amount", (Object[])new Object[]{nf.format(milliBuckets)});
            tooltip.add((Component)amountString.withStyle(ChatFormatting.GRAY));
        }
        return tooltip;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    static enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST;

    }
}

