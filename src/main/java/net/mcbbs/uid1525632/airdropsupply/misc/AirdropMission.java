package net.mcbbs.uid1525632.airdropsupply.misc;

import com.mojang.datafixers.util.Pair;
import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.block.AirdropSupplyBlock;
import net.mcbbs.uid1525632.airdropsupply.capability.AirdropPlayerData;
import net.mcbbs.uid1525632.airdropsupply.entry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

import java.lang.ref.WeakReference;

public class AirdropMission {
    final WeakReference<Player> playerRef;
    BlockPos calculatePos;
    boolean done = false;

    public AirdropMission(Player player) {
        this.playerRef = new WeakReference<>(player);
    }

    void run(){
        if (done) return;
        var player = (ServerPlayer) playerRef.get();
        if(player==null){
            done = true;
            return;
        }
        var data = player.getCapability(AirdropPlayerData.CAPABILITY);
        data.ifPresent(cap->{
            var overworld = player.level.getServer().overworld();
            // Summon airdrop
            if(calculatePos==null){
                BlockPos dropPos = cap.fixDropLocation;
                if(dropPos==null){
                    var respawnPos = player.getRespawnPosition();
                    if(respawnPos==null || !player.getRespawnDimension().equals(Level.OVERWORLD)){
                        respawnPos = overworld.getSharedSpawnPos();
                    }
                    var ang = player.getRandom().nextDouble() * 360;
                    var x = respawnPos.getX() + Math.cos(ang) * Configuration.AIRDROP_SPREAD_RANGE.get();
                    var z = respawnPos.getZ() + Math.sin(ang) * Configuration.AIRDROP_SPREAD_RANGE.get();
                    dropPos = new BlockPos(x, overworld.getMaxBuildHeight() - 1, z);
                }
                calculatePos = dropPos;
            }
            int step = 0;
            var onState = overworld.getBlockState(calculatePos);
            var belowState = overworld.getBlockState(calculatePos.below());
            while((!onState.isAir() && !onState.is(BlockTags.LEAVES) && !onState.is(BlockTags.SNOW))
                    || (belowState.isAir() || belowState.is(BlockTags.LEAVES) || onState.is(BlockTags.SNOW))){
                if(step>5) return;
                if(calculatePos.getY()<=overworld.getMinBuildHeight() + 1){
                    player.sendMessage(
                            new TranslatableComponent("notification.airdrop_supply.airdrop_crash",player.getScoreboardName()),
                            player.getUUID());
                    done = true;
                    return;
                }
                calculatePos = calculatePos.below();
                step++;
                onState = overworld.getBlockState(calculatePos);
                belowState = overworld.getBlockState(calculatePos.below());
            }
            if(overworld.getBlockState(calculatePos).isAir()){
                var result = player.getRandom().nextInt(Configuration.NO_AIRDROP_WEIGHT.get() + Configuration.AMMO_AIRDROP_WEIGHT.get() + Configuration.MEDIC_AIRDROP_WEIGHT.get());
                AirdropSupplyBlock.Type type = null;
                if(result < Configuration.AMMO_AIRDROP_WEIGHT.get()){
                    type = AirdropSupplyBlock.Type.NORMAL;
                } else if(result < Configuration.AMMO_AIRDROP_WEIGHT.get() + Configuration.MEDIC_AIRDROP_WEIGHT.get()){
                    type = AirdropSupplyBlock.Type.MEDIC;
                }
                if(type==null) {
                    player.sendMessage(
                            new TranslatableComponent("notification.airdrop_supply.airdrop_crash",player.getScoreboardName()),
                            player.getUUID());
                } else {
                    int day = (int) (player.level.getDayTime() / 24000) + 1;
                    int nw = Configuration.BASIC_BASE_WEIGHT.get() + day *  Configuration.BASIC_MULTIPLE_WEIGHT.get();
                    int mw = Configuration.MEDIUM_BASE_WEIGHT.get() + day *  Configuration.MEDIUM_MULTIPLE_WEIGHT.get();
                    int aw = Configuration.ADVANCED_BASE_WEIGHT.get() + day *  Configuration.ADVANCED_MULTIPLE_WEIGHT.get();
                    AirdropSupplyBlock.CaseLevel caseLevel = AirdropSupplyBlock.CaseLevel.BASIC;
                    if(mw>0){
                        if(aw>0){
                            var result2 = player.getRandom().nextInt(nw+mw+aw);
                            if(result2 > nw+mw) caseLevel = AirdropSupplyBlock.CaseLevel.ADVANCED;
                            else if(result2>nw) caseLevel = AirdropSupplyBlock.CaseLevel.MEDIUM;
                        } else {
                            var result2 = player.getRandom().nextInt(nw+mw);
                            if(result2 > nw) caseLevel = AirdropSupplyBlock.CaseLevel.MEDIUM;
                        }
                    } else if(aw>0){
                        var result2 = player.getRandom().nextInt(nw+aw);
                        if(result2 > nw) caseLevel = AirdropSupplyBlock.CaseLevel.ADVANCED;
                    }
                    spawnAirdropCrate(overworld,calculatePos,player,cap,type,caseLevel);
                }
                done = true;
            }
        });
    }

    boolean done() {
        return done;
    }

    private static void spawnAirdropCrate(Level overworld, BlockPos dropPos, Player player, AirdropPlayerData data, AirdropSupplyBlock.Type type, AirdropSupplyBlock.CaseLevel caseLevel){
        overworld.setBlockAndUpdate(dropPos, ModBlocks.AIRDROP_SUPPLY.get().defaultBlockState()
                .setValue(AirdropSupplyBlock.TYPE, type)
                .setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(player.getRandom())));
        RandomizableContainerBlockEntity.setLootTable(overworld, player.getRandom(), dropPos, AirdropSupply.LootTables.calculateLootTable(type,caseLevel));
        AirdropSupplyBlock.setDespawnTime(overworld,dropPos);
        player.sendMessage(
                new TranslatableComponent("notification.airdrop_supply.airdrop_arrive",dropPos.getX(),dropPos.getY(),dropPos.getZ(),player.getScoreboardName()),
                player.getUUID());
        data.airdropDespawnInfo.add(Pair.of(overworld.getGameTime()+Configuration.AIRDROP_DESPAWN_TIME.get(),dropPos));
    }
}
