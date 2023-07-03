package com.jensdev.analysis.service;

import com.jensdev.analysis.dto.TrendingMenuItemDto;
import com.jensdev.analysis.repository.OrderAnalysisRepository;
import com.jensdev.analysis.dto.PeakHourDto;
import com.jensdev.analysis.dto.TrendingCategoryDto;
import com.jensdev.analysis.dto.ValuableCustomerDto;
import com.jensdev.common.infrastructureException.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OrderAnalysisServiceImpl implements OrderAnalysisService {

    private final OrderAnalysisRepository orderAnalysisRepository;

    @Override
    public List<PeakHourDto> peakHours() {
        List<Map<String, Object>> peakTime = orderAnalysisRepository.getPeakHours();
        List<Long> hourlySell = new ArrayList<>(Collections.nCopies(24, 0L));
        Consumer<Map<String, Object>> extractHourlySells = (item) -> {
            try {
                Integer hour = (Integer) item.get("hour");
                Long count = (Long) item.get("count");
                assert 0 <= hour && hour < 24;
                hourlySell.set(hour, count);
            } catch (Exception e) {
                throw new InfrastructureException(e.getMessage());
            }
        };
        peakTime.forEach(extractHourlySells);
        return IntStream.range(0, hourlySell.size())
                .mapToObj(index ->
                        PeakHourDto.builder()
                                .hour(index)
                                .count(hourlySell.get(index))
                                .build()
                ).toList();
    }

    @Override
    public List<TrendingCategoryDto> trendingCategories() {
        List<Map<String, Object>> trendingCategories = orderAnalysisRepository.getTrendingCategories();
        Function<Map<String, Object>, TrendingCategoryDto> extractTrendingCategories = (item) -> {
            try {
                Long id = (Long) item.get("id");
                String value = (String) item.get("value");  // category name
                Long count = (Long) item.get("count");
                return TrendingCategoryDto.builder()
                        .id(id)
                        .value(value)
                        .count(count).build();
            } catch (Exception e) {
                throw new InfrastructureException(e.getMessage());
            }
        };
        return trendingCategories.stream().map(extractTrendingCategories).toList();
    }

    @Override
    public List<TrendingMenuItemDto> trendingMenuItems() {
        List<Map<String, Object>> trendingItems = orderAnalysisRepository.getTrendingMenuItems();
        Function<Map<String, Object>, TrendingMenuItemDto> extractTrendingItems = (item) -> {
            try {
                Long id = (Long) item.get("id");
                String name = (String) item.get("name");  // category name
                Long count = (Long) item.get("count");
                return TrendingMenuItemDto.builder()
                        .id(id)
                        .name(name)
                        .count(count).build();
            } catch (Exception e) {
                throw new InfrastructureException(e.getMessage());
            }
        };
        return trendingItems.stream().map(extractTrendingItems).toList();
    }

    @Override
    public List<ValuableCustomerDto> valuableCustomers() {
        List<Map<String, Object>> valuableCustomers = orderAnalysisRepository.getValuableCustomers();
        Function<Map<String, Object>, ValuableCustomerDto> extractTrendingCustomers = (items) -> {
            try {
                Long id = (Long) items.get("id");
                String firstname = (String) items.get("firstname");
                String lastname = (String) items.get("lastname");
                String imageSrc = (String) items.get("imageSrc");
                Date registeredAt = (Date) items.get("registeredAt");
                Double totalSpend = (Double) items.get("totalSpend");
                return ValuableCustomerDto.builder()
                        .id(id)
                        .firstname(firstname)
                        .lastname(lastname)
                        .imageSrc(imageSrc)
                        .registeredAt(registeredAt)
                        .totalSpend(totalSpend)
                        .build();
            } catch (Exception e) {
                throw new InfrastructureException(e.getMessage());
            }
        };
        return valuableCustomers.stream().map(extractTrendingCustomers).toList();
    }
}
