package dev.quozul.lifeless;

import net.minecraft.server.world.ServerWorld;

import java.util.Optional;


public interface ServerEntityManagerExt {
    default void set$World(ServerWorld world) {
        throw new UnsupportedOperationException("Cannot set world on ServerEntityManager");
    }

    default Optional<ServerWorld> get$World() {
        return Optional.empty();
    }
}