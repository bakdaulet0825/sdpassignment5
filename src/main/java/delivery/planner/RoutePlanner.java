package delivery.planner;

import delivery.model.DeliveryRequest;
import delivery.model.Route;
import delivery.model.RouteDecision;
import delivery.strategy.RouteStrategy;
import java.util.List;

public class RoutePlanner {
    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public RouteStrategy getStrategy() {
        return strategy;
    }

    public RouteDecision plan(List<Route> routes, DeliveryRequest request) {
        return strategy.selectRoute(routes, request);
    }
}