package delivery.strategy;

import delivery.model.DeliveryRequest;
import delivery.model.Route;

import java.util.List;

public abstract class AbstractRouteStrategy implements RouteStrategy {

    protected void validateInput(List<Route> routes, DeliveryRequest request) {
        if (routes == null) {
            throw new IllegalArgumentException("Routes list cannot be null");
        }
        if (routes.isEmpty()) {
            throw new IllegalArgumentException("Routes list cannot be empty");
        }
        if (request == null) {
            throw new IllegalArgumentException("Delivery request cannot be null");
        }
    }

    protected boolean satisfiesConstraints(Route route, DeliveryRequest request, List<String> failureReasons) {
        if (!route.isAvailable()) {
            failureReasons.add("Route " + route.id() + " is not available.");
            return false;
        }
        if (route.cost() > request.maxBudget()) {
            failureReasons.add("Route " + route.id() + " exceeds budget limit.");
            return false;
        }
        if (route.estimatedMinutes() > request.maxAllowedTimeMinutes()) {
            failureReasons.add("Route " + route.id() + " exceeds time limit.");
            return false;
        }
        if (route.distanceKm() > request.maxDistanceKm()) {
            failureReasons.add("Route " + route.id() + " exceeds distance limit.");
            return false;
        }
        return true;
    }
}