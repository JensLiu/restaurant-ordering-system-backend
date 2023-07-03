package com.jensdev.analysis.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PeakHourDto {
    Integer hour;
    Long count;
}
