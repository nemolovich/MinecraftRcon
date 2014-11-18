package fr.nemolovich.apps.minecraftrcon.gui.colors;

public class MinecraftColors {
	private char code;
	private String name;
	private String foregroundColor;
	private String backgroundColor;
	
	public MinecraftColors(char code, String name, String foregroundColor,
			String backgroundColor) {
		super();
		this.code = code;
		this.name = name;
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
	}

    public char getCode() {
        return code;
    }

    public void setCode(char code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
        
        
}
