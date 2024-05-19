package com.GreenThumb.GT.DTO;

import com.GreenThumb.GT.Views.Views;
import com.GreenThumb.GT.models.Resource.Resource;
import com.GreenThumb.GT.models.Resource.ResourceType;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO {
    @JsonView(Views.User.class)

    private Long id;
    @JsonView(Views.User.class)

    private String name;
    @JsonView(Views.User.class)
    private String description;
    @JsonView(Views.User.class)

    private double price;
    @JsonView(Views.User.class)

        private ResourceType type;
    @JsonView(Views.ADMIN.class)
    private int quantity; // Visible only to admins

    @JsonView(Views.User.class)


        private UserDTO owner;

        public ResourceDTO(Resource exchange) {
            this.id = exchange.getId();
            this.name = exchange.getName();
            this.description = exchange.getDescription();
            this.price = exchange.getPrice();
            this.type = exchange.getType();
            this.owner = new UserDTO(exchange.getOwner().getEmail());
        }

}
