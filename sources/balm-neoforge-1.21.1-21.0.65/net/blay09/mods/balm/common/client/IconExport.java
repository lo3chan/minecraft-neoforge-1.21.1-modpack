package net.blay09.mods.balm.common.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public class IconExport {
   public static void export(String filter) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.execute(
         () -> {
            RenderTarget renderTarget = null;

            try {
               renderTarget = new TextureTarget(64, 64, true, Minecraft.ON_OSX);
               renderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               CreativeModeTabs.tryRebuildTabContents(
                  minecraft.player.connection.enabledFeatures(), (Boolean)minecraft.options.operatorItemsTab().get(), minecraft.level.registryAccess()
               );
               int colonIndex = filter.indexOf(58);
               String filterModId = colonIndex != -1 ? filter.substring(0, colonIndex) : filter;
               String filterItemId = colonIndex != -1 ? filter.substring(colonIndex + 1) : null;
               File exportFolder = new File("exports/icons/" + filterModId);
               if (!exportFolder.exists() && !exportFolder.mkdirs()) {
                  throw new RuntimeException("Failed to create export folder: " + exportFolder);
               }

               GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());

               for (CreativeModeTab creativeModeTab : CreativeModeTabs.allTabs()) {
                  for (ItemStack itemStack : creativeModeTab.getDisplayItems()) {
                     ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                     if (itemId.getNamespace().equals(filterModId) && (filterItemId == null || itemId.getPath().equals(filterItemId))) {
                        renderTarget.clear(false);
                        RenderSystem.enableDepthTest();
                        renderTarget.bindWrite(false);
                        Matrix4f matrix = new Matrix4f().setOrtho(0.0F, 16.0F, 16.0F, 0.0F, 1000.0F, 21000.0F);
                        RenderSystem.setProjectionMatrix(matrix, VertexSorting.ORTHOGRAPHIC_Z);
                        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
                        modelViewStack.pushMatrix();
                        modelViewStack.translation(0.0F, 0.0F, -11000.0F);
                        RenderSystem.applyModelViewMatrix();
                        Lighting.setupForFlatItems();
                        guiGraphics.renderItem(itemStack, 0, 0);
                        guiGraphics.flush();
                        modelViewStack.popMatrix();
                        RenderSystem.applyModelViewMatrix();
                        renderTarget.unbindWrite();
                        RenderSystem.disableDepthTest();
                        NativeImage nativeImage = new NativeImage(renderTarget.width, renderTarget.height, false);

                        try {
                           RenderSystem.bindTexture(renderTarget.getColorTextureId());
                           nativeImage.downloadTexture(0, false);
                           nativeImage.flipY();
                           nativeImage.writeToFile(new File(exportFolder, itemId.getPath() + ".png"));
                        } catch (Throwable var24) {
                           try {
                              nativeImage.close();
                           } catch (Throwable var23) {
                              var24.addSuppressed(var23);
                           }

                           throw var24;
                        }

                        nativeImage.close();
                     }
                  }
               }
            } catch (Exception var25) {
               var25.printStackTrace();
            } finally {
               if (renderTarget != null) {
                  renderTarget.destroyBuffers();
               }
            }
         }
      );
   }
}
