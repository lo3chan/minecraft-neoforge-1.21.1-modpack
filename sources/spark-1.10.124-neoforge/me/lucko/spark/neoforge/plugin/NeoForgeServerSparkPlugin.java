package me.lucko.spark.neoforge.plugin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.lucko.spark.common.monitor.ping.PlayerPingProvider;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.platform.serverconfig.ServerConfigProvider;
import me.lucko.spark.common.platform.world.WorldInfoProvider;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.neoforge.NeoForgePlatformInfo;
import me.lucko.spark.neoforge.NeoForgePlayerPingProvider;
import me.lucko.spark.neoforge.NeoForgeServerCommandSender;
import me.lucko.spark.neoforge.NeoForgeServerConfigProvider;
import me.lucko.spark.neoforge.NeoForgeSparkMod;
import me.lucko.spark.neoforge.NeoForgeTickHook;
import me.lucko.spark.neoforge.NeoForgeTickReporter;
import me.lucko.spark.neoforge.NeoForgeWorldInfoProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent.Nodes;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode.PermissionResolver;

public class NeoForgeServerSparkPlugin extends NeoForgeSparkPlugin implements Command<CommandSourceStack>, SuggestionProvider<CommandSourceStack> {
   private static final PermissionResolver<Boolean> DEFAULT_PERMISSION_VALUE = (player, playerUUID, context) -> {
      if (player == null) {
         return false;
      } else {
         MinecraftServer server = player.getServer();
         return server != null && server.isSingleplayerOwner(player.getGameProfile()) ? true : player.hasPermissions(4);
      }
   };
   private final MinecraftServer server;
   private final ThreadDumper gameThreadDumper;
   private Map<String, PermissionNode<Boolean>> registeredPermissions = Collections.emptyMap();

   public static void register(NeoForgeSparkMod mod, ServerAboutToStartEvent event) {
      NeoForgeServerSparkPlugin plugin = new NeoForgeServerSparkPlugin(mod, event.getServer());
      plugin.enable();
   }

   public NeoForgeServerSparkPlugin(NeoForgeSparkMod mod, MinecraftServer server) {
      super(mod);
      this.server = server;
      this.gameThreadDumper = new ThreadDumper.Specific(server.getRunningThread());
   }

   @Override
   public void enable() {
      super.enable();
      this.registerCommands(this.server.getCommands().getDispatcher());
      NeoForge.EVENT_BUS.register(this);
   }

   @Override
   public void disable() {
      super.disable();
      NeoForge.EVENT_BUS.unregister(this);
   }

   @SubscribeEvent
   public void onDisable(ServerStoppingEvent event) {
      if (event.getServer() == this.server) {
         this.disable();
      }
   }

   @SubscribeEvent
   public void onPermissionGather(Nodes e) {
      List<String> permissions = this.platform.getCommands().stream().map(me.lucko.spark.common.command.Command::primaryAlias).collect(Collectors.toList());
      permissions.add("all");
      Builder<String, PermissionNode<Boolean>> builder = ImmutableMap.builder();
      Map<String, PermissionNode<?>> alreadyRegistered = e.getNodes().stream().collect(Collectors.toMap(PermissionNode::getNodeName, Function.identity()));

      for (String permission : permissions) {
         String permissionString = "spark." + permission;
         PermissionNode<?> existing = alreadyRegistered.get(permissionString);
         if (existing != null) {
            builder.put(permissionString, existing);
         } else {
            PermissionNode<Boolean> node = new PermissionNode(
               "spark", permission, PermissionTypes.BOOLEAN, DEFAULT_PERMISSION_VALUE, new PermissionDynamicContextKey[0]
            );
            e.addNodes(new PermissionNode[]{node});
            builder.put(permissionString, node);
         }
      }

      this.registeredPermissions = builder.build();
   }

   @SubscribeEvent
   public void onCommandRegister(RegisterCommandsEvent e) {
      this.registerCommands(e.getDispatcher());
   }

   private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
      registerCommands(dispatcher, this, this, new String[]{"spark"});
   }

   public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      String[] args = processArgs(context, false, new String[]{"/spark", "spark"});
      if (args == null) {
         return 0;
      } else {
         this.platform.executeCommand(new NeoForgeServerCommandSender((CommandSourceStack)context.getSource(), this), args);
         return 1;
      }
   }

   public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
      String[] args = processArgs(context, true, new String[]{"/spark", "spark"});
      return args == null
         ? Suggestions.empty()
         : this.generateSuggestions(new NeoForgeServerCommandSender((CommandSourceStack)context.getSource(), this), args, builder);
   }

   public boolean hasPermission(CommandSourceStack source, String permission) {
      ServerPlayer player = source.getPlayer();
      if (player != null) {
         if (permission.equals("spark")) {
            permission = "spark.all";
         }

         PermissionNode<Boolean> permissionNode = this.registeredPermissions.get(permission);
         if (permissionNode == null) {
            throw new IllegalStateException("spark permission not registered: " + permission);
         } else {
            return (Boolean)PermissionAPI.getPermission(player, permissionNode, new PermissionDynamicContext[0]);
         }
      } else {
         return true;
      }
   }

   @Override
   public Stream<NeoForgeServerCommandSender> getCommandSenders() {
      return Stream.concat(
            this.server.getPlayerList().getPlayers().stream().map(Entity::createCommandSourceStack), Stream.of(this.server.createCommandSourceStack())
         )
         .map(stack -> new NeoForgeServerCommandSender(stack, this));
   }

   @Override
   public void executeSync(Runnable task) {
      this.server.executeIfPossible(task);
   }

   @Override
   public ThreadDumper getDefaultThreadDumper() {
      return this.gameThreadDumper;
   }

   @Override
   public TickHook createTickHook() {
      return new NeoForgeTickHook.Server();
   }

   @Override
   public TickReporter createTickReporter() {
      return new NeoForgeTickReporter.Server();
   }

   @Override
   public PlayerPingProvider createPlayerPingProvider() {
      return new NeoForgePlayerPingProvider(this.server);
   }

   @Override
   public ServerConfigProvider createServerConfigProvider() {
      return new NeoForgeServerConfigProvider();
   }

   @Override
   public WorldInfoProvider createWorldInfoProvider() {
      return new NeoForgeWorldInfoProvider.Server(this.server);
   }

   @Override
   public PlatformInfo getPlatformInfo() {
      return new NeoForgePlatformInfo(PlatformInfo.Type.SERVER);
   }

   @Override
   public String getCommandName() {
      return "spark";
   }
}
