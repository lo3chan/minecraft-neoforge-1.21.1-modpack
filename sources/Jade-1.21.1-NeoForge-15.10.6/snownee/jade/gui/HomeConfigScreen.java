package snownee.jade.gui;

import com.google.common.collect.Lists;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import snownee.jade.Jade;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.impl.config.PluginConfig;
import snownee.jade.util.ModIdentification;
import snownee.jade.util.SmoothChasingValue;

public class HomeConfigScreen extends Screen {
   private final RandomSource random = RandomSource.create(42L);
   private final Screen parent;
   private final SmoothChasingValue titleY;
   private final List<HomeConfigScreen.TextParticle> particles = Lists.newArrayList();
   private final List<HomeConfigScreen.TextParticle> pendingParticles = Lists.newArrayList();
   private float ticks;
   private byte festival;
   private float nextParticleIn;
   private CreditButton creditButton;
   private boolean showTranslators;

   public HomeConfigScreen(Screen parent) {
      super(Component.translatable("gui.jade.configuration"));
      this.parent = parent;
      this.titleY = new SmoothChasingValue().start(8.0F).target(32.0F).withSpeed(0.1F);
      LocalDate now = LocalDate.now();
      int month = now.getMonthValue();
      int day = now.getDayOfMonth();
      if (month == 12 && day >= 24 && day <= 26) {
         this.festival = 1;
      } else if (month == 6 && day == 28) {
         this.festival = 2;
      } else if (month <= 2 && isLunarNewYear(now)) {
         this.festival = 99;
      }
   }

   private static boolean isLunarNewYear(LocalDate now) {
      int year = now.getYear();

      int newYearMonthAndDay = switch (year) {
         case 2025 -> 129;
         case 2026 -> 217;
         case 2027 -> 206;
         case 2028 -> 126;
         case 2029 -> 213;
         case 2030 -> 203;
         case 2031 -> 123;
         case 2032 -> 211;
         case 2033 -> 131;
         case 2034 -> 219;
         case 2035 -> 208;
         case 2036 -> 128;
         case 2037 -> 215;
         case 2038 -> 204;
         case 2039 -> 124;
         case 2040 -> 212;
         case 2041 -> 201;
         case 2042 -> 122;
         case 2043 -> 210;
         default -> 0;
      };
      if (newYearMonthAndDay == 0) {
         return false;
      } else {
         int newYearMonth = newYearMonthAndDay / 100;
         int newYearDay = newYearMonthAndDay % 100;
         LocalDate newYearDate = LocalDate.of(year, newYearMonth, newYearDay);
         int newYearDayOfYear = newYearDate.getDayOfYear();
         int dayOfYear = now.getDayOfYear();
         return dayOfYear >= newYearDayOfYear - 1 && dayOfYear <= newYearDayOfYear + 2;
      }
   }

   protected void init() {
      Objects.requireNonNull(this.minecraft);
      this.particles.clear();
      Component modSettings = Component.translatable("gui.jade.jade_settings");
      Component pluginSettings = Component.translatable("gui.jade.plugin_settings");
      int maxWidth = Math.max(100, Math.max(this.font.width(modSettings) + 8, this.font.width(pluginSettings) + 8));
      maxWidth = Math.min(maxWidth, Math.min(240, this.width / 2 - 40));
      this.addRenderableWidget(Button.builder(modSettings, w -> {
         this.titleY.set(this.titleY.getTarget());
         this.minecraft.setScreen(new WailaConfigScreen(this));
         this.showTranslators = true;
      }).bounds(this.width / 2 - 5 - maxWidth, this.height / 2 - 10, maxWidth, 20).build());
      this.addRenderableWidget(Button.builder(pluginSettings, w -> {
         this.titleY.set(this.titleY.getTarget());
         this.minecraft.setScreen(new PluginsConfigScreen(this));
         this.showTranslators = true;
      }).bounds(this.width / 2 + 5, this.height / 2 - 10, maxWidth, 20).build());
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, w -> this.onClose()).bounds(this.width / 2 - 50, this.height / 2 + 20, 100, 20).build()
      );
      Style style = Style.EMPTY;
      if (this.festival != 0 && this.festival != 1) {
         style = style.withColor(15852452);
      }

      Component title = Component.translatable("gui.jade.by", new Object[]{Component.literal("❤").withStyle(ChatFormatting.RED)}).withStyle(style);
      Component hoveredTitle = Component.translatable("gui.jade.by.hovered").withStyle(style);
      int btnWidth = this.font.width(title);
      int btnX = (int)(this.width * 0.5F - btnWidth * 0.5F);
      int btnY = (int)(this.height * 0.9F - 5.0F);
      Component narration = Component.translatable("narration.jade.by");
      this.creditButton = (CreditButton)this.addRenderableWidget(
         new CreditButton(
            btnX,
            btnY,
            btnWidth,
            10,
            title,
            hoveredTitle,
            b -> ConfirmLinkScreen.confirmLinkNow(this, "https://www.curseforge.com/members/snownee_/projects"),
            this::triggerAuthorButton,
            $ -> narration.copy()
         )
      );
      if (this.showTranslators) {
         this.creditButton.showTranslators();
      }
   }

   private void triggerAuthorButton(Button button) {
      IntList colors = new IntArrayList();
      String text = "❄";
      if (this.festival == 2) {
         this.festival = 3;
      } else if (this.festival == 99) {
         for (int i = 0; i < 11; i++) {
            colors.add(this.random.nextBoolean() ? 11010048 : 12589056);
         }

         text = "✐";
      } else {
         for (int i = 0; i < 11; i++) {
            colors.add(Mth.color(1.0F - this.random.nextFloat() * 0.6F, 1.0F, 1.0F));
         }
      }

      IntListIterator var13 = colors.iterator();

      while (var13.hasNext()) {
         int color = (Integer)var13.next();
         int ox = this.random.nextIntBetweenInclusive(-button.getWidth() / 2, button.getWidth() / 2);
         float x = this.width * 0.5F + ox;
         float y = this.random.nextIntBetweenInclusive(button.getY(), button.getY() + button.getHeight());
         float dx = ox * 0.08F;
         float dy = -5.0F - this.random.nextFloat() * 3.0F;
         HomeConfigScreen.TextParticle particle = new HomeConfigScreen.TextParticle(text, x, y, dx, dy, color, 0.75F + this.random.nextFloat() * 0.5F);
         this.particles.add(particle);
         if (this.festival == 99) {
            particle.age = 8.0F + this.random.nextFloat() * 5.0F;
         }
      }
   }

   public void onClose() {
      Jade.CONFIG.save();
      PluginConfig.INSTANCE.save();
      WailaClientRegistration.instance().reloadIgnoreLists();
      Objects.requireNonNull(this.minecraft).setScreen(this.parent);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      Objects.requireNonNull(this.minecraft);
      float deltaTicks = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
      this.ticks += deltaTicks;
      if (this.ticks > this.nextParticleIn) {
         if (this.festival == 3) {
            this.nextParticleIn = this.ticks + 1.0F;
            if (this.pendingParticles.isEmpty()) {
               this.festival3populateNew();
            }

            HomeConfigScreen.TextParticle particle = (HomeConfigScreen.TextParticle)this.pendingParticles.removeFirst();
            particle.x = mouseX - 5;
            particle.y = mouseY;
            this.particles.add(particle);
         } else if (this.festival == 1) {
            this.nextParticleIn = this.ticks + 10.0F + this.random.nextFloat() * 10.0F;
            int color = Mth.color(1.0F - this.random.nextFloat() * 0.6F, 1.0F, 1.0F);
            color |= this.random.nextInt(80) + 40 << 24;
            int x = this.random.nextIntBetweenInclusive(40, this.width + 100);
            HomeConfigScreen.TextParticle particle = new HomeConfigScreen.TextParticle("❄", x, -20.0F, -0.3F, 0.5F, color, 2.0F + this.random.nextFloat());
            particle.gravity = 0.0F;
            this.particles.add(particle);
         }
      }

      super.render(guiGraphics, mouseX, mouseY, partialTicks);
      boolean smallUI = this.minecraft.getWindow().getGuiScale() < 3.0;
      int left = this.width / 2 - 105;
      int top = this.height / 4 - 20;
      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(left, top, 0.0F);
      float scale = smallUI ? 2.0F : 1.5F;
      guiGraphics.pose().scale(scale, scale, scale);
      guiGraphics.drawString(this.font, ModIdentification.getModName("jade").orElse("Jade"), 0, 0, 16777215);
      guiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
      this.titleY.tick(deltaTicks);
      String desc2 = I18n.get("gui.jade.configuration.desc2", new Object[0]);
      float scaledX;
      float scaledY;
      if (desc2.isEmpty()) {
         guiGraphics.pose().popPose();
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(left, top, 0.0F);
         scaledX = mouseX - left;
         scaledY = mouseY - top;
      } else {
         scaledX = (mouseX - left) / scale * 2.0F;
         scaledY = (mouseY - top) / scale * 2.0F;
      }

      this.drawFancyTitle(guiGraphics, I18n.get("gui.jade.configuration.desc1", new Object[0]), Math.min(this.titleY.value, 20.0F), 20.0F, scaledX, scaledY);
      if (!desc2.isEmpty()) {
         this.drawFancyTitle(guiGraphics, desc2, Math.min(this.titleY.value + 3.0F, 32.0F), 32.0F, scaledX, scaledY);
      }

      guiGraphics.pose().popPose();
      this.particles.removeIf(p -> {
         p.tick(deltaTicks);
         if (p.y > this.height + 20) {
            return true;
         } else {
            p.render(guiGraphics, this.font);
            return false;
         }
      });
   }

   private void festival3populateNew() {
      IntList colors = new IntArrayList();
      String text = this.random.nextBoolean() ? "UwU" : "OwO";
      switch (this.random.nextInt(7)) {
         case 0:
            colors.add(14942979);
            colors.add(16747520);
            colors.add(16772352);
            colors.add(32806);
            colors.add(7547266);
            colors.add(7547266);
            break;
         case 1:
            colors.add(6016762);
            colors.add(16099768);
            colors.add(16777215);
            colors.add(16099768);
            colors.add(6016762);
            break;
         case 2:
            colors.add(14025328);
            colors.add(14025328);
            colors.add(10178454);
            colors.add(14504);
            colors.add(14504);
            break;
         case 3:
            colors.add(16720268);
            colors.add(16720268);
            colors.add(16766976);
            colors.add(16766976);
            colors.add(2208255);
            colors.add(2208255);
            break;
         case 4:
            colors.add(0);
            colors.add(10724259);
            colors.add(16777215);
            colors.add(8388736);
            break;
         case 5:
            colors.add(16742052);
            colors.add(16777215);
            colors.add(12587479);
            colors.add(0);
            colors.add(3095742);
            break;
         case 6:
            colors.add(16577588);
            colors.add(16777215);
            colors.add(10246609);
            colors.add(2894892);
      }

      int ox = this.random.nextIntBetweenInclusive(this.creditButton.getX(), this.creditButton.getX() + this.creditButton.getWidth());
      float dx = ox * 0.08F;
      float dy = -5.0F - this.random.nextFloat() * 3.0F;
      IntListIterator var6 = colors.iterator();

      while (var6.hasNext()) {
         int color = (Integer)var6.next();

         for (int i = 0; i < 5; i++) {
            HomeConfigScreen.TextParticle particle = new HomeConfigScreen.TextParticle(text, 0.0F, 0.0F, dx, dy, color, 1.0F);
            this.pendingParticles.add(particle);
         }
      }
   }

   private void drawFancyTitle(GuiGraphics guiGraphics, String text, float y, float expectY, float mouseX, float mouseY) {
      float distY = Math.abs(y - expectY);
      if (!(distY >= 9.0F)) {
         int color = IWailaConfig.IConfigOverlay.applyAlpha(11184810, 1.0F - distY / 10.0F);
         JadeFont jadeFont = (JadeFont)this.font;
         jadeFont.jade$setGlint((this.ticks - y / 5.0F) % 90.0F / 45.0F * this.width, mouseX);
         jadeFont.jade$setGlintStrength(1.0F, 1.0F - Mth.clamp(Math.abs(mouseY - y) / 20.0F, 0.0F, 1.0F));
         guiGraphics.pose().pushPose();
         guiGraphics.pose().translate(0.0F, y, 0.0F);
         guiGraphics.drawString(this.font, text, 0, 0, color);
         guiGraphics.pose().popPose();
         jadeFont.jade$setGlint(0.0F / 0.0F, 0.0F / 0.0F);
      }
   }

   private class TextParticle {
      private float age;
      private String text;
      private float x;
      private float y;
      private float motionX;
      private float motionY;
      private int color;
      private float scale;
      private float gravity = 0.98F;

      public TextParticle(String text, float x, float y, float motionX, float motionY, int color, float scale) {
         this.text = text;
         this.x = x;
         this.y = y;
         this.motionX = motionX;
         this.motionY = motionY;
         this.color = color;
         this.scale = scale;
      }

      private void tick(float partialTicks) {
         this.x = this.x + this.motionX * partialTicks;
         this.y = this.y + this.motionY * partialTicks;
         this.motionY = this.motionY + this.gravity * partialTicks;
         if (HomeConfigScreen.this.festival == 99) {
            boolean greaterThanZero = this.age > 0.0F;
            this.age -= partialTicks;
            if (greaterThanZero && this.age <= 0.0F) {
               this.text = HomeConfigScreen.this.random.nextBoolean() ? "✴" : "✳";
               this.color = HomeConfigScreen.this.random.nextBoolean() ? 16765991 : 15778837;
               Objects.requireNonNull(HomeConfigScreen.this.minecraft);
               HomeConfigScreen.this.minecraft
                  .getSoundManager()
                  .play(
                     SimpleSoundInstance.forUI(
                        HomeConfigScreen.this.random.nextBoolean() ? SoundEvents.FIREWORK_ROCKET_BLAST : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 0.7F
                     )
                  );
            }
         } else if (HomeConfigScreen.this.festival == 1) {
            this.age -= partialTicks;
         }
      }

      private void render(GuiGraphics guiGraphics, Font font) {
         if (HomeConfigScreen.this.festival != 99 || !(this.age < -4.0F)) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(this.x, this.y, 0.0F);
            guiGraphics.pose().scale(this.scale, this.scale, this.scale);
            if (HomeConfigScreen.this.festival == 1) {
               guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(this.age));
            }

            guiGraphics.drawString(font, this.text, 0, 0, this.color);
            guiGraphics.pose().popPose();
         }
      }
   }
}
