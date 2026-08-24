package net.mcreator.undeadrevamp.init;

import net.mcreator.undeadrevamp.client.renderer.AxestromRenderer;
import net.mcreator.undeadrevamp.client.renderer.BigsuckerRenderer;
import net.mcreator.undeadrevamp.client.renderer.BomberRenderer;
import net.mcreator.undeadrevamp.client.renderer.CloggerRenderer;
import net.mcreator.undeadrevamp.client.renderer.CoppertarRenderer;
import net.mcreator.undeadrevamp.client.renderer.CrackleballRenderer;
import net.mcreator.undeadrevamp.client.renderer.DeadcloggerRenderer;
import net.mcreator.undeadrevamp.client.renderer.FlameRenderer;
import net.mcreator.undeadrevamp.client.renderer.HonniethrowProjectileRenderer;
import net.mcreator.undeadrevamp.client.renderer.INVISIBLEBIDYRenderer;
import net.mcreator.undeadrevamp.client.renderer.InvisicloggerRenderer;
import net.mcreator.undeadrevamp.client.renderer.InvisiimmortalRenderer;
import net.mcreator.undeadrevamp.client.renderer.InvisilehceryRenderer;
import net.mcreator.undeadrevamp.client.renderer.LecheryRenderer;
import net.mcreator.undeadrevamp.client.renderer.NeocrorinesRenderer;
import net.mcreator.undeadrevamp.client.renderer.Propball1Renderer;
import net.mcreator.undeadrevamp.client.renderer.SkeeperthrowprojectileRenderer;
import net.mcreator.undeadrevamp.client.renderer.SlavemanRenderer;
import net.mcreator.undeadrevamp.client.renderer.SleepsmokebombRenderer;
import net.mcreator.undeadrevamp.client.renderer.SmokesmitterRenderer;
import net.mcreator.undeadrevamp.client.renderer.SuckerRenderer;
import net.mcreator.undeadrevamp.client.renderer.TheMoonflowerRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThebeartamerRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThebidyRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThebidyupsideRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThedungeonRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThegliterRenderer;
import net.mcreator.undeadrevamp.client.renderer.TheheavyRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThehorrorsRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThehorrorsdecoysRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThehunterRenderer;
import net.mcreator.undeadrevamp.client.renderer.TheimmortalRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThelurkerRenderer;
import net.mcreator.undeadrevamp.client.renderer.TheordureRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThepregnantRenderer;
import net.mcreator.undeadrevamp.client.renderer.TherodRenderer;
import net.mcreator.undeadrevamp.client.renderer.TheskeeperRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThesmokerRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThesomnolenceRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThespectreRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThespitterRenderer;
import net.mcreator.undeadrevamp.client.renderer.TheswarmerRenderer;
import net.mcreator.undeadrevamp.client.renderer.ThewolfRenderer;
import net.mcreator.undeadrevamp.client.renderer.WeakspotRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class UndeadRevamp2ModEntityRenderers {
   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.BOMBER.get(), BomberRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THESPECTRE.get(), ThespectreRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THESPITTER.get(), ThespitterRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEHORRORS.get(), ThehorrorsRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEHORRORSDECOYS.get(), ThehorrorsdecoysRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THESMOKER.get(), ThesmokerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THE_MOONFLOWER.get(), TheMoonflowerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.SUCKER.get(), SuckerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEHUNTER.get(), ThehunterRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEBEARTAMER.get(), ThebeartamerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEWOLF.get(), ThewolfRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.AXESTROM.get(), AxestromRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEORDURE.get(), TheordureRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.CRACKLEBALL.get(), CrackleballRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THESWARMER.get(), TheswarmerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEBIDY.get(), ThebidyRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.INVISIBLEBIDY.get(), INVISIBLEBIDYRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEBIDYUPSIDE.get(), ThebidyupsideRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEPREGNANT.get(), ThepregnantRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.FLAME.get(), FlameRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.SLAVEMAN.get(), SlavemanRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEROD.get(), TherodRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.COPPERTAR.get(), CoppertarRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEGLITER.get(), ThegliterRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEHEAVY.get(), TheheavyRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.CLOGGER.get(), CloggerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.INVISICLOGGER.get(), InvisicloggerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.PROPBALL_1.get(), Propball1Renderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.DEADCLOGGER.get(), DeadcloggerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEIMMORTAL.get(), TheimmortalRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.INVISIIMMORTAL.get(), InvisiimmortalRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.SPITTERNECC_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.ARAPOHOLIASPRAY_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.ACIDSACK_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.HONNIETHROW_PROJECTILE.get(), HonniethrowProjectileRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.QUEENBEEPERFUME_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.PREGNANTNECC_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.F_IRESACK_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.WINDTEXT_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.BOULDERTHROW_PROJECTILE.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THESKEEPER.get(), TheskeeperRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.SKEEPERTHROWPROJECTILE.get(), SkeeperthrowprojectileRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THESOMNOLENCE.get(), ThesomnolenceRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.SLEEPSMOKEBOMB.get(), SleepsmokebombRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.SMOKESMITTER.get(), SmokesmitterRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THELURKER.get(), ThelurkerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.WITHERBALL.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.NEOCRORINES.get(), NeocrorinesRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.THEDUNGEON.get(), ThedungeonRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.BIGSUCKER.get(), BigsuckerRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.LECHERY.get(), LecheryRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.WEAKSPOT.get(), WeakspotRenderer::new);
      event.registerEntityRenderer((EntityType)UndeadRevamp2ModEntities.INVISILEHCERY.get(), InvisilehceryRenderer::new);
   }
}
