package com.jensdev.analysis.dto;

import com.jensdev.menu.dto.MenuItemCategoryDto;
import com.jensdev.menu.modal.MenuItemCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrendingCategoryDto {
    Long id;
    String value;
    Long count;
}
