package delivery.strategy;

import delivery.model.DeliveryRequest;
import delivery.model.Route;
import delivery.model.RouteDecision;

import java.util.List;

public interface RouteStrategy {
    RouteDecision selectRoute(List<Route> routes, DeliveryRequest request);
    String getStrategyName();
}