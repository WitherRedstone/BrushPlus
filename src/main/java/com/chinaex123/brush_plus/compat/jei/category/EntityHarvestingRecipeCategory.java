package com.chinaex123.brush_plus.compat.jei.category;

import com.chinaex123.brush_plus.BrushPlus;
import com.chinaex123.brush_plus.crafting.recipe.EntityHarvestingRecipe;
import com.chinaex123.brush_plus.init.BPRecipes;
import com.google.common.collect.Lists;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityHarvestingRecipeCategory implements IRecipeCategory<RecipeHolder<EntityHarvestingRecipe>> {

    public static final RecipeType<RecipeHolder<EntityHarvestingRecipe>> TYPE = RecipeType.createRecipeHolderType(BrushPlus.id("entity_harvesting"));
    private static final MutableComponent TITLE = Component.translatable("jei.brush_plus.entity_harvesting.title");

    public static final int WIDTH = 120;
    public static final int HEIGHT = 65;

    private static final int SOURCE_X = 10;
    private static final int SOURCE_Y = 23;
    private static final int DROP_X = 70;
    private static final int DROP_SLOT_SIZE = 18;
    private static final int DROP_SLOT_GAP = 2;

    private static final float BRUSH_SCALE = 1.5f;
    private static final int BRUSH_SIZE = (int) (16 * BRUSH_SCALE);
    private static final int BRUSH_X = (SOURCE_X + DROP_X + 16) / 2 - BRUSH_SIZE / 2;
    private static final int BRUSH_BASE_Y = SOURCE_Y + 8 - BRUSH_SIZE / 2 - 6;
    private static final int BRUSH_PRESS_OFFSET = 5;
    private static final long BRUSH_ANIM_PERIOD_MS = 600;

    private static final int TIME_Y = 48;

    private final IDrawable iconDraw;
    private final IDrawable dropChanceSlotBg;

    public EntityHarvestingRecipeCategory(IGuiHelper guiHelper) {
        this.iconDraw = guiHelper.createDrawableItemLike(Items.BRUSH);
        ResourceLocation bg = BrushPlus.id("textures/gui/jei/drop_chance.png");
        this.dropChanceSlotBg = guiHelper.drawableBuilder(bg, 0, 0, 18, 18)
                .setTextureSize(18, 18)
                .build();
    }

    public static List<RecipeHolder<EntityHarvestingRecipe>> getRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        List<RecipeHolder<EntityHarvestingRecipe>> recipes = Lists.newArrayList();
        recipes.addAll(level.getRecipeManager().getAllRecipesFor(BPRecipes.ENTITY_HARVESTING_RECIPE));
        return recipes;
    }

    @Override
    public void draw(RecipeHolder<EntityHarvestingRecipe> recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        long time = System.currentTimeMillis();
        float phase = (float) ((time % BRUSH_ANIM_PERIOD_MS) / (double) BRUSH_ANIM_PERIOD_MS);
        float t = (float) Math.sin(phase * Math.PI);
        int brushY = BRUSH_BASE_Y + (int) (BRUSH_PRESS_OFFSET * t);

        renderBrushPressAnim(guiGraphics, BRUSH_X, brushY, t);

        int ticks = recipe.value().totalTicks();
        if (ticks > 0) {
            drawLeftString(guiGraphics,
                    Component.translatable("jei.brush_plus.entity_harvesting.time", ticks / 20.0),
                    SOURCE_X, TIME_Y);
        }
    }

    private void renderBrushPressAnim(GuiGraphics guiGraphics, int x, int y, float t) {
        ItemStack brushStack = Items.BRUSH.getDefaultInstance();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + BRUSH_SIZE / 2.0, y + BRUSH_SIZE / 2.0, 0);
        guiGraphics.pose().scale(BRUSH_SCALE, BRUSH_SCALE, 1.0f);
        guiGraphics.pose().translate(-8, -8, 0);
        guiGraphics.renderItem(brushStack, 0, 0);
        guiGraphics.pose().popPose();
    }

    private void drawLeftString(GuiGraphics guiGraphics, Component text, int x, int y) {
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = text.getVisualOrderText();
        guiGraphics.drawString(font, sequence, x, y, 0x555555, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EntityHarvestingRecipe> holder, IFocusGroup focuses) {
        EntityHarvestingRecipe recipe = holder.value();

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(recipe.entityId());
        SpawnEggItem egg = SpawnEggItem.byId(entityType);
        ItemStack entityDisplay;
        if (egg != null) {
            entityDisplay = egg.getDefaultInstance();
        } else {
            entityDisplay = Items.BRUSH.getDefaultInstance();
        }

        builder.addSlot(RecipeIngredientRole.INPUT, SOURCE_X, SOURCE_Y)
                .setStandardSlotBackground()
                .addItemStack(entityDisplay)
                .addRichTooltipCallback((slot, tooltipBuilder) ->
                        tooltipBuilder.add(Component.translatable(entityType.getDescriptionId()))
                );

        List<EntityHarvestingRecipe.DropEntry> drops = recipe.validDrops();
        if (!drops.isEmpty()) {
            int totalH = drops.size() * DROP_SLOT_SIZE + (drops.size() - 1) * DROP_SLOT_GAP;
            int dropStartY = HEIGHT / 2 - totalH / 2;

            for (int i = 0; i < drops.size(); i++) {
                EntityHarvestingRecipe.DropEntry entry = drops.get(i);
                int y = dropStartY + i * (DROP_SLOT_SIZE + DROP_SLOT_GAP);
                IRecipeSlotBuilder dropSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, DROP_X, y);

                if (entry.chance() < 1.0f) {
                    dropSlot.setBackground(dropChanceSlotBg, -1, -1);
                    final String pct = String.format("%.0f%%", entry.chance() * 100);
                    dropSlot.addRichTooltipCallback((slot, tooltipBuilder) ->
                            tooltipBuilder.add(Component.translatable("jei.brush_plus.entity_harvesting.drop_chance", pct)
                                    .withStyle(s -> s.withColor(0xFFC700)))
                    );
                } else {
                    dropSlot.setStandardSlotBackground();
                }
                dropSlot.addItemStack(entry.item());
            }
        }
    }

    @Override
    public RecipeType<RecipeHolder<EntityHarvestingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return iconDraw;
    }
}