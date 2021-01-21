package no.kristianped.recipemongo.controllers;

import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.services.ImageService;
import no.kristianped.recipemongo.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = ImageController.class)
class ImageControllerTest {

    @MockBean
    ImageService imageService;

    @MockBean
    RecipeService recipeService;

    ImageController imageController;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        imageController = new ImageController(recipeService, imageService);
    }

    @Test
    void getImageForm() throws Exception {
        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        // when
        Mockito.when(recipeService.findByCommandById(ArgumentMatchers.anyString())).thenReturn(Mono.just(recipeCommand));

        // then
        webTestClient.get().uri("/recipe/1/image")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();
        Mockito.verify(recipeService, Mockito.times(1)).findByCommandById(ArgumentMatchers.anyString());
    }

    @Test
    void handleImagePost() throws Exception {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain", "Kristian".getBytes());
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

        webTestClient.post().uri("/recipe/1/image")
                .body(BodyInserters.fromMultipartData("imagefile", filePart))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/recipe/1/show");

        Mockito.verify(imageService, Mockito.times(1)).saveImageFile(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    void renderImageFromDatabase() throws Exception {
        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        String s = "fake image text";

        recipeCommand.setImage(s.getBytes());

        // when
        Mockito.when(recipeService.findByCommandById(ArgumentMatchers.anyString())).thenReturn(Mono.just(recipeCommand));

        // then

        EntityExchangeResult<byte[]> result = webTestClient.get()
                .uri("/recipe/1/recipeimage")
                .exchange()
                .expectStatus().isOk()
                .expectBody().returnResult();

        byte[] responseBytes = result.getResponseBody();
        assertEquals(s.getBytes().length, responseBytes.length);
    }
}