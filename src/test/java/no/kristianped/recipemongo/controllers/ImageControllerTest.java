package no.kristianped.recipemongo.controllers;

import no.kristianped.recipemongo.commands.RecipeCommand;
import no.kristianped.recipemongo.services.ImageService;
import no.kristianped.recipemongo.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageControllerTest {

    @Mock
    ImageService imageService;

    @Mock
    RecipeService recipeService;

    ImageController imageController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageController = new ImageController(recipeService, imageService);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    void getImageForm() throws Exception {
        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        // when
        Mockito.when(recipeService.findByCommandById(ArgumentMatchers.anyString())).thenReturn(recipeCommand);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/image"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("recipe"));
        Mockito.verify(recipeService, Mockito.times(1)).findByCommandById(ArgumentMatchers.anyString());
    }

    @Test
    void handleImagePost() throws Exception {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("imagefile", "testing.txt", "text/plain", "Kristian".getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/recipe/1/image").file(multipartFile))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/recipe/1/show"));

        Mockito.verify(imageService, Mockito.times(1)).saveImageFile(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    void renderImageFromDatabase() throws Exception {
        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        String s = "fake image text";
        Byte[] bytes = new Byte[s.getBytes().length];
        int i = 0;

        for (byte b : s.getBytes())
            bytes[i++] = b;

        recipeCommand.setImage(bytes);

        // when
        Mockito.when(recipeService.findByCommandById(ArgumentMatchers.anyString())).thenReturn(recipeCommand);

        // then
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/recipe/1/recipeimage"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        byte[] responseBytes = response.getContentAsByteArray();
        assertEquals(s.getBytes().length, responseBytes.length);
    }
}