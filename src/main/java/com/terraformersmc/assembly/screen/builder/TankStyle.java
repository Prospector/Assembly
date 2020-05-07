package com.terraformersmc.assembly.screen.builder;

public enum TankStyle {
	THREE(150, 18, 22, 56, 4, 4, 14, 48, 172, 18),
	TWO(150, 74, 22, 40, 4, 4, 14, 32, 172, 74),
	ONE(150, 114, 22, 40, 4, 4, 14, 32, 172, 114);

    public int x;
    public int y;
    public int width;
    public int height;
    public int fluidXOffset;
    public int fluidYOffset;
    public int fluidWidth;
    public int fluidHeight;
    public int overlayX;
    public int overlayY;

    TankStyle(int x, int y, int width, int height, int fluidXOffset, int fluidYOffset, int fluidWidth, int fluidHeight, int overlayX, int overlayY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fluidXOffset = fluidXOffset;
        this.fluidYOffset = fluidYOffset;
        this.fluidWidth = fluidWidth;
        this.fluidHeight = fluidHeight;
        this.overlayX = overlayX;
        this.overlayY = overlayY;
    }
}
