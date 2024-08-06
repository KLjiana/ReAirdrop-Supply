package net.mcbbs.uid1525632.airdropsupply.capability;

import com.mojang.datafixers.util.Pair;
import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@Mod.EventBusSubscriber()
public class AirdropPlayerDataProvider implements ICapabilitySerializable<CompoundTag> {
    private final AirdropPlayerData imp = new AirdropPlayerData();
    private final LazyOptional<AirdropPlayerData> impOptional = LazyOptional.of(() -> imp);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == AirdropPlayerData.CAPABILITY) {
            return impOptional.cast();
        } else return LazyOptional.empty();
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundNBT = new CompoundTag();
        if (AirdropPlayerData.CAPABILITY != null) {
            compoundNBT.putInt("NextAirdropCountdown", imp.nextAirdropCountdown);
            var lt = new ListTag();
            imp.airdropDespawnInfo.forEach(p->{
                var cp = new CompoundTag();
                cp.putLong("InvalidateTime",p.getFirst());
                cp.put("AirdropPosition",NbtUtils.writeBlockPos(p.getSecond()));
                lt.add(cp);
            });
            compoundNBT.put("AirdropDespawnInfo",lt);
            if(imp.fixDropLocation!=null)
                compoundNBT.put("FixDropLocation",NbtUtils.writeBlockPos(imp.fixDropLocation));
        }
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (AirdropPlayerData.CAPABILITY != null) {
            imp.nextAirdropCountdown = nbt.getInt("NextAirdropCountdown");
            imp.airdropDespawnInfo.clear();
            for (var i : (ListTag) Objects.requireNonNull(nbt.get("AirdropDespawnInfo"))) {
                imp.airdropDespawnInfo.add(Pair.of(
                        ((CompoundTag) i).getLong("InvalidateTime"),
                        NbtUtils.readBlockPos((CompoundTag) Objects.requireNonNull(((CompoundTag) i).get("AirdropPosition")))
                ));
            }
            if(nbt.contains("FixDropLocation"))
                imp.fixDropLocation = NbtUtils.readBlockPos((CompoundTag) Objects.requireNonNull(nbt.get("FixDropLocation")));
        }
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof Player) {
            AirdropPlayerDataProvider provider = new AirdropPlayerDataProvider();
            event.addCapability(new ResourceLocation(AirdropSupply.MOD_ID, "airdrop_supply_player_data"), provider);
        }
    }

    @SubscribeEvent
    public static void migrateCapabilityWhenPlayerRespawn(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        LazyOptional<AirdropPlayerData> oldData = event.getOriginal().getCapability(AirdropPlayerData.CAPABILITY);
        LazyOptional<AirdropPlayerData> newData = event.getOriginal().getCapability(AirdropPlayerData.CAPABILITY);
        newData.ifPresent((newCap) -> oldData.ifPresent((oldCap) -> {
            newCap.nextAirdropCountdown = oldCap.nextAirdropCountdown;
            newCap.airdropDespawnInfo.addAll(oldCap.airdropDespawnInfo);
            newCap.fixDropLocation = oldCap.fixDropLocation;
        }));
        event.getOriginal().invalidateCaps();
    }

}
