package net.qiuyu.horrorcooked9.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModItems;
import net.qiuyu.horrorcooked9.register.ModEffects;

public class ModLangGenEnUs extends LanguageProvider {
    public ModLangGenEnUs(PackOutput output, String locale) {
        super(output, HorrorCooked9.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.CHOPPING_BOARD.get(), "Chopping Board");
        add(ModBlocks.SALAD_BOWL.get(), "Salad Bowl");
        add(ModBlocks.SIMPLE_FILTER.get(), "Simple Filter");
        add(ModBlocks.JUICER.get(), "Juicer");
        add(ModItems.CLEAVER.get(), "Cleaver");
        add(ModItems.FILTERED_WATER_BUCKET.get(), "Filtered Water Bucket");
        add(ModItems.CLEAR_WATER_BUCKET.get(), "Clear Water Bucket");
        add(ModItems.PINEAPPLE_JUICE_BUCKET.get(), "Pineapple Juice Bucket");
        add(ModItems.PINEAPPLE_JUICE_BOTTLE.get(), "Pineapple Juice Bottle");
        add(ModItems.SHARPENING_STONE.get(), "Sharpening Stone");
        add(ModItems.WOODEN_SALAD_SPOON.get(), "Wooden Salad Spoon");
        add(ModItems.CRYSTAL_TOMATO.get(), "[Crystal Tomato]");
        add(ModItems.CRYSTAL_TOMATO_SLICED.get(), "[Crystal Tomato Sliced]");
        add(ModItems.CRYSTAL_SALAD.get(), "Crystal Salad");
        add(ModItems.SQUID_SALAD.get(), "Squid Salad");
        add(ModItems.OCEAN_SALAD.get(), "Ocean Salad");
        add(ModItems.CRISPY_NORI.get(), "Crispy Nori");
        add(ModItems.CATFISH_SKEWER.get(), "Catfish Skewer");
        add(ModItems.KALE.get(), "[Kale]");
        add(ModItems.KALE_LEAVES.get(), "[Kale Leaves]");
        add(ModItems.BRACKEN_FERN.get(), "[Bracken Fern]");
        add(ModItems.PICKLED_BRACKEN_FERN.get(), "[Pickled Bracken Fern]");
        add(ModItems.PINEAPPLE.get(), "[Pineapple]");
        add(ModItems.PINEAPPLE_CHUNKS.get(), "[Pineapple Chunks]");
        add(ModItems.PARASITIC_BEAN_SPROUTS.get(), "[Parasitic Bean Sprouts]");
        add(ModItems.HAPPY_RICE.get(), "[Happy Rice]");
        add(ModItems.HAPPY_COOKED_RICE.get(), "[Happy Cooked Rice]");
        add(ModItems.CAPTAIN_HAT.get(), "Captain Hat");
        add(ModEffects.CAPTAIN_INSPIRATION.get(), "Captain's Inspiration");
        add(ModBlocks.FOODWORKS_TABLE.get(), "Foodworks Table");
        add(ModItems.PIG_CARCASS.get(), "Pig Carcass");
        add(ModItems.COW_CARCASS.get(), "Cow Carcass");
        add(ModItems.SHEEP_CARCASS.get(), "Sheep Carcass");
        add(ModItems.CHICKEN_CARCASS.get(), "Chicken Carcass");
        add(ModItems.PIG_LIVER.get(), "Pig Liver");
        add(ModItems.PIG_STOMACH.get(), "Pig Stomach");
        add(ModItems.PIG_INTESTINE.get(), "Pig Intestine");
        add(ModItems.PIG_HEART.get(), "Pig Heart");
        add(ModItems.PIG_KIDNEY.get(), "Pig Kidney");
        add(ModItems.PIG_LUNG.get(), "Pig Lung");
        add(ModItems.PIG_TONGUE.get(), "Pig Tongue");
        add(ModItems.PIG_BRAIN.get(), "Pig Brain");
        add(ModItems.BEEF_TRIPE.get(), "Beef Tripe");
        add(ModItems.BEEF_OMASUM.get(), "Beef Omasum");
        add(ModItems.BEEF_HEART.get(), "Beef Heart");
        add(ModItems.BEEF_LIVER.get(), "Beef Liver");
        add(ModItems.BEEF_TONGUE.get(), "Beef Tongue");
        add(ModItems.BEEF_MARROW.get(), "Beef Marrow");
        add(ModItems.BEEF_TENDON.get(), "Beef Tendon");
        add(ModItems.LAMB_OFFAL_MIX.get(), "Lamb Offal Mix");
        add(ModItems.LAMB_KIDNEY.get(), "Lamb Kidney");
        add(ModItems.LAMB_TRIPE.get(), "Lamb Tripe");
        add(ModItems.LAMB_LIVER.get(), "Lamb Liver");
        add(ModItems.CHICKEN_GIZZARD.get(), "Chicken Gizzard");
        add(ModItems.CHICKEN_HEART.get(), "Chicken Heart");
        add(ModItems.CHICKEN_LIVER.get(), "Chicken Liver");
        add(ModItems.CHICKEN_INTESTINE.get(), "Chicken Intestine");
        add(ModItems.CHICKEN_BLOOD.get(), "Chicken Blood");
        add(ModItems.DUCK_GIZZARD.get(), "Duck Gizzard");
        add(ModItems.DUCK_LIVER.get(), "Duck Liver");
        add(ModItems.DUCK_INTESTINE.get(), "Duck Intestine");
        add(ModItems.DUCK_BLOOD.get(), "Duck Blood");
        add(ModItems.DUCK_HEART.get(), "Duck Heart");

        add("item.horrorcooked9.crystal_tomato.desc.1", "A crop from Earth that mutated on an alien world, hard as a \"Droplet\".");
        add("item.horrorcooked9.crystal_tomato.desc.2", "Witnesses say from orbit, the planet looks like a red sphere.");
        add("item.horrorcooked9.crystal_tomato.desc.3", "Handled properly, it is delicious and nourishing... if you can cut it open.");
        add("item.horrorcooked9.crystal_tomato_sliced.desc.1", "Slices of a full Gem Tomato, cut only with tremendous effort.");
        add("item.horrorcooked9.crystal_tomato_sliced.desc.2", "Its shell is still hard, but the flesh inside is soft, tangy-sweet, and sandy-textured.");

        add("item.horrorcooked9.pickled_bracken_fern.desc.1", "Someone mixed leftover fern with seasonings; days later, a rich aroma rose from it.");
        add("item.horrorcooked9.pickled_bracken_fern.desc.2", "Its porous leaves absorb flavor deeply and hold many kinds of spices.");
        add("item.horrorcooked9.bracken_fern.desc.1", "[Bracken Fern]");
        add("item.horrorcooked9.bracken_fern.desc.2", "A bizarre plant crawling out of old-world corpse mounds: delicious, yet extremely rare.");
        add("item.horrorcooked9.bracken_fern.desc.3", "If you grow it, guard it with your life; once mature, nothing else will live in that soil again.");
        add("item.horrorcooked9.bracken_fern.desc.4", "The Alliance lists it as both an endangered species and an invasive species.");

        add("item.horrorcooked9.kale.desc.1", "Crisp and thin, it glints faintly when sliced, like cold crystals in space.");
        add("item.horrorcooked9.kale.desc.2", "Its taste is extremely light, almost scentless, ideal for salads and sides.");
        add("item.horrorcooked9.kale.desc.3", "Quiet and gentle, it drifts with lonely ships from one star system to another.");

        add("item.horrorcooked9.kale_leaves.desc.1", "Simple cut leaves from Tranquil Kale.");
        add("item.horrorcooked9.kale_leaves.desc.2", "Very mild raw taste; best paired with other ingredients.");

        add("item.horrorcooked9.pineapple.desc.1", "Looks like a normal pineapple from afar, only strange up close.");
        add("item.horrorcooked9.pineapple.desc.2", "Sweet, juicy, and tender, yet the better it tastes, the nearer the void feels.");
        add("item.horrorcooked9.pineapple.desc.3", "A cultivar domesticated from alien pineapple after deep-space colonization.");
        add("item.horrorcooked9.pineapple.desc.4", "Its Earth-like look exists to calm the mind and invite another bite.");
        add("item.horrorcooked9.pineapple.desc.5", "The danger hides in sweetness: cosmic silence quietly erodes your consciousness.");

        add("item.horrorcooked9.pineapple_chunks.desc.1", "Cut chunks of Far-Honey Pineapple still pretending to be ordinary fruit.");
        add("item.horrorcooked9.pineapple_chunks.desc.2", "Juicier and more tempting, yet strangely colder and more addictive.");

        add("item.horrorcooked9.parasitic_bean_sprouts.desc.1", "A black-market high-value crop that only becomes processable at certain times.");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.2", "No one knows where it came from; one sprout can keep hunger away for months, even a year.");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.3", "If it appears among your harvest, congratulations: you have been marked.");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.4", "For the coming days... do not close your eyes.");

        add("item.horrorcooked9.happy_rice.desc.1", "A crop perfected across generations of research, packed with dense energy matter.");
        add("item.horrorcooked9.happy_rice.desc.2", "An excellent direct energy source for physical activity, promoted across the universe.");
        add("item.horrorcooked9.happy_rice.desc.3", "Exceptionally delicious, a staple in every household.");

        add("item.horrorcooked9.happy_cooked_rice.desc.1", "Cooked Happy Rice whose aroma travels for miles.");
        add("item.horrorcooked9.happy_cooked_rice.desc.2", "It tastes amazing and is the top choice of countless families.");
        add("item.horrorcooked9.pig_carcass.desc.1", "Hold a butchery tool in offhand and right-click to harvest pig offal.");
        add("item.horrorcooked9.cow_carcass.desc.1", "Hold a butchery tool in offhand and right-click to harvest beef offal.");
        add("item.horrorcooked9.sheep_carcass.desc.1", "Hold a butchery tool in offhand and right-click to harvest lamb offal.");
        add("item.horrorcooked9.chicken_carcass.desc.1", "Hold a butchery tool in offhand and right-click to harvest chicken offal.");
        add("screen.horrorcooked9.juicer.fluid", "Fluid: %s mb");
        add("screen.horrorcooked9.juicer.pulp", "Pulp: %s mb");

        add("book.horrorcooked9.guide.name", "Horror Cooked Handbook");
        add("book.horrorcooked9.guide.landing", "Welcome to Horror Cooked 9. This handbook introduces your first tools and dishes.");
        add("book.horrorcooked9.guide.category.getting_started", "Getting Started");
        add("book.horrorcooked9.guide.category.getting_started.desc", "Basic cooking workflow and how to obtain your first guide book.");
        add("book.horrorcooked9.guide.entry.first_steps", "First Steps");
        add("book.horrorcooked9.guide.entry.first_steps.page1", "Use the Chopping Board and Salad Bowl to process ingredients and mix dishes.");
        add("book.horrorcooked9.guide.entry.first_steps.page2", "Craft this guide book at any time if you lose it.");

        add("creativetab.horrorcooked9_tab", "Horror Cooked");
    }
}
