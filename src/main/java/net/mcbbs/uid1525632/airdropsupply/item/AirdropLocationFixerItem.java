package net.mcbbs.uid1525632.airdropsupply.item;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.block.AirdropSupplyBlock;
import net.mcbbs.uid1525632.airdropsupply.capability.AirdropPlayerData;
import net.mcbbs.uid1525632.airdropsupply.entry.ModBlocks;
import net.mcbbs.uid1525632.airdropsupply.entry.ModItems;
import net.mcbbs.uid1525632.airdropsupply.misc.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.jetbrains.annotations.NotNull;

public class AirdropLocationFixerItem extends Item {

    private final boolean cancel;

    public AirdropLocationFixerItem(boolean cancel) {
        super(new Properties().tab(CreativeModeTab.TAB_MISC));
        this.cancel = cancel;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand pUsedHand) {
        var itemStack = player.getItemInHand(pUsedHand);
        if((!cancel && !itemStack.is(ModItems.AIRDROP_LOCATION_FIXER.get())) ||
                (cancel && !itemStack.is(ModItems.AIRDROP_LOCATION_CANCELLER.get())))
            return InteractionResultHolder.pass(player.getItemInHand(pUsedHand));
        if(!level.isClientSide()){
            if(level.dimension().equals(Level.OVERWORLD)){
                var data = player.getCapability(AirdropPlayerData.CAPABILITY);
                data.ifPresent(cap->cap.fixDropLocation= cancel? null : new BlockPos(player.getOnPos().getX(), level.getMaxBuildHeight() - 1,player.getOnPos().getZ()));
                if(!player.isCreative()){
                    itemStack.shrink(1);
                }
                player.sendMessage(
                        cancel?
                                new TranslatableComponent("notification.airdrop_supply.airdrop_location_unset",player.getScoreboardName()) :
                                new TranslatableComponent("notification.airdrop_supply.airdrop_location_set",player.getOnPos().getX(),player.getOnPos().getZ(),player.getScoreboardName()),
                        player.getUUID());
            } else {
                player.sendMessage(
                        cancel?
                                new TranslatableComponent("notification.airdrop_supply.airdrop_location_unset_invalid_dimension"):
                                new TranslatableComponent("notification.airdrop_supply.airdrop_location_set_invalid_dimension",player.getScoreboardName()),
                        player.getUUID());
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack,level.isClientSide());
    }
}
