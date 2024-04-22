package com.petbuddyz.petbuddyzapp.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageDto {
    @NotBlank
    @Size(max = 255)
    private String content;

    private String media;

    private Long postId;
}
