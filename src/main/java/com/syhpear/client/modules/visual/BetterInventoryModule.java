package com.syhpear.client.modules.visual;

import com.syhpear.client.SyhpearClient;
import com.syhpear.client.event.EventBus;
import com.syhpear.client.module.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class BetterInventoryModule extends Module {

    private boolean blurBackground   = true;
    private boolean roundedBorders   = true;
    private boolean itemTooltipEnhanced = true;
    private boolean searchBar        = true;
    private int     bgColor          = 0xCC1A1A2E;  // Dark navy
    private int     borderColor      = 0xFF00BFFF;  // Neon blue

    public BetterInventoryModule() {
        super("Better Inventory", "Modernizes the inventory screen with blur, rounded borders, and search.", Category.VISUAL);
    }

    // Applied via mixin on HandledScreen#render and HandledScreen#drawBackground
    // blurBackground → renders blur layer behind inventory
    // roundedBorders → overrides border rendering with rounded rect shader
    // searchBar → injects search TextField into creative/chest screens

    public void setBlur(boolean b)     { this.blurBackground = b; }
    public void setRounded(boolean r)  { this.roundedBorders = r; }
    public void setSearchBar(boolean s){ this.searchBar = s; }
    public void setBgColor(int c)      { this.bgColor = c; }
    public void setBorderColor(int c)  { this.borderColor = c; }
}
