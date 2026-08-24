package jeresources.compatibility.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import jeresources.api.render.IMobRenderHook;
import org.joml.Matrix4fStack;

public class RenderHooks {
   public static final IMobRenderHook ENDER_DRAGON = (renderInfo, entity) -> {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.rotate(Axis.XP.rotationDegrees(20.0F));
      modelViewStack.rotate(Axis.YP.rotationDegrees(180.0F));
      renderInfo.pitch = -renderInfo.pitch - 80.0;
      modelViewStack.rotate(Axis.YN.rotationDegrees((float)(renderInfo.yaw < 90.0 ? (renderInfo.yaw < -90.0 ? 90.0 : -renderInfo.yaw) : -90.0) / 2.0F));
      return renderInfo;
   };
   public static final IMobRenderHook BAT = (renderInfo, entity) -> {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.rotate(Axis.XP.rotationDegrees(20.0F));
      modelViewStack.rotate(Axis.YP.rotationDegrees(180.0F));
      renderInfo.pitch = -renderInfo.pitch;
      return renderInfo;
   };
   public static final IMobRenderHook ELDER_GUARDIAN = (renderInfo, entity) -> {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.scale(0.6F, 0.6F, 0.6F);
      return renderInfo;
   };
   public static final IMobRenderHook SQUID = (renderInfo, entity) -> {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.rotate(Axis.XP.rotationDegrees(50.0F));
      return renderInfo;
   };
   public static final IMobRenderHook GIANT = (renderInfo, entity) -> {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.translate(0.0F, 2.0F, 0.0F);
      modelViewStack.scale(0.7F, 0.7F, 0.7F);
      return renderInfo;
   };
   public static final IMobRenderHook SHULKER = (renderInfo, entity) -> renderInfo;
   public static final IMobRenderHook GROUP_FISH = (renderInfo, entity) -> {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.translate(-0.1F, -0.5F, 0.0F);
      modelViewStack.rotate(Axis.ZP.rotationDegrees(90.0F));
      double pitch = renderInfo.pitch;
      renderInfo.pitch = renderInfo.yaw;
      renderInfo.yaw = -pitch;
      return renderInfo;
   };
}
