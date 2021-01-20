package no.kristianped.recipemongo.controllers;

import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.exceptions.NotFoundException;
import no.kristianped.recipemongo.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    RecipeService recipeService;

    @InjectMocks
    RecipeController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ControllerExceptionHandler()).build();
    }

    @Test
    void testGetRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeService.findById(anyString())).thenReturn(Mono.just(recipe));

        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/show"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("recipe/show"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("recipe"));
    }

    @Test
    void testGetRecipeNotFound() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeService.findById(anyString())).thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/show"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.view().name("404error"));
    }

    @Test
    void testBadRequest() throws Exception {
        when(recipeService.findById(anyString())).thenThrow(NumberFormatException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/show"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.view().name("400error"));
    }

    @Test
    void testGetNewRecipeForm() throws Exception {
        RecipeCommand command = new RecipeCommand();

        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("recipe/recipeform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("recipe"));
    }

    @Test
    void testPostNewRecipeForm() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));

        mockMvc.perform(MockMvcRequestBuilders.post("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "some string")
                .param("directions", "a direction")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/recipe/2/show"));
    }

    @Test
    void testGetUpdateView() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.findByCommandById(anyString())).thenReturn(Mono.just(command));

        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/update"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("recipe/recipeform"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("recipe"));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/delete"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"));

        verify(recipeService, times(1)).deleteById(anyString());
    }
}