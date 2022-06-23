package vazkii.patchouli.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.item.PatchouliItems;

import javax.annotation.Nonnull;

import java.util.function.BiFunction;

public record BookRecipeSerializer<T extends Recipe<?>, U extends T> (RecipeSerializer<T> compose, BiFunction<T, ResourceLocation, U> converter) implements RecipeSerializer<U> {
	@Override
	@Nonnull
	public U fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
		if (!json.has("result")) {
			JsonObject object = new JsonObject();
			object.addProperty("item", PatchouliItems.BOOK_ID.toString());
			json.add("result", object);
		}
		T recipe = compose().fromJson(id, json);

		ResourceLocation outputBook = new ResourceLocation(GsonHelper.getAsString(json, "book"));
		if (!BookRegistry.INSTANCE.books.containsKey(outputBook)) {
			PatchouliAPI.LOGGER.warn("Book {} in recipe {} does not exist!", outputBook, id);
		}

		return converter().apply(recipe, outputBook);
	}

	@Override
	@Nonnull
	public U fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buf) {
		T recipe = compose().fromNetwork(id, buf);
		return converter().apply(recipe, null);
	}

	@Override
	public void toNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull U recipe) {
		compose().toNetwork(buf, recipe);
	}
}