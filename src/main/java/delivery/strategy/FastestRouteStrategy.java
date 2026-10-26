package com.delivery.strategy;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import java.util.ArrayList;
import java.util.List;

public class FastestRouteStrategy extends AbstractRouteStrategy {

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("Route list cannot be null or empty.");
        }

        List<String> rejectionLog = new ArrayList<>();
        Route bestRoute = null;
        int minTotalTime = Integer.MAX_VALUE;

        for (Route route : routes) {
            if (!satisfiesConstraints(route, request, rejectionLog)) {
                continue;
            }
            int totalTime = route.estimatedMinutes() + route.trafficDelayMinutes();
            if (totalTime < minTotalTime) {
                minTotalTime = totalTime;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies the constraints for FastestRouteStrategy.");
        }

        String explanation = String.format("Selected Route %s. Base time: %d min, Traffic delay: +%d min, Total: %d min.",
                bestRoute.id(), bestRoute.estimatedMinutes(), bestRoute.trafficDelayMinutes(), minTotalTime);

        return new RouteDecision(bestRoute, getStrategyName(), true, explanation, rejectionLog);
    }

    @Override
    public String getStrategyName() {
        return "Fastest Strategy";
    }
}