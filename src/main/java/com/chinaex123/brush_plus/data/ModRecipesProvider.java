package com.chinaex123.brush_plus.data;

import com.chinaex123.brush_plus.data.builder.BlockTransmutationRecipeBuilder;
import com.chinaex123.brush_plus.init.BPItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BPItems.IRON_BRUSH.get())
                .pattern(" A ")
                .pattern(" B ")
                .pattern(" C ")
                .define('A', Tags.Items.FEATHERS)
                .define('B', Tags.Items.INGOTS_IRON)
                .define('C', Tags.Items.RODS_WOODEN)
                .unlockedBy(getHasName(Items.FEATHER), has(Tags.Items.FEATHERS))
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Tags.Items.INGOTS_IRON))
                .unlockedBy(getHasName(Items.STICK), has(Tags.Items.RODS_WOODEN))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BPItems.GOLD_BRUSH.get())
                .pattern(" A ")
                .pattern(" B ")
                .pattern(" C ")
                .define('A', Tags.Items.FEATHERS)
                .define('B', Tags.Items.INGOTS_GOLD)
                .define('C', Tags.Items.RODS_WOODEN)
                .unlockedBy(getHasName(Items.FEATHER), has(Tags.Items.FEATHERS))
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Tags.Items.INGOTS_GOLD))
                .unlockedBy(getHasName(Items.STICK), has(Tags.Items.RODS_WOODEN))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BPItems.DIAMOND_BRUSH.get())
                .pattern(" A ")
                .pattern(" B ")
                .pattern(" C ")
                .define('A', Tags.Items.FEATHERS)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', Tags.Items.RODS_WOODEN)
                .unlockedBy(getHasName(Items.FEATHER), has(Tags.Items.FEATHERS))
                .unlockedBy(getHasName(Items.DIAMOND), has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy(getHasName(Items.STICK), has(Tags.Items.RODS_WOODEN))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BPItems.NETHERITE_BRUSH.get())
                .pattern(" A ")
                .pattern(" B ")
                .pattern(" C ")
                .define('A', Tags.Items.FEATHERS)
                .define('B', Tags.Items.INGOTS_NETHERITE)
                .define('C', Tags.Items.RODS_WOODEN)
                .unlockedBy(getHasName(Items.FEATHER), has(Tags.Items.FEATHERS))
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Tags.Items.INGOTS_NETHERITE))
                .unlockedBy(getHasName(Items.STICK), has(Tags.Items.RODS_WOODEN))
                .save(recipeOutput);

        buildBlockTransmutationRecipes(recipeOutput);
    }

    /** 方块转化配方 */
    private void buildBlockTransmutationRecipes(@NotNull RecipeOutput output) {
        // 苔石 → 原石 + 藤蔓
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.COBBLESTONES_MOSSY)
                .addResultBlock(Blocks.COBBLESTONE)
                .addDrop(Items.VINE)
                .setTotalTicks(80)
                .save(output, "mossy_cobblestone_to_vine");
        // 哭泣的黑曜石 → 黑曜石
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.OBSIDIANS_CRYING)
                .addResultBlock(Blocks.OBSIDIAN)
                .setTotalTicks(100)
                .save(output, "crying_obsidian_to_obsidian");
        // 砂土 → 泥土
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Blocks.COARSE_DIRT)
                .addResultBlock(Blocks.DIRT)
                .setTotalTicks(40)
                .save(output, "coarse_dirt_to_dirt");
        // 雪 → 雪球
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Blocks.SNOW)
                .addResultBlock(Blocks.AIR)
                .addDrop(Items.SNOWBALL)
                .setTotalTicks(40)
                .save(output, "snow_to_snowball");


        // 煤矿石 → 煤炭
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_COAL)
                .addResultBlock(Blocks.COBBLESTONE, 0.25f)
                .addDrop(new ItemStack(Items.COAL, 2))
                .addDrop(new ItemStack(Items.COAL, 2), 0.5f)
                .addDrop(new ItemStack(Items.COAL, 1), 0.2f)
                .setTotalTicks(40)
                .save(output, "ore_coal_to_coal");
        // 铜矿石 → 粗铜
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_COPPER)
                .addResultBlock(Blocks.COBBLESTONE, 0.25f)
                .addDrop(new ItemStack(Items.RAW_COPPER, 2))
                .addDrop(new ItemStack(Items.RAW_COPPER, 2), 0.5f)
                .addDrop(new ItemStack(Items.RAW_COPPER, 1), 0.2f)
                .setTotalTicks(40)
                .save(output, "ore_copper_to_raw_copper");
        // 铁矿石 → 粗铁
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_IRON)
                .addResultBlock(Blocks.COBBLESTONE, 0.25f)
                .addDrop(Items.RAW_IRON)
                .addDrop(new ItemStack(Items.RAW_IRON, 1), 0.5f)
                .addDrop(new ItemStack(Items.RAW_IRON, 1), 0.05f)
                .setTotalTicks(40)
                .save(output, "ore_iron_to_raw_iron");
        // 金矿石 → 粗金
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_GOLD)
                .addResultBlock(Blocks.COBBLESTONE, 0.25f)
                .addDrop(Items.RAW_GOLD)
                .addDrop(new ItemStack(Items.RAW_GOLD, 1), 0.5f)
                .addDrop(new ItemStack(Items.RAW_GOLD, 1), 0.05f)
                .setTotalTicks(40)
                .save(output, "ore_gold_to_raw_gold");
        // 钻矿石 → 钻石
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_DIAMOND)
                .addResultBlock(Blocks.COBBLESTONE, 0.50f)
                .addDrop(Items.DIAMOND)
                .addDrop(new ItemStack(Items.DIAMOND, 1), 0.5f)
                .addDrop(new ItemStack(Items.DIAMOND, 1), 0.05f)
                .setTotalTicks(40)
                .save(output, "ore_diamond_to_diamond");
        // 绿宝石矿石 → 绿宝石
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_EMERALD)
                .addResultBlock(Blocks.COBBLESTONE, 0.25f)
                .addDrop(Items.EMERALD)
                .addDrop(new ItemStack(Items.EMERALD, 1), 0.5f)
                .addDrop(new ItemStack(Items.EMERALD, 1), 0.05f)
                .setTotalTicks(40)
                .save(output, "ore_emerald_to_emerald");
        // 红石矿石 → 红石粉
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_REDSTONE)
                .addResultBlock(Blocks.COBBLESTONE, 0.25f)
                .addDrop(new ItemStack(Items.REDSTONE, 2))
                .addDrop(new ItemStack(Items.REDSTONE, 2), 0.5f)
                .addDrop(new ItemStack(Items.REDSTONE, 1), 0.2f)
                .setTotalTicks(40)
                .save(output, "ore_redstone_to_redstone");
        // 青金石矿石 → 青金石
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_LAPIS)
                .addResultBlock(Blocks.COBBLESTONE, 0.25f)
                .addDrop(new ItemStack(Items.LAPIS_LAZULI, 2))
                .addDrop(new ItemStack(Items.LAPIS_LAZULI, 2), 0.5f)
                .addDrop(new ItemStack(Items.LAPIS_LAZULI, 1), 0.2f)
                .setTotalTicks(40)
                .save(output, "ore_lapis_lazuli_to_lapis_lazuli");
        // 下界石英矿石 → 下界石英
        BlockTransmutationRecipeBuilder.builder()
                .setIngredient(Tags.Items.ORES_QUARTZ)
                .addResultBlock(Blocks.NETHERRACK, 0.25f)
                .addDrop(new ItemStack(Items.QUARTZ, 2))
                .addDrop(new ItemStack(Items.QUARTZ, 1), 0.5f)
                .addDrop(new ItemStack(Items.QUARTZ, 1), 0.2f)
                .setTotalTicks(40)
                .save(output, "ore_quartz_to_quartz");

//        // 测试配方
//        BlockTransmutationRecipeBuilder.builder()
//                .setIngredient(Blocks.IRON_BLOCK)
//                .setResultBlock(Blocks.DIAMOND_BLOCK)
//                .addDrop(Items.IRON_INGOT)
//                .addDrop(new ItemStack(Items.DIAMOND, 2), 0.8f)
//                .addDrop(Items.EMERALD, 0.3f)
//                .setTotalTicks(40)
//                .save(output, "iron_block_to_diamond_block");
//        BlockTransmutationRecipeBuilder.builder()
//                .setIngredient(Blocks.DIAMOND_BLOCK)
//                .setResultBlock(Blocks.GOLD_BLOCK)
//                .addDrop(Items.GOLD_INGOT)
//                .addDrop(Items.EMERALD, 0.3f)
//                .setTotalTicks(40)
//                .save(output, "diamond_block_to_gold_block");
//        // 单个掉落，带概率
//        BlockTransmutationRecipeBuilder.builder()
//                .setIngredient(Blocks.MOSSY_COBBLESTONE)
//                .setResultBlock(Blocks.COBBLESTONE)
//                .addDrop(Items.VINE, 0.3f)
//                .setTotalTicks(80)
//                .save(output, "mossy_cobblestone_to_vine");
//        // 单个掉落，100% 必掉
//        BlockTransmutationRecipeBuilder.builder()
//                .addDrop(new ItemStack(Items.STICK, 2))
//                .save(output, "xxx");
//        // 多个掉落
//        BlockTransmutationRecipeBuilder.builder()
//                .setIngredient(Blocks.XXX)
//                .setResultBlock(Blocks.YYY)
//                .addDrop(Items.VINE, 0.3f)
//                .addDrop(new ItemStack(Items.STICK, 2), 0.8f)
//                .addDrop(Items.STRING)
//                .setTotalTicks(80)
//                .save(output, "xxx_to_yyy");
//        // 无掉落
//        BlockTransmutationRecipeBuilder.builder()
//                .setIngredient(Blocks.XXX)
//                .setResultBlock(Blocks.YYY)
//                .noDrop()
//                .save(output, "xxx_to_yyy");

    }

}