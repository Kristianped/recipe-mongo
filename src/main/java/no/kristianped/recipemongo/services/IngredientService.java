package no.kristianped.recipemongo.services;


import no.kristianped.recipemongo.commands.IngredientCommand;

public interface IngredientService {

    IngredientCommand saveIngredientCommand(IngredientCommand command);

    IngredientCommand findByRecipeIdAndIngredientId(String recipeId, String ingredientId);

    void deleteById(String recipeId, String ingredientId);
}
