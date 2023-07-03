package com.jensdev.analysis.service;

import com.jensdev.analysis.dto.PeakHourDto;
import com.jensdev.analysis.dto.TrendingCategoryDto;
import com.jensdev.analysis.dto.TrendingMenuItemDto;
import com.jensdev.analysis.dto.ValuableCustomerDto;

import java.util.List;

public interface OrderAnalysisService {
    List<PeakHourDto> peakHours();

    List<TrendingCategoryDto> trendingCategories();

    List<TrendingMenuItemDto> trendingMenuItems();

    List<ValuableCustomerDto> valuableCustomers();
}
