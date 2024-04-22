

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.petbuddyz.petbuddyzapp.Pet.Pet;
import com.petbuddyz.petbuddyzapp.Pet.PetController;
import com.petbuddyz.petbuddyzapp.Pet.PetModelAssembler;
import com.petbuddyz.petbuddyzapp.Pet.PetService;
@SpringBootTest(classes = {PetController.class, PetService.class, PetModelAssembler.class})
@AutoConfigureMockMvc
public class PetControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PetService petService;

    @MockBean
    private PetModelAssembler petModelAssembler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new PetController(petService, petModelAssembler)).build();
    }
    @Test
    public void testGetAllPets_Success() {
        // Mock dependencies
        PetService petService = mock(PetService.class);
        PetModelAssembler petModelAssembler = mock(PetModelAssembler.class);

        // Create instance of your controller with mocked dependencies
        PetController controller = new PetController(petService, petModelAssembler);
   

        // Mock behavior of dependencies
        List<Pet> petsList = new ArrayList<>();
        petsList.add(new Pet());
        petsList.add(new Pet());
        when(petService.getAllPets()).thenReturn(petsList);

        // Mock behavior of petModelAssembler
        when(petModelAssembler.toModel(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            return EntityModel.of(pet);
        });

        // Call the method under test
        CollectionModel<EntityModel<Pet>> result = controller.getAllPets();

        // Verify the result
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(petsList.size(), result.getContent().size());
    }

    @Test
    public void testGetPet_Success() {
        // Mock dependencies
        PetService petService = mock(PetService.class);
        PetModelAssembler petModelAssembler = mock(PetModelAssembler.class);

        // Create instance of your controller with mocked dependencies
        PetController controller = new PetController(petService, petModelAssembler);
      
        // Mock input data
        Long petId = 1L;

        // Mock behavior of dependencies
        Pet mockPet = new Pet();
        when(petService.getPet(petId)).thenReturn(mockPet);

        // Mock behavior of petModelAssembler
        EntityModel<Pet> mockEntityModel = EntityModel.of(mockPet);
        when(petModelAssembler.toModel(mockPet)).thenReturn(mockEntityModel);

        // Call the method under test
        EntityModel<Pet> result = controller.getPet(petId);

        // Verify the result
        assertNotNull(result);
        assertEquals(mockPet, result.getContent());
    }

    @Test
    public void testGetPetsByOwner_OwnerId_Success() {
        // Mock dependencies
        PetService petService = mock(PetService.class);
        PetModelAssembler petModelAssembler = mock(PetModelAssembler.class);

        // Create instance of your controller with mocked dependencies
        PetController controller = new PetController(petService, petModelAssembler);
       

        // Mock input data
        Long ownerId = 1L;

        // Mock behavior of dependencies
        List<Pet> petsList = new ArrayList<>();
        petsList.add(new Pet());
        petsList.add(new Pet());
        when(petService.getPetsByOwnerId(ownerId)).thenReturn(petsList);

        // Mock behavior of petModelAssembler
        List<EntityModel<Pet>> entityModels = petsList.stream()
                .map(pet -> EntityModel.of(pet))
                .collect(Collectors.toList());
        when(petModelAssembler.toModel(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            return EntityModel.of(pet);
        });

        // Call the method under test
        CollectionModel<EntityModel<Pet>> result = controller.getPetsByOwner_OwnerId(ownerId);

        // Verify the result
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(petsList.size(), result.getContent().size());
    }

}
