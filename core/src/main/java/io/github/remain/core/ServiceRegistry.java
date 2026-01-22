package io.github.remain.core;

import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Type-safe service locator for dependency management.
 * This registry provides a centralized location for accessing application services
 * without resorting to global static variables or passing dependencies through
 * every constructor.
 * Design Rationale: While dependency injection frameworks exist (Guice, Spring),
 * they add complexity for game development. This lightweight service locator provides
 * type safety and explicit service lifecycle management.
 * Usage Pattern:
 * {@code
 * Registration (in GameApplication.create())
 * serviceRegistry.register(AssetService.class, new AssetServiceImpl());
 * Retrieval (in screens/systems)
 * AssetService assets = serviceRegistry.get(AssetService.class);
 * }
 * Thread Safety: Not thread-safe. Register all services during initialization
 * on the main thread before concurrent access.
 * @author SarCraft
 * @since 1.0
 */
public final class ServiceRegistry implements Disposable {
    
    private final Map<Class<?>, Object> services;
    private boolean disposed;
    
    /**
     * Creates a new empty service registry.
     */
    public ServiceRegistry() {
        this.services = new HashMap<>();
        this.disposed = false;
    }
    
    /**
     * Registers a service instance with its interface type.
     * If a service of the same type already exists, it will be replaced.
     * The old service will NOT be disposed automatically.
     * @param serviceType The interface or class type (used as key)
     * @param implementation The service instance
     * @param <T> The service type
     * @throws NullPointerException if serviceType or implementation is null
     * @throws IllegalStateException if the registry has been disposed
     */
    public <T> void register(Class<T> serviceType, T implementation) {
        if (disposed) {
            throw new IllegalStateException("Cannot register service on disposed registry");
        }
        if (serviceType == null) {
            throw new NullPointerException("serviceType cannot be null");
        }
        if (implementation == null) {
            throw new NullPointerException("implementation cannot be null");
        }
        
        services.put(serviceType, implementation);
    }
    
    /**
     * Retrieves a service by its type.
     * @param serviceType The service interface or class type
     * @param <T> The service type
     * @return The service instance
     * @throws IllegalStateException if the service is not registered
     * @throws NullPointerException if serviceType is null
     */
    public <T> T get(Class<T> serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("serviceType cannot be null");
        }
        
        Object service = services.get(serviceType);
        if (service == null) {
            throw new IllegalStateException(
                "Service not registered: " + serviceType.getName() + 
                ". Did you forget to register it in GameApplication?"
            );
        }
        
        @SuppressWarnings("unchecked")
        T typedService = (T) service;
        return typedService;
    }
    
    /**
     * Retrieves a service by its type, returning Optional.empty() if not found.
     * Use this when the service is optional or you want to handle absence gracefully.
     * @param serviceType The service interface or class type
     * @param <T> The service type
     * @return Optional containing the service, or empty if not registered
     */
    public <T> Optional<T> getOptional(Class<T> serviceType) {
        if (serviceType == null) {
            return Optional.empty();
        }
        
        Object service = services.get(serviceType);
        if (service == null) {
            return Optional.empty();
        }
        
        @SuppressWarnings("unchecked")
        T typedService = (T) service;
        return Optional.of(typedService);
    }
    
    /**
     * Checks if a service is registered.
     * @param serviceType The service type to check
     * @return true if registered, false otherwise
     */
    public boolean isRegistered(Class<?> serviceType) {
        return services.containsKey(serviceType);
    }
    
    /**
     * Unregisters a service.
     * The service will NOT be disposed automatically. Call dispose() on the
     * service manually if needed.
     * @param serviceType The service type to unregister
     * @param <T> The service type
     * @return The removed service instance, or null if not found
     */
    public <T> T unregister(Class<T> serviceType) {
        Object service = services.remove(serviceType);
        
        @SuppressWarnings("unchecked")
        T typedService = (T) service;
        return typedService;
    }
    
    /**
     * Returns the number of registered services.
     * @return Service count
     */
    public int size() {
        return services.size();
    }
    
    /**
     * Disposes all registered services that implement Disposable.
     * Services are disposed in reverse registration order to handle dependencies.
     * After disposal, the registry cannot be used anymore.
     * Thread Safety: This method is not thread-safe. Ensure all other
     * threads have stopped accessing services before calling.
     */
    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        
        // Dispose services in reverse order (handles dependencies)
        services.values().stream()
            .filter(service -> service instanceof Disposable)
            .map(service -> (Disposable) service)
            .forEach(disposable -> {
                try {
                    disposable.dispose();
                } catch (Exception e) {
                    // Log error but continue disposing other services
                    System.err.println("Error disposing service: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        
        services.clear();
        disposed = true;
    }
    
    /**
     * Checks if the registry has been disposed.
     * @return true if disposed, false otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }
}
