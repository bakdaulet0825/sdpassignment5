package delivery;

import delivery.model.*;
import delivery.planner.RoutePlanner;
import delivery.selector.StrategySelector;
import delivery.strategy.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Location start = new Location("Central Hub", 51.1694, 71.4491);
        Location end = new Location("Client Address", 51.1282, 71.4304);

        List<Route> routes = List.of(
                new Route("Route-1", start, end, 12.5, 25, 1200.0, 2.1, true, 2, 0.95),
                new Route("Route-2", start, end, 10.0, 20, 2500.0, 3.5, true, 0, 0.90),
                new Route("Route-3", start, end, 15.0, 40, 800.0, 1.2, true, 5, 0.80),
                new Route("Route-4", start, end, 11.2, 22, 1500.0, 1.0, true, 1, 0.99),
                new Route("Route-5", start, end, 18.0, 35, 600.0, 4.0, true, 10, 0.70),
                new Route("Route-6", start, end, 9.5, 50, 4000.0, 5.0, false, 0, 0.50),
                new Route("Route-7", start, end, 14.0, 28, 1100.0, 1.8, true, 3, 0.92),
                new Route("Route-8", start, end, 22.0, 60, 500.0, 0.8, true, 12, 0.60)
        );

        System.out.println("=== 1. RUNTIME STRATEGY SWITCHING DEMO ===");
        DeliveryRequest standardRequest = new DeliveryRequest(DeliveryPriority.STANDARD, 5000.0, 60, false, 10.0);
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        System.out.println("Using: " + planner.getStrategy().getStrategyName());
        RouteDecision decision1 = planner.plan(routes, standardRequest);
        System.out.println(decision1.explanation());

        planner.setStrategy(new EcoFriendlyRouteStrategy());
        System.out.println("\nSwitched to: " + planner.getStrategy().getStrategyName());
        RouteDecision decision2 = planner.plan(routes, standardRequest);
        System.out.println(decision2.explanation());

        planner.setStrategy(new CheapestRouteStrategy());
        System.out.println("\nSwitched to: " + planner.getStrategy().getStrategyName());
        RouteDecision decision3 = planner.plan(routes, standardRequest);
        System.out.println(decision3.explanation());

        System.out.println("\n=== 2. AUTOMATIC STRATEGY SELECTION & COMPARISON MODE ===");
        DeliveryRequest urgentRequest = new DeliveryRequest(
                DeliveryPriority.URGENT,
                10000.0,
                120,
                true,
                50.0
        );
        RouteStrategy autoStrategy = StrategySelector.selectStrategy(urgentRequest);
        planner.setStrategy(autoStrategy);

        System.out.println("Request Priority: URGENT -> Auto-selected: " + planner.getStrategy().getStrategyName());
        RouteDecision autoDecision = planner.plan(routes, urgentRequest);
        System.out.println(autoDecision.explanation());

        System.out.println("\n--- Strategy Comparison Mode for Urgent Request ---");
        List<RouteStrategy> allStrategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(0.4, 0.3, 0.1, 0.2),
                new PremiumRouteStrategy()
        );

        for (RouteStrategy s : allStrategies) {
            try {
                RouteDecision res = s.selectRoute(routes, urgentRequest);
                System.out.println("-> " + s.getStrategyName() + ": Selected " + res.selectedRoute().id());
            } catch (Exception e) {
                System.out.println("-> " + s.getStrategyName() + ": Failed / No route (" + e.getMessage() + ")");
            }
        }
    }
}