package net.qiuyu.horrorcooked9.armor.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.qiuyu.horrorcooked9.register.ModEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.qiuyu.horrorcooked9.armor.renderer.CaptainHatRenderer;

public class CaptainHat extends ArmorItem {
    private static final String ARMOR_TEXTURE_PATH = "horrorcooked9:textures/item/captain_hat_layer_1.png";

    public static final ArmorMaterial MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurabilityForType(Type pType) {
            return 368;
        }

        @Override
        public int getDefenseForType(Type pType) {
            return 1;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            // use leather to repair
            return Ingredient.of(Items.LEATHER);
        }

        @Override
        public String getName() {
            return "horrorcooked9:captain_hat";
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

    public CaptainHat(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(CaptainHatRenderer.CLIENT_EXTENSIONS);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE_PATH;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if (!level.isClientSide() && slotIndex == 39) {
            List<? extends Player> nearbyPlayers = level.getEntitiesOfClass(Player.class,
                    player.getBoundingBox().inflate(7.0));
            for (Player nearby : nearbyPlayers) {
                nearby.addEffect(new MobEffectInstance(ModEffects.CAPTAIN_INSPIRATION.get(), 60, 0, false, true));
            }
        }
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot pEquipmentSlot) {
        if (pEquipmentSlot == this.type.getSlot()) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
            double healthBonus = HEALTH_BONUS_MAP.getOrDefault(this.type, 0.0D);
            builder.put(Attributes.MAX_HEALTH, new AttributeModifier(HEALTH_MODIFIER_UUID_PER_SLOT[pEquipmentSlot.getIndex()], "Max Health modifier", healthBonus, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    private static final Map<Type, Double> HEALTH_BONUS_MAP = Util.make(new EnumMap<>(ArmorItem.Type.class), (bonus) -> {
        bonus.put(ArmorItem.Type.BOOTS, 4.0D);
        bonus.put(ArmorItem.Type.LEGGINGS, 7.0D);
        bonus.put(ArmorItem.Type.CHESTPLATE, 8.0D);
        bonus.put(ArmorItem.Type.HELMET, 2.0D);
    });

    private static final UUID[] HEALTH_MODIFIER_UUID_PER_SLOT = new UUID[]{
            UUID.fromString("7842b57d-e641-4da3-b441-a3a2341d7254"),
            UUID.fromString("c5963b61-4682-411c-9602-544158428574"),
            UUID.fromString("d9426f0f-08e8-466d-96f7-921360662d0d"),
            UUID.fromString("747206ca-9781-4328-8d00-47b744a87b54")
    };

}
