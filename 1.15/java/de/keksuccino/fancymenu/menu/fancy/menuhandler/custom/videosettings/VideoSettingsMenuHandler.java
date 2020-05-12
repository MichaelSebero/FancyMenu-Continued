package de.keksuccino.fancymenu.menu.fancy.menuhandler.custom.videosettings;

import java.lang.reflect.Field;
import java.util.List;

import de.keksuccino.fancymenu.menu.button.ButtonCachedEvent;
import de.keksuccino.fancymenu.menu.fancy.menuhandler.MenuHandlerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.FullscreenResolutionOption;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class VideoSettingsMenuHandler extends MenuHandlerBase {

	private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.GRAPHICS, AbstractOption.RENDER_DISTANCE, AbstractOption.AO, AbstractOption.FRAMERATE_LIMIT, AbstractOption.VSYNC, AbstractOption.VIEW_BOBBING, AbstractOption.GUI_SCALE, AbstractOption.ATTACK_INDICATOR, AbstractOption.GAMMA, AbstractOption.RENDER_CLOUDS, AbstractOption.FULLSCREEN, AbstractOption.PARTICLES, AbstractOption.MIPMAP_LEVELS, AbstractOption.ENTITY_SHADOWS};
	
	public VideoSettingsMenuHandler() {
		super(VideoSettingsScreen.class.getName());
	}
	
	@Override
	public void onInitPost(ButtonCachedEvent e) {
		if (this.shouldCustomize(e.getGui())) {
			try {
				VideoSettingsList l = new VideoSettingsList(Minecraft.getInstance(), e.getGui().width, e.getGui().height, 32, e.getGui().height - 32, 25, this);
				l.func_214333_a(new FullscreenResolutionOption(Minecraft.getInstance().func_228018_at_()));
			    l.func_214333_a(AbstractOption.BIOME_BLEND_RADIUS);
			    l.func_214335_a(OPTIONS);
				Field f = ObfuscationReflectionHelper.findField(VideoSettingsScreen.class, "field_146501_h");
				e.getGui().children().remove(f.get(e.getGui()));
				f.set(e.getGui(), l);
				addChildren(e.getGui(), l);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		super.onInitPost(e);
	}
	
	private static void addChildren(Screen s, IGuiEventListener e) {
		try {
			Field f = ObfuscationReflectionHelper.findField(Screen.class, "children");
			((List<IGuiEventListener>)f.get(s)).add(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}