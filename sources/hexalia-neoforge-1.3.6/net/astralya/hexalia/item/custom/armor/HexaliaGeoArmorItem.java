package net.astralya.hexalia.item.custom.armor;

import java.util.List;
import java.util.function.Consumer;
import net.astralya.hexalia.util.MagicResistanceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;

public class HexaliaGeoArmorItem extends ArmorItem implements GeoItem {
   private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
   private final ResourceLocation modelResource;
   private final ResourceLocation textureResource;
   private final ResourceLocation animationResource;

   public HexaliaGeoArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, String name, String texture) {
      this(material, type, properties, name, texture, "animations/" + name + ".animation.json");
   }

   public HexaliaGeoArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, String name, String texture, String animationPath) {
      super(material, type, properties);
      this.modelResource = ResourceLocation.fromNamespaceAndPath("hexalia", "geo/item/armor/" + name + ".geo.json");
      this.textureResource = ResourceLocation.fromNamespaceAndPath("hexalia", "textures/armor/" + texture + ".png");
      this.animationResource = ResourceLocation.fromNamespaceAndPath("hexalia", animationPath);
      SingletonGeoAnimatable.registerSyncedAnimatable(this);
   }

   public ResourceLocation modelResource() {
      return this.modelResource;
   }

   public ResourceLocation textureResource() {
      return this.textureResource;
   }

   public ResourceLocation animationResource() {
      return this.animationResource;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      float magicResistance = MagicResistanceHelper.getMagicResistancePct(stack);
      if (magicResistance > 0.0F) {
         tooltip.add(
            Component.translatable("tooltip.hexalia.magic_resistance", new Object[]{MagicResistanceHelper.formatPercent(magicResistance)})
               .withStyle(ChatFormatting.BLUE)
         );
      }
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(this.createClientRenderProvider());
   }

   private GeoRenderProvider createClientRenderProvider() {
      try {
         Class<?> rendererClass = Class.forName("net.astralya.hexalia.client.renderer.item.HexaliaArmorRenderer");
         return (GeoRenderProvider)rendererClass.getMethod("createRenderProvider", HexaliaGeoArmorItem.class).invoke(null, this);
      } catch (ReflectiveOperationException var2) {
         throw new IllegalStateException("Unable to create Hexalia armor renderer", var2);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, state -> {
         state.getController().setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
         return PlayState.CONTINUE;
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
