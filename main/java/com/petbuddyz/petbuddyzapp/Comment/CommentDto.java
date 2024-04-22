package com.petbuddyz.petbuddyzapp.Comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CommentDto {

    @NotBlank
    @Size(max = 50)
    private String content;
    
}
