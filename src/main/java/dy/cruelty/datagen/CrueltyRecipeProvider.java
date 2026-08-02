package dy.cruelty.datagen;

import dy.cruelty.blocks.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class CrueltyRecipeProvider extends FabricRecipeProvider {
	public CrueltyRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
		this.registriesFuture = registriesFuture;
	}

	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

	@Override
	protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
		return new RecipeGenerator(registries, exporter) {
			@Override
			public void generate() {
				RegistryWrapper.WrapperLookup lookup = registriesFuture.join();
				RegistryEntryLookup<Item> itemLookup = lookup.getOrThrow(RegistryKeys.ITEM);

				ShapedRecipeJsonBuilder.create(itemLookup, RecipeCategory.MISC, ModBlocks.GRASS_TRAP)
						.pattern("AAA")
						.pattern("BCB")
						.input('A', TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "leaves")))
						.input('B', Items.STICK)
						.input('C', Items.STRING)
						.criterion("has_leaves", conditionsFromTag(TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "leaves"))))
						.offerTo(exporter);
			}
		};
	}

	@Override
	public String getName() {
		return "Cruelty Recipes";
	}
}
