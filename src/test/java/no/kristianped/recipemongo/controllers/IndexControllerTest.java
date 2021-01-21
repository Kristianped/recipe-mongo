package no.kristianped.recipemongo.controllers;

import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = IndexController.class)
class IndexControllerTest {

    @MockBean
    RecipeService recipeService;

    @MockBean
    Model model;

    IndexController controller;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        controller = new IndexController(recipeService);
    }

    @Test
    void testMockMVC() throws Exception {
        // given

        // when
        when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe()));

        EntityExchangeResult<byte[]> result = webTestClient.get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
    }

    @Test
    void getIndexPage() {
        // given
        Set<Recipe> recipes = new HashSet<>();
        recipes.add(new Recipe());

        Recipe recipe = new Recipe();
        recipe.setId("2");
        recipes.add(recipe);

        when(recipeService.getRecipes()).thenReturn(Flux.fromIterable(recipes));

        ArgumentCaptor<Flux<Recipe>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);

        // when
        String name = controller.getIndexPage(model);

        // then
        assertEquals("index", name);
        verify(recipeService, times(1)).getRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
        List<Recipe> setIndexController = argumentCaptor.getValue().collectList().block();
        assertEquals(2, setIndexController.size());
    }
}