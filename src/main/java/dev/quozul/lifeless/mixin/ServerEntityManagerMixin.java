package dev.quozul.lifeless.mixin;

import dev.quozul.lifeless.LifelessWorld;
import dev.quozul.lifeless.ServerEntityManagerExt;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(ServerEntityManager.class)
public abstract class ServerEntityManagerMixin<T extends EntityLike> implements ServerEntityManagerExt {
    @Shadow
    protected abstract boolean addEntity(T entity, boolean existing);

    @Redirect(method = "addEntity(Lnet/minecraft/world/entity/EntityLike;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerEntityManager;addEntity(Lnet/minecraft/world/entity/EntityLike;Z)Z"))
    private boolean addEntityMixin(ServerEntityManager<T> instance, T entity, boolean existing) {
        if (this.isAllowed(entity)) {
            return this.addEntity(entity, existing);
        }
        return false;
    }

    @Redirect(method = "addEntities", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private void addEntitiesMixin(Stream<T> stream, Consumer<? super T> action) {
        stream.filter(this::isAllowed).forEach(action);
    }

    @Redirect(method = "loadEntities", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private void loadEntitiesMixin(Stream<T> stream, Consumer<? super T> action) {
        stream.filter(this::isAllowed).forEach(action);
    }

    @Unique
    private boolean isAllowed(T entity) {
        return this.shouldEntitySpawn()
                || entity.isPlayer()
                || entity instanceof BlazeEntity
                || entity instanceof EnderDragonEntity
                || entity instanceof ItemEntity item && item.getStack().isOf(Items.BLAZE_ROD);
    }

    @Unique
    private boolean shouldEntitySpawn() {
        Optional<ServerWorld> world = this.get$World();
        return world.isEmpty() || world.get().getGameRules().getBoolean(LifelessWorld.SHOULD_ENTITY_SPAWN);
    }

    @Unique
    private ServerWorld alone$world = null;

    @Override
    @Unique
    public void set$World(ServerWorld world) {
        this.alone$world = world;
    }

    @Override
    @Unique
    public Optional<ServerWorld> get$World() {
        return Optional.ofNullable(this.alone$world);
    }
}
