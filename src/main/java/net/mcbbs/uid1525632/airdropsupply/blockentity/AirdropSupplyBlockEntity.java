package net.mcbbs.uid1525632.airdropsupply.blockentity;

import net.mcbbs.uid1525632.airdropsupply.block.AirdropSupplyBlock;
import net.mcbbs.uid1525632.airdropsupply.entry.ModBlockEntities;
import net.mcbbs.uid1525632.airdropsupply.entry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Objects;

public class AirdropSupplyBlockEntity extends RandomizableContainerBlockEntity {

    public static final DustColorTransitionOptions AIRDROP_SIGNAL = new DustColorTransitionOptions(new Vector3f(Vec3.fromRGB24(14761505).toVector3f()), DustParticleOptions.REDSTONE_PARTICLE_COLOR, 4.0F);
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> new InvWrapper(this));
    private long despawnTime = Long.MAX_VALUE;
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(@NotNull Level pLevel, @NotNull BlockPos blockPos, @NotNull BlockState pState) {
            playSound(pState, ModSoundEvents.OPEN_AIRDROP.get());
        }

        @Override
        protected void onClose(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState) {}

        @Override
        protected void openerCountChanged(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, int pCount, int pOpenCount) {}

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu)player.containerMenu).getContainer();
                return container == AirdropSupplyBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public AirdropSupplyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.AIRDROP_SUPPLY.get(), blockPos, blockState);
    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos blockPos, BlockState blockState, T t) {
        tick(level, blockPos, blockState, (AirdropSupplyBlockEntity) t);
    }
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AirdropSupplyBlockEntity pBlockEntity){
        if(pLevel.isClientSide){
            for(int i=0;i<6;i++){
                pLevel.addAlwaysVisibleParticle(AIRDROP_SIGNAL,
                        pPos.getX() + pLevel.random.nextGaussian() * 1/3,
                        pPos.getY() + 10 + Math.abs(pLevel.random.nextGaussian() * 30),
                        pPos.getZ() + pLevel.random.nextGaussian()  * 1/3,
                        0,3,0);
            }
            for(int i=0;i<6;i++){
                pLevel.addAlwaysVisibleParticle(AIRDROP_SIGNAL,
                        pPos.getX() + pLevel.random.nextGaussian(),
                        pPos.getY() + 20 + Math.abs(pLevel.random.nextGaussian() * 20),
                        pPos.getZ() + pLevel.random.nextGaussian(),
                        0,3,0);
            }
            for(int i=0;i<6;i++){
                pLevel.addAlwaysVisibleParticle(AIRDROP_SIGNAL,
                        pPos.getX() + pLevel.random.nextGaussian() * 2.5,
                        pPos.getY() + 30 + Math.abs(pLevel.random.nextGaussian() * 10),
                        pPos.getZ() + pLevel.random.nextGaussian() * 2.5,
                        0,3,0);
            }
        } else {
            if (!pBlockEntity.remove) {
                pBlockEntity.openersCounter.recheckOpeners(pLevel, pPos, pState);
            }
            if(pLevel.getGameTime() >= pBlockEntity.despawnTime){
                pBlockEntity.items.clear();
                pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
            }
            if(pBlockEntity.isEmpty() && pBlockEntity.lootTable==null){
                pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
            }
        }
    }
    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.airdrop_supply.airdrop_supply");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int pId, @NotNull Inventory pPlayer) {
        return ChestMenu.threeRows(pId, pPlayer, this);
    }

    @Override
    public void startOpen(@NotNull Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.incrementOpeners(pPlayer, Objects.requireNonNull(this.getLevel()), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public void stopOpen(@NotNull Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void setDespawnTime(long despawnTime) {
        this.despawnTime = despawnTime;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!this.trySaveLootTable(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.items);
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(pTag)) {
            ContainerHelper.loadAllItems(pTag, this.items);
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            unpackLootTable(null);
            return handler.cast();
        }
        return super.getCapability(cap);
    }

    private void playSound(BlockState pState, SoundEvent pSound) {
        Vec3i vec3i = pState.getValue(AirdropSupplyBlock.FACING).getNormal();
        double d0 = (double)this.worldPosition.getX() + 0.5D + (double)vec3i.getX() / 2.0D;
        double d1 = (double)this.worldPosition.getY() + 0.5D + (double)vec3i.getY() / 2.0D;
        double d2 = (double)this.worldPosition.getZ() + 0.5D + (double)vec3i.getZ() / 2.0D;
        this.level.playSound(null, d0, d1, d2, pSound, SoundSource.BLOCKS, 3F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
}
