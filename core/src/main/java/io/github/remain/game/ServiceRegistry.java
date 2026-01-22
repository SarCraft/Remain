package io.github.remain.game;

import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registre des services du jeu ("boîte à outils").
 * 
 * Cette classe stocke tous les services (outils) du jeu et permet d'y accéder
 * facilement depuis n'importe où dans le code.
 * 
 * Exemple d'utilisation :
 *    Enregistrement (dans GameApplication.create())
 *   serviceRegistry.register(AssetService.class, new AssetServiceImpl());
 *   
 *    Récupération (dans les écrans/systèmes)
 *   AssetService assets = serviceRegistry.get(AssetService.class);
 */
public final class ServiceRegistry implements Disposable {
    
    private final Map<Class<?>, Object> services;
    private boolean disposed;
    
    /**
     * Crée un nouveau registre de services (vide au départ).
     */
    public ServiceRegistry() {
        this.services = new HashMap<>();
        this.disposed = false;
    }
    
    /**
     * Enregistre un service dans le registre.
     * 
     * Si un service du même type existe déjà, il sera remplacé.
     * 
     * @param serviceType Le type du service (exemple : AssetService.class)
     * @param implementation L'instance du service (exemple : new AssetServiceImpl())
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
     * Récupère un service du registre.
     * 
     * Lance une erreur si le service n'existe pas.
     * 
     * @param serviceType Le type du service à récupérer
     * @return Le service demandé
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
     * Récupère un service du registre (version sécurisée).
     * 
     * Retourne Optional.empty() si le service n'existe pas au lieu de lancer une erreur.
     * Utile pour les services optionnels.
     * 
     * @param serviceType Le type du service à récupérer
     * @return Optional contenant le service, ou vide si non trouvé
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
     * Vérifie si un service est enregistré.
     */
    public boolean isRegistered(Class<?> serviceType) {
        return services.containsKey(serviceType);
    }
    
    /**
     * Retire un service du registre.
     * 
     * Le service ne sera PAS fermé automatiquement. Appelez dispose()
     * manuellement si nécessaire.
     * 
     * @return Le service retiré, ou null si non trouvé
     */
    public <T> T unregister(Class<T> serviceType) {
        Object service = services.remove(serviceType);
        
        @SuppressWarnings("unchecked")
        T typedService = (T) service;
        return typedService;
    }
    
    /**
     * Retourne le nombre de services enregistrés.
     */
    public int size() {
        return services.size();
    }
    
    /**
     * Ferme tous les services enregistrés pour libérer la mémoire.
     * 
     * Les services sont fermés dans l'ordre inverse de leur enregistrement
     * pour gérer les dépendances correctement.
     * Après la fermeture, le registre ne peut plus être utilisé.
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
     * Vérifie si le registre a été fermé.
     */
    public boolean isDisposed() {
        return disposed;
    }
}
