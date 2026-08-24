package vazkii.psi.common.lib;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import vazkii.psi.common.Psi;

public class LibResources {
   public static final String PREFIX_MOD = "psi:";
   public static final ResourceLocation PATCHOULI_BOOK = Psi.location("encyclopaedia_psionica");
   public static final ResourceKey<DamageType> PSI_OVERLOAD = ResourceKey.create(Registries.DAMAGE_TYPE, Psi.location("psi_overload"));
   public static final String PREFIX_GUI = "psi:textures/gui/";
   public static final String GUI_CAD_ASSEMBLER = "psi:textures/gui/cad_assembler.png";
   public static final String GUI_PSI_BAR = "psi:textures/gui/psi_bar.png";
   public static final String GUI_PSI_BAR_MASK = "psi:textures/gui/psi_bar_mask.png";
   public static final String GUI_PSI_BAR_SHATTER = "psi:textures/gui/psi_bar_shatter.png";
   public static final String GUI_SIGN = "psi:textures/gui/signs/sign%d.png";
   public static final String GUI_PROGRAMMER = "psi:textures/gui/programmer.png";
   public static final String GUI_CREATIVE = "psi.png";
   public static final String PREFIX_MODEL = "textures/model/";
   public static final ResourceLocation MODEL_PSIMETAL_EXOSUIT = Psi.location("textures/model/psimetal_exosuit.png");
   public static final ResourceLocation MODEL_PSIMETAL_EXOSUIT_SENSOR = Psi.location("textures/model/psimetal_exosuit_sensor.png");
   public static final String PREFIX_MISC = "psi:textures/misc/";
   public static final String MISC_SPELL_CIRCLE = "psi:textures/misc/spell_circle%d.png";
   public static final String SHADER_PSI_BAR = "psi_bar";
   public static final String SPELL_CONNECTOR_LINES = "psi:spell/connector_lines";
}
