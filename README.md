# Assignment 5: Smart Delivery & Route Optimization Engine

## 1. Core Architecture & Strategy Pattern
- **Problem:** Avoid `if-else` or `switch` monoliths when selecting route optimization algorithms.
- **Context:** `RoutePlanner` maintains a reference to a `RouteStrategy` abstraction and delegates route selection to it.
- **Strategy:** `RouteStrategy` interface with concrete strategies: `FastestRouteStrategy`, `CheapestRouteStrategy`, `EcoFriendlyRouteStrategy`, `BalancedRouteStrategy`, `PremiumRouteStrategy`.

## 2. Dynamic Business Conditions & Constraints
- Hard constraints (e.g., budget limits, max delivery time, emissions, availability) are encapsulated in `AbstractRouteStrategy`.
- Routes violating constraints are safely rejected with explicit logs in `RouteDecision`.

## 3. Difference between Strategy and Bridge
- **Strategy:** Focuses on algorithm substitution at runtime (*behavioral pattern*).
- **Bridge:** Separates abstraction from implementation so both can vary independently (*structural pattern*).

## 4. SOLID Principles Applied
- **SRP:** `RoutePlanner` handles context execution, `StrategySelector` maps rules, strategies compute optimal routes.
- **OCP:** Adding `PremiumRouteStrategy` required zero modifications to `RoutePlanner` or existing strategies.
- **DIP:** `RoutePlanner` depends on the abstraction `RouteStrategy`.

## 5. How to Run
```bash
mvn clean test
mvn compile exec:java -Dexec.mainClass="com.delivery.Main"