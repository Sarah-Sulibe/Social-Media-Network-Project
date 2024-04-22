package com.petbuddyz.petbuddyzapp.Community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDto {
    private String communityName;
    private String communityDescription;
}
