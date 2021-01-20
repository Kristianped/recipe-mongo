package no.kristianped.recipemongo.controllers;

import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.services.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    Model model;

    @InjectMocks
    IndexController controller;

    @Test
    void testMockMVC() throws Exception {
        // given
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // when
        when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe()));

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));
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

        ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        // when
        String name = controller.getIndexPage(model);

        // then
        assertEquals("index", name);
        verify(recipeService, times(1)).getRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
        List<Recipe> setIndexController = argumentCaptor.getValue();
        assertEquals(2, setIndexController.size());
    }
}