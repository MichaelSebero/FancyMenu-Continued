package de.keksuccino.core.gui.screens.popup;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.keksuccino.core.gui.content.AdvancedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.Screen;

public abstract class Popup {
	
	private boolean displayed = false;
	private int alpha;
	private List<AdvancedButton> buttons = new ArrayList<AdvancedButton>();
	
	public Popup(int backgroundAlpha) {
		this.alpha = backgroundAlpha;
	}
	
	public void render(MatrixStack matrix, int mouseX, int mouseY, Screen renderIn) {
		if (!this.isDisplayed()) {
			return;
		}
		RenderSystem.enableBlend();
		IngameGui.func_238467_a_(matrix, 0, 0, renderIn.field_230708_k_, renderIn.field_230709_l_, new Color(0, 0, 0, this.alpha).getRGB());
		RenderSystem.disableBlend();
	}
	
	public boolean isDisplayed() {
		return this.displayed;
	}
	
	public void setDisplayed(boolean b) {
		this.displayed = b;
	}
	
	public List<AdvancedButton> getButtons() {
		return this.buttons;
	}
	
	protected void addButton(AdvancedButton b) {
		if (!this.buttons.contains(b)) {
			this.buttons.add(b);
			this.colorizePopupButton(b);
		}
	}
	
	protected void removeButton(AdvancedButton b) {
		if (this.buttons.contains(b)) {
			this.buttons.remove(b);
		}
	}
	
	protected void renderButtons(MatrixStack matrix, int mouseX, int mouseY) {
		for (AdvancedButton b : this.buttons) {
			b.render(matrix, mouseX, mouseY, Minecraft.getInstance().getRenderPartialTicks());
		}
	}
	
	protected void colorizePopupButton(AdvancedButton b) {
		b.setBackgroundColor(new Color(102, 102, 153), new Color(133, 133, 173), new Color(163, 163, 194), new Color(163, 163, 194), 1);
	}

}