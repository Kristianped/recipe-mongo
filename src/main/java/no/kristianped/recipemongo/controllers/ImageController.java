package no.kristianped.recipemongo.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.services.ImageService;
import no.kristianped.recipemongo.services.RecipeService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ImageController {

    RecipeService recipeService;
    ImageService imageService;

    @GetMapping("/recipe/{recipeId}/image")
    public String getForm(@PathVariable String recipeId, Model model) {
        model.addAttribute("recipe", recipeService.findByCommandById(recipeId));

        return "recipe/imageuploadform";
    }

    @PostMapping("recipe/{id}/image")
    public String handleImagePost(@PathVariable String id, @RequestPart("imagefile") FilePart imagefile) {
        imageService.saveImageFile(id, imagefile);

        return "redirect:/recipe/" + id + "/show";
    }

    @GetMapping(value = "/recipe/{id}/recipeimage")
    public @ResponseBody Mono<byte[]> renderImageFromDB(@PathVariable String id) {
        if (id == null)
            return null;

        return recipeService.findByCommandById(id)
                .filter(recipeCommand -> recipeCommand.getImage() != null)
                .map(recipeCommand -> recipeCommand.getImage());
    }
}
