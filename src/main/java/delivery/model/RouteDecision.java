package delivery.model;

import java.util.List;

public record RouteDecision(
        boolean success,
        Route selectedRoute,
        String strategyUsed,
        String explanation
) {
    public RouteDecision(Route selectedRoute, String strategyUsed, boolean success, String explanation, List<String> alternativeRouteIds) {
        this(success, selectedRoute, strategyUsed, explanation);
    }

    public String strategyName() {
        return strategyUsed;
    }
}