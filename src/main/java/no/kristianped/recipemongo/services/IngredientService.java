package no.kristianped.recipemongo.services;


import no.kristianped.recipemongo.commands.IngredientCommand;
import reactor.core.publisher.Mono;

public interface IngredientService {

    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command);

    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);

    Mono<Void> deleteById(String recipeId, String ingredientId);
}
