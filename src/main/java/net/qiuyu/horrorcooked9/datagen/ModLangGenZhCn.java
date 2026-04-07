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
        add(ModItems.CANDIED_CRYSTAL_TOMATO_SLICES.get(), "糖渍宝石番茄片");
        add(ModItems.ROASTED_CRYSTAL_TOMATO.get(), "炙烤宝石番茄");
        add(ModItems.CRYSTAL_SALAD.get(), "宝石沙拉");
        add(ModItems.CRYSTAL_TOMATO_KETCHUP.get(), "宝石番茄酱");
        add(ModItems.SQUID_SALAD.get(), "鱿鱼沙拉");
        add(ModItems.TWIN_CORPSE_PARASITIC_SALAD.get(), "双尸寄生拌");
        add(ModItems.CRISPY_NORI.get(), "香脆海苔");
        add(ModItems.CATFISH_SKEWER.get(), "鲶鱼烤串");
        add(ModItems.FAR_REALM_GEM_BOWL.get(), "远境宝石碗");
        add(ModItems.TRANQUIL_MIXED_RICE.get(), "静谧拌饭");
        add(ModItems.COLORFUL_PALETTE.get(), "缤纷色彩");
        add(ModItems.TRANQUIL_BASE.get(), "宁静半成品");
        add(ModItems.FRIED_TRANQUIL_BASE.get(), "油炸宁静半成品");
        add(ModItems.TRANQUILITY.get(), "宁静");
        add(ModItems.KALE.get(), "【静谧甘蓝】");
        add(ModItems.KALE_LEAVES.get(), "【静谧甘蓝菜叶】");
        add(ModItems.KALE_PUREE.get(), "【静谧菜泥】");
        add(ModItems.BRACKEN_FERN.get(), "【尸苔蕨菜】");
        add(ModItems.PICKLED_BRACKEN_FERN.get(), "【腌制尸苔蕨菜叶】");
        add(ModItems.PINEAPPLE.get(), "【远境蜜菠萝】");
        add(ModItems.PINEAPPLE_CHUNKS.get(), "【远境蜜菠萝切块】");
        add(ModItems.ROASTED_PINEAPPLE_CHUNKS.get(), "烤菠萝切块");
        add(ModItems.PINEAPPLE_FRIED_RICE_BASE.get(), "菠萝炒饭半成品");
        add(ModItems.FRIED_PINEAPPLE_FRIED_RICE_BASE.get(), "油炸菠萝炒饭半成品");
        add(ModItems.PINEAPPLE_FRIED_RICE.get(), "菠萝炒饭");
        add(ModItems.PARASITIC_BEAN_SPROUTS.get(), "【寄生豆芽】");
        add(ModItems.DRIED_BEAN_SPROUTS.get(), "干豆芽");
        add(ModItems.HAPPY_RICE.get(), "【快乐米】");
        add(ModItems.HAPPY_COOKED_RICE.get(), "【快乐米饭】");
        add(ModItems.SHIT.get(), "粪便");
        add(ModItems.GOLDEN_SHIT.get(), "黄金粪便");
        add(ModItems.CAPTAIN_HAT.get(), "船长帽");
        add(ModBlocks.FOODWORKS_TABLE.get(), "手工料理台");

        add(ModEffects.CAPTAIN_INSPIRATION.get(), "船长的激励");
        add(ModEffects.DIARRHEA.get(), "腹泻");
        add(ModEffects.PINEAPPLE_POWER_I.get(), "菠萝力量1");
        add("effect.horrorcooked9.diarrhea.events.desc.1", "效果持续期间，每 10 秒有 75% 几率仅发出不适感；否则短暂获得缓慢与挖掘疲劳、极强短暂缓慢，并伴随爆炸声在身后掉落粪便。");
        add("effect.horrorcooked9.pineapple_power_i.desc.1", "持续期间：受到任何直接攻击时反击伤害来源 3 点生命值，并提高 3 点生命值上限。");

        add("item.horrorcooked9.crystal_tomato.desc.1", "原产自地球的作物，被偷到异星后就莫名其妙地变异，硬度堪比\"水滴\"。");
        add("item.horrorcooked9.crystal_tomato.desc.2", "有目击者称，在太空中看异星外围，只能看到红色的球体。");
        add("item.horrorcooked9.crystal_tomato.desc.3", "处理得当的话，是十分美味的大补之物，前提是你能找到能切开它的东西。");
        add("item.horrorcooked9.crystal_tomato_sliced.desc.1", "费九牛二虎之力切好的完整的宝石番茄的切片。");
        add("item.horrorcooked9.crystal_tomato_sliced.desc.2", "虽然外壳依旧坚硬，但其中的果肉柔软酸甜，还有沙沙的口感。");
        add("item.horrorcooked9.candied_crystal_tomato_slices.desc.1", "人们发现，在腌制过后，会改变宝石番茄外皮的化学结构，使其外壳变软");
        add("item.horrorcooked9.candied_crystal_tomato_slices.desc.2", "虽然腌制过后皱巴巴的，但是在独特的味道的衬托下，甜味更加的突出");
        add("item.horrorcooked9.roasted_crystal_tomato.desc.1", "经过科学人员夜以继日的研究宝石番茄的表层结构及组成物质");
        add("item.horrorcooked9.roasted_crystal_tomato.desc.2", "最后得出结论，对宝石番茄的表层持续加热后，表层会发生一系列物理变化");
        add("item.horrorcooked9.roasted_crystal_tomato.desc.3", "最后呈现钢化玻璃的特性，一旦受到外力后会迅速碎成粉末");
        add("item.horrorcooked9.roasted_crystal_tomato.desc.4", "这道菜最初诞生于");
        add("item.horrorcooked9.roasted_crystal_tomato.desc.4.bold", "研究员的卧室");
        add("item.horrorcooked9.crystal_tomato_ketchup.desc.1", "有人发现宝石番茄的外壳会受热量影响变性，于是乎宝石番茄酱诞生了");
        add("item.horrorcooked9.crystal_tomato_ketchup.desc.2", "并不反对直接吃番茄酱，但是最好还是作为其他菜肴的配料。");
        add("item.horrorcooked9.dried_bean_sprouts.desc.1", "关于这个做法的来源有两种说法");
        add("item.horrorcooked9.dried_bean_sprouts.desc.2", "一种是一位种植户发现仓库已经被豆芽占满，最后想出了这种方法来减小体型");
        add("item.horrorcooked9.dried_bean_sprouts.desc.3", "另一种则是，一名种植户打算用火处理掉多余的豆芽，无意间得出的办法");
        add("item.horrorcooked9.dried_bean_sprouts.desc.4", "当然无论是基于哪一种说法，这都是不错的方法");
        add("item.horrorcooked9.dried_bean_sprouts.desc.5", "干燥后的豆芽口感酥脆，香味突出，是不错的配料");
        add("item.horrorcooked9.dried_bean_sprouts.desc.6", "至少，你感觉奇怪的视线消失了....");

        add("item.horrorcooked9.pickled_bracken_fern.desc.1", "不知道哪个富哥把没吃完的蕨菜和调料倒在一起，没几天，一股香味不断地从蕨菜上飘出。");
        add("item.horrorcooked9.pickled_bracken_fern.desc.2", "研究发现，尸苔蕨菜的叶子有很多细孔，非常通透，会渗入各种调料。");
        add("item.horrorcooked9.bracken_fern.desc.1", "这是从旧世界腐尸堆爬出来的诡异植物，味道鲜美，但极其稀有");
        add("item.horrorcooked9.bracken_fern.desc.2", "你种它，就要用命护着，等它成熟，这片地，就再也长不出别的活物了");
        add("item.horrorcooked9.bracken_fern.desc.3", "被联盟同时列为濒危物种和入侵物种");

        add("item.horrorcooked9.kale.desc.1", "质地极脆、极薄，切开时会微微反光，像太空中漂浮着的冷光冰晶。");
        add("item.horrorcooked9.kale.desc.2", "味道极度清淡，几乎没有气味，适合做拌菜和配菜。");
        add("item.horrorcooked9.kale.desc.3", "它不争夺味道，不制造麻烦，安安静静地陪每一艘孤独的飞船，从一个星系飘向另一个星系。");

        add("item.horrorcooked9.kale_leaves.desc.1", "经过简单切制，从静谧甘蓝上切下来的菜叶。");
        add("item.horrorcooked9.kale_leaves.desc.2", "生吃味道极度清淡，建议搭配其他食材。");
        add("item.horrorcooked9.kale_puree.desc.1", "用静谧甘蓝的菜叶切条挤压，经过几个地球周的时间，静谧菜叶会变成有一定粘稠性的蔬菜泥。");
        add("item.horrorcooked9.kale_puree.desc.2", "非常下饭，能吃十碗大米饭。");

        add("item.horrorcooked9.pineapple.desc.1", "远看和普通菠萝几乎一样，只有凑近才看得出异常，总是会被误以为是普通地球菠萝。");
        add("item.horrorcooked9.pineapple.desc.2", "味道极好，香甜浓郁、汁水饱满、果肉脆嫩无渣，越好吃，精神就越靠近虚空。");
        add("item.horrorcooked9.pineapple.desc.3", "这是人类殖民深空后，从外星菠萝驯化而来的品种。");
        add("item.horrorcooked9.pineapple.desc.4", "它长得像地球菠萝，是为了骗过大脑，让人安心吃下。");
        add("item.horrorcooked9.pineapple.desc.5", "真正的危险藏在味道里，每一口甜，都是宇宙寂静在悄悄侵蚀你的意识。");

        add("item.horrorcooked9.pineapple_chunks.desc.1", "经过切制处理的远境蜜菠萝切块，仍旧在伪装成普通菠萝。");
        add("item.horrorcooked9.pineapple_chunks.desc.2", "虽然看着更多汁诱人，但不知为何比整个菠萝多出一种令人沉迷的寒意。");
        add("item.horrorcooked9.roasted_pineapple_chunks.desc.1", "人类获取食物后，总会以最原始的方式烹饪，也就是烤制。");
        add("item.horrorcooked9.roasted_pineapple_chunks.desc.2", "远境蜜菠萝也不例外，烤制后它的香味更明显。");
        add("item.horrorcooked9.roasted_pineapple_chunks.desc.3", "但闻久了似乎会有幻觉。");
        add("item.horrorcooked9.roasted_pineapple_chunks.desc.4", "关于这一现象还在研究中。");
        add("item.horrorcooked9.far_realm_gem_bowl.desc.1", "看上去就是很普通的水果拼盘。");
        add("item.horrorcooked9.far_realm_gem_bowl.desc.2", "闪闪发光的样子很是耀眼。");
        add("item.horrorcooked9.far_realm_gem_bowl.desc.3", "多汁诱人，酸甜可口。");
        add("item.horrorcooked9.far_realm_gem_bowl.desc.4", "但你知道，这是远境蜜菠萝给予你的错觉。");

        add("item.horrorcooked9.parasitic_bean_sprouts.desc.1", "从黑市流通而来的奇怪作物，至于起源，无从得知。");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.2", "种植时请务必做好监管，如果你发现它消失在你的耕地里，那你最好能立马把它找出来。");
        add("item.horrorcooked9.parasitic_bean_sprouts.desc.3", "切割时产生的汁液对生物健康的影响似乎不是很好。");

        add("item.horrorcooked9.happy_rice.desc.1", "科研人员耗费十几代的心血，极致改良的作物，内含大量的能量物质。");
        add("item.horrorcooked9.happy_rice.desc.2", "是一种极其优秀的身体活动最直接的能量来源，被全宇宙推广。");
        add("item.horrorcooked9.happy_rice.desc.3", "极其美味，是每家每户必备单品。");

        add("item.horrorcooked9.happy_cooked_rice.desc.1", "煮熟过后的快乐稻米，味道千里飘香。");
        add("item.horrorcooked9.happy_cooked_rice.desc.2", "味道好极了，是千家万户的首选。");
        add("item.horrorcooked9.twin_corpse_parasitic_salad.desc.1", "满碗管饱的尸苔蕨菜，让调味料无处可逃");
        add("item.horrorcooked9.twin_corpse_parasitic_salad.desc.2", "虽然从颜色上不太好看，但鲜香味绝对不会让你失望");
        add("item.horrorcooked9.twin_corpse_parasitic_salad.desc.3", "不必去思考它的生长环境，以及碗内的视线");
        add("item.horrorcooked9.tranquil_mixed_rice.desc.1", "最早由一些农户开创的吃法");
        add("item.horrorcooked9.tranquil_mixed_rice.desc.2", "后续被演变成一种特色料理");
        add("item.horrorcooked9.tranquil_mixed_rice.desc.3", "静谧甘蓝菜泥让每个米粒都像星星般闪耀");
        add("item.horrorcooked9.tranquility.desc.1", "静谧甘蓝铺在底部，在上面点缀蜜菠萝块");
        add("item.horrorcooked9.tranquility.desc.2", "甘蓝菜泥和鸡蛋搅拌后在上面画上图案");
        add("item.horrorcooked9.tranquility.desc.3", "整道菜给人带来一种寂静般的冷冽气息");
        add("item.horrorcooked9.colorful_palette.desc.1", "由丰富的蔬菜和水果制成的拼盘");
        add("item.horrorcooked9.colorful_palette.desc.2", "颜色艳丽，令人胃口大开");
        add("item.horrorcooked9.colorful_palette.desc.3", "不必在意里面的材料都是什么");
        add("item.horrorcooked9.colorful_palette.desc.4", "你现在只需要将它们吃进胃里");
        add("item.horrorcooked9.pineapple_fried_rice.desc.1", "由新鲜的远境蜜菠萝搭配上上好的隔夜饭炒制而成");
        add("item.horrorcooked9.pineapple_fried_rice.desc.2", "炒饭上面的黄色果肉不断地散发出诱惑的香气");

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
