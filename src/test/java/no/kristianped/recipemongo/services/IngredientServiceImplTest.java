package no.kristianped.recipemongo.services;

import no.kristianped.recipemongo.commands.IngredientCommand;
import no.kristianped.recipemongo.converters.IngredientCommandToIngredient;
import no.kristianped.recipemongo.converters.IngredientToIngredientCommand;
import no.kristianped.recipemongo.converters.UnitOfMeasureCommandToUnitOfMeasure;
import no.kristianped.recipemongo.converters.UnitOfMeasureToUnitOfMeasureCommand;
import no.kristianped.recipemongo.domain.Ingredient;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.RecipeRepository;
import no.kristianped.recipemongo.repositories.UnitOfMeasureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngredientServiceImplTest {

    IngredientToIngredientCommand ingredientToIngredientCommand;
    IngredientCommandToIngredient ingredientCommandToIngredient;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    UnitOfMeasureRepository unitOfMeasureRepository;

    IngredientServiceImpl ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());

        ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand, ingredientCommandToIngredient, recipeRepository, unitOfMeasureRepository);
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
        when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);
        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1", "3");

        // then
        assertEquals("3", ingredientCommand.getId());
        assertEquals("1", ingredientCommand.getRecipeId());
        verify(recipeRepository, times(1)).findById(anyString());
    }

    @Test
    void testSaveRecipeCommand() {
        // given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");

        Optional<Recipe> recipeOptional = Optional.of(new Recipe());

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(new Ingredient());
        savedRecipe.getIngredients().iterator().next().setId("3");

        // when
        when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);
        when(recipeRepository.save(any())).thenReturn(savedRecipe);
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);

        // then
        assertEquals("3", savedCommand.getId());
        verify(recipeRepository, times(1)).findById(anyString());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    void testDelete() {
        // given
        Recipe recipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId("3");
        recipe.addIngredient(ingredient);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        // when
        when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);
        ingredientService.deleteById("1", "3");

        // then
        verify(recipeRepository, times(1)).findById(anyString());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }
}