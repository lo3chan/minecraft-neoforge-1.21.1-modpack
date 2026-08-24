package io.wispforest.owo.mixin.tweaks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;
import net.minecraft.server.Eula;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Eula.class})
public abstract class EulaReaderMixin {
   @Shadow
   @Final
   private static Logger LOGGER;
   @Shadow
   @Final
   private Path file;

   @Shadow
   public abstract boolean hasAgreedToEULA();

   @Inject(
      method = {"readFile()Z"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void overrideEulaAgreement(CallbackInfoReturnable<Boolean> cir) {
      if (!this.hasAgreedToEULA()) {
         Scanner scanner = new Scanner(System.in);
         LOGGER.info(
            "By answering 'true' to this prompt you are indicating your agreement to Minecraft's EULA (https://account.mojang.com/documents/minecraft_eula)\nEULA:"
         );
         String input = scanner.next();
         if (input.equalsIgnoreCase("true")) {
            try (
               InputStream inStream = Files.newInputStream(this.file);
               OutputStream outStream = Files.newOutputStream(this.file);
            ) {
               Properties properties = new Properties();
               properties.load(inStream);
               properties.setProperty("eula", "true");
               properties.store(
                  outStream,
                  "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)."
               );
            } catch (IOException var12) {
               LOGGER.info("Could not accept eula", var12);
            }

            LOGGER.info("EULA accepted");
            cir.setReturnValue(true);
         }
      }
   }
}
