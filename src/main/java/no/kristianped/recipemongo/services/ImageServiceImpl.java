package no.kristianped.recipemongo.services;

import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.domain.Recipe;
import no.kristianped.recipemongo.repositories.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final RecipeRepository recipeRepository;

    public ImageServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void saveImageFile(String id, MultipartFile file) {
        try {
            Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new Exception("Recipe not found with ID: " + id));
            Byte[] bytes = new Byte[file.getBytes().length];
            int i = 0;

            for (byte b : file.getBytes())
                bytes[i++] = b;

            recipe.setImage(bytes);
            recipeRepository.save(recipe);
        } catch (Exception e) {
            log.error("Error occured", e);
            e.printStackTrace();
        }
    }
}
