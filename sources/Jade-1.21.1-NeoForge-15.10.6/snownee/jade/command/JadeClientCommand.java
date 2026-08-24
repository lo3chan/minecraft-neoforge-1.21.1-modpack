package snownee.jade.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import snownee.jade.Jade;
import snownee.jade.gui.HomeConfigScreen;
import snownee.jade.util.DumpGenerator;

public class JadeClientCommand {
   public static <T> LiteralArgumentBuilder<T> create(
      Function<String, LiteralArgumentBuilder<T>> literalFactory, BiConsumer<T, Component> sendSuccess, BiConsumer<T, Component> sendFailure
   ) {
      return (LiteralArgumentBuilder<T>)((LiteralArgumentBuilder)literalFactory.apply("jadec").then(literalFactory.apply("handlers").executes(context -> {
         File file = new File("jade_handlers.md");

         try {
            byte var5;
            try (FileWriter writer = new FileWriter(file)) {
               writer.write(DumpGenerator.generateInfoDump());
               sendSuccess.accept((T)context.getSource(), Component.translatable("command.jade.dump.success"));
               var5 = 1;
            }

            return var5;
         } catch (IOException var9) {
            sendFailure.accept((T)context.getSource(), Component.literal(var9.getClass().getSimpleName() + ": " + var9.getMessage()));
            return 0;
         }
      }))).then(literalFactory.apply("config").executes(context -> {
         Minecraft.getInstance().tell(() -> {
            Jade.CONFIG.invalidate();
            Minecraft.getInstance().setScreen(new HomeConfigScreen(null));
         });
         return 1;
      }));
   }
}
