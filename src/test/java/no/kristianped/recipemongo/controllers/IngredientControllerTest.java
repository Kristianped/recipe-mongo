package no.kristianped.recipemongo.controllers;

import no.kristianped.recipemongo.commands.IngredientCommand;
import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.commands.UnitOfMeasureCommand;
import no.kristianped.recipemongo.services.IngredientService;
import no.kristianped.recipemongo.services.RecipeService;
import no.kristianped.recipemongo.services.UnitOfMeasureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = IngredientController.class)
class IngredientControllerTest {

    @MockBean
    RecipeService recipeService;

    @MockBean
    IngredientService ingredientService;

    @MockBean
    UnitOfMeasureService unitOfMeasureService;

    @Autowired
    WebTestClient webTestClient;

    IngredientController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller =  new IngredientController(recipeService, ingredientService, unitOfMeasureService);
    }

    @Test
    void testListIngredients() throws Exception {
        // given
        RecipeCommand command = new RecipeCommand();
        when(recipeService.findByCommandById(anyString())).thenReturn(Mono.just(command));

        webTestClient.get().uri("/recipe/1/ingredients")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();

        // then
        verify(recipeService, times(1)).findByCommandById(anyString());
    }

    @Test
    void testShowIngredient() throws Exception {
        // given
        IngredientCommand command = new IngredientCommand();
        UnitOfMeasureCommand uom = new UnitOfMeasureCommand();
        uom.setDescription("Hei");
        command.setUnitOfMeasure(uom);

        // when
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(command));

        // then
        webTestClient.get().uri("/recipe/1/ingredient/2/show")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
    }

    @Test
    void testNewIngredient() throws Exception {
        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        UnitOfMeasureCommand uom = new UnitOfMeasureCommand();
        uom.setId("1");
        uom.setDescription("Hei");

        // when
        when(recipeService.findByCommandById(anyString())).thenReturn(Mono.just(recipeCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.just(uom));

        // then
        webTestClient.get().uri("/recipe/1/ingredient/new")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
    }

    @Test
    void testUpdateIngredientForm() throws Exception {
        // given
        IngredientCommand ingredientCommand = new IngredientCommand();
        UnitOfMeasureCommand uom = new UnitOfMeasureCommand();
        uom.setId("1");
        uom.setDescription("Hei");
        ingredientCommand.setUnitOfMeasure(uom);

        // when
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(ingredientCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.just(uom));

        // then

        webTestClient.get().uri("/recipe/1/ingredient/2/update")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
    }

    @Disabled
    @Test
    void testSaveOrUpdate() throws Exception {
        // given
        IngredientCommand command = new IngredientCommand();
        command.setId("3");
        command.setRecipeId("2");

        UnitOfMeasureCommand uom = new UnitOfMeasureCommand();
        uom.setId("1");
        uom.setDescription("Each");

        // when
        when(ingredientService.saveIngredientCommand(command)).thenReturn(Mono.just(command));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.just(uom));

        // then
        webTestClient.post().uri("/recipe/2/ingredient")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("id", "").with("description", "string")
                        .with("unitOfMeasure", uom).with("amount", ""))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectBody().returnResult();
    }

    @Test
    void testDelete() throws Exception {
        // given

        // when
        when(ingredientService.deleteById(anyString(), anyString())).thenReturn(Mono.empty());

        // then

        webTestClient.get().uri("/recipe/1/ingredient/3/delete")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectBody().returnResult();

        verify(ingredientService, times(1)).deleteById(anyString(), anyString());
    }
}