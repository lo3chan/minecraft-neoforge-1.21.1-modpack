/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins.guiphysics;

import com.mojang.blaze3d.platform.InputConstants;
import net.diebuddies.bridge.KeyBindingsRegistry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.BodyType;
import net.diebuddies.jbox2d.dynamics.World;
import net.diebuddies.math.Math;
import net.diebuddies.physics.Box2DUtil;
import net.diebuddies.physics.settings.gui.ScreenExtension;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Screen.class})
public class MixinScreen
implements ScreenExtension {
    @Unique
    private World physicsWorld;
    @Unique
    private float passedTime;

    @Override
    public World getPhysicsWorld() {
        if (this.physicsWorld == null && ConfigClient.guiPhysics) {
            this.initPhysicsWorld();
        }
        return this.physicsWorld;
    }

    @Unique
    private void initPhysicsWorld() {
        this.physicsWorld = new World(new Vec2(0.0f, 98.1f));
        this.physicsWorld.setAllowSleep(false);
        this.createBorders();
    }

    @Unique
    private void createBorders() {
        Screen screen = (Screen)this;
        int windowWidth = screen.width;
        int windowHeight = screen.height;
        int borderSize = 20;
        Box2DUtil.createBox(this.physicsWorld, -borderSize, -borderSize, windowWidth + borderSize * 2, borderSize, BodyType.STATIC);
        Box2DUtil.createBox(this.physicsWorld, -borderSize, windowHeight, windowWidth + borderSize * 2, borderSize, BodyType.STATIC);
        Box2DUtil.createBox(this.physicsWorld, -borderSize, -borderSize, borderSize, windowHeight + borderSize * 2, BodyType.STATIC);
        Box2DUtil.createBox(this.physicsWorld, windowWidth, -borderSize, borderSize, windowHeight + borderSize * 2, BodyType.STATIC);
    }

    @Inject(at={@At(value="HEAD")}, method={"init"})
    public final void init(Minecraft minecraft, int mouseX, int mouseY, CallbackInfo info) {
        if (this.physicsWorld != null && ConfigClient.guiPhysics) {
            this.initPhysicsWorld();
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"keyPressed"})
    public void keyPressed(int key, int scancode, int mods, CallbackInfoReturnable<Boolean> info) {
        if (((KeyMapping)KeyBindingsRegistry.GUI_PHYSICS.get()).isActiveAndMatches(InputConstants.getKey((int)key, (int)scancode))) {
            ConfigClient.guiPhysics = !ConfigClient.guiPhysics;
        }
    }

    @Inject(at={@At(value="HEAD")}, method={"render"})
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.physicsWorld != null) {
            this.physicsWorld.step(delta * 0.05f, 15, 15);
            this.passedTime += delta * 0.05f;
            if (this.passedTime >= 3.0f) {
                float strength = Math.random() * 3.0f + 1.0f;
                double angle = java.lang.Math.toRadians((double)Math.random() * 360.0);
                this.physicsWorld.setGravity(new Vec2((float)java.lang.Math.sin(angle) * 98.1f * strength, (float)java.lang.Math.cos(angle) * 98.1f * strength));
                this.passedTime = 0.0f;
            }
            if (!ConfigClient.guiPhysics) {
                this.physicsWorld = null;
            }
        }
    }
}

