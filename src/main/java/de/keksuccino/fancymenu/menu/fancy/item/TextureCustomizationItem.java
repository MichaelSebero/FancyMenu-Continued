package de.keksuccino.fancymenu.menu.fancy.item;

import java.io.File;
import java.io.IOException;

import de.keksuccino.konkrete.properties.PropertiesSection;
import de.keksuccino.konkrete.rendering.animation.ExternalGifAnimationRenderer;
import de.keksuccino.konkrete.resources.ExternalTextureResourceLocation;
import de.keksuccino.konkrete.resources.TextureHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class TextureCustomizationItem extends CustomizationItemBase {
	
	public ExternalTextureResourceLocation texture;
	public ExternalGifAnimationRenderer gif;
	
	public TextureCustomizationItem(PropertiesSection item) {
		super(item);
		
		if ((this.action != null) && this.action.equalsIgnoreCase("addtexture")) {
			this.value = fixBackslashPath(item.getEntryValue("path"));
			if (this.value != null) {
				this.value = this.value.replace("\\", "/");
				
				File f = new File(this.value);
				String finalValue = this.value;
				if (!f.exists() || !f.getAbsolutePath().replace("\\", "/").startsWith(Minecraft.getMinecraft().mcDataDir.getAbsolutePath().replace("\\", "/"))) {
					finalValue = Minecraft.getMinecraft().mcDataDir.getAbsolutePath().replace("\\", "/") + "/" + this.value;
					f = new File(finalValue);
				}
				if (f.exists() && f.isFile() && (f.getName().endsWith(".png") || f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg") || f.getName().endsWith(".gif"))) {
					try {
						int w = 0;
					    int h = 0;
					    double ratio;

						if (f.getName().endsWith(".gif")) {
							this.gif = TextureHandler.getGifResource(finalValue);
							if (this.gif != null) {
								w = this.gif.getWidth();
								h = this.gif.getHeight();
							}
						} else {
							this.texture = TextureHandler.getResource(finalValue);
							if (this.texture != null) {
								w = this.texture.getWidth();
								h = this.texture.getHeight();
							}
						}
						
						ratio = (double) w / (double) h;

						//Calculate missing width
						if ((this.getWidth() < 0) && (this.getHeight() >= 0)) {
							this.setWidth((int)(this.getHeight() * ratio));
						}
						//Calculate missing height
						if ((this.getHeight() < 0) && (this.getWidth() >= 0)) {
							this.setHeight((int)(this.getWidth() / ratio));
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void render(GuiScreen menu) throws IOException {
		
		if (this.shouldRender()) {
			
			int x = this.getPosX(menu);
			int y = this.getPosY(menu);
			
			if (this.gif != null) {
				int w = this.gif.getWidth();
				int h = this.gif.getHeight();
				int x2 = this.gif.getPosX();
				int y2 = this.gif.getPosY();
				
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, this.opacity);
				
				this.gif.setPosX(x);
				this.gif.setPosY(y);
				this.gif.setWidth(this.getWidth());
				this.gif.setHeight(this.getHeight());

				this.gif.setOpacity(this.opacity);

				this.gif.render();
				
				this.gif.setPosX(x2);
				this.gif.setPosY(y2);
				this.gif.setWidth(w);
				this.gif.setHeight(h);
				
				GlStateManager.disableBlend();
				
			} else if (this.texture != null) {
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture.getResourceLocation());
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, this.opacity);
				GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
				GlStateManager.disableBlend();
				
			}
			
		}

	}
	
	@Override
	public boolean shouldRender() {
		if ((this.texture == null) && (this.gif == null)) {
			return false;
		}
		if ((this.getWidth() < 0) || (this.getHeight() < 0)) {
			return false;
		}
		return super.shouldRender();
	}

}
