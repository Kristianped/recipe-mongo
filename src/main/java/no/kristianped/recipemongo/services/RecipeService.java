package no.kristianped.recipemongo.services;


import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.domain.Recipe;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecipeService {

    Flux<Recipe> getRecipes();

    Mono<Recipe> findById(String anyLong);

    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command);

    Mono<RecipeCommand> findByCommandById(String id);

    Mono<Void> deleteById(String id);
}
