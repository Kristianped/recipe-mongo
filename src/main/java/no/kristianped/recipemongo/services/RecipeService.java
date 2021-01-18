package no.kristianped.recipemongo.services;


import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.domain.Recipe;

import java.util.Set;

public interface RecipeService {

    Set<Recipe> getRecipes();

    Recipe findById(String anyLong);

    RecipeCommand saveRecipeCommand(RecipeCommand command);

    RecipeCommand findByCommandById(String id);

    void deleteById(String id);
}
