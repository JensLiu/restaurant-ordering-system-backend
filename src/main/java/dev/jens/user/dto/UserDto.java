package dev.jens.user.dto;

import dev.jens.user.Role;
import dev.jens.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String imageSrc;
    private String role;
    private Date registeredAt;
    private String password;

    public UserDto(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.imageSrc = user.getImageSrc();
        this.role = user.getRole().name();
        this.registeredAt = user.getRegisteredAt();
        this.password = "";
    }

}
