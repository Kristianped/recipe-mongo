package no.kristianped.recipemongo.services;

import no.kristianped.recipemongo.commands.IngredientCommand;
import no.kristianped.recipemongo.commands.UnitOfMeasureCommand;
import no.kristianped.recipemongo.converters.IngredientCommandToIngredient;
import no.kristianped.recipemongo.converters.IngredientToIngredientCommand;
import no.kristianped.recipemongo.converters.UnitOfMeasureCommandToUnitOfMeasure;
import no.kristianped.recipemongo.converters.UnitOfMeasureToUnitOfMeasureCommand;
import no.kristianped.recipemongo.domain.Ingredient;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.domain.UnitOfMeasure;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import no.kristianped.recipemongo.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    IngredientToIngredientCommand ingredientToIngredientCommand;
    IngredientCommandToIngredient ingredientCommandToIngredient;

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    IngredientServiceImpl ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());

        ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand, ingredientCommandToIngredient, recipeReactiveRepository, unitOfMeasureRepository);
    }

    @Test
    void findByRecipeIdAndIngredientId() {
        // given
        Recipe recipe = new Recipe();
        recipe.setId("1");

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId("1");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId("2");

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId("3");

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        // when
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1", "3").block();

        // then
        assertEquals("3", ingredientCommand.getId());
        assertEquals("1", ingredientCommand.getRecipeId());
        verify(recipeReactiveRepository, times(1)).findById(anyString());
    }

    @Test
    void testSaveRecipeCommand() {
        //given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");
        command.setUnitOfMeasure(new UnitOfMeasureCommand());
        command.getUnitOfMeasure().setId("1234");

        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(ingredient);

        when(recipeReactiveRepository.findById(command.getRecipeId())).thenReturn(Mono.just(savedRecipe));
        when(unitOfMeasureRepository.findById(command.getUnitOfMeasure().getId())).thenReturn(Mono.just(new UnitOfMeasure()));

        //when
        StepVerifier.create(ingredientService.saveIngredientCommand(command))
                .expectNextMatches(savedCommand -> savedCommand.getId().equals(command.getId()))
                .expectComplete()
                .verify();

        verify(recipeReactiveRepository).findById(command.getRecipeId());
        verify(recipeReactiveRepository).save(any(Recipe.class));
    }

    @Test
    void testDelete() {
        // given
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");
        recipe.addIngredient(ingredient);

        // when
        when(recipeReactiveRepository.findById(recipe.getId())).thenReturn(Mono.just(recipe));
        when(recipeReactiveRepository.save(recipe)).thenReturn(Mono.just(recipe));
        StepVerifier.create(ingredientService.deleteById(recipe.getId(), ingredient.getId()))
                .expectComplete()
                .verify();

        // then
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
    }
}