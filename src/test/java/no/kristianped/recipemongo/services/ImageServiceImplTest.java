package no.kristianped.recipemongo.services;

import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ImageServiceImplTest {

    @Mock
    RecipeReactiveRepository recipeRepository;

    ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageServiceImpl(recipeRepository);
    }

    @Test
    void saveImageFile() throws IOException {
        // given
        String id = "1";
        MultipartFile file = new MockMultipartFile("imagefile", "testing.txt", "text/plain", "Kristian".getBytes());

        Recipe recipe = new Recipe();
        recipe.setId(id);

        // when
        when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(Mono.just(recipe));

        ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass(Recipe.class);
        imageService.saveImageFile(id, file);

        // then
        verify(recipeRepository, times(1)).save(argumentCaptor.capture());
        Recipe savedRecipe = argumentCaptor.getValue();
        assertEquals(file.getBytes().length, savedRecipe.getImage().length);
    }

    @Test
    void saveImageFileException() throws IOException {
        // given
        String id = "1";
        Recipe recipe = new Recipe();
        recipe.setId(id);
        ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass(Recipe.class);

        // when
        when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        imageService.saveImageFile(id, null);

        // then this will throw an exception and the image cannot be saved, since it is null
        verify(recipeRepository, times(0)).save(argumentCaptor.capture());
    }
}