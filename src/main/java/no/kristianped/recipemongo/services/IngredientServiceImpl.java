package no.kristianped.recipemongo.services;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.commands.IngredientCommand;
import no.kristianped.recipemongo.converters.IngredientCommandToIngredient;
import no.kristianped.recipemongo.converters.IngredientToIngredientCommand;
import no.kristianped.recipemongo.domain.Ingredient;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.domain.UnitOfMeasure;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import no.kristianped.recipemongo.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.Optional;
import java.util.function.Consumer;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    IngredientToIngredientCommand ingredientToIngredientCommand;
    IngredientCommandToIngredient ingredientCommandToIngredient;
    RecipeReactiveRepository recipeReactiveRepository;
    UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
        return recipeReactiveRepository
                .findById(recipeId)
                .publishOn(Schedulers.parallel())
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand ingredientCommand = ingredientToIngredientCommand.convert(ingredient);
                    ingredientCommand.setRecipeId(recipeId);
                    return ingredientCommand;
                });
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        var recipeMono = recipeReactiveRepository.findById(command.getRecipeId());
        var ingredientMono = findOrCreateIngredient(recipeMono, command);

        return recipeMono.zipWith(ingredientMono)
                .publishOn(Schedulers.parallel())
                .doOnNext(recipeAndIngredient -> recipeReactiveRepository.save(recipeAndIngredient.getT1()))
                .map(recipeAndIngredient -> {
                   Ingredient ingredient = recipeAndIngredient.getT2();
                   Recipe recipe = recipeAndIngredient.getT1();

                   IngredientCommand ingredientCommand = ingredientToIngredientCommand.convert(ingredient);
                   ingredientCommand.setRecipeId(recipe.getId());
                   return ingredientCommand;
                }).defaultIfEmpty(new IngredientCommand());
    }

    @Override
    public Mono<Void> deleteById(String recipeId, String ingredientId) {
        return recipeReactiveRepository.findById(recipeId)
                .publishOn(Schedulers.parallel())
                .doOnNext(recipe -> {
                    log.debug("Found recipe with id: " + recipe.getId());
                    Optional<Ingredient> ingredientOptional = recipe.getIngredients()
                            .stream()
                            .filter(ingredient -> ingredient.getId().equals(ingredientId))
                            .findFirst();

                    if (!ingredientOptional.isPresent()) {
                        log.debug("Ingrident not found with id: " + ingredientId);
                    } else {
                        Ingredient ingredient = ingredientOptional.get();
                        recipe.getIngredients().remove(ingredient);
                    }

                    recipeReactiveRepository.save(recipe).subscribe();
                })
                .doOnError(throwable -> log.error("Unexpected exception during obtaining recipe " + recipeId, throwable))
                .doOnTerminate(() -> {
                    log.debug("Deleted recipe with id ", recipeId);
                })
                .then();
    }

    private Mono<Ingredient> findOrCreateIngredient(Mono<Recipe> recipeMono, IngredientCommand command) {
        return recipeMono.flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equals(command.getId()))
                .single()
                .zipWith(unitOfMeasureRepository.findById(command.getUnitOfMeasure().getId()))
                .doOnNext(updateIngredient(command))
                .map(Tuple2::getT1)
                .switchIfEmpty(addNewIngredientIntoRecipe(recipeMono, command));
    }

    private Consumer<Tuple2<Ingredient, UnitOfMeasure>> updateIngredient(IngredientCommand command) {
        return ingredientAndUnit -> {
            Ingredient ingredient = ingredientAndUnit.getT1();
            UnitOfMeasure unitOfMeasure = ingredientAndUnit.getT2();

            ingredient.setDescription(command.getDescription());
            ingredient.setAmount(command.getAmount());
            ingredient.setUnitOfMeasure(unitOfMeasure);
        };
    }

    private Ingredient createNew(Mono<Recipe> recipeMono, IngredientCommand command) {
        Ingredient ingredient = ingredientCommandToIngredient.convert(command);

        recipeMono.doOnNext(recipe -> recipe.addIngredient(ingredient)).subscribe();

        return ingredient;
    }

    private Mono<Ingredient> addNewIngredientIntoRecipe(Mono<Recipe> recipeMono, IngredientCommand command) {
        return recipeMono
                .zipWith(Mono.just(ingredientCommandToIngredient.convert(command)))
                .doOnNext(recipeAndNewIngredient -> {
                    Recipe recipe = recipeAndNewIngredient.getT1();
                    Ingredient ingredient = recipeAndNewIngredient.getT2();
                    recipe.addIngredient(ingredient);
                })
                .map(Tuple2::getT2);
    }
}
