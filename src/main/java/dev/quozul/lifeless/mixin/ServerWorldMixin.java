package dev.quozul.lifeless.mixin;

import dev.quozul.lifeless.ServerEntityManagerExt;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow
    @Final
    private ServerEntityManager<Entity> entityManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        ServerEntityManager<Entity> manager = this.entityManager;
        ServerWorld world = (ServerWorld) (Object) this;
        ServerEntityManagerExt managerMixin = (ServerEntityManagerExt) manager;
        managerMixin.set$World(world);
    }
}
