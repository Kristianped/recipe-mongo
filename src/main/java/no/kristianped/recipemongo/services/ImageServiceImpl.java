package no.kristianped.recipemongo.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    RecipeReactiveRepository recipeRepository;

    @Override
    public Mono<Void> saveImageFile(String id, FilePart file) {
        Mono<Recipe> recipeMono = recipeRepository.findById(id);

        recipeMono.map(recipe -> {
            file.content().map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);

                recipe.setImage(bytes);
                recipeRepository.save(recipe).subscribe(result -> log.debug("Recipe image has been saved"));
                return bytes;
            }).subscribe();


            return recipe;
        }).subscribe();

        return Mono.empty();
    }
}
