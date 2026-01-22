package io.github.remain.domain.world;

import io.github.remain.domain.common.Position3D;

import java.util.Objects;

/**
 * Représente un seul bloc (cube) dans le monde du jeu.
 * 
 * Un bloc est l'élément de base du monde. Chaque bloc a :
 * - Une position fixe (les blocs ne bougent pas)
 * - Un type qui détermine son apparence (herbe, terre, pierre, eau...)
 * 
 * Le type peut changer (exemple : creuser transforme herbe en air)
 * mais la position reste toujours la même.
 */
public final class Block {
    
    private final Position3D position;
    private BlockType type;
    
    /**
     * Crée un nouveau bloc à la position spécifiée.
     * 
     * @param x Colonne de la grille
     * @param y Altitude
     * @param z Ligne de la grille
     * @param type Type du bloc (herbe, terre, pierre...)
     */
    public Block(int x, int y, int z, BlockType type) {
        this(new Position3D(x, y, z), type);
    }
    
    /**
     * Crée un nouveau bloc à la position spécifiée.
     */
    public Block(Position3D position, BlockType type) {
        this.position = Objects.requireNonNull(position, "position cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }
    
    /**
     * Retourne la position du bloc.
     */
    public Position3D getPosition() {
        return position;
    }
    
    /**
     * Retourne la coordonnée X (colonne de la grille).
     */
    public int getX() {
        return position.x();
    }
    
    /**
     * Retourne la coordonnée Y (altitude/hauteur).
     */
    public int getY() {
        return position.y();
    }
    
    /**
     * Retourne la coordonnée Z (ligne de la grille).
     */
    public int getZ() {
        return position.z();
    }
    
    /**
     * Retourne le type du bloc.
     */
    public BlockType getType() {
        return type;
    }
    
    /**
     * Change le type du bloc.
     * 
     * Utile pour les modifications de terrain (creuser, placer des blocs).
     */
    public void setType(BlockType type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }
    
    /**
     * Vérifie si on peut marcher sur ce bloc.
     */
    public boolean isWalkable() {
        return type.isWalkable();
    }
    
    /**
     * Vérifie si ce bloc est solide (bloque le mouvement).
     */
    public boolean isSolid() {
        return type.isSolid();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Block other)) return false;
        return position.equals(other.position) && type == other.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(position, type);
    }
    
    @Override
    public String toString() {
        return String.format("Block[position=%s, type=%s]", position, type);
    }
}
