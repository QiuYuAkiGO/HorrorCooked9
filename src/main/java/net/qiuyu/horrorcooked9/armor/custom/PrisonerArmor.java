package net.qiuyu.horrorcooked9.armor.custom;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class PrisonerArmor extends ArmorItem {
    private static final String ARMOR_TEXTURE_PATH = "horrorcooked9:textures/item/prisoner_armor_layer_1.png";
    private static final String LEGGINGS_TEXTURE_PATH = "horrorcooked9:textures/item/prisoner_leggings_layer_1.png";

    public static final ArmorMaterial MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurabilityForType(@NotNull Type pType) {
            return 368;
        }

        @Override
        public int getDefenseForType(@NotNull Type pType) {
            return switch (pType) {
                case CHESTPLATE -> 3;
                case LEGGINGS -> 2;
                default -> 0;
            };
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public @NotNull SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.LEATHER);
        }

        @Override
        public @NotNull String getName() {
            return "horrorcooked9:prisoner";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    };

    public PrisonerArmor(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return slot == EquipmentSlot.LEGS ? LEGGINGS_TEXTURE_PATH : ARMOR_TEXTURE_PATH;
    }
}
