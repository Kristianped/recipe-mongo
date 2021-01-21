package no.kristianped.recipemongo.services;

import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;

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
        FilePart filePart = new FilePart() {
            @Override
            public String filename() {
                return "testing.txt";
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return DataBufferUtils.write(content(), dest);
            }

            @Override
            public String name() {
                return "imagefile";
            }

            @Override
            public HttpHeaders headers() {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "form-data");
                headers.add("Content-Disposition", "name=\"imagefile\"");
                headers.add("Content-Disposition", "filename=\"testing.txt\"");

                headers.add("Content-Type", "text/plain");

                return headers;
            }

            @Override
            public Flux<DataBuffer> content() {
                DefaultDataBufferFactory factory = new DefaultDataBufferFactory();

                return Flux.just(factory.wrap("Kristian".getBytes()));
            }
        };

        Recipe recipe = new Recipe();
        recipe.setId(id);

        // when
        when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(Mono.just(recipe));

        imageService.saveImageFile(id, filePart);

        // then
        verify(recipeRepository, times(1)).findById(anyString());
        verify(recipeRepository, times(1)).save(any());
    }
}