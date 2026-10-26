package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import java.util.List;

public abstract class AbstractRouteStrategy implements RouteStrategy {

    protected boolean satisfiesConstraints(Route route, DeliveryRequest request, List<String> rejectionLog) {
        if (!route.isAvailable()) {
            rejectionLog.add("Route " + route.id() + " rejected: Route is currently unavailable.");
            return false;
        }
        if (route.cost() > request.maxBudget()) {
            rejectionLog.add("Route " + route.id() + " rejected: Cost (" + route.cost() + ") exceeds max budget (" + request.maxBudget() + ").");
            return false;
        }
        int totalTime = route.estimatedMinutes() + route.trafficDelayMinutes();
        if (totalTime > request.maxDeliveryMinutes()) {
            rejectionLog.add("Route " + route.id() + " rejected: Total time (" + totalTime + " min) exceeds deadline (" + request.maxDeliveryMinutes() + " min).");
            return false;
        }
        if (route.carbonEmission() > request.maxCarbonEmission()) {
            rejectionLog.add("Route " + route.id() + " rejected: Emissions (" + route.carbonEmission() + " kg) exceed max allowed (" + request.maxCarbonEmission() + " kg).");
            return false;
        }
        return true;
    }
}