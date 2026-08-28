/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.ImageButton
 *  net.minecraft.client.gui.components.Tooltip
 *  net.minecraft.client.gui.components.WidgetSprites
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.packs.PackSelectionScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package traben.entity_texture_features.mixin.mixins;

import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.config.screens.ETFConfigScreenMain;
import traben.entity_texture_features.utils.ETFUtils2;

@Mixin(value={PackSelectionScreen.class})
public abstract class MixinPackScreen
extends Screen {
    @Unique
    private static final ResourceLocation etf$FOCUSED = ETFUtils2.res("entity_features", "textures/gui/settings_focused.png");
    @Unique
    private static final ResourceLocation etf$UNFOCUSED = ETFUtils2.res("entity_features", "textures/gui/settings_unfocused.png");
    @Shadow
    @Final
    private Path packDir;
    @Shadow
    private Button doneButton;
    @Unique
    private ImageButton etf$button = null;

    protected MixinPackScreen(Component title) {
        super(title);
    }

    @Inject(method={"repositionElements"}, at={@At(value="TAIL")})
    private void etf$etfButtonResize(CallbackInfo ci) {
        if (this.etf$button == null) {
            return;
        }
        int[] vals = this.etf$etfButtonReSize();
        if (vals == null) {
            return;
        }
        int x = vals[0];
        int y = vals[1];
        this.etf$button.setX(x);
        this.etf$button.setY(y);
    }

    @Inject(method={"init"}, at={@At(value="TAIL")})
    private void etf$etfButton(CallbackInfo ci) {
        if (ETF.config().getConfig().configButtonLoc == ETFConfig.SettingsButtonLocation.OFF) {
            return;
        }
        if (this.minecraft == null || !this.packDir.equals(this.minecraft.getResourcePackDirectory()) || ETF.isFabric() != ETF.FABRIC_API) {
            return;
        }
        int[] vals = this.etf$etfButtonReSize();
        if (vals == null) {
            return;
        }
        int x = vals[0];
        int y = vals[1];
        this.etf$button = (ImageButton)this.addRenderableWidget((GuiEventListener)new ImageButton(this, x, y, 24, 20, new WidgetSprites(etf$UNFOCUSED, etf$FOCUSED), button -> Objects.requireNonNull(this.minecraft).setScreen((Screen)new ETFConfigScreenMain(this)), Component.nullToEmpty((String)"")){
            {
                this.setTooltip(Tooltip.create((Component)ETF.getTextFromTranslation("config.entity_features.button_tooltip")));
            }

            public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
                ResourceLocation identifier = this.isHoveredOrFocused() ? etf$FOCUSED : etf$UNFOCUSED;
                context.blit(identifier, this.getX(), this.getY(), 0.0f, 0.0f, this.width, this.height, this.width, this.height);
            }
        });
    }

    @Unique
    private int @Nullable [] etf$etfButtonReSize() {
        int y;
        int x;
        switch (ETF.config().getConfig().configButtonLoc) {
            case BOTTOM_RIGHT: {
                x = this.doneButton.getX() + this.doneButton.getWidth() + 8;
                y = this.doneButton.getY();
                break;
            }
            case BOTTOM_LEFT: {
                int middle = this.width / 2;
                int bottomRight = this.doneButton.getX() + this.doneButton.getWidth() + 8;
                int offset = bottomRight - middle;
                x = middle - offset - 24;
                y = this.doneButton.getY();
                break;
            }
            case TOP_RIGHT: {
                x = this.doneButton.getX() + this.doneButton.getWidth() + 8;
                y = this.height - this.doneButton.getY() - this.doneButton.getHeight();
                break;
            }
            case TOP_LEFT: {
                int middle = this.width / 2;
                int bottomRight = this.doneButton.getX() + this.doneButton.getWidth() + 8;
                int offset = bottomRight - middle;
                x = middle - offset - 24;
                y = this.height - this.doneButton.getY() - this.doneButton.getHeight();
                break;
            }
            default: {
                return null;
            }
        }
        return new int[]{x, y};
    }
}

