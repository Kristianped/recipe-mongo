package no.kristianped.recipemongo.converters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.domain.Recipe;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecipeToRecipeCommand implements Converter<Recipe, RecipeCommand> {

    CategoryToCategoryCommand categoryConverter;
    IngredientToIngredientCommand ingredientConverter;
    NotesToNotesCommand notesConverter;

    @Nullable
    @Override
    public RecipeCommand convert(Recipe source) {
        if (source == null)
            return null;

        final RecipeCommand recipe = new RecipeCommand();
        recipe.setId(source.getId());
        recipe.setCookTime(source.getCookTime());
        recipe.setPrepTime(source.getPrepTime());
        recipe.setDescription(source.getDescription());
        recipe.setDifficulty(source.getDifficulty());
        recipe.setDirections(source.getDirections());
        recipe.setServings(source.getServings());
        recipe.setSource(source.getSource());
        recipe.setUrl(source.getUrl());
        recipe.setNotes(notesConverter.convert(source.getNotes()));
        recipe.setImage(source.getImage());

        if (Objects.nonNull(source.getCategories()))
            source.getCategories().stream().map(categoryConverter::convert).forEach(recipe.getCategories()::add);

        if (Objects.nonNull(source.getIngredients()))
            source.getIngredients().stream().map(ingredientConverter::convert).forEach(recipe.getIngredients()::add);

        return recipe;
    }
}
