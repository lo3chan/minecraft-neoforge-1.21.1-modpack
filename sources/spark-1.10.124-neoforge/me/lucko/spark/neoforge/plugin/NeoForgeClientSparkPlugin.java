package me.lucko.spark.neoforge.plugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.platform.world.WorldInfoProvider;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.neoforge.NeoForgeClientCommandSender;
import me.lucko.spark.neoforge.NeoForgePlatformInfo;
import me.lucko.spark.neoforge.NeoForgeSparkMod;
import me.lucko.spark.neoforge.NeoForgeTickHook;
import me.lucko.spark.neoforge.NeoForgeTickReporter;
import me.lucko.spark.neoforge.NeoForgeWorldInfoProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientCommandHandler;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeClientSparkPlugin extends NeoForgeSparkPlugin implements Command<CommandSourceStack>, SuggestionProvider<CommandSourceStack> {
   private final Minecraft minecraft;
   private final ThreadDumper gameThreadDumper;

   public static void register(NeoForgeSparkMod mod, FMLClientSetupEvent event) {
      NeoForgeClientSparkPlugin plugin = new NeoForgeClientSparkPlugin(mod, Minecraft.getInstance());
      plugin.enable();
   }

   public NeoForgeClientSparkPlugin(NeoForgeSparkMod mod, Minecraft minecraft) {
      super(mod);
      this.minecraft = minecraft;
      this.gameThreadDumper = new ThreadDumper.Specific(minecraft.gameThread);
   }

   @Override
   public void enable() {
      super.enable();
      NeoForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onCommandRegister(RegisterClientCommandsEvent e) {
      registerCommands(e.getDispatcher(), this, this, new String[]{"sparkc", "sparkclient"});
   }

   public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      String[] args = processArgs(context, false, new String[]{"sparkc", "sparkclient"});
      if (args == null) {
         return 0;
      } else {
         this.platform.executeCommand(new NeoForgeClientCommandSender((CommandSourceStack)context.getSource()), args);
         return 1;
      }
   }

   public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
      String[] args = processArgs(context, true, new String[]{"/sparkc", "/sparkclient"});
      return args == null
         ? Suggestions.empty()
         : this.generateSuggestions(new NeoForgeClientCommandSender((CommandSourceStack)context.getSource()), args, builder);
   }

   @Override
   public Stream<NeoForgeClientCommandSender> getCommandSenders() {
      return Stream.of(new NeoForgeClientCommandSender(ClientCommandHandler.getSource()));
   }

   @Override
   public void executeSync(Runnable task) {
      this.minecraft.executeIfPossible(task);
   }

   @Override
   public ThreadDumper getDefaultThreadDumper() {
      return this.gameThreadDumper;
   }

   @Override
   public TickHook createTickHook() {
      return new NeoForgeTickHook.Client();
   }

   @Override
   public TickReporter createTickReporter() {
      return new NeoForgeTickReporter.Client();
   }

   @Override
   public WorldInfoProvider createWorldInfoProvider() {
      return new NeoForgeWorldInfoProvider.Client(this.minecraft);
   }

   @Override
   public PlatformInfo getPlatformInfo() {
      return new NeoForgePlatformInfo(PlatformInfo.Type.CLIENT);
   }

   @Override
   public String getCommandName() {
      return "sparkc";
   }
}
