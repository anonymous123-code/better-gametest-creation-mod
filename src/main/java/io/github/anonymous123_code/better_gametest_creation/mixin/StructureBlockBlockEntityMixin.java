package io.github.anonymous123_code.better_gametest_creation.mixin;

import io.github.anonymous123_code.better_gametest_creation.BetterGametestCreationMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.text.Text;
import net.minecraft.unmapped.C_temnquoh;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Mixin(StructureBlockBlockEntity.class)
public abstract class StructureBlockBlockEntityMixin extends BlockEntity {

	@Shadow
	private Identifier structureName;

	private StructureBlockBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {super(type, pos, state);}


	@Inject(at=@At(value = "RETURN", ordinal = 2), method = "saveStructure(Z)Z")
	public void saveStructure(boolean bl, CallbackInfoReturnable<Boolean> c) {
		if (!BetterGametestCreationMod.<Boolean>getMainConfigValue("structureBlockSaveBehaviorTweak")) return;
		if (this.structureName.getNamespace().equals(BetterGametestCreationMod.<String>getMainConfigValue("gametestStructureBlockTweakNamespace"))) {
			if (world == null || world.getServer() == null) {
				return;
			}
			PlayerEntity targetPlayer = world.getServer().getPlayerManager().getPlayerList().stream().min((player1, player2) -> (int) (player1.squaredDistanceTo(Vec3d.ofCenter(pos)) - player2.squaredDistanceTo(Vec3d.ofCenter(pos)))).orElse(null);

			export(targetPlayer != null ? targetPlayer.getCommandSource() : world.getServer().getCommandSource(), this.structureName);
		}
	}

	@Unique
	private void export(ServerCommandSource source, Identifier structure) {
		Path outputPath = Paths.get(StructureTestUtil.testStructuresDirectoryName);
		Path inputPath = source.getWorld().getStructureTemplateManager().getStructurePath(structure, ".nbt");
		Path resultFilePath = NbtProvider.convertNbtToSnbt(C_temnquoh.f_wntcbvuf, inputPath, structure.getPath(), outputPath);
		if (resultFilePath == null) {
			source.sendFeedback(Text.literal("Failed to export " + inputPath), false);
		} else {
			try {
				Files.createDirectories(resultFilePath.getParent());
			} catch (IOException e) {
				source.sendFeedback(Text.literal("Could not create folder " + resultFilePath.getParent()), false);
				e.printStackTrace();
				return;
			}

			source.sendFeedback(Text.literal("Exported " + structure + " to " + resultFilePath.toAbsolutePath()), false);
		}
	}
}
