package net.irisshaders.iris.pbr.mipmap;

import net.minecraft.util.FastColor.ABGR32;

public class ChannelMipmapGenerator extends AbstractMipmapGenerator {
   protected final ChannelMipmapGenerator.BlendFunction redFunc;
   protected final ChannelMipmapGenerator.BlendFunction greenFunc;
   protected final ChannelMipmapGenerator.BlendFunction blueFunc;
   protected final ChannelMipmapGenerator.BlendFunction alphaFunc;

   public ChannelMipmapGenerator(
      ChannelMipmapGenerator.BlendFunction redFunc,
      ChannelMipmapGenerator.BlendFunction greenFunc,
      ChannelMipmapGenerator.BlendFunction blueFunc,
      ChannelMipmapGenerator.BlendFunction alphaFunc
   ) {
      this.redFunc = redFunc;
      this.greenFunc = greenFunc;
      this.blueFunc = blueFunc;
      this.alphaFunc = alphaFunc;
   }

   @Override
   public int blend(int c0, int c1, int c2, int c3) {
      return ABGR32.color(
         this.alphaFunc.blend(ABGR32.alpha(c0), ABGR32.alpha(c1), ABGR32.alpha(c2), ABGR32.alpha(c3)),
         this.blueFunc.blend(ABGR32.blue(c0), ABGR32.blue(c1), ABGR32.blue(c2), ABGR32.blue(c3)),
         this.greenFunc.blend(ABGR32.green(c0), ABGR32.green(c1), ABGR32.green(c2), ABGR32.green(c3)),
         this.redFunc.blend(ABGR32.red(c0), ABGR32.red(c1), ABGR32.red(c2), ABGR32.red(c3))
      );
   }

   public interface BlendFunction {
      int blend(int var1, int var2, int var3, int var4);
   }
}
