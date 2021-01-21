package no.kristianped.recipemongo.converters;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.domain.Recipe;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecipeCommandToRecipe implements Converter<RecipeCommand, Recipe> {

    CategoryCommandToCategory categoryConverter;
    IngredientCommandToIngredient ingredientConverter;
    NotesCommandToNotes notesConverter;

    @Nullable
    @Override
    public Recipe convert(RecipeCommand source) {
        if (source == null)
            return null;

        final Recipe recipe = new Recipe();
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

        if (source.getCategories() != null && !(source.getCategories().isEmpty()))
            source.getCategories().stream().map(categoryConverter::convert).forEach(recipe.getCategories()::add);

        if (source.getIngredients() != null && !(source.getIngredients().isEmpty()))
            source.getIngredients().stream().map(ingredientConverter::convert).forEach(recipe.getIngredients()::add);

        return recipe;
    }
}
