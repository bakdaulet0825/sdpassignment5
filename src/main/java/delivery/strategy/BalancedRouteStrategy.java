package delivery.strategy;

import delivery.exception.InvalidStrategyConfigurationException;
import delivery.exception.NoSuitableRouteException;
import delivery.model.DeliveryRequest;
import delivery.model.Route;
import delivery.model.RouteDecision;
import java.util.ArrayList;
import java.util.List;

public class BalancedRouteStrategy extends AbstractRouteStrategy {

    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(double timeWeight, double costWeight, double distanceWeight, double emissionWeight) {
        double sum = timeWeight + costWeight + distanceWeight + emissionWeight;
        if (Math.abs(sum - 1.0) > 0.001) {
            throw new InvalidStrategyConfigurationException("Balanced strategy weights must sum up to 1.0. Current sum: " + sum);
        }
        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    public RouteDecision selectRoute(List<Route> routes, DeliveryRequest request) {
        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException("Route list cannot be null or empty.");
        }

        List<String> rejectionLog = new ArrayList<>();
        List<Route> validRoutes = new ArrayList<>();

        for (Route route : routes) {
            if (satisfiesConstraints(route, request, rejectionLog)) {
                validRoutes.add(route);
            }
        }

        if (validRoutes.isEmpty()) {
            throw new NoSuitableRouteException("No route satisfies the constraints for BalancedRouteStrategy.");
        }

        double maxTime = validRoutes.stream().mapToInt(r -> r.estimatedMinutes() + r.trafficDelayMinutes()).max().orElse(1);
        double maxCost = validRoutes.stream().mapToDouble(Route::cost).max().orElse(1);
        double maxDist = validRoutes.stream().mapToDouble(Route::distanceKm).max().orElse(1);
        double maxEmis = validRoutes.stream().mapToDouble(Route::carbonEmission).max().orElse(1);

        Route bestRoute = null;
        double lowestScore = Double.MAX_VALUE;

        for (Route route : validRoutes) {
            double normTime = (route.estimatedMinutes() + route.trafficDelayMinutes()) / (maxTime == 0 ? 1 : maxTime);
            double normCost = route.cost() / (maxCost == 0 ? 1 : maxCost);
            double normDist = route.distanceKm() / (maxDist == 0 ? 1 : maxDist);
            double normEmis = route.carbonEmission() / (maxEmis == 0 ? 1 : maxEmis);

            double score = (normTime * timeWeight) + (normCost * costWeight) + (normDist * distanceWeight) + (normEmis * emissionWeight);

            if (score < lowestScore) {
                lowestScore = score;
                bestRoute = route;
            }
        }

        String explanation = String.format("Selected Route %s with Balanced Score: %.4f (Weights -> Time: %.2f, Cost: %.2f, Dist: %.2f, Emis: %.2f)",
                bestRoute.id(), lowestScore, timeWeight, costWeight, distanceWeight, emissionWeight);

        return new RouteDecision(bestRoute, getStrategyName(), true, explanation, rejectionLog);
    }

    @Override
    public String getStrategyName() {
        return "Balanced Strategy";
    }
}