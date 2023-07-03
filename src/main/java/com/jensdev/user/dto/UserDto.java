package com.jensdev.user.dto;

import com.jensdev.user.modal.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    Long id;
    String firstname;
    String lastname;
    String email;
    String imageSrc;
    String role;
    Date registeredAt;
    String password;

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

    public static UserDto fromDomain(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .imageSrc(user.getImageSrc())
                .role(user.getRole().name())
                .registeredAt(user.getRegisteredAt())
                .build();
    }

}
