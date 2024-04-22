package com.petbuddyz.petbuddyzapp.Owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OwnerUpdateDto {
    private String ownerName;
    private String ownerPicture;
    private String ownerBio;

}
