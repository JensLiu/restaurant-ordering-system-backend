package com.jensdev.analysis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class OrderProcessTimeDto {
    Long id;
    Date createdTime;
    Date paymentTime;
    Date servedTime;
}
