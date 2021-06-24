package vazkii.patchouli.client.book.page.abstr;

import net.minecraft.client.MinecraftClient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.mixin.AccessorRecipeManager;

import javax.annotation.Nullable;

import java.util.Map;

public abstract class PageDoubleRecipeRegistry<T extends Recipe<?>> extends PageDoubleRecipe<T> {
	private final RecipeType<? extends T> recipeType;

	public PageDoubleRecipeRegistry(RecipeType<? extends T> recipeType) {
		this.recipeType = recipeType;
	}

	@Nullable
	private T getRecipe(Identifier id) {
		RecipeManager manager = MinecraftClient.getInstance().world.getRecipeManager();
		Map<Identifier, T> recipes = (Map<Identifier, T>) ((AccessorRecipeManager) manager).patchouli_callGetAllOfType(recipeType);
		return recipes.get(id);
	}

	@Override
	protected T loadRecipe(BookContentsBuilder builder, BookEntry entry, Identifier res) {
		if (res == null) {
			return null;
		}

		T tempRecipe = getRecipe(res);
		if (tempRecipe == null) { // this is hacky but it works around Forge requiring custom recipes to have the prefix of the adding mod
			tempRecipe = getRecipe(new Identifier("crafttweaker", res.getPath()));
		}

		if (tempRecipe != null) {
			entry.addRelevantStack(builder, tempRecipe.getOutput(), pageNum);
			return tempRecipe;
		}

		Patchouli.LOGGER.warn("Recipe {} (of type {}) not found", res, Registry.RECIPE_TYPE.getId(recipeType));
		return null;
	}

}
