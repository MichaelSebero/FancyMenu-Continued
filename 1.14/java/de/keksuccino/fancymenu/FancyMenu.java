package de.keksuccino.fancymenu;

import java.io.File;

import de.keksuccino.core.config.Config;
import de.keksuccino.core.config.exceptions.InvalidValueException;
import de.keksuccino.core.filechooser.FileChooser;
import de.keksuccino.core.gui.screens.popup.PopupHandler;
import de.keksuccino.core.input.KeyboardHandler;
import de.keksuccino.core.sound.SoundHandler;
import de.keksuccino.fancymenu.keybinding.Keybinding;
import de.keksuccino.fancymenu.localization.Locals;
import de.keksuccino.fancymenu.menu.animation.AnimationHandler;
import de.keksuccino.fancymenu.menu.fancy.MenuCustomization;
import de.keksuccino.fancymenu.menu.fancy.gameintro.GameIntroHandler;
import de.keksuccino.fancymenu.menu.systemtray.FancyMenuTray;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("fancymenu")
public class FancyMenu {
	
	public static final String VERSION = "1.2.1";
	private static boolean isNotHeadless = false;
	
	public static Config config;
	
	private static File animationsPath = new File("config/fancymenu/animations");
	private static File customizationPath = new File("config/fancymenu/customization");
	
	public FancyMenu() {
		//Check if FancyMenu was loaded client- or serverside
    	if (FMLEnvironment.dist == Dist.CLIENT) {
    		
    		//Create all important directorys
    		animationsPath.mkdirs();
    		customizationPath.mkdirs();

    		updateConfig();

    		AnimationHandler.init();
    		AnimationHandler.loadCustomAnimations();
    		
    		GameIntroHandler.init();
    		
        	MenuCustomization.init();
        	
        	PopupHandler.init();
        	
        	KeyboardHandler.init();
        	
        	SoundHandler.init();
        	
        	if (config.getOrDefault("enablehotkeys", true)) {
        		Keybinding.init();
        	}
        	
        	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        	
        	if (config.getOrDefault("enablesystemtray", true) && !Minecraft.IS_RUNNING_ON_MAC) {
        		isNotHeadless = FancyMenuTray.init();
        	}
        	
        	if (isNotHeadless) {
        		FileChooser.init();
        	}
        	
    	} else {
    		System.out.println("## WARNING ## 'FancyMenu' is a client mod and has no effect when loaded on a server!");
    	}
	}
	
	private void onClientSetup(FMLClientSetupEvent e) {
		Locals.init();
	}
	
	public static void updateConfig() {
    	try {
    		config = new Config("config/fancymenu/config.txt");
    		
    		if (!Minecraft.IS_RUNNING_ON_MAC) {
    			config.registerValue("enablesystemtray", true, "general", "A minecraft restart is required after changing this value.");
    		}
    		
    		config.registerValue("enablehotkeys", true, "general", "A minecraft restart is required after changing this value.");
    		
    		config.registerValue("showcustomizationbuttons", true, "customization");
    		
			config.registerValue("hidebranding", true, "mainmenu");
			config.registerValue("hidelogo", false, "mainmenu");
			config.registerValue("showmainmenufooter", false, "mainmenu");
			config.registerValue("hiderealmsnotifications", false, "mainmenu");

			config.registerValue("hidesplashtext", false, "mainmenu_splash");
			config.registerValue("splashoffsetx", 0, "mainmenu_splash");
			config.registerValue("splashoffsety", 0, "mainmenu_splash");
			config.registerValue("splashrotation", -20, "mainmenu_splash");
			
			config.registerValue("gameintroanimation", "", "loading");
			config.registerValue("loadingscreendarkmode", false, "loading");
			
			config.syncConfig();
			
			//Updating all categorys at start to keep them synchronized with older config files
			config.setCategory("enablesystemtray", "general");
			
			if (!Minecraft.IS_RUNNING_ON_MAC) {
				config.setCategory("enablehotkeys", "general");
			}
    		
			config.setCategory("showcustomizationbuttons", "customization");
			
			config.setCategory("hidebranding", "mainmenu");
			config.setCategory("hidelogo", "mainmenu");
			config.setCategory("showmainmenufooter", "mainmenu");
			config.setCategory("hiderealmsnotifications", "mainmenu");
			
			config.setCategory("hidesplashtext", "mainmenu_splash");
			config.setCategory("splashoffsetx", "mainmenu_splash");
			config.setCategory("splashoffsety", "mainmenu_splash");
			config.setCategory("splashrotation", "mainmenu_splash");
			
			config.setCategory("gameintroanimation", "loading");
			config.setCategory("loadingscreendarkmode", "loading");
			
			config.clearUnusedValues();
		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
	}
	
	public static File getAnimationPath() {
		if (!animationsPath.exists()) {
			animationsPath.mkdirs();
		}
		return animationsPath;
	}
	
	public static File getCustomizationPath() {
		if (!customizationPath.exists()) {
			customizationPath.mkdirs();
		}
		return customizationPath;
	}
	
	public static boolean isNotHeadless() {
		return isNotHeadless;
	}

}
