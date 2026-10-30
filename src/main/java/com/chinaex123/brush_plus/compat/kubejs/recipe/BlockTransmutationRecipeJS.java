package com.chinaex123.brush_plus.compat.kubejs.recipe;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.component.CustomObjectRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface BlockTransmutationRecipeJS {

    CustomObjectRecipeComponent RESULT_BLOCK_ENTRY = new CustomObjectRecipeComponent(List.of(
            new CustomObjectRecipeComponent.Key("block", StringComponent.STRING.instance(), false),
            new CustomObjectRecipeComponent.Key("chance", NumberComponent.FLOAT, true)
    ));
    CustomObjectRecipeComponent DROP_ENTRY = new CustomObjectRecipeComponent(List.of(
            new CustomObjectRecipeComponent.Key("item", ItemStackComponent.ITEM_STACK.instance(), false),
            new CustomObjectRecipeComponent.Key("chance", NumberComponent.FLOAT, true)
    ));
    RecipeComponent<List<List<CustomObjectRecipeComponent.Value>>> RESULT_BLOCK_LIST = new ObjListComponent(RESULT_BLOCK_ENTRY);
    RecipeComponent<List<List<CustomObjectRecipeComponent.Value>>> DROP_LIST = new ObjListComponent(DROP_ENTRY);
    RecipeKey<Ingredient> INGREDIENT = IngredientComponent.INGREDIENT.inputKey("ingredient");
    RecipeKey<List<List<CustomObjectRecipeComponent.Value>>> RESULT_BLOCKS = RESULT_BLOCK_LIST.otherKey("result_blocks").optional(List.of());
    RecipeKey<List<List<CustomObjectRecipeComponent.Value>>> DROPS = DROP_LIST.otherKey("drops").optional(List.of());
    RecipeKey<Integer> TOTAL_TICKS = NumberComponent.INT.otherKey("total_ticks").optional(80);

    RecipeSchema SCHEMA = new RecipeSchema(INGREDIENT, RESULT_BLOCKS, DROPS, TOTAL_TICKS);

    record ObjListComponent(CustomObjectRecipeComponent entry) implements RecipeComponent<List<List<CustomObjectRecipeComponent.Value>>> {
        @Override
        public Codec<List<List<CustomObjectRecipeComponent.Value>>> codec() {
            return entry.codec().listOf();
        }

        @Override
        public TypeInfo typeInfo() {
            return TypeInfo.RAW_LIST.withParams(entry.typeInfo());
        }

        @Override
        public RecipeComponentType<?> type() {
            return entry.type();
        }

        @Override
        public List<List<CustomObjectRecipeComponent.Value>> wrap(RecipeScriptContext cx, Object from) {
            List<Object> raw = new ArrayList<>();

            if (from instanceof Iterable<?> iterable) {
                for (Object o : iterable) raw.add(o);
            } else if (from != null && from.getClass().isArray()) {
                int len = Array.getLength(from);
                for (int i = 0; i < len; i++) raw.add(Array.get(from, i));
            } else if (from != null) {
                raw.add(from);
            }

            String firstKey = entry.keys().getFirst().name();

            List<List<CustomObjectRecipeComponent.Value>> result = new ArrayList<>(raw.size());
            for (Object o : raw) {
                if (o instanceof String || o instanceof net.minecraft.resources.ResourceLocation) {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put(firstKey, o);
                    result.add(entry.wrap(cx, map));
                } else {
                    result.add(entry.wrap(cx, o));
                }
            }
            return result;
        }
    }
}