package net.mcbbs.uid1525632.airdropsupply.mixin;

import net.mcbbs.uid1525632.airdropsupply.entry.ModBlocks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    @Shadow
    private BlockState blockState;

    public FallingBlockEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canBeReplaced(Lnet/minecraft/world/item/context/BlockPlaceContext;)Z"))
    private boolean injected(BlockState instance, BlockPlaceContext blockPlaceContext) {
        if(blockState.is(ModBlocks.AIRDROP_SUPPLY.get())) return true;
        return instance.canBeReplaced(blockPlaceContext);
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void airdropSupply$neverHurtEntity(float pFallDistance, float pMultiplier, DamageSource pSource, CallbackInfoReturnable<Boolean> cir) {
        if(blockState.is(ModBlocks.AIRDROP_SUPPLY.get())) cir.setReturnValue(false);
    }
}
