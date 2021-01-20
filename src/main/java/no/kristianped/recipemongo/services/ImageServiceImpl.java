package no.kristianped.recipemongo.services;

import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.reactive.RecipeReactiveRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final RecipeReactiveRepository recipeRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Mono<Void> saveImageFile(String id, MultipartFile file) {
        Mono<Recipe> recipeMono = recipeRepository.findById(id)
                .map(recipe -> {
                    Byte[] bytes = new Byte[0];

                    try {
                        bytes = new Byte[file.getBytes().length];
                        int i = 0;

                        for (byte b : file.getBytes())
                            bytes[i++] = b;

                        recipe.setImage(bytes);
                        return recipe;
                    } catch (Exception e) {
                        log.error("Error occured", e);
                        e.printStackTrace();
                    }
                    return null;
                });

        try {
            recipeRepository.save(recipeMono.block());
        } catch (Exception e) {
            // just catch
        }


        return Mono.empty();
    }
}
