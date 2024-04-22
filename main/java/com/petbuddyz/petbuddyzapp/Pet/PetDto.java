package com.petbuddyz.petbuddyzapp.Pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PetDto {
    @NotBlank
    @Size(max = 50)
    private String petName;
}
