package de.keksuccino.fancymenu.menu.fancy.helper;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.io.Files;

import de.keksuccino.fancymenu.FancyMenu;
import de.keksuccino.fancymenu.menu.animation.AdvancedAnimation;
import de.keksuccino.fancymenu.menu.animation.AnimationHandler;
import de.keksuccino.fancymenu.menu.fancy.MenuCustomization;
import de.keksuccino.fancymenu.menu.fancy.guicreator.CustomGuiBase;
import de.keksuccino.fancymenu.menu.fancy.guicreator.CustomGuiLoader;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.LayoutEditorScreen;
import de.keksuccino.fancymenu.menu.fancy.helper.layoutcreator.PreloadedLayoutEditorScreen;
import de.keksuccino.fancymenu.menu.fancy.menuhandler.MenuHandlerRegistry;
import de.keksuccino.fancymenu.thread.MainThreadTaskExecutor;
import de.keksuccino.konkrete.properties.PropertiesSection;
import de.keksuccino.konkrete.properties.PropertiesSerializer;
import de.keksuccino.konkrete.properties.PropertiesSet;
import de.keksuccino.konkrete.rendering.animation.ExternalGifAnimationRenderer;
import de.keksuccino.konkrete.rendering.animation.IAnimationRenderer;
import de.keksuccino.konkrete.resources.ITextureResourceLocation;
import de.keksuccino.konkrete.resources.TextureHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomizationHelper {
	
	public static void init() {
		
		MinecraftForge.EVENT_BUS.register(new CustomizationHelper());

		CustomizationHelperUI.init();
		
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderPost(GuiScreenEvent.DrawScreenEvent.Post e) {

		if (!MenuCustomization.isBlacklistedMenu(e.getGui().getClass().getName())) {
			if (!e.getGui().getClass().getName().startsWith("de.keksuccino.spiffyhud.")) {
				if (!e.getGui().getClass().getName().startsWith("de.keksuccino.drippyloadingscreen.")) {

					CustomizationHelperUI.render(e.getGui());

				}
			}
		}

	}
	
	public static void updateUI() {
		CustomizationHelperUI.updateUI();
	}

	public static void reloadSystemAndMenu() {
		
		FancyMenu.updateConfig();

		clearKonkreteTextureCache();
		MenuCustomization.resetSounds();
		MenuCustomization.stopSounds();
		AnimationHandler.resetAnimations();
		AnimationHandler.resetAnimationSounds();
		AnimationHandler.stopAnimationSounds();
		MenuCustomization.reload();
		MenuHandlerRegistry.setActiveHandler(null);
		CustomGuiLoader.loadCustomGuis();
		if (!FancyMenu.config.getOrDefault("showcustomizationbuttons", true)) {
			CustomizationHelperUI.showButtonInfo = false;
			CustomizationHelperUI.showMenuInfo = false;
		}

		MinecraftForge.EVENT_BUS.post(new MenuReloadedEvent(Minecraft.getMinecraft().currentScreen));
		
		try {
			Minecraft.getMinecraft().displayGuiScreen(Minecraft.getMinecraft().currentScreen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void clearKonkreteTextureCache() {
		try {

			Field texturesField = TextureHandler.class.getDeclaredField("textures");
			texturesField.setAccessible(true);
			Map<String, ITextureResourceLocation> textures = (Map<String, ITextureResourceLocation>) texturesField.get(TextureHandler.class);
			textures.clear();

			Field gifsField = TextureHandler.class.getDeclaredField("gifs");
			gifsField.setAccessible(true);
			Map<String, ExternalGifAnimationRenderer> gifs = (Map<String, ExternalGifAnimationRenderer>) gifsField.get(TextureHandler.class);
			for (ExternalGifAnimationRenderer g : gifs.values()) {
				g.setLooped(false);
				g.resetAnimation();
			}
			gifs.clear();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openFile(File f) {
		try {
			String url = f.toURI().toURL().toString();
			String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
			URL u = new URL(url);
			if (!Minecraft.IS_RUNNING_ON_MAC) {
				if (s.contains("win")) {
					Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
				} else {
					if (u.getProtocol().equals("file")) {
						url = url.replace("file:", "file://");
					}
					Runtime.getRuntime().exec(new String[]{"xdg-open", url});
				}
			} else {
				Runtime.getRuntime().exec(new String[]{"open", url});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void editLayout(GuiScreen current, File layout) {
		
		try {
			
			if ((layout != null) && (current != null) && (layout.exists()) && (layout.isFile())) {
				
				List<PropertiesSet> l = new ArrayList<PropertiesSet>();
				PropertiesSet set = PropertiesSerializer.getProperties(layout.getPath());
				
				l.add(set);
				
				List<PropertiesSection> meta = set.getPropertiesOfType("customization-meta");
				if (meta.isEmpty()) {
					meta = set.getPropertiesOfType("type-meta");
				}
				
				if (!meta.isEmpty()) {
					
					meta.get(0).addEntry("path", layout.getPath());
					
					LayoutEditorScreen.isActive = true;
					Minecraft.getMinecraft().displayGuiScreen(new PreloadedLayoutEditorScreen(current, l));
					MenuCustomization.stopSounds();
					MenuCustomization.resetSounds();
					for (IAnimationRenderer r : AnimationHandler.getAnimations()) {
						if (r instanceof AdvancedAnimation) {
							((AdvancedAnimation)r).stopAudio();
							if (((AdvancedAnimation)r).replayIntro()) {
								((AdvancedAnimation)r).resetAnimation();
							}
						}
					}
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Will save the layout as layout file.
	 * 
	 * @param to Full file path with file name + extension.
	 */
	public static boolean saveLayoutTo(PropertiesSet layout, String to) {
		
		File f = new File(to);
		String s = Files.getFileExtension(to);
		if ((s != null) && !s.equals("")) {
			
			if (f.exists() && f.isFile()) {
				f.delete();
			}
			
			PropertiesSerializer.writeProperties(layout, f.getPath());
			
			return true;
			
		}
		
		return false;
		
	}
	
	/**
	 * Will save the layout as layout file.
	 * 
	 * @param to Full file path with file name + extension.
	 */
	public static boolean saveLayoutTo(List<PropertiesSection> layout, String to) {
		
		PropertiesSet props = new PropertiesSet("menu");
		for (PropertiesSection sec : layout) {
			props.addProperties(sec);
		}
		
		return saveLayoutTo(props, to);
		
	}
	
	public static boolean isScreenOverridden(GuiScreen current) {
		if ((current != null) && (current instanceof CustomGuiBase) && (((CustomGuiBase)current).getOverriddenScreen() != null)) {
			return true;
		}
		return false;
	}

	@Deprecated
	/** Use {@link MainThreadTaskExecutor#executeInMainThread(Runnable, MainThreadTaskExecutor.ExecuteTiming)} instead! **/
	public static void runTaskInMainThread(Runnable task) {
		MainThreadTaskExecutor.executeInMainThread(task, MainThreadTaskExecutor.ExecuteTiming.POST_CLIENT_TICK);
	}

}
