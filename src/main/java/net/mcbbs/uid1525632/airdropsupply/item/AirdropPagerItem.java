package net.mcbbs.uid1525632.airdropsupply.item;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.block.AirdropSupplyBlock;
import net.mcbbs.uid1525632.airdropsupply.entry.ModBlocks;
import net.mcbbs.uid1525632.airdropsupply.entry.ModItems;
import net.mcbbs.uid1525632.airdropsupply.misc.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.jetbrains.annotations.NotNull;

public class AirdropPagerItem extends Item {

    public AirdropPagerItem() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand pUsedHand) {
        var itemStack = player.getItemInHand(pUsedHand);
        if(!itemStack.is(ModItems.AIRDROP_PAGER.get()))
            return InteractionResultHolder.pass(player.getItemInHand(pUsedHand));
        if(!level.isClientSide()){
            if(level.dimension().equals(Level.OVERWORLD)){
                var dropPos = new BlockPos((int) player.getX(), level.getMaxBuildHeight(), (int) player.getZ());
                var result = level.getRandom().nextInt(Configuration.AMMO_AIRDROP_WEIGHT.get() + Configuration.MEDIC_AIRDROP_WEIGHT.get());

                int day = (int) (player.level().getDayTime() / 24000) + 1;
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

                var isAmmo = result<Configuration.AMMO_AIRDROP_WEIGHT.get();
                var fallingCrate = FallingBlockEntity.fall(level,dropPos,isAmmo?
                        ModBlocks.AIRDROP_SUPPLY.get().defaultBlockState()
                                .setValue(AirdropSupplyBlock.TYPE, AirdropSupplyBlock.Type.NORMAL)
                                .setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()))
                                .setValue(AirdropSupplyBlock.LEVEL, caseLevel):
                        ModBlocks.AIRDROP_SUPPLY.get().defaultBlockState()
                                .setValue(AirdropSupplyBlock.TYPE, AirdropSupplyBlock.Type.MEDIC)
                                .setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()))
                                .setValue(AirdropSupplyBlock.LEVEL, caseLevel));
                fallingCrate.blockData = new CompoundTag();
                fallingCrate.blockData.putString("LootTable",AirdropSupply.LootTables.calculateLootTable(isAmmo?AirdropSupplyBlock.Type.NORMAL:AirdropSupplyBlock.Type.MEDIC,caseLevel).toString());
                fallingCrate.blockData.putLong("LootTableSeed",level.random.nextLong());
                if(!player.isCreative()){
                    itemStack.shrink(1);
                }
                player.sendSystemMessage(
                        Component.translatable("notification.airdrop_supply.airdrop_summoned",player.getScoreboardName()));
            } else {
                player.sendSystemMessage(
                        Component.translatable("notification.airdrop_supply.airdrop_summoned_invalid_dimension",player.getScoreboardName()));
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack,level.isClientSide());
    }
}
