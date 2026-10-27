package delivery.strategy;

import delivery.exception.NoSuitableRouteException;
import delivery.model.DeliveryRequest;
import delivery.model.Route;
import delivery.model.RouteDecision;
import java.util.ArrayList;
import java.util.List;

public class CheapestRouteStrategy extends AbstractRouteStrategy {

    private final double baseFee = 500.0;
    private final double perKmRate = 120.0;

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("Route list cannot be null or empty.");
        }

        List<String> rejectionLog = new ArrayList<>();
        Route bestRoute = null;
        double lowestCost = Double.MAX_VALUE;

        for (Route route : routes) {
            if (!satisfiesConstraints(route, request, rejectionLog)) {
                continue;
            }
            double calculatedCost = baseFee + (route.distanceKm() * perKmRate) + route.cost();
            if (calculatedCost < lowestCost) {
                lowestCost = calculatedCost;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies the constraints for CheapestRouteStrategy.");
        }

        String explanation = String.format("Selected Route %s. Calculated total cost: %.2f (Base: %.2f + DistanceFee: %.2f + RouteCost: %.2f)",
                bestRoute.id(), lowestCost, baseFee, bestRoute.distanceKm() * perKmRate, bestRoute.cost());

        return new RouteDecision(bestRoute, getStrategyName(), true, explanation, rejectionLog);
    }

    @Override
    public String getStrategyName() {
        return "Cheapest Strategy";
    }
}