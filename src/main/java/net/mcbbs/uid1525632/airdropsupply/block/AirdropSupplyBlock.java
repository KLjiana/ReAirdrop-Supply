package net.mcbbs.uid1525632.airdropsupply.block;

import net.mcbbs.uid1525632.airdropsupply.blockentity.AirdropSupplyBlockEntity;
import net.mcbbs.uid1525632.airdropsupply.entry.ModBlockEntities;
import net.mcbbs.uid1525632.airdropsupply.misc.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AirdropSupplyBlock extends HorizontalDirectionalBlock implements EntityBlock {

    // TODO Collision Shape
    private static final VoxelShape COLLISION_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type",Type.class);
    public static final EnumProperty<CaseLevel> LEVEL = EnumProperty.create("level",CaseLevel.class);
    public AirdropSupplyBlock() {
        super(BlockBehaviour.Properties.of().strength(20F).sound(SoundType.METAL).explosionResistance(20).noOcclusion());
        registerDefaultState((this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(TYPE, Type.MEDIC).setValue(LEVEL,CaseLevel.BASIC)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return ModBlockEntities.AIRDROP_SUPPLY.get().create(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModBlockEntities.AIRDROP_SUPPLY.get() ? AirdropSupplyBlockEntity::ticker : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING).add(TYPE).add(LEVEL);
    }

    @Override
    public InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AirdropSupplyBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer,(AirdropSupplyBlockEntity)blockentity);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof Container) {
                Containers.dropContents(pLevel, pPos, (Container)blockentity);
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
    // TODO
    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return COLLISION_SHAPE;
    }

    public static void setDespawnTime(Level overworld, BlockPos pPos){
        if(overworld.getBlockEntity(pPos) instanceof AirdropSupplyBlockEntity airdropSupplyBlockEntity){
            airdropSupplyBlockEntity.setDespawnTime(overworld.getGameTime() + Configuration.AIRDROP_DESPAWN_TIME.get());
        }
    }

    public enum Type implements StringRepresentable {
        NORMAL,
        MEDIC;
        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }

    public enum CaseLevel implements StringRepresentable {
        BASIC,
        MEDIUM,
        ADVANCED;
        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
