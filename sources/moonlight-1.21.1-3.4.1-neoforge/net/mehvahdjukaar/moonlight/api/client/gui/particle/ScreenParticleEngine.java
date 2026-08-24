package net.mehvahdjukaar.moonlight.api.client.gui.particle;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

public class ScreenParticleEngine implements Renderable {
   private static final int DEFAULT_CAP = 256;
   private final List<ScreenParticle> particles = new ArrayList<>();
   private final int cap;
   private long lastMs = -1L;

   public ScreenParticleEngine() {
      this(256);
   }

   public ScreenParticleEngine(int cap) {
      this.cap = cap;
   }

   public ScreenParticle add(ScreenParticle particle) {
      if (this.particles.size() >= this.cap) {
         this.particles.removeFirst();
      }

      this.particles.add(particle);
      return particle;
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      this.renderAndTick(graphics);
   }

   public void renderAndTick(GuiGraphics graphics) {
      long now = Util.getMillis();
      float dt = this.lastMs < 0L ? 0.0F : Math.min((float)(now - this.lastMs) / 1000.0F, 0.1F);
      this.lastMs = now;
      this.particles.removeIf(px -> !px.tick(dt));

      for (ScreenParticle p : this.particles) {
         p.render(graphics);
      }
   }

   public void clear() {
      this.particles.clear();
   }

   public boolean isEmpty() {
      return this.particles.isEmpty();
   }
}
