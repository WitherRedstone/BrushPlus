package com.chinaex123.brush_plus.data;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.init.BPEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagsProvider extends EnchantmentTagsProvider {
    public ModEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                      @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, BrushPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        // ===== 获取途径 =====
        // 可通过附魔台获得
        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .add(BPEnchantments.EXTRA_YIELD)
                .add(BPEnchantments.GOLDEN_TOUCH)
                .add(BPEnchantments.QUICK_BRUSH);
        // 非宝藏附魔
        tag(EnchantmentTags.NON_TREASURE);
        // 宝藏附魔（附魔台不出，只能钓鱼/交易/战利品箱等获取）
        tag(EnchantmentTags.TREASURE);
        // 村民会出售该附魔书
        tag(EnchantmentTags.TRADEABLE)
                .add(BPEnchantments.EXTRA_YIELD)
                .add(BPEnchantments.GOLDEN_TOUCH)
                .add(BPEnchantments.QUICK_BRUSH);
        // 村民交易该附魔书需要双倍价格
        tag(EnchantmentTags.DOUBLE_TRADE_PRICE);
        // 生物自然生成时装备上可能带的附魔
        tag(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT);
        // 村民出售的装备上的附魔
        tag(EnchantmentTags.ON_TRADED_EQUIPMENT);
        // 战利品箱中装备上可能带的附魔
        tag(EnchantmentTags.ON_RANDOM_LOOT);

        // ===== 互斥关系 =====
        // 盔甲附魔互斥
        tag(EnchantmentTags.ARMOR_EXCLUSIVE);
        // 靴子附魔互斥
        tag(EnchantmentTags.BOOTS_EXCLUSIVE);
        // 弓附魔互斥
        tag(EnchantmentTags.BOW_EXCLUSIVE);
        // 弩附魔互斥
        tag(EnchantmentTags.CROSSBOW_EXCLUSIVE);
        // 伤害类附魔互斥
        tag(EnchantmentTags.DAMAGE_EXCLUSIVE);
        // 挖掘类附魔互斥
        tag(EnchantmentTags.MINING_EXCLUSIVE);
        // 激流附魔互斥
        tag(EnchantmentTags.RIPTIDE_EXCLUSIVE);

        // ===== 特殊功能 =====
        // 诅咒附魔
        tag(EnchantmentTags.CURSE);
        // 使战利品经过烧炼
        tag(EnchantmentTags.SMELTS_LOOT);
        // 挖掘蜂巢时不激怒蜜蜂
        tag(EnchantmentTags.PREVENTS_BEE_SPAWNS_WHEN_MINING);
        // 防止饰纹陶罐被破坏时碎掉
        tag(EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING);
        // 防止冰被破坏时融化
        tag(EnchantmentTags.PREVENTS_ICE_MELTING);
        // 防止破坏虫蚀方块时生成蠹虫
        tag(EnchantmentTags.PREVENTS_INFESTED_SPAWNS);
    }
}