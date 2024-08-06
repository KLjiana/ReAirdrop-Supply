package net.mcbbs.uid1525632.airdropsupply.entry;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.item.AirdropLocationFixerItem;
import net.mcbbs.uid1525632.airdropsupply.item.AirdropPagerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AirdropSupply.MOD_ID);
    public static final RegistryObject<Item> AIRDROP_PAGER = ITEMS.register("airdrop_pager", AirdropPagerItem::new);
    public static final RegistryObject<Item> AIRDROP_LOCATION_FIXER = ITEMS.register("airdrop_location_fixer", ()-> new AirdropLocationFixerItem(false));
    public static final RegistryObject<Item> AIRDROP_LOCATION_CANCELLER = ITEMS.register("airdrop_location_canceller", ()-> new AirdropLocationFixerItem(true));

}
