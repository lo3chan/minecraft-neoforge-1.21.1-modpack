package xxrexraptorxx.additionalstructures.utils;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.XubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.VersionChecker;
import net.neoforged.fml.VersionChecker.CheckResult;
import net.neoforged.fml.VersionChecker.Status;
import net.neoforged.fml.common.XventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import xxrexraptorxx.additionalstructures.main.AdditionalStructures;

@XventBusSubscriber(
   modid = "additionalstructures",
   bus = Bus.GAME
)
public class Events {
   private static boolean hasShownUp = false;

   @XubscribeEvent
   public static void onClientTick(Pre event) {
      if (Config.UPDATE_CHECKER != null && (Boolean)Config.UPDATE_CHECKER.get() && !hasShownUp && Minecraft.getInstance().screen == null) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player == null) {
            return;
         }

         ModContainer modContainer = (ModContainer)ModList.get().getModContainerById("additionalstructures").orElse(null);
         if (modContainer != null) {
            CheckResult versionCheckResult = VersionChecker.getResult(modContainer.getModInfo());
            if (versionCheckResult.status() == Status.OUTDATED || versionCheckResult.status() == Status.BETA_OUTDATED) {
               MutableComponent url = Component.literal(ChatFormatting.GREEN + "Click here to update!");
               url.withStyle(
                  url.getStyle().withClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/additional-structures"))
               );
               player.displayClientMessage(
                  Component.literal(
                     ChatFormatting.BLUE + "A newer version of " + ChatFormatting.YELLOW + "Additional Structures" + ChatFormatting.BLUE + " is available!"
                  ),
                  false
               );
               player.displayClientMessage(url, false);
               hasShownUp = true;
            } else if (versionCheckResult.status() == Status.FAILED) {
               AdditionalStructures.LOGGER.error("Additional Structures's version checker failed!");
               hasShownUp = true;
            }
         }
      }
   }

   @XubscribeEvent
   public static void SupporterRewards(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      Level level = player.level();
      if (Config.PATREON_REWARDS != null && (Boolean)Config.PATREON_REWARDS.get()) {
         try {
            URL SUPPORTER_URL = new URL("https://raw.githubusercontent.com/XxRexRaptorxX/Patreons/main/Supporter");
            URL PREMIUM_SUPPORTER_URL = new URL("https://raw.githubusercontent.com/XxRexRaptorxX/Patreons/main/Premium%20Supporter");
            URL ELITE_URL = new URL("https://raw.githubusercontent.com/XxRexRaptorxX/Patreons/main/Elite");
            if (!player.getInventory().contains(new ItemStack(Items.PAPER))) {
               ServerPlayer serverPlayer = (ServerPlayer)player;
               if (serverPlayer.getStats().getValue(Stats.CUSTOM, Stats.PLAY_TIME) < 5) {
                  if (SupporterCheck(SUPPORTER_URL, player)) {
                     ItemStack certificate = new ItemStack(Items.PAPER);
                     certificate.set(
                        DataComponents.CUSTOM_NAME,
                        Component.literal("Thank you for supporting me in my work!")
                           .withStyle(ChatFormatting.GOLD)
                           .append(Component.literal(" - XxRexRaptorxX").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GREEN))
                     );
                     ItemStack reward = new ItemStack(Items.PLAYER_HEAD);
                     GameProfile profile = new GameProfile(player.getUUID(), player.getName().getString());
                     reward.set(DataComponents.PROFILE, new ResolvableProfile(profile));
                     level.playSound(
                        (Player)null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, level.random.nextFloat() * 0.15F + 0.8F
                     );
                     player.addItem(reward);
                     player.addItem(certificate);
                  }

                  if (SupporterCheck(PREMIUM_SUPPORTER_URL, player)) {
                     ItemStack reward = new ItemStack(Items.DIAMOND_SWORD, 1);
                     Registry<Enchantment> enchantmentsRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
                     reward.enchant(enchantmentsRegistry.getHolderOrThrow(Enchantments.MENDING), 1);
                     reward.enchant(enchantmentsRegistry.getHolderOrThrow(Enchantments.SHARPNESS), 3);
                     reward.set(DataComponents.ENCHANTMENTS, (ItemEnchantments)reward.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
                     reward.set(DataComponents.CUSTOM_NAME, Component.literal("Rex's Night Sword").withStyle(ChatFormatting.DARK_GRAY));
                     player.addItem(reward);
                  }

                  if (SupporterCheck(ELITE_URL, player)) {
                     ItemStack star = new ItemStack(Items.NETHER_STAR);
                     star.set(DataComponents.CUSTOM_NAME, Component.literal("Elite Star"));
                     player.addItem(star);
                  }
               }
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }
      }
   }

   private static boolean SupporterCheck(URL url, Player player) {
      try {
         Scanner scanner = new Scanner(url.openStream());

         for (String name : scanner.tokens().toList()) {
            if (player.getName().getString().equals(name)) {
               return true;
            }
         }

         scanner.close();
      } catch (MalformedURLException var6) {
         AdditionalStructures.LOGGER.error("Supporter list URL not found! >>" + url);
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return false;
   }

   @XubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         Path configDir = FMLPaths.CONFIGDIR.get();
         Path marker = configDir.resolve("#STOP_MOD_REPOSTS.txt");

         try {
            if (Files.notExists(marker)) {
               String fileContent = "    Sites like 9minecraft.net, mc-mod.net, and many others, are known for reuploading mod files without permission from the authors. These sites will also contain a bunch of ads, to try to make money from mods they did not create.\n\n    These sites will use various methods to appear higher in Google when you search for the mod name, so a lot of people will download mods from them instead of the proper place. If you were linked to this site, you're one of these people.\n\n    FOR YOU, AS A PLAYER, THIS CAN MEAN ANY OF THE FOLLOWING:\n    > Getting versions of the mods advertised for the wrong Minecraft versions, which will 100% crash when you load them.\n    > Getting old, and broken, versions of the mods, possibly causing problems in your game.\n    > Getting modified versions of the mods, which may contain malware and viruses.\n    > Having your information stolen from malicious ads in the sites.\n    > Taking money and views away from the official authors, which may cause them to stop making new mods.\n\n    WHAT DO I DO NOW?\n    The most important thing to do now is to make sure you stop visiting these sites, and get the mods from official sources. We also recommend you do the following:\n\n    > Delete all the mods you've downloaded from these sites.\n    > Install the StopModReposts plugin, which makes sure you never visit them again.\n    > Run a virus/malware scan. We recommend MalwareBytes.\n    > Check out the #StopModReposts campaign, that tries to put an end to these sites. (https://stopmodreposts.org/)\n    > Spread the word. If you have any friends that use these sites, inform them to keep them safe.\n\n    WHERE DO I GET MODS NOW?\n    Here's a bunch of links to places where you can download official versions of mods, hosted by their real authors:\n\n    > CurseForge, where most modders host their mods. If it exists, it's probably there.\n    > Modrinth, a new hosting platform for mods that's also legit and more popular by the day.\n    > OptiFine.net, the official OptiFine site.\n    > Neoforged.net, which you need for any other Neoforge mods.\n    > FabricMC.net, which you need for any other Fabric mods.\n    > MinecraftForge Files, which you need for any other Forge mods.\n\n    This doesn't mean other sites aren't legit. In general, the first place to look for a mod is CurseForge and Modrinth, so look there first.\n\n    FAQ\n    Q: What if I've never had problems before?\n    > Just because you've never had problems with these sites before doesn't mean they're good. You should still avoid them for all the reasons listed above.\n\n    Q: Is there a list of these sites I can check out?\n    > Yes, however, due to these showing up all the time, it's possible to be incomplete. (https://github.com/StopModReposts/Illegal-Mod-Sites/blob/master/SITES.md)\n\n    Q: Why can't you just take these sites down?\n    > Unfortunately, these sites are often hosted in countries like Russia or Vietnam, where doing so isn't as feasible.\n\n    Q: What if it says \"Official Download\" on the sites?\n    > Sometimes they'll do that to trick you. If you're uncertain, you should verify with the StopModReposts list linked above.\n\n\n    Credits: XxRexRaptorxX, Vazkii, StopModReposts campaign\n";
               Files.writeString(marker, fileContent, StandardCharsets.UTF_8);
            }
         } catch (IOException var5) {
            AdditionalStructures.LOGGER.error(var5);
         }
      }
   }
}
