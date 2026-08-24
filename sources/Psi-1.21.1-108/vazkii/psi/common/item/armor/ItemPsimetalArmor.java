package vazkii.psi.common.item.armor;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.exosuit.IPsiEventArmor;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.api.material.PsimetalArmorMaterial;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModDataComponents;
import vazkii.psi.common.item.tool.IPsimetalTool;
import vazkii.psi.common.item.tool.ToolSocketable;
import vazkii.psi.common.lib.LibResources;

@EventBusSubscriber(
   modid = "psi"
)
public class ItemPsimetalArmor extends ArmorItem implements IPsimetalTool, IPsiEventArmor {
   public ItemPsimetalArmor(Type type, Properties props) {
      this(type, PsimetalArmorMaterial.PSIMETAL_ARMOR_MATERIAL, props);
   }

   public ItemPsimetalArmor(Type type, Holder<ArmorMaterial> mat, Properties props) {
      super(mat, type, props.component((DataComponentType)ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));
   }

   @SubscribeEvent
   public static void adjustAttributes(ItemAttributeModifierEvent event) {
      if (event.getItemStack().getItem() instanceof ItemPsimetalArmor && !IPsimetalTool.isEnabled(event.getItemStack())) {
         event.removeAllModifiersFor(Attributes.ARMOR);
         event.removeAllModifiersFor(Attributes.ARMOR_TOUGHNESS);
      }
   }

   public void setDamage(ItemStack stack, int damage) {
      if (damage > stack.getMaxDamage()) {
         damage = stack.getDamageValue();
      }

      super.setDamage(stack, damage);
   }

   @NotNull
   public String getDescriptionId(@NotNull ItemStack stack) {
      String name = super.getDescriptionId(stack);
      if (!IPsimetalTool.isEnabled(stack)) {
         name = name + ".broken";
      }

      return name;
   }

   public void inventoryTick(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull Entity entityIn, int itemSlot, boolean isSelected) {
      IPsimetalTool.regen(stack, entityIn);
   }

   @Nullable
   @Override
   public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
      return new ItemPsimetalArmor.ArmorSocketable(stack, 3);
   }

   public void cast(ItemStack stack, PsiArmorEvent event) {
      PlayerDataHandler.PlayerData data = PlayerDataHandler.get(event.getEntity());
      ItemStack playerCad = PsiAPI.getPlayerCAD(event.getEntity());
      if (IPsimetalTool.isEnabled(stack) && !playerCad.isEmpty()) {
         int timesCast = (Integer)stack.getOrDefault(ModDataComponents.TIMES_CAST, 0);
         ItemStack bullet = ISocketable.socketable(stack).getSelectedBullet();
         ItemCAD.cast(
            event.getEntity().getCommandSenderWorld(),
            event.getEntity(),
            data,
            bullet,
            playerCad,
            this.getCastCooldown(stack),
            0,
            this.getCastVolume(),
            context -> {
               context.tool = stack;
               context.attackingEntity = event.attacker;
               context.damageTaken = event.damage;
               context.loopcastIndex = timesCast;
            },
            (int)(data.calculateDamageDeduction((float)event.damage) * 0.75)
         );
         stack.set(ModDataComponents.TIMES_CAST, timesCast + 1);
      }
   }

   @Override
   public void onEvent(ItemStack stack, PsiArmorEvent event) {
      if (event.type.equals(this.getEvent(stack))) {
         this.cast(stack, event);
      }
   }

   public String getEvent(ItemStack stack) {
      return "psi.event.none";
   }

   public int getCastCooldown(ItemStack stack) {
      return 5;
   }

   public float getCastVolume() {
      return 0.025F;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
      TooltipHelper.tooltipIfShift(tooltip, () -> {
         Component componentName = ISocketable.getSocketedItemName(stack, "psimisc.none");
         tooltip.add(Component.translatable("psimisc.spell_selected", new Object[]{componentName}));
         tooltip.add(Component.translatable(this.getEvent(stack)));
      });
   }

   public boolean isRepairable(@NotNull ItemStack stack) {
      return super.isRepairable(stack);
   }

   public ResourceLocation getArmorTexture(
      @NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, @NotNull Layer layer, boolean innerModel
   ) {
      return LibResources.MODEL_PSIMETAL_EXOSUIT;
   }

   public int getColor(@NotNull ItemStack stack) {
      return -15481345;
   }

   public static class ArmorSocketable extends ToolSocketable {
      public ArmorSocketable(ItemStack tool, int slots) {
         super(tool, slots);
      }

      @Override
      public void setSelectedSlot(int slot) {
         super.setSelectedSlot(slot);
         this.tool.set(ModDataComponents.TIMES_CAST, 0);
      }

      @Override
      public void setBulletInSocket(int slot, ItemStack bullet) {
         super.setBulletInSocket(slot, bullet);
         this.tool.set(ModDataComponents.TIMES_CAST, 0);
      }
   }
}
