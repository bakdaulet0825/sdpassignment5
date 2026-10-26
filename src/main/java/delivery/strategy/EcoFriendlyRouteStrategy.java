package com.delivery.strategy;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import java.util.ArrayList;
import java.util.List;

public class EcoFriendlyRouteStrategy extends AbstractRouteStrategy {

    private final double emissionWeight = 0.7;
    private final double distanceWeight = 0.3;

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("Route list cannot be null or empty.");
        }

        List<String> rejectionLog = new ArrayList<>();
        Route bestRoute = null;
        double lowestEcoScore = Double.MAX_VALUE;

        for (Route route : routes) {
            if (!satisfiesConstraints(route, request, rejectionLog)) {
                continue;
            }
            double ecoScore = (route.carbonEmission() * emissionWeight) + (route.distanceKm() * distanceWeight);
            if (ecoScore < lowestEcoScore) {
                lowestEcoScore = ecoScore;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException("No route satisfies the constraints for EcoFriendlyRouteStrategy.");
        }

        String explanation = String.format("Selected Route %s. EcoScore: %.2f (Emissions: %.2f kg, Distance: %.2f km)",
                bestRoute.id(), lowestEcoScore, bestRoute.carbonEmission(), bestRoute.distanceKm());

        return new RouteDecision(bestRoute, getStrategyName(), true, explanation, rejectionLog);
    }

    @Override
    public String getStrategyName() {
        return "Eco Strategy";
    }
}