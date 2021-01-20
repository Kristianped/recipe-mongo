package no.kristianped.recipemongo.controllers;

import lombok.extern.slf4j.Slf4j;
import no.kristianped.recipemongo.commands.IngredientCommand;
import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.commands.UnitOfMeasureCommand;
import no.kristianped.recipemongo.converters.IngredientToIngredientCommand;
import no.kristianped.recipemongo.converters.UnitOfMeasureToUnitOfMeasureCommand;
import no.kristianped.recipemongo.domain.Ingredient;
import no.kristianped.recipemongo.services.IngredientService;
import no.kristianped.recipemongo.services.RecipeService;
import no.kristianped.recipemongo.services.UnitOfMeasureService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class IngredientController {

    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService, UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model) {
        model.addAttribute("recipe", recipeService.findByCommandById(recipeId).block());

        return "recipe/ingredient/list";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/new")
    public String newIngredient(@PathVariable String recipeId, Model model) {
        RecipeCommand recipeCommand = recipeService.findByCommandById(recipeId).block();

        IngredientCommand ingredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand()).convert(new Ingredient());
        ingredientCommand.setRecipeId(recipeId);
        model.addAttribute("ingredient", ingredientCommand);

        // init uom
        ingredientCommand.setUnitOfMeasure(new UnitOfMeasureCommand());
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms().collectList().block());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/show")
    public String showIngredient(@PathVariable String recipeId, @PathVariable String id, Model model) {
        model.addAttribute("ingredient",ingredientService.findByRecipeIdAndIngredientId(recipeId, id).block());
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId, @PathVariable String id, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id).block());
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms().collectList().block());

        return "recipe/ingredient/ingredientform";
    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command) {
        log.debug("Ingredient to be saved: " + command.toString());
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();

        log.debug("SAVED INGREDIENT:");
        log.debug("Recipe ID:" + savedCommand.getRecipeId());
        log.debug("Ingredient ID: " + savedCommand.getId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/delete")
    public String deleteById(@PathVariable String recipeId, @PathVariable String id) {
        ingredientService.deleteById(recipeId, id).block();

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }

}
