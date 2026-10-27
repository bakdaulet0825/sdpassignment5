package delivery.strategy;

import delivery.exception.NoSuitableRouteException;
import delivery.model.DeliveryRequest;
import delivery.model.Route;
import delivery.model.RouteDecision;

import java.util.Comparator;
import java.util.List;

public class FastestRouteStrategy extends AbstractRouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        validateInput(routes, request);

        List<Route> validRoutes = routes.stream()
                .filter(Route::isAvailable)
                .filter(r -> calculateTotalTime(r, request) <= request.maxAllowedTimeMinutes())
                .filter(r -> r.cost() <= request.maxBudget())
                .toList();

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException("No route matches the constraints for Fastest strategy.");
        }

        Route bestRoute = validRoutes.stream()
                .min(Comparator.comparingDouble(r -> calculateTotalTime(r, request)))
                .orElseThrow();

        double totalTime = calculateTotalTime(bestRoute, request);
        String explanation = String.format("Selected Fastest Route %s. Base time: %d min, Traffic delay: +%d min, Total: %.0f min.",
                bestRoute.id(), bestRoute.baseTimeMinutes(), bestRoute.trafficDelayMinutes(), totalTime);

        return new RouteDecision(true, bestRoute, getStrategyName(), explanation);
    }

    private double calculateTotalTime(Route route, DeliveryRequest request) {
        return route.baseTimeMinutes() + route.trafficDelayMinutes();
    }

    @Override
    public String getStrategyName() {
        return "Fastest Strategy";
    }
}