package delivery.model;

public record Route(
        String id,
        Location startLocation,
        Location endLocation,
        double distanceKm,
        int baseTimeMinutes,
        double cost,
        double emissionsKg,
        boolean isAvailable,
        int trafficDelayMinutes,
        double safetyIndex
) {
    public int estimatedMinutes() {
        return baseTimeMinutes + trafficDelayMinutes;
    }

    public double carbonEmission() {
        return emissionsKg;
    }
}