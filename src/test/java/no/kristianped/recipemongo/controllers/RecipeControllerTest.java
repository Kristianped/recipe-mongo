package no.kristianped.recipemongo.controllers;

import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.domain.Difficulty;
import no.kristianped.recipemongo.domain.Notes;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.exceptions.NotFoundException;
import no.kristianped.recipemongo.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = RecipeController.class)
class RecipeControllerTest {

    @MockBean
    RecipeService recipeService;

    @InjectMocks
    RecipeController controller;

    @Autowired
    WebTestClient webTestClient;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        controller = new RecipeController(recipeService);
    }

    @Test
    void testGetRecipe() throws Exception {

        Recipe recipe = new Recipe();
        recipe.setId("1");
        recipe.setDescription("");
        recipe.setCookTime(10);
        recipe.setPrepTime(10);
        recipe.setDifficulty(Difficulty.MODERATE);
        recipe.setDirections("");
        recipe.setNotes(new Notes());

        when(recipeService.findById(anyString())).thenReturn(Mono.just(recipe));

        webTestClient.get().uri("/recipe/1/show")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
    }

    @Test
    void testGetRecipeNotFound() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeService.findById(anyString())).thenThrow(NotFoundException.class);

//        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/show"))
//                .andExpect(MockMvcResultMatchers.status().isNotFound())
//                .andExpect(MockMvcResultMatchers.view().name("404error"));

        webTestClient.get().uri("/recipe/1/show")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().returnResult();
    }

    @Test
    void testBadRequest() throws Exception {
        when(recipeService.findById(anyString())).thenThrow(NumberFormatException.class);

        webTestClient.get().uri("/recipe/1/show")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().returnResult();
    }

    @Test
    void testGetNewRecipeForm() throws Exception {
        RecipeCommand command = new RecipeCommand();

        webTestClient.get().uri("/recipe/new")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
    }

    @Test
    void testPostNewRecipeForm() throws Exception {
        RecipeCommand recipe = new RecipeCommand();
        recipe.setId("1");

        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(recipe));

        webTestClient.post().uri("/recipe")
                .body(BodyInserters.fromFormData("id", "").with("description", "some string").with("directions", "a direction"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectBody().returnResult();
    }

    @Test
    void testGetUpdateView() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("2");

        when(recipeService.findByCommandById(anyString())).thenReturn(Mono.just(command));

        webTestClient.get().uri("/recipe/1/update")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
    }

    @Test
    void testDelete() throws Exception {
        when(recipeService.deleteById(anyString())).thenReturn(Mono.empty());

        webTestClient.get().uri("/recipe/1/delete")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectBody().returnResult();

        verify(recipeService, times(1)).deleteById(anyString());
    }
}