package com.GreenThumb.GT.DTO.ResourceExchangeDTO;

import com.GreenThumb.GT.Views.Views;
import com.GreenThumb.GT.models.User.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserDTO {

    // Getters
    @Getter
    @JsonView(Views.User.class)
    private String email;
    @JsonView(Views.ADMIN.class)
    private String phoneNumber; // Additional field visible to admins

    @JsonView(Views.ADMIN.class)
    private String username;

    @JsonView(Views.ADMIN.class)
    private Role role;


    public UserDTO(String email) {
            this.email = email;
        }
}
