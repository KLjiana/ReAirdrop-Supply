package net.mcbbs.uid1525632.airdropsupply.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.mcbbs.uid1525632.airdropsupply.AirdropSupply;
import net.mcbbs.uid1525632.airdropsupply.block.AirdropSupplyBlock;
import net.mcbbs.uid1525632.airdropsupply.entry.ModBlocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.server.command.EnumArgument;

public class CallingAirdropCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TextComponent("Unable to create airdrop in this location."));

    private static final EnumArgument<AirdropSupplyBlock.CaseLevel> CASE_LEVEL = EnumArgument.enumArgument(AirdropSupplyBlock.CaseLevel.class);
    private static final EnumArgument<AirdropSupplyBlock.Type> CASE_TYPE = EnumArgument.enumArgument(AirdropSupplyBlock.Type.class);
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("airdrop").requires((sourceStack) -> {
            return sourceStack.hasPermission(2);
        }).then(Commands.argument("pos", BlockPosArgument.blockPos())
                .then(Commands.argument("level", CASE_LEVEL)
                        .then(Commands.argument("type", CASE_TYPE)
                        .executes((sourceStack2) -> {
                            if(sourceStack2.getSource().getLevel().dimension().equals(Level.OVERWORLD)){
                                var level = sourceStack2.getSource().getLevel();
                                var dropPos = BlockPosArgument.getLoadedBlockPos(sourceStack2, "pos").atY(300);
                                var caseLevel = sourceStack2.getArgument("level",AirdropSupplyBlock.CaseLevel.class);
                                var caseType = sourceStack2.getArgument("type",AirdropSupplyBlock.Type.class);
                                var fallingCrate =  FallingBlockEntity.fall(level,dropPos,ModBlocks.AIRDROP_SUPPLY.get().defaultBlockState()
                                                .setValue(AirdropSupplyBlock.TYPE, caseType)
                                                .setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()))
                                                .setValue(AirdropSupplyBlock.LEVEL, caseLevel));
                                fallingCrate.blockData = new CompoundTag();
                                fallingCrate.blockData.putString("LootTable", AirdropSupply.LootTables.calculateLootTable(caseType,caseLevel).toString());
                                fallingCrate.blockData.putLong("LootTableSeed",level.random.nextLong());
                                return 1;
                            } else {
                                throw ERROR_FAILED.create();
                            }
        })))));
    }
}
