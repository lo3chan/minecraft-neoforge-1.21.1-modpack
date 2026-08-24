package vazkii.psi.api.spell;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.internal.IPlayerData;

public class PreSpellCastEvent extends Event implements ICancellableEvent {
   private final Player player;
   private final IPlayerData playerData;
   private final ItemStack cad;
   private final ItemStack bullet;
   private int cost;
   private float sound;
   private int particles;
   private int cooldown;
   private Spell spell;
   private SpellContext context;
   @Nullable
   private String cancellationMessage = "psimisc.canceled_spell";

   public PreSpellCastEvent(
      int cost,
      float sound,
      int particles,
      int cooldown,
      Spell spell,
      SpellContext context,
      Player player,
      IPlayerData playerData,
      ItemStack cad,
      ItemStack bullet
   ) {
      this.cost = cost;
      this.sound = sound;
      this.particles = particles;
      this.cooldown = cooldown;
      this.spell = spell;
      this.context = context;
      this.player = player;
      this.playerData = playerData;
      this.cad = cad;
      this.bullet = bullet;
   }

   @Nullable
   public String getCancellationMessage() {
      return this.cancellationMessage;
   }

   public void setCancellationMessage(@Nullable String cancellationMessage) {
      this.cancellationMessage = cancellationMessage;
   }

   public int getCost() {
      return this.cost;
   }

   public void setCost(int cost) {
      this.cost = cost;
   }

   public float getSound() {
      return this.sound;
   }

   public void setSound(float sound) {
      this.sound = sound;
   }

   public int getParticles() {
      return this.particles;
   }

   public void setParticles(int particles) {
      this.particles = particles;
   }

   public int getCooldown() {
      return this.cooldown;
   }

   public void setCooldown(int cooldown) {
      this.cooldown = cooldown;
   }

   public Spell getSpell() {
      return this.spell;
   }

   public void setSpell(Spell spell) {
      this.spell = spell;
      this.context.setSpell(spell);
   }

   public SpellContext getContext() {
      return this.context;
   }

   public void setContext(SpellContext context) {
      this.context = context;
   }

   public Player getPlayer() {
      return this.player;
   }

   public IPlayerData getPlayerData() {
      return this.playerData;
   }

   public ItemStack getCad() {
      return this.cad;
   }

   public ItemStack getBullet() {
      return this.bullet;
   }
}
