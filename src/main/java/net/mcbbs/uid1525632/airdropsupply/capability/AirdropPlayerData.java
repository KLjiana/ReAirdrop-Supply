package net.mcbbs.uid1525632.airdropsupply.capability;

import com.mojang.datafixers.util.Pair;
import net.mcbbs.uid1525632.airdropsupply.misc.Configuration;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AirdropPlayerData {
    public static final Capability<AirdropPlayerData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public int nextAirdropCountdown = Configuration.AIRDROP_SPAWN_INTERVAL.get();
    @Nullable
    public BlockPos fixDropLocation = null;
    public List<Pair<Long, BlockPos>> airdropDespawnInfo = new ArrayList<>();
    public AirdropPlayerData() {}
}
