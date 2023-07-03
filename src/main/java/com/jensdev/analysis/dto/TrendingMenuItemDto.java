package com.jensdev.analysis.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrendingMenuItemDto {
    Long id;
    String name;
    Long count;
}
