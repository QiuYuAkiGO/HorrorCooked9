package net.qiuyu.horrorcooked9.events;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.gameplay.butchery.ButcheryConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber(modid = HorrorCooked9.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CarcassDropEvents {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        if (event.getSource().getDirectEntity() != player) {
            return;
        }

        ResourceManager resourceManager = player.level().getServer() != null ? player.level().getServer().getResourceManager() : null;
        ButcheryConfig.InteractionProfile interaction = ButcheryConfig.resolveInteraction(resourceManager);
        TagKey<Item> killTag = TagKey.create(Registries.ITEM, interaction.killWeaponItemTag());
        ItemStack killWeapon = player.getMainHandItem();
        if (!killWeapon.is(killTag)) {
            return;
        }

        Optional<ButcheryConfig.EntityProfile> profileOptional = ButcheryConfig.resolveEntityProfile(resourceManager, event.getEntity().getType());
        if (profileOptional.isEmpty()) {
            return;
        }
        ButcheryConfig.EntityProfile profile = profileOptional.get();
        if (!profile.enabled()) {
            return;
        }

        Set<ResourceLocation> removeItems = new HashSet<>(profile.removeItems());
        if (profile.removeVanillaRawMeat()) {
            removeItems.addAll(defaultRawMeatDropIds(event.getEntity().getType()));
        }
        if (!removeItems.isEmpty()) {
            event.getDrops().removeIf(drop -> {
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(drop.getItem().getItem());
                return itemId != null && removeItems.contains(itemId);
            });
        }

        List<ItemStack> extraDrops = ButcheryConfig.rollEntityExtraDrops(profile, event.getEntity().level().getRandom());
        for (ItemStack extraDrop : extraDrops) {
            if (!extraDrop.isEmpty()) {
                event.getDrops().add(new ItemEntity(
                        event.getEntity().level(),
                        event.getEntity().getX(),
                        event.getEntity().getY(),
                        event.getEntity().getZ(),
                        extraDrop
                ));
            }
        }

        if (!profile.spawnCarcass() || profile.carcassItem() == null) {
            return;
        }

        Item carcassItem = ForgeRegistries.ITEMS.getValue(profile.carcassItem());
        if (carcassItem == null) {
            return;
        }

        int min = Math.max(0, profile.carcassCountMin());
        int max = Math.max(min, profile.carcassCountMax());
        int count = min == max ? min : min + event.getEntity().level().getRandom().nextInt(max - min + 1);
        if (count <= 0) {
            return;
        }

        ItemStack carcass = new ItemStack(carcassItem, count);
        event.getDrops().add(new ItemEntity(
                event.getEntity().level(),
                event.getEntity().getX(),
                event.getEntity().getY(),
                event.getEntity().getZ(),
                carcass
        ));
    }

    private static Set<ResourceLocation> defaultRawMeatDropIds(EntityType<?> entityType) {
        Set<ResourceLocation> result = new HashSet<>();
        if (entityType == EntityType.PIG) {
            result.add(ForgeRegistries.ITEMS.getKey(Items.PORKCHOP));
        } else if (entityType == EntityType.COW) {
            result.add(ForgeRegistries.ITEMS.getKey(Items.BEEF));
        } else if (entityType == EntityType.SHEEP) {
            result.add(ForgeRegistries.ITEMS.getKey(Items.MUTTON));
        } else if (entityType == EntityType.CHICKEN) {
            result.add(ForgeRegistries.ITEMS.getKey(Items.CHICKEN));
        }
        result.remove(null);
        return result;
    }
}
