package com.delivery.strategy;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import java.util.ArrayList;
import java.util.List;

public class PremiumRouteStrategy extends AbstractRouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("Route list cannot be null or empty.");
        }

        List<String> rejectionLog = new ArrayList<>();
        Route bestRoute = null;
        double highestSafety = -1.0;

        for (Route route : routes) {
            if (!satisfiesConstraints(route, request, rejectionLog)) {
                continue;
            }
            if (route.safetyIndex() > highestSafety) {
                highestSafety = route.safetyIndex();
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies constraints for PremiumRouteStrategy.");
        }

        String explanation = String.format("Selected Route %s (Premium Safety Index: %.2f, Traffic Delay: %d min)",
                bestRoute.id(), bestRoute.safetyIndex(), bestRoute.trafficDelayMinutes());

        return new RouteDecision(bestRoute, getStrategyName(), true, explanation, rejectionLog);
    }

    @Override
    public String getStrategyName() {
        return "Premium Strategy";
    }
}