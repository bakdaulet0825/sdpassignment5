package delivery.model;

public record DeliveryRequest(
        DeliveryPriority priority,
        double maxBudget,
        int maxAllowedTimeMinutes,
        boolean ecoPreferred,
        double maxDistanceKm
) {}