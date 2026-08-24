package vazkii.psi.common.core.handler;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.cad.RegenPsiEvent;
import vazkii.psi.api.exosuit.IPsiEventArmor;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.api.internal.IPlayerData;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.ISpellAcceptor;
import vazkii.psi.api.spell.LoopcastEndEvent;
import vazkii.psi.api.spell.PieceExecutedEvent;
import vazkii.psi.api.spell.PieceGroupAdvancementComplete;
import vazkii.psi.api.spell.PieceKnowledgeEvent;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.client.core.handler.ClientTickHandler;
import vazkii.psi.client.render.entity.RenderSpellCircle;
import vazkii.psi.common.Psi;
import vazkii.psi.common.attribute.base.ModAttributes;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.lib.LibResources;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageDataSync;
import vazkii.psi.common.network.message.MessageDeductPsi;
import vazkii.psi.common.network.message.MessagePsiOverflow;
import vazkii.psi.common.network.message.MessageTriggerJumpSpell;

public class PlayerDataHandler {
   public static final Set<SpellContext> delayedContexts = new LinkedHashSet<>();
   private static final WeakHashMap<Player, PlayerDataHandler.PlayerData> remotePlayerData = new WeakHashMap<>();
   private static final WeakHashMap<Player, PlayerDataHandler.PlayerData> playerData = new WeakHashMap<>();
   private static final String DATA_TAG = "PsiData";

   @NotNull
   public static PlayerDataHandler.PlayerData get(Player player) {
      if (player == null) {
         return new PlayerDataHandler.PlayerData();
      } else {
         Map<Player, PlayerDataHandler.PlayerData> dataMap = player.level().isClientSide ? remotePlayerData : playerData;
         PlayerDataHandler.PlayerData data = dataMap.computeIfAbsent(player, PlayerDataHandler.PlayerData::new);
         if (data.playerWR != null && data.playerWR.get() != player) {
            CompoundTag cmp = new CompoundTag();
            data.writeToNBT(cmp);
            dataMap.remove(player);
            data = get(player);
            data.readFromNBT(cmp);
         }

         return data;
      }
   }

   public static CompoundTag getDataCompoundForPlayer(Player player) {
      CompoundTag forgeData = player.getPersistentData();
      if (!forgeData.contains("PlayerPersisted")) {
         forgeData.put("PlayerPersisted", new CompoundTag());
      }

      CompoundTag persistentData = forgeData.getCompound("PlayerPersisted");
      if (!persistentData.contains("PsiData")) {
         persistentData.put("PsiData", new CompoundTag());
      }

      return persistentData.getCompound("PsiData");
   }

   @EventBusSubscriber(
      modid = "psi"
   )
   public static class EventHandler {
      @SubscribeEvent
      public static void onServerTick(Post event) {
         for (SpellContext context : new ArrayList<>(PlayerDataHandler.delayedContexts)) {
            context.delay--;
            if (context.delay <= 0) {
               PlayerDataHandler.delayedContexts.remove(context);
               context.delay = 0;
               context.cspell.safeExecute(context);
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerTick(Pre event) {
         if (!event.getEntity().isSpectator()) {
            Player player = event.getEntity();
            ItemStack cadStack = PsiAPI.getPlayerCAD(player);
            if (!cadStack.isEmpty() && cadStack.getItem() instanceof ICAD && PsiAPI.canCADBeUpdated(player)) {
               ((ICAD)cadStack.getItem()).incrementTime(cadStack);
            }

            PsiArmorEvent.post(new PsiArmorEvent(player, "psi.event.tick"));
            PlayerDataHandler.get(player).tick();
         }
      }

      @SubscribeEvent
      public static void onEntityDamage(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre event) {
         if (event.getEntity() instanceof Player player) {
            PlayerDataHandler.get(player).damage(event.getNewDamage());
            LivingEntity attacker = null;
            if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity) {
               attacker = (LivingEntity)event.getSource().getEntity();
            }

            PsiArmorEvent.post(new PsiArmorEvent(player, "psi.event.damage", event.getNewDamage(), attacker));
            if (event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE)) {
               PsiArmorEvent.post(new PsiArmorEvent(player, "psi.event.on_fire"));
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerInteractArmorStand(EntityInteractSpecific event) {
         Player player = event.getEntity();
         if (player.isSecondaryUseActive()) {
            if (event.getTarget() instanceof ArmorStand) {
               ItemStack itemStackIn = player.getItemInHand(event.getHand());
               ItemStack playerCad = PsiAPI.getPlayerCAD(player);
               if (playerCad == itemStackIn) {
                  event.setCanceled(true);
                  event.setCancellationResult(InteractionResult.PASS);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerLogin(PlayerLoggedInEvent event) {
         if (event.getEntity() instanceof ServerPlayer) {
            MessageDataSync message = new MessageDataSync(PlayerDataHandler.get(event.getEntity()));
            MessageRegister.sendToPlayer((ServerPlayer)event.getEntity(), message);
         }
      }

      @SubscribeEvent
      public static void onEntityJump(LivingJumpEvent event) {
         if (event.getEntity() instanceof Player player && event.getEntity().level().isClientSide && !event.getEntity().isSpectator()) {
            PsiArmorEvent.post(new PsiArmorEvent(player, "psi.event.jump"));
            MessageRegister.sendToServer(new MessageTriggerJumpSpell());
         }
      }

      @SubscribeEvent
      public static void onPsiArmorEvent(PsiArmorEvent event) {
         if (!event.getEntity().isSpectator()) {
            for (int i = 0; i < 4; i++) {
               ItemStack armor = (ItemStack)event.getEntity().getInventory().armor.get(i);
               if (!armor.isEmpty() && armor.getItem() instanceof IPsiEventArmor handler) {
                  handler.onEvent(armor, event);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onChangeDimension(PlayerChangedDimensionEvent event) {
         PlayerDataHandler.get(event.getEntity()).eidosChangelog.clear();
      }

      @SubscribeEvent
      @OnlyIn(Dist.CLIENT)
      public static void onRenderWorldLast(RenderLevelStageEvent event) {
         if (event.getStage() == Stage.AFTER_PARTICLES) {
            Minecraft mc = Minecraft.getInstance();
            Entity cameraEntity = mc.getCameraEntity();
            if (cameraEntity != null && mc.level != null) {
               float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);

               for (Player player : mc.level.players()) {
                  PlayerDataHandler.get(player).render(player, partialTicks, event.getPoseStack());
               }
            }
         }
      }

      @SubscribeEvent
      @OnlyIn(Dist.CLIENT)
      public static void onFOVUpdate(ComputeFovModifierEvent event) {
         PlayerDataHandler.PlayerData data = PlayerDataHandler.get(Minecraft.getInstance().player);
         if (data.isAnchored) {
            float fov = event.getNewFovModifier();
            if (data.eidosAnchorTime > 0) {
               fov *= Math.min(5.0F, data.eidosAnchorTime - ClientTickHandler.partialTicks) / 5.0F;
            } else {
               fov *= (10.0F - Math.min(10.0F, data.postAnchorRecallTime + ClientTickHandler.partialTicks)) / 10.0F;
            }

            event.setNewFovModifier(fov);
         }
      }
   }

   public static class PlayerData implements IPlayerData {
      private static final String TAG_AVAILABLE_PSI = "availablePsi";
      private static final String TAG_REGEN_CD = "regenCd";
      private static final String TAG_OVERFLOWED = "overflowed";
      private static final String TAG_EIDOS_ANCHOR_X = "eidosAnchorX";
      private static final String TAG_EIDOS_ANCHOR_Y = "eidosAnchorY";
      private static final String TAG_EIDOS_ANCHOR_Z = "eidosAnchorZ";
      private static final String TAG_EIDOS_ANCHOR_PITCH = "eidosAnchorPitch";
      private static final String TAG_EIDOS_ANCHOR_YAW = "eidosAnchorYaw";
      private static final String TAG_EIDOS_ANCHOR_TIME = "eidosAnchorTime";
      private static final String TAG_CUSTOM_DATA = "customData";
      public final Stack<Vector3> eidosChangelog = new Stack<>();
      public final List<PlayerDataHandler.PlayerData.Deduction> deductions = new ArrayList<>();
      public final WeakReference<Player> playerWR;
      private final boolean client;
      public int availablePsi;
      public int lastAvailablePsi;
      public int regenCooldown;
      public boolean loopcasting = false;
      public InteractionHand loopcastHand = null;
      public ItemStack lastTickLoopcastStack;
      public int loopcastTime = 1;
      public int loopcastAmount = 0;
      public int loopcastFadeTime = 0;
      public boolean overflowed = false;
      public Vector3 eidosAnchor = new Vector3(0.0, 0.0, 0.0);
      public double eidosAnchorPitch;
      public double eidosAnchorYaw;
      public boolean isAnchored;
      public boolean isReverting;
      public int eidosAnchorTime;
      public int postAnchorRecallTime;
      public int eidosReversionTime;
      public DimensionType lastDimension;
      public boolean deductTick;
      private boolean lowLight;
      private boolean underwater;
      private boolean lowHp;
      private CompoundTag customData;

      private PlayerData() {
         this.playerWR = new WeakReference<>(null);
         this.client = true;
      }

      public PlayerData(Player player) {
         this.playerWR = new WeakReference<>(player);
         this.client = player.getCommandSenderWorld().isClientSide;
         this.load();
      }

      public void tick() {
         Player player = this.playerWR.get();
         if (player != null) {
            DimensionType dimension = player.getCommandSenderWorld().dimensionType();
            if (this.deductTick) {
               this.deductTick = false;
            } else {
               this.lastAvailablePsi = this.availablePsi;
            }

            int max = this.getTotalPsi();
            if (this.availablePsi > max) {
               this.availablePsi = max;
            }

            ItemStack cadStack = this.getCAD();
            if (!cadStack.isEmpty()) {
               ICAD cad = (ICAD)cadStack.getItem();
               int overflow = cad.getStatValue(cadStack, EnumCADStat.OVERFLOW);
               if (overflow == -1) {
                  this.availablePsi = max;
               } else {
                  this.applyRegen(player, max, cadStack);
               }
            } else {
               this.applyRegen(player, max, cadStack);
            }

            int color = -15481345;
            if (!cadStack.isEmpty()) {
               color = Psi.proxy.getColorForCAD(cadStack);
            }

            float r = PsiRenderHelper.r(color) / 255.0F;
            float g = PsiRenderHelper.g(color) / 255.0F;
            float b = PsiRenderHelper.b(color) / 255.0F;
            if (player.isSpectator()) {
               this.stopLoopcast();
            }

            if (this.overflowed) {
               this.stopLoopcast();
            }

            if (this.loopcasting && this.loopcastHand != null) {
               ItemStack stackInHand = player.getItemInHand(this.loopcastHand);
               label236:
               if (!stackInHand.isEmpty() && ISocketable.isSocketable(stackInHand) && ISocketable.socketable(stackInHand).canLoopcast()) {
                  if (this.lastTickLoopcastStack != null) {
                     if (!ItemStack.isSameItem(this.lastTickLoopcastStack, stackInHand) || !ISocketable.isSocketable(this.lastTickLoopcastStack)) {
                        this.stopLoopcast();
                        break label236;
                     }

                     ISocketable lastTickItem = ISocketable.socketable(this.lastTickLoopcastStack);
                     ISocketable thisTickItem = ISocketable.socketable(stackInHand);
                     int lastSlot = lastTickItem.getSelectedSlot();
                     int thisSlot = thisTickItem.getSelectedSlot();
                     if (lastSlot != thisSlot) {
                        this.stopLoopcast();
                        break label236;
                     }

                     ItemStack lastTick = lastTickItem.getBulletInSocket(lastSlot);
                     ItemStack thisTick = thisTickItem.getBulletInSocket(thisSlot);
                     if (!ItemStack.matches(lastTick, thisTick)) {
                        this.stopLoopcast();
                        break label236;
                     }
                  }

                  this.lastTickLoopcastStack = stackInHand.copy();
                  ISocketable socketable = ISocketable.socketable(stackInHand);

                  for (int i = 0; i < 5; i++) {
                     double x = player.getX() + (Math.random() - 0.5) * 2.1 * player.getBbWidth();
                     double y = player.getY() + 0.35;
                     double z = player.getZ() + (Math.random() - 0.5) * 2.1 * player.getBbWidth();
                     float grav = -0.15F - (float)Math.random() * 0.03F;
                     Psi.proxy.sparkleFX(x, y, z, r, g, b, grav, 0.25F, 15);
                  }

                  if (this.loopcastTime > 0 && this.loopcastTime % 5 == 0) {
                     ItemStack bullet = socketable.getSelectedBullet();
                     if (bullet.isEmpty() || !ISpellAcceptor.hasSpell(bullet)) {
                        this.stopLoopcast();
                        break label236;
                     }

                     ISpellAcceptor spellContainer = ISpellAcceptor.acceptor(bullet);
                     Spell spell = spellContainer.getSpell();
                     SpellContext context = new SpellContext().setPlayer(player).setSpell(spell).setLoopcastIndex(this.loopcastAmount + 1);
                     context.castFrom = this.loopcastHand;
                     if (context.isValid() && context.cspell.metadata.evaluateAgainst(cadStack)) {
                        int cost = ItemCAD.getRealCost(cadStack, bullet, context.cspell.metadata.getStat(EnumSpellStat.COST));
                        if (cost > 0 || cost == -1) {
                           if (cost != -1) {
                              this.deductPsi(cost, 0, true);
                           }

                           if (!player.getCommandSenderWorld().isClientSide && this.loopcastTime % 10 == 0) {
                              player.getCommandSenderWorld()
                                 .playSound(
                                    null,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    PsiSoundHandler.loopcast,
                                    SoundSource.PLAYERS,
                                    0.1F,
                                    (float)(0.15 + Math.random() * 0.85)
                                 );
                           }
                        }

                        if (!player.getCommandSenderWorld().isClientSide && !spellContainer.loopcastSpell(context)) {
                           this.stopLoopcast();
                           break label236;
                        }

                        this.loopcastAmount++;
                     }
                  }

                  this.loopcastTime++;
               } else {
                  this.stopLoopcast();
               }
            } else if (this.loopcastFadeTime > 0) {
               this.loopcastFadeTime--;
            }

            if (!player.isAlive() || dimension != this.lastDimension) {
               this.eidosAnchorTime = 0;
               this.eidosReversionTime = 0;
               this.eidosChangelog.clear();
               this.isAnchored = false;
               this.isReverting = false;
            }

            if (this.eidosAnchorTime > 0) {
               if (this.eidosAnchorTime == 1) {
                  if (player instanceof ServerPlayer pmp) {
                     pmp.connection
                        .teleport(this.eidosAnchor.x, this.eidosAnchor.y, this.eidosAnchor.z, (float)this.eidosAnchorYaw, (float)this.eidosAnchorPitch);

                     for (Entity riding = player.getVehicle(); riding != null; riding = riding.getVehicle()) {
                        riding.setPos(this.eidosAnchor.x, this.eidosAnchor.y, this.eidosAnchor.z);
                     }
                  }

                  this.postAnchorRecallTime = 0;
               }

               this.eidosAnchorTime--;
            } else if (this.postAnchorRecallTime < 5) {
               this.postAnchorRecallTime--;
               this.isAnchored = false;
            }

            if (this.eidosReversionTime > 0) {
               if (this.eidosChangelog.isEmpty()) {
                  this.eidosReversionTime = 0;
                  this.isReverting = false;
               } else {
                  this.eidosChangelog.pop();
                  if (this.eidosChangelog.isEmpty()) {
                     this.eidosReversionTime = 0;
                     this.isReverting = false;
                  } else {
                     Vector3 vec = this.eidosChangelog.pop();
                     if (player instanceof ServerPlayer pmp) {
                        pmp.connection.teleport(vec.x, vec.y, vec.z, 0.0F, 0.0F, ImmutableSet.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT));
                        pmp.connection.resetPosition();
                     } else {
                        player.setPos(vec.x, vec.y, vec.z);
                     }

                     for (Entity riding = player.getVehicle(); riding != null; riding = riding.getVehicle()) {
                        riding.setPos(vec.x, vec.y, vec.z);
                     }

                     if (player.level().isClientSide) {
                        for (int i = 0; i < 5; i++) {
                           double spread = 0.6;
                           double x = player.getX() + (Math.random() - 0.5) * spread;
                           double y = player.getY() + (Math.random() - 0.5) * spread;
                           double z = player.getZ() + (Math.random() - 0.5) * spread;
                           Psi.proxy.sparkleFX(x, y, z, r, g, b, 0.0F, 0.0F, 0.0F, 1.2F, 12);
                        }
                     }

                     player.setDeltaMovement(0.0, 0.0, 0.0);
                     player.fallDistance = 0.0F;
                  }
               }

               this.eidosReversionTime--;
               if (this.eidosReversionTime == 0 || player.isShiftKeyDown()) {
                  this.eidosChangelog.clear();
                  this.isReverting = false;
               }
            } else {
               if (this.eidosChangelog.size() > 600) {
                  this.eidosChangelog.removeFirst();
               }

               this.eidosChangelog.push(Vector3.fromEntity(player));
            }

            BlockPos pos = player.blockPosition();
            int light = player.getCommandSenderWorld().getLightEngine().getRawBrightness(pos, 0);
            boolean lowLight = light == 0;
            if (!this.lowLight && lowLight) {
               PsiArmorEvent.post(new PsiArmorEvent(player, "psi.event.low_light"));
            }

            this.lowLight = lowLight;
            boolean underwater = player.isInWater();
            if (!this.underwater && underwater) {
               PsiArmorEvent.post(new PsiArmorEvent(player, "psi.event.underwater"));
            }

            this.underwater = underwater;
            boolean lowHp = player.getHealth() <= 6.0F;
            if (!this.lowHp && lowHp) {
               PsiArmorEvent.post(new PsiArmorEvent(player, "psi.event.low_hp"));
            }

            this.lowHp = lowHp;
            List<PlayerDataHandler.PlayerData.Deduction> remove = new ArrayList<>();

            for (PlayerDataHandler.PlayerData.Deduction d : this.deductions) {
               if (d.invalid) {
                  remove.add(d);
               } else {
                  d.tick();
               }
            }

            this.deductions.removeAll(remove);
            this.lastDimension = dimension;
         }
      }

      private void applyRegen(Player player, int max, ItemStack cadStack) {
         RegenPsiEvent event = new RegenPsiEvent(player, this, cadStack);
         if (!((RegenPsiEvent)NeoForge.EVENT_BUS.post(event)).isCanceled()) {
            if (!cadStack.isEmpty()) {
               ICAD cad = (ICAD)cadStack.getItem();
               cad.regenPsi(cadStack, event.getCadRegen());
            }

            boolean anyChange = this.availablePsi != max && event.getPlayerRegen() > 0;
            int prevPsi = this.availablePsi;
            this.availablePsi = Math.min(max, this.availablePsi + event.getPlayerRegen());
            if (this.overflowed && event.willHealOverflow()) {
               anyChange = true;
               this.overflowed = false;
            }

            if (this.regenCooldown != event.getRegenCooldown()) {
               anyChange = true;
            }

            this.regenCooldown = event.getRegenCooldown();
            if (anyChange) {
               if (player instanceof ServerPlayer) {
                  MessageDeductPsi message = new MessageDeductPsi(prevPsi, this.availablePsi, this.regenCooldown, false);
                  MessageRegister.sendToPlayer((ServerPlayer)player, message);
               }

               this.save();
            }
         }
      }

      public void stopLoopcast() {
         Player player = this.playerWR.get();
         if (this.loopcasting) {
            this.loopcastFadeTime = 5;
            NeoForge.EVENT_BUS.post(new LoopcastEndEvent(player, this, this.loopcastHand, this.loopcastAmount));
         }

         this.loopcasting = false;
         this.lastTickLoopcastStack = null;
         this.loopcastHand = null;
         this.loopcastTime = 1;
         this.loopcastAmount = 0;
         if (player instanceof ServerPlayer) {
            LoopcastTrackingHandler.syncForTrackersAndSelf((ServerPlayer)player);
         }
      }

      public int calculateDamageDeduction(float amount) {
         return (int)(this.getTotalPsi() * 0.02 * amount);
      }

      public void damage(float amount) {
         int psi = this.calculateDamageDeduction(amount);
         if (psi > 0 && this.availablePsi > 0) {
            psi = Math.min(psi, this.availablePsi);
            this.deductPsi(psi, 20, true, true);
         }
      }

      public ItemStack getCAD() {
         return PsiAPI.getPlayerCAD(this.playerWR.get());
      }

      public void deductPsi(int psi, int cd, boolean sync) {
         this.deductPsi(psi, cd, sync, false);
      }

      @Override
      public void deductPsi(int psi, int cd, boolean sync, boolean shatter) {
         int currentPsi = this.availablePsi;
         Player player = this.playerWR.get();
         if (player != null) {
            ItemStack cadStack = this.getCAD();
            if (!cadStack.isEmpty()) {
               ICAD cad = (ICAD)cadStack.getItem();
               int storedPsi = cad.getStoredPsi(cadStack);
               if (storedPsi == -1) {
                  return;
               }
            }

            this.availablePsi -= psi;
            if (this.regenCooldown < cd) {
               this.regenCooldown = cd;
            }

            if (this.availablePsi < 0) {
               int overflow = -this.availablePsi;
               this.availablePsi = 0;
               if (!cadStack.isEmpty()) {
                  ICAD cad = (ICAD)cadStack.getItem();
                  overflow = cad.consumePsi(cadStack, overflow);
               }

               if (!shatter && overflow > 0) {
                  float dmg = (float)overflow / (this.loopcasting ? 50 : 125);
                  if (!this.client) {
                     Registry<DamageType> types = player.damageSources().damageTypes;
                     DamageSource overloadSource = new DamageSource(types.getHolderOrThrow(LibResources.PSI_OVERLOAD));
                     player.hurt(overloadSource, dmg);
                  }

                  this.overflowed = true;
                  if (sync && player instanceof ServerPlayer) {
                     MessagePsiOverflow message = new MessagePsiOverflow(true);
                     MessageRegister.sendToPlayer((ServerPlayer)player, message);
                  }
               }
            }

            if (sync && player instanceof ServerPlayer) {
               MessageDeductPsi message = new MessageDeductPsi(currentPsi, this.availablePsi, this.regenCooldown, shatter);
               MessageRegister.sendToPlayer((ServerPlayer)player, message);
            }

            this.save();
         }
      }

      public void addDeduction(int current, int deduct, boolean shatter) {
         if (deduct > current) {
            deduct = current;
         }

         if (deduct < 0) {
            deduct = 0;
         }

         if (deduct != 0) {
            this.deductions.add(new PlayerDataHandler.PlayerData.Deduction(current, deduct, 20, shatter));
         }
      }

      @Override
      public int getAvailablePsi() {
         return this.availablePsi;
      }

      @Override
      public int getLastAvailablePsi() {
         return this.lastAvailablePsi;
      }

      @Override
      public int getTotalPsi() {
         Player player = this.playerWR.get();
         return player != null ? (int)player.getAttributeValue(ModAttributes.TOTAL_PSI) : (int)((Attribute)ModAttributes.TOTAL_PSI.get()).getDefaultValue();
      }

      @Override
      public int getRegenPerTick() {
         Player player = this.playerWR.get();
         return player != null ? (int)player.getAttributeValue(ModAttributes.REGEN) : (int)((Attribute)ModAttributes.REGEN.get()).getDefaultValue();
      }

      @Override
      public boolean isOverflowed() {
         return this.overflowed;
      }

      @Override
      public int getRegenCooldown() {
         return this.regenCooldown;
      }

      public boolean hasAdvancement(ResourceLocation group) {
         Player player = this.playerWR.get();
         return Psi.proxy.hasAdvancement(group, player);
      }

      @Override
      public boolean isPieceGroupUnlocked(ResourceLocation group, @Nullable ResourceLocation name) {
         Player player = this.playerWR.get();
         if (player == null) {
            return false;
         } else if (player.isCreative()) {
            return true;
         } else {
            boolean hasAdvancement = this.hasAdvancement(group);
            PieceKnowledgeEvent event = new PieceKnowledgeEvent(group, name, player, this, hasAdvancement);
            NeoForge.EVENT_BUS.post(event);
            return !event.isCanceled();
         }
      }

      @Override
      public void unlockPieceGroup(ResourceLocation resourceLocation) {
         Player player = this.playerWR.get();
         if (player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.getServer() == null) {
               return;
            }

            AdvancementHolder advancement = serverPlayer.getServer().getAdvancements().get(resourceLocation);
            if (advancement != null && !serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
               for (String s : serverPlayer.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria()) {
                  serverPlayer.getAdvancements().getOrStartProgress(advancement).grantProgress(s);
               }
            }
         }
      }

      @Override
      public void markPieceExecuted(SpellPiece piece) {
         Player player = this.playerWR.get();
         if (player != null) {
            PieceExecutedEvent event = new PieceExecutedEvent(piece, player);
            NeoForge.EVENT_BUS.post(event);
            Optional<Entry<ResourceKey<Collection<Class<? extends SpellPiece>>>, Collection<Class<? extends SpellPiece>>>> advancementEntry = PsiAPI.ADVANCEMENT_GROUP_REGISTRY
               .entrySet()
               .stream()
               .filter(entry -> entry.getValue().contains(piece.getClass()))
               .findFirst();
            if (!advancementEntry.isEmpty()) {
               ResourceLocation advancement = advancementEntry.get().getKey().location();
               Object advancementMainPieceClass = advancementEntry.get().getValue().toArray()[0];
               if (advancementMainPieceClass == piece.getClass() && !this.hasAdvancement(advancement)) {
                  NeoForge.EVENT_BUS.post(new PieceGroupAdvancementComplete(piece, player, advancement));
               }
            }
         }
      }

      @Override
      public CompoundTag getCustomData() {
         return this.customData == null ? (this.customData = new CompoundTag()) : this.customData;
      }

      @Override
      public void save() {
         if (!this.client) {
            Player player = this.playerWR.get();
            if (player != null) {
               CompoundTag cmp = PlayerDataHandler.getDataCompoundForPlayer(player);
               this.writeToNBT(cmp);
            }
         }
      }

      public void writeToNBT(CompoundTag cmp) {
         cmp.putInt("availablePsi", this.availablePsi);
         cmp.putInt("regenCd", this.regenCooldown);
         cmp.putBoolean("overflowed", this.overflowed);
         cmp.putDouble("eidosAnchorX", this.eidosAnchor.x);
         cmp.putDouble("eidosAnchorY", this.eidosAnchor.y);
         cmp.putDouble("eidosAnchorZ", this.eidosAnchor.z);
         cmp.putDouble("eidosAnchorPitch", this.eidosAnchorPitch);
         cmp.putDouble("eidosAnchorYaw", this.eidosAnchorYaw);
         cmp.putInt("eidosAnchorTime", this.eidosAnchorTime);
         if (this.customData != null) {
            cmp.put("customData", this.customData);
         }
      }

      public void load() {
         if (!this.client) {
            Player player = this.playerWR.get();
            if (player != null) {
               CompoundTag cmp = PlayerDataHandler.getDataCompoundForPlayer(player);
               this.readFromNBT(cmp);
            }
         }
      }

      public void readFromNBT(CompoundTag cmp) {
         this.availablePsi = cmp.getInt("availablePsi");
         this.regenCooldown = cmp.getInt("regenCd");
         this.overflowed = cmp.getBoolean("overflowed");
         double x = cmp.getDouble("eidosAnchorX");
         double y = cmp.getDouble("eidosAnchorX");
         double z = cmp.getDouble("eidosAnchorX");
         this.eidosAnchor.set(x, y, z);
         this.eidosAnchorPitch = cmp.getDouble("eidosAnchorPitch");
         this.eidosAnchorYaw = cmp.getDouble("eidosAnchorYaw");
         this.eidosAnchorTime = cmp.getInt("eidosAnchorTime");
         this.customData = cmp.getCompound("customData");
      }

      @OnlyIn(Dist.CLIENT)
      public void render(Player player, float partTicks, PoseStack ms) {
         EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
         double x = player.xOld + (player.getX() - player.xOld) * partTicks - renderManager.camera.getPosition().x;
         double y = player.yOld + (player.getY() - player.yOld) * partTicks - renderManager.camera.getPosition().y;
         double z = player.zOld + (player.getZ() - player.zOld) * partTicks - renderManager.camera.getPosition().z;
         float scale = 0.75F;
         if (this.loopcasting) {
            float mul = Math.min(5.0F, this.loopcastTime + partTicks) / 5.0F;
            scale *= mul;
         } else {
            if (this.loopcastFadeTime <= 0) {
               return;
            }

            float mul = Math.min(5.0F, this.loopcastFadeTime - partTicks) / 5.0F;
            scale *= mul;
         }

         int color = -15481345;
         ItemStack cad = PsiAPI.getPlayerCAD(this.playerWR.get());
         if (!cad.isEmpty() && cad.getItem() instanceof ICAD icad) {
            color = icad.getSpellColor(cad);
         }

         ms.pushPose();
         ms.translate(x, y + 0.15, z);
         BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
         RenderSpellCircle.renderSpellCircle(ClientTickHandler.ticksInGame + partTicks, scale, 1.0F, 0.0F, -1.0F, 0.0F, color, ms, buffers);
         buffers.endBatch();
         ms.popPose();
      }

      public static class Deduction {
         public final int current;
         public final int deduct;
         public final int cd;
         public final boolean shatter;
         public int elapsed;
         public boolean invalid;

         public Deduction(int current, int deduct, int cd, boolean shatter) {
            this.current = current;
            this.deduct = deduct;
            this.cd = cd;
            this.shatter = shatter;
         }

         public void tick() {
            this.elapsed++;
            if (this.elapsed >= this.cd) {
               this.invalid = true;
            }
         }

         public float getPercentile(float partTicks) {
            return 1.0F - Math.min(1.0F, (this.elapsed + partTicks) / this.cd);
         }
      }
   }
}
