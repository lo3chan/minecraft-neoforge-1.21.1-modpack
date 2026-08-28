/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 */
package net.diebuddies.physics.settings.ux;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.FocusSelector;
import net.diebuddies.physics.settings.ux.ImageElement;
import net.diebuddies.physics.settings.ux.Parallaxes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class ParallaxBackground
extends AbstractWidget {
    private float transitionLength = 0.7f;
    private List<ImageElement> elements = new ObjectArrayList();
    private List<ImageElement> currentTransition;
    private ImageElement[] loadingElements;
    private float transitionTime;
    private FocusSelector focus;

    public ParallaxBackground(float x, float y, float width, float height, FocusSelector focus) {
        super((int)x, (int)y, (int)width, (int)height, null);
        this.focus = focus;
    }

    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        boolean sameParallaxAsCurrent;
        Object parallaxes;
        Animatable focusedElement;
        ObjectArrayList nextElements = null;
        if (this.focus != null && this.currentTransition == null && (focusedElement = this.focus.getFocusedElement()) != null && (parallaxes = focusedElement.getAnimator(Parallaxes.class)) != null) {
            ImageElement[] parallaxElements = ((Parallaxes)parallaxes).elements;
            if (this.loadingElements != parallaxElements) {
                if (this.loadingElements != null) {
                    for (ImageElement element : this.loadingElements) {
                        element.destroyLoadedTexture();
                    }
                }
                this.loadingElements = parallaxElements;
            }
            ObjectArrayList nextList = null;
            if (parallaxElements != null && parallaxElements.length > 0) {
                boolean allLoaded = true;
                for (ImageElement element : parallaxElements) {
                    if (element.loadImage()) continue;
                    allLoaded = false;
                }
                if (allLoaded) {
                    nextList = new ObjectArrayList((Object[])parallaxElements);
                }
            }
            nextElements = nextList;
        }
        if (sameParallaxAsCurrent = this.elements.equals(nextElements)) {
            this.loadingElements = null;
        }
        if (this.currentTransition == null && nextElements != null && !sameParallaxAsCurrent) {
            this.currentTransition = nextElements;
            this.loadingElements = null;
            nextElements = null;
            this.transitionTime = 0.0f;
        }
        for (ImageElement element : this.elements) {
            ((Animatable)((Object)element)).setAnimAlpha(1.0f);
            element.render(guiGraphics, mouseX, mouseY, delta);
        }
        if (this.currentTransition != null) {
            float tickAdjustedDelta = delta / 20.0f;
            this.transitionTime += tickAdjustedDelta;
            float alpha = this.transitionTime / this.transitionLength;
            for (ImageElement element : this.currentTransition) {
                ((Animatable)((Object)element)).setAnimAlpha(Math.min(alpha, 1.0f));
                element.render(guiGraphics, mouseX, mouseY, delta);
            }
            if (alpha >= 1.0f) {
                for (ImageElement element : this.elements) {
                    element.destroyLoadedTexture();
                }
                this.elements = this.currentTransition;
                this.currentTransition = null;
            }
        }
    }

    public void addImageElement(ImageElement element) {
        this.elements.add(element);
    }

    public void addImageElements(ImageElement[] elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public void removeImageElement(ImageElement element) {
        this.elements.remove((Object)element);
    }

    public void clearImageElements() {
        this.elements.clear();
    }

    public void setTransitionLength(float transitionLength) {
        this.transitionLength = transitionLength;
    }

    public float getTransitionLength() {
        return this.transitionLength;
    }

    public void updateWidgetNarration(NarrationElementOutput narration) {
    }
}

