package net.mcbbs.uid1525632.airdropsupply.entry;

import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.block.AirdropSupplyBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AirdropSupply.MOD_ID);
    public static final RegistryObject<Block> AIRDROP_SUPPLY = BLOCKS.register("airdrop_supply", AirdropSupplyBlock::new);
}
