package com.jensdev.analysis.controller;

import com.jensdev.analysis.dto.PeakHourDto;
import com.jensdev.analysis.dto.TrendingCategoryDto;
import com.jensdev.analysis.dto.TrendingMenuItemDto;
import com.jensdev.analysis.dto.ValuableCustomerDto;
import com.jensdev.analysis.service.OrderAnalysisService;
import com.jensdev.common.authException.AuthException;
import com.jensdev.user.modal.Role;
import com.jensdev.user.modal.User;
import com.jensdev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final OrderAnalysisService orderAnalysisService;
    private final UserService userService;

    @GetMapping("/peak-hours")
    public ResponseEntity<List<PeakHourDto>> getPeakHoursAnalysis(Authentication authentication) {
        checkIfAdmin(authentication);
        return ResponseEntity.ok(orderAnalysisService.peakHours());
    }

    @GetMapping("/trending-categories")
    public ResponseEntity<List<TrendingCategoryDto>>
    getTrendingCategoriesAnalysis(Authentication authentication) {
        checkIfAdmin(authentication);
        return ResponseEntity.ok(orderAnalysisService.trendingCategories());
    }

    @GetMapping("/trending-items")
    public ResponseEntity<List<TrendingMenuItemDto>> getTrendingItemsAnalysis(Authentication authentication) {
        checkIfAdmin(authentication);
        return ResponseEntity.ok(orderAnalysisService.trendingMenuItems());
    }

    @GetMapping("/valuable-customers")
    public ResponseEntity<List<ValuableCustomerDto>> getValuableCustomers(Authentication authentication) {
        checkIfAdmin(authentication);
        return ResponseEntity.ok(orderAnalysisService.valuableCustomers());
    }

    private void checkIfAdmin(Authentication authentication) {
        User user = userService.getUser(authentication);
        if (user.getRole() != Role.ADMIN) {
            throw new AuthException("You do not have the authority to access this service");
        }
    }

}
