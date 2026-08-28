/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.AgeableListModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.renderer.entity.EntityRenderer
 *  net.minecraft.client.renderer.entity.LivingEntityRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.neoforged.fml.util.ObfuscationReflectionHelper
 */
package net.diebuddies.bridge;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

public class ReflectionsForge {
    public static final Method setupRotations = ObfuscationReflectionHelper.findMethod(LivingEntityRenderer.class, (String)"setupRotations", (Class[])new Class[]{LivingEntity.class, PoseStack.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE});
    public static final Method getBlockLightLevel = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, (String)"getBlockLightLevel", (Class[])new Class[]{Entity.class, BlockPos.class});
    public static final Method headParts = ObfuscationReflectionHelper.findMethod(AgeableListModel.class, (String)"headParts", (Class[])new Class[0]);
    public static final Method bodyParts = ObfuscationReflectionHelper.findMethod(AgeableListModel.class, (String)"bodyParts", (Class[])new Class[0]);

    public static Iterable<ModelPart> headParts(AgeableListModel model) {
        try {
            return (Iterable)headParts.invoke(model, new Object[0]);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Iterable<ModelPart> bodyParts(AgeableListModel model) {
        try {
            return (Iterable)bodyParts.invoke(model, new Object[0]);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}

