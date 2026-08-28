/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.minecraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;

public class PhysicsDebugOverlay {
    public static final String OCEAN_RENDERING = "ocean_rendering";
    public static final String LIQUID_RENDERING = "liquid_rendering";
    public static final String SMOKE_RENDERING = "smoke_rendering";
    public static final String SNOW_RENDERING = "snow_rendering";
    public static final String MAIN_RENDERING = "blocks_mobs_particles_rendering";
    public static final String TRANSPARENT_RENDERING = "transparent_blocks_mobs_particles_rendering";
    public static final String CLOTH_RENDERING = "cloth_rendering";
    public static final String PHYSICS_TICK = "physics_tick";
    public static final String PHYSICS_TICK_PHYSX = "physics_tick_physx";
    public static final String PHYSICS_TICK_ENTITIES = "physics_tick_entities";
    public static final String PHYSICS_TICK_CLOTH = "physics_tick_cloth";
    private static final String TITLE_PREFIX = String.valueOf(ChatFormatting.BOLD) + String.valueOf(ChatFormatting.GOLD);
    private static final String SUB_PREFIX = String.valueOf(ChatFormatting.BOLD) + String.valueOf(ChatFormatting.AQUA);
    private final Minecraft minecraft;
    private final Font font;
    private List<String> debugInformation;

    public PhysicsDebugOverlay(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.font = minecraft.font;
        this.debugInformation = new ObjectArrayList();
    }

    public void render(GuiGraphics guiGraphics) {
        this.processDebugInformation();
        this.drawPhysicsDebugInformation(guiGraphics);
        PerformanceTracker.flush(OCEAN_RENDERING);
        PerformanceTracker.flush(LIQUID_RENDERING);
        PerformanceTracker.flush(SMOKE_RENDERING);
        PerformanceTracker.flush(SNOW_RENDERING);
        PerformanceTracker.flush(MAIN_RENDERING);
        PerformanceTracker.flush(TRANSPARENT_RENDERING);
        PerformanceTracker.flush(CLOTH_RENDERING);
    }

    public void processDebugInformation() {
        double totalPhysics = PerformanceTracker.getInMillis(PHYSICS_TICK_ENTITIES) + PerformanceTracker.getInMillis(PHYSICS_TICK_PHYSX) + PerformanceTracker.getInMillis(PHYSICS_TICK_CLOTH) * 0.5 + PerformanceTracker.getInMillis(PHYSICS_TICK);
        this.debugInformation.clear();
        this.debugInformation.add(TITLE_PREFIX + "Physics Times (ms/physics_tick, 40 times/second)");
        this.debugInformation.add(SUB_PREFIX + "Dynamic Entities Hitbox: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(PHYSICS_TICK_ENTITIES));
        this.debugInformation.add(SUB_PREFIX + "PhysX Simulation: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(PHYSICS_TICK_PHYSX));
        this.debugInformation.add(SUB_PREFIX + "Physics Mod Simulation: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(PHYSICS_TICK));
        this.debugInformation.add(SUB_PREFIX + "Cloth Simulation: " + String.valueOf(ChatFormatting.WHITE) + String.format("%.2f", PerformanceTracker.getInMillis(PHYSICS_TICK_CLOTH) * 0.5));
        this.debugInformation.add(SUB_PREFIX + "Total: " + String.valueOf(ChatFormatting.WHITE) + String.format("%.2f", totalPhysics));
        double totalRender = PerformanceTracker.getInMillis(MAIN_RENDERING) + PerformanceTracker.getInMillis(TRANSPARENT_RENDERING) + PerformanceTracker.getInMillis(OCEAN_RENDERING) + PerformanceTracker.getInMillis(SNOW_RENDERING) + PerformanceTracker.getInMillis(LIQUID_RENDERING) + PerformanceTracker.getInMillis(SMOKE_RENDERING) + PerformanceTracker.getInMillis(CLOTH_RENDERING);
        this.debugInformation.add("");
        this.debugInformation.add(TITLE_PREFIX + "Render Times (ms/frame, CPU overhead)");
        this.debugInformation.add(SUB_PREFIX + "Blocks/Mobs/Particles: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(MAIN_RENDERING));
        this.debugInformation.add(SUB_PREFIX + "Transparent Blocks/Mobs/Particles: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(TRANSPARENT_RENDERING));
        this.debugInformation.add(SUB_PREFIX + "Ocean: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(OCEAN_RENDERING));
        this.debugInformation.add(SUB_PREFIX + "Snow: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(SNOW_RENDERING));
        this.debugInformation.add(SUB_PREFIX + "Liquids: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(LIQUID_RENDERING));
        this.debugInformation.add(SUB_PREFIX + "Smoke: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(SMOKE_RENDERING));
        this.debugInformation.add(SUB_PREFIX + "Cloth: " + String.valueOf(ChatFormatting.WHITE) + PerformanceTracker.getInMillisFormatted(CLOTH_RENDERING));
        this.debugInformation.add(SUB_PREFIX + "Total: " + String.valueOf(ChatFormatting.WHITE) + String.format("%.2f", totalRender));
        this.debugInformation.add("");
        this.debugInformation.add(TITLE_PREFIX + "GPU Memory Usage (MiB)");
        int objectsMemoryUsage = 0;
        int snowMemoryUsage = 0;
        int oceanMemoryUsage = 0;
        for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
            PhysicsWorld physicsWorld = mod.getPhysicsWorld();
            objectsMemoryUsage += physicsWorld.getGPUMemoryUsage();
            snowMemoryUsage += physicsWorld.getSnowWorld().getGPUMemoryUsage();
            oceanMemoryUsage += physicsWorld.getOceanWorld().getGPUMemoryUsage();
        }
        this.debugInformation.add(SUB_PREFIX + "Blocks/Mobs/Particles: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(objectsMemoryUsage / 0x100000));
        this.debugInformation.add(SUB_PREFIX + "Snow: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(snowMemoryUsage / 0x100000));
        this.debugInformation.add(SUB_PREFIX + "Ocean: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(oceanMemoryUsage / 0x100000));
        if (GL.getCapabilities().GL_NVX_gpu_memory_info) {
            int totalVRAM = GL32C.glGetInteger((int)36935);
            int freeVRAM = GL32C.glGetInteger((int)36937);
            this.debugInformation.add(SUB_PREFIX + "Free: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(freeVRAM / 1024));
            this.debugInformation.add(SUB_PREFIX + "Total: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(totalVRAM / 1024));
        }
        int totalObjects = 0;
        int ragdolls = 0;
        int dynamicBlockRagdolls = 0;
        int smoke = 0;
        int liquids = 0;
        for (PhysicsMod mod : PhysicsMod.getInstances().values()) {
            PhysicsWorld physicsWorld = mod.getPhysicsWorld();
            totalObjects += physicsWorld.getBodies().size();
            for (Ragdoll ragdoll : physicsWorld.getRagdolls()) {
                if (ragdoll instanceof DynamicRagdoll) {
                    ++dynamicBlockRagdolls;
                    continue;
                }
                ++ragdolls;
            }
            smoke += physicsWorld.getSmokeDomain().getAllParticles().size();
            for (Liquid liquid : physicsWorld.getLiquids()) {
                liquids += liquid.particles.size();
            }
        }
        this.debugInformation.add("");
        this.debugInformation.add(TITLE_PREFIX + "Physics Objects");
        this.debugInformation.add(SUB_PREFIX + "Total Objects: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(totalObjects));
        this.debugInformation.add(SUB_PREFIX + "Mob Ragdolls: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(ragdolls));
        this.debugInformation.add(SUB_PREFIX + "Dynamic Block Links: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(dynamicBlockRagdolls));
        this.debugInformation.add(SUB_PREFIX + "Smoke: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(smoke));
        this.debugInformation.add(SUB_PREFIX + "Liquids: " + String.valueOf(ChatFormatting.WHITE) + Integer.toString(liquids));
    }

    private void drawPhysicsDebugInformation(GuiGraphics guiGraphics) {
        for (int i = 0; i < this.debugInformation.size(); ++i) {
            String text = this.debugInformation.get(i);
            if (text.isBlank()) continue;
            Objects.requireNonNull(this.font);
            int lineHeight = 9;
            int padding = 2;
            int y = padding + lineHeight * i;
            int textWidth = this.font.width(text);
            guiGraphics.fill(1, y - 1, 2 + textWidth + 1, y + lineHeight - 1, -1873784752);
            guiGraphics.drawString(this.font, text, padding, y, 0xE0E0E0);
        }
    }
}

