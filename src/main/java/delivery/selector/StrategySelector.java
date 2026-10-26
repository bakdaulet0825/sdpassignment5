package com.delivery.selector;

import com.delivery.model.DeliveryRequest;
import com.delivery.strategy.*;

public class StrategySelector {

    public static RouteStrategy selectStrategy(DeliveryRequest request) {
        if (request == null || request.priority() == null) {
            return new BalancedRouteStrategy(0.25, 0.25, 0.25, 0.25);
        }

        return switch (request.priority()) {
            case URGENT -> new FastestRouteStrategy();
            case ECONOMY -> new CheapestRouteStrategy();
            case ECO -> new EcoFriendlyRouteStrategy();
            case PREMIUM -> new PremiumRouteStrategy();
            case STANDARD -> new BalancedRouteStrategy(0.40, 0.30, 0.10, 0.20);
        };
    }
}