package com.petbuddyz.petbuddyzapp.Post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostDTO {
    @NotBlank
    @Size(max = 500)
    private String postContent;

    private String postMedia;

    @NotNull
    private Long petId;

    
    private Long communityId;
}