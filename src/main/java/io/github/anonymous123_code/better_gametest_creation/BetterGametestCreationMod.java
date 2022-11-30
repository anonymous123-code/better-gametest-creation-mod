package io.github.anonymous123_code.better_gametest_creation;

import net.minecraft.test.StructureTestUtil;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.config.QuiltConfig;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterGametestCreationMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Better Gametest Creation");

	public static MainConfig getMainConfig() {
		return MAIN_CONFIG;
	}

	private static MainConfig MAIN_CONFIG;

	@Override
	public void onInitialize(ModContainer mod) {
		MAIN_CONFIG = QuiltConfig.create("", mod.metadata().id(), MainConfig.class);

		if (MAIN_CONFIG.gametestCustomExportStructuresPath) {
			StructureTestUtil.testStructuresDirectoryName = MAIN_CONFIG.gametestExportStructuresPath;
		}
	}

	public static class MainConfig extends WrappedConfig {
		@Comment("When active, uses the Fabric API's gametest API default behavior as a fallback, while using the vanilla behavior with a custom directory.")
		public final boolean gametestApiSearchFix = true;
		@Comment("The custom directory to use when the gametestApiSearchFix is active")
		public final String gametestSearchStructuresPath = "../gameteststructures";

		@Comment("When active, the output directory of /test export is changed")
		public final boolean gametestCustomExportStructuresPath = true;
		@Comment("The output directory of /test export")
		public final String gametestExportStructuresPath = "../gameteststructures";
	}
}
