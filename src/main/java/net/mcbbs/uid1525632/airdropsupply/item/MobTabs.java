package net.mcbbs.uid1525632.airdropsupply.item;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.entry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MobTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AirdropSupply.MOD_ID);
    // 工具
    public static final RegistryObject<CreativeModeTab> VENDING_MACHINE = TABS.register("vending_machine",
            () -> CreativeModeTab
                    .builder()
                    .title(Component.translatable("creativetab.airdrop_supply.airdrop_supply"))
                    .icon(() -> new ItemStack(ModItems.AIRDROP_PAGER.get()))
                    .displayItems((parameters, output)->{
                        output.accept(ModItems.AIRDROP_PAGER.get());
                        output.accept(ModItems.AIRDROP_LOCATION_CANCELLER.get());
                        output.accept(ModItems.AIRDROP_LOCATION_FIXER.get());
                    })
                    .build()
    );
}
