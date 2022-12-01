package io.github.anonymous123_code.better_gametest_creation.mixin;

import io.github.anonymous123_code.better_gametest_creation.BetterGametestCreationMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;



@Mixin(value = StructureTestUtil.class, priority = 999)
public abstract class StructureTestUtilMixin {
	@Shadow
	private static NbtCompound loadSnbt(Path path) {throw new AssertionError();}

	@Inject(at=@At(value = "HEAD"), cancellable = true, method = "createStructure(Ljava/lang/String;Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/structure/Structure;")
	private static void betterGametestCreation$createStructure(String id, ServerWorld world, CallbackInfoReturnable<Structure> ci) {
		if (!BetterGametestCreationMod.getMainConfig().gametestApiSearchFix) return;

		Optional<Structure> optional = world.getStructureTemplateManager().getStructure(new Identifier(id));
		if (optional.isPresent()) {
			ci.setReturnValue(optional.get());
		} else {
			Identifier structureId = new Identifier(id);
			String fileName = structureId.getPath() + ".snbt";
			Path path;
			if (BetterGametestCreationMod.getMainConfig().stripModIDfromSearchStructurePath) {
				path = Paths.get(BetterGametestCreationMod.getMainConfig().gametestSearchStructuresPath, fileName);
			} else {
				path = Paths.get(BetterGametestCreationMod.getMainConfig().gametestSearchStructuresPath, structureId.getNamespace(), fileName);
			}
			NbtCompound result = loadSnbt(path);
			if (result == null) {
				BetterGametestCreationMod.LOGGER.warn("failed to load structure for gametest" + id + ", trying fabric gametest API");
			} else {
				ci.setReturnValue(world.getStructureTemplateManager().createStructureFromNbt(result));
			}
		}
	}
}
