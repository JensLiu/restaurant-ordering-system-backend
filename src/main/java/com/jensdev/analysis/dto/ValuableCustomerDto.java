package com.jensdev.analysis.dto;

import com.jensdev.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ValuableCustomerDto {
    Long id;
    String firstname;
    String lastname;
    String imageSrc;
    Date registeredAt;
    Double totalSpend;
}
