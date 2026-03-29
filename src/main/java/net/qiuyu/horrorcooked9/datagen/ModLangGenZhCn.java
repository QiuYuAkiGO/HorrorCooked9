package net.qiuyu.horrorcooked9.datagen;


import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.qiuyu.horrorcooked9.HorrorCooked9;
import net.qiuyu.horrorcooked9.register.ModBlocks;
import net.qiuyu.horrorcooked9.register.ModEffects;
import net.qiuyu.horrorcooked9.register.ModItems;

public class ModLangGenZhCn extends LanguageProvider {
    public ModLangGenZhCn(PackOutput output, String locale) {
        super(output, HorrorCooked9.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.CHOPPING_BOARD.get(), "砧板");
        add(ModBlocks.SALAD_BOWL.get(), "沙拉碗");


        add(ModItems.CLEAVER.get(), "切肉刀");
        add(ModItems.SHARPENING_STONE.get(), "磨刀石");
        add(ModItems.WOODEN_SALAD_SPOON.get(), "木制沙拉勺");
        add(ModItems.CRYSTAL_TOMATO.get(), "【宝石番茄】");
        add(ModItems.CRYSTAL_TOMATO_SLICED.get(), "【宝石番茄切片】");
        add(ModItems.CRYSTAL_SALAD.get(), "宝石沙拉");
        add(ModItems.SQUID_SALAD.get(), "鱿鱼沙拉");
        add(ModItems.OCEAN_SALAD.get(), "海洋沙拉");
        add(ModItems.CRISPY_NORI.get(), "香脆海苔");
        add(ModItems.CATFISH_SKEWER.get(), "鲶鱼烤串");
        add(ModItems.KALE.get(), "【静谧甘蓝】");
        add(ModItems.KALE_LEAVES.get(), "【静谧甘蓝菜叶】");
        add(ModItems.BRACKEN_FERN.get(), "【尸苔蕨菜】");
        add(ModItems.PICKLED_BRACKEN_FERN.get(), "【腌制尸苔蕨菜叶】");
        add(ModItems.PINEAPPLE.get(), "【远境蜜菠萝】");
        add(ModItems.PINEAPPLE_CHUNKS.get(), "【远境蜜菠萝切块】");
        add(ModItems.PARASITIC_BEAN_SPROUTS.get(), "【寄生豆芽】");
        add(ModItems.HAPPY_RICE.get(), "【快乐米】");
        add(ModItems.HAPPY_COOKED_RICE.get(), "【快乐米饭】");
        add(ModItems.CAPTAIN_HAT.get(), "船长帽");
        add(ModBlocks.FOODWORKS_TABLE.get(), "手工料理台");

        add(ModEffects.CAPTAIN_INSPIRATION.get(), "船长的激励");

        add("item.horrorcooked9.crystal_tomato.desc.1", "原产自地球的作物，被偷到异星后就莫名其妙地变异，硬度堪比\"水滴\"。");
        add("item.horrorcooked9.crystal_tomato.desc.2", "有目击者称，在太空中看异星外围，只能看到红色的球体。");
        add("item.horrorcooked9.crystal_tomato.desc.3", "处理得当的话，是十分美味的大补之物，前提是你能找到能切开它的东西。");
        add("item.horrorcooked9.crystal_tomato_sliced.desc.1", "费九牛二虎之力切好的完整的宝石番茄的切片。");
        add("item.horrorcooked9.crystal_tomato_sliced.desc.2", "虽然外壳依旧坚硬，但其中的果肉柔软酸甜，还有沙沙的口感。");

        add("item.horrorcooked9.pickled_bracken_fern.desc.1", "不知道哪个富哥把没吃完的蕨菜和调料倒在一起，没几天，一股香味不断地从蕨菜上飘出。");
        add("item.horrorcooked9.pickled_bracken_fern.desc.2", "研究发现，尸苔蕨菜的叶子有很多细孔，非常通透，会渗入各种调料。");
        add("item.horrorcooked9.bracken_fern.desc.1", "【尸苔蕨菜】");
        add("item.horrorcooked9.bracken_fern.desc.2", "这是从旧世界腐尸堆爬出来的诡异植物，味道鲜美，但极其稀有");
        add("item.horrorcooked9.bracken_fern.desc.3", "你种它，就要用命护着，等它成熟，这片地，就再也长不出别的活物了");
        add("item.horrorcooked9.bracken_fern.desc.4", "被联盟同时列为濒危物种和入侵物种");

        add("item.horrorcooked9.kale.desc.1", "质地极脆、极薄，切开时会微微反光，像太空中漂浮着的冷光冰晶。");
        add("item.horrorcooked9.kale.desc.2", "味道极度清淡，几乎没有气味，适合做拌菜和配菜。");
        add("item.horrorcooked9.kale.desc.3", "它不争夺味道，不制造麻烦，安安静静地陪每一艘孤独的飞船，从一个星系飘向另一个星系。");

        add("item.horrorcooked9.kale_leaves.desc.1", "经过简单切制，从静谧甘蓝上切下来的菜叶。");
        add("item.horrorcooked9.kale_leaves.desc.2", "生吃味道极度清淡，建议搭配其他食材。");

        add("item.horrorcooked9.pineapple.desc.1", "远看和普通菠萝几乎一样，只有凑近才看得出异常，总是会被误以为是普通地球菠萝。");
        add("item.horrorcooked9.pineapple.desc.2", "味道极好，香甜浓郁、汁水饱满、果肉脆嫩无渣，越好吃，精神就越靠近虚空。");
        add("item.horrorcooked9.pineapple.desc.3", "这是人类殖民深空后，从外星菠萝驯化而来的品种。");
        add("item.horrorcooked9.pineapple.desc.4", "它长得像地球菠萝，是为了骗过大脑，让人安心吃下。");
        add("item.horrorcooked9.pineapple.desc.5", "真正的危险藏在味道里，每一口甜，都是宇宙寂静在悄悄侵蚀你的意识。");

        add("item.horrorcooked9.pineapple_chunks.desc.1", "经过切制处理的远境蜜菠萝切块，仍旧在伪装成普通菠萝。");
        add("item.horrorcooked9.pineapple_chunks.desc.2", "虽然看着更多汁诱人，但不知为何比整个菠萝多出一种令人沉迷的寒意。");

        add("item.horrorcooked9.parasitic_bean_sprouts.desc.1", "流通于黑市的高价作物，只会在某个时间变成可处理状态。");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.2", "没有人知道它从哪里来，但它，只需一颗，便可以让你一个月甚至一年内摆脱饥饿。");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.3", "当你收获时发现它出现在你的作物上，恭喜你，你被盯上了，接下来的时间。");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.4", "请不要闭上眼睛。");

        add("item.horrorcooked9.happy_rice.desc.1", "科研人员耗费十几代的心血，极致改良的作物，内含大量的能量物质。");
        add("item.horrorcooked9.happy_rice.desc.2", "是一种极其优秀的身体活动最直接的能量来源，被全宇宙推广。");
        add("item.horrorcooked9.happy_rice.desc.3", "极其美味，是每家每户必备单品。");

        add("item.horrorcooked9.happy_cooked_rice.desc.1", "煮熟过后的快乐稻米，味道千里飘香。");
        add("item.horrorcooked9.happy_cooked_rice.desc.2", "味道好极了，是千家万户的首选。");

        add("book.horrorcooked9.guide.name", "恐怖烹饪手册");
        add("book.horrorcooked9.guide.landing", "欢迎来到 Horror Cooked 9。本手册会带你快速了解基础料理流程。");
        add("book.horrorcooked9.guide.category.getting_started", "入门指南");
        add("book.horrorcooked9.guide.category.getting_started.desc", "基础烹饪流程，以及如何获取第一本手册。");
        add("book.horrorcooked9.guide.entry.first_steps", "第一步");
        add("book.horrorcooked9.guide.entry.first_steps.page1", "先用砧板处理食材，再通过沙拉碗进行混合与出菜。");
        add("book.horrorcooked9.guide.entry.first_steps.page2", "如果手册丢失，可以随时重新合成。");

        add("creativetab.horrorcooked9_tab", "恐怖烹饪");
    }
}
