package com.delivery;

import com.delivery.exception.InvalidStrategyConfigurationException;
import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.*;
import com.delivery.planner.RoutePlanner;
import com.delivery.selector.StrategySelector;
import com.delivery.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteOptimizationTest {

    private List<Route> sampleRoutes;
    private Location start;
    private Location end;

    @BeforeEach
    void setUp() {
        start = new Location("Hub A", 0.0, 0.0);
        end = new Location("Hub B", 1.0, 1.0);

        sampleRoutes = new ArrayList<>();
        sampleRoutes.add(new Route("R1", start, end, 10.0, 30, 1000.0, 2.0, true, 5, 0.90)); // Time: 35
        sampleRoutes.add(new Route("R2", start, end, 8.0, 20, 2000.0, 3.0, true, 0, 0.95));  // Time: 20
        sampleRoutes.add(new Route("R3", start, end, 15.0, 50, 500.0, 1.0, true, 2, 0.80));  // Time: 52
        sampleRoutes.add(new Route("R4", start, end, 12.0, 25, 1200.0, 1.5, false, 0, 0.99)); // Unavailable
    }

    @Test
    void testFastestStrategy() {
        RouteStrategy strategy = new FastestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000.0, 60, false, 5.0);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, request);

        assertTrue(decision.success());
        assertEquals("R2", decision.selectedRoute().id());
    }

    @Test
    void testCheapestStrategy() {
        RouteStrategy strategy = new CheapestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.ECONOMY, 5000.0, 60, false, 5.0);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, request);

        assertTrue(decision.success());
        assertEquals("R3", decision.selectedRoute().id());
    }

    @Test
    void testEcoStrategy() {
        RouteStrategy strategy = new EcoFriendlyRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.ECO, 5000.0, 60, true, 5.0);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, request);

        assertTrue(decision.success());
        assertEquals("R3", decision.selectedRoute().id());
    }

    @Test
    void testBalancedStrategy() {
        RouteStrategy strategy = new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2);
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000.0, 60, false, 5.0);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, request);

        assertTrue(decision.success());
        assertNotNull(decision.selectedRoute());
    }

    @Test
    void testRuntimeSwitching() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000.0, 60, false, 5.0);

        assertEquals("Fastest Strategy", planner.getStrategy().getStrategyName());
        planner.setStrategy(new EcoFriendlyRouteStrategy());
        assertEquals("Eco Strategy", planner.getStrategy().getStrategyName());
        planner.setStrategy(new CheapestRouteStrategy());
        assertEquals("Cheapest Strategy", planner.getStrategy().getStrategyName());
    }

    @Test
    void testAutomaticStrategySelection() {
        DeliveryRequest urgentReq = new DeliveryRequest(DeliveryPriority.URGENT, 5000.0, 60, false, 5.0);
        RouteStrategy selected = StrategySelector.selectStrategy(urgentReq);
        assertEquals("Fastest Strategy", selected.getStrategyName());

        DeliveryRequest ecoReq = new DeliveryRequest(DeliveryPriority.ECO, 5000.0, 60, true, 5.0);
        RouteStrategy ecoSelected = StrategySelector.selectStrategy(ecoReq);
        assertEquals("Eco Strategy", ecoSelected.getStrategyName());
    }

    @Test
    void testHardConstraintBudgetFiltering() {
        RouteStrategy strategy = new CheapestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.ECONOMY, 400.0, 60, false, 5.0);

        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(sampleRoutes, request));
    }

    @Test
    void testHardConstraintDeadlineRestriction() {
        RouteStrategy strategy = new FastestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000.0, 15, false, 5.0);

        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(sampleRoutes, request));
    }

    @Test
    void testUnavailableRouteFilteredOut() {
        RouteStrategy strategy = new FastestRouteStrategy();
        List<Route> customRoutes = List.of(
                new Route("R4", start, end, 5.0, 10, 100.0, 1.0, false, 0, 0.9)
        );
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000.0, 60, false, 5.0);

        assertThrows(NoSuitableRouteException.class, () -> strategy.selectRoute(customRoutes, request));
    }

    @Test
    void testInvalidBalancedWeightsThrowsException() {
        assertThrows(InvalidStrategyConfigurationException.class, () -> {
            new BalancedRouteStrategy(0.5, 0.5, 0.5, 0.5);
        });
    }

    @Test
    void testNewFifthStrategyPremium() {
        RouteStrategy strategy = new PremiumRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.PREMIUM, 5000.0, 60, false, 5.0);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, request);

        assertTrue(decision.success());
        assertEquals(0.95, decision.selectedRoute().safetyIndex(), 0.001);
    }

    @Test
    void testStrategyComparisonMode() {
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.STANDARD, 5000.0, 60, false, 5.0);
        List<RouteStrategy> strategies = List.of(new FastestRouteStrategy(), new CheapestRouteStrategy());

        for (RouteStrategy s : strategies) {
            RouteDecision decision = s.selectRoute(sampleRoutes, request);
            assertNotNull(decision.selectedRoute());
            assertFalse(decision.strategyName().isEmpty());
        }
    }

    @Test
    void testEmptyRouteListThrowsException() {
        RouteStrategy strategy = new FastestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000.0, 60, false, 5.0);

        assertThrows(IllegalArgumentException.class, () -> strategy.selectRoute(new ArrayList<>(), request));
    }

    @Test
    void testNullRouteListThrowsException() {
        RouteStrategy strategy = new FastestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000.0, 60, false, 5.0);

        assertThrows(IllegalArgumentException.class, () -> strategy.selectRoute(null, request));
    }

    @Test
    void testExplanationIsGenerated() {
        RouteStrategy strategy = new FastestRouteStrategy();
        DeliveryRequest request = new DeliveryRequest(DeliveryPriority.URGENT, 5000.0, 60, false, 5.0);
        RouteDecision decision = strategy.selectRoute(sampleRoutes, request);

        assertNotNull(decision.explanation());
        assertFalse(decision.explanation().isBlank());
    }
}