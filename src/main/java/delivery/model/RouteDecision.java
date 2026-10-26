package com.delivery.model;

import java.util.List;

public record RouteDecision(
        Route selectedRoute,
        String strategyName,
        boolean success,
        String explanation,
        List<String> rejectedRoutesLog
) {}