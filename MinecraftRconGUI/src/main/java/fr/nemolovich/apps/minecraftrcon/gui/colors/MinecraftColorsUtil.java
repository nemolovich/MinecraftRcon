package fr.nemolovich.apps.minecraftrcon.gui.colors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinecraftColorsUtil {

    private static final List<MinecraftColors> COLORS;

    static {
        COLORS = Collections.synchronizedList(new ArrayList());
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.BLACK_COLOR_CODE,
            MinecraftColorsConstants.BLACK_COLOR_NAME,
            MinecraftColorsConstants.BLACK_COLOR_FG,
            MinecraftColorsConstants.BLACK_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.DARK_BLUE_COLOR_CODE,
            MinecraftColorsConstants.DARK_BLUE_COLOR_NAME,
            MinecraftColorsConstants.DARK_BLUE_COLOR_FG,
            MinecraftColorsConstants.DARK_BLUE_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.DARK_GREEN_COLOR_CODE,
            MinecraftColorsConstants.DARK_GREEN_COLOR_NAME,
            MinecraftColorsConstants.DARK_GREEN_COLOR_FG,
            MinecraftColorsConstants.DARK_GREEN_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.DARK_AQUA_COLOR_CODE,
            MinecraftColorsConstants.DARK_AQUA_COLOR_NAME,
            MinecraftColorsConstants.DARK_AQUA_COLOR_FG,
            MinecraftColorsConstants.DARK_AQUA_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.DARK_RED_COLOR_CODE,
            MinecraftColorsConstants.DARK_RED_COLOR_NAME,
            MinecraftColorsConstants.DARK_RED_COLOR_FG,
            MinecraftColorsConstants.DARK_RED_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.DARK_PURPLE_COLOR_CODE,
            MinecraftColorsConstants.DARK_PURPLE_COLOR_NAME,
            MinecraftColorsConstants.DARK_PURPLE_COLOR_FG,
            MinecraftColorsConstants.DARK_PURPLE_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.GOLD_COLOR_CODE,
            MinecraftColorsConstants.GOLD_COLOR_NAME,
            MinecraftColorsConstants.GOLD_COLOR_FG,
            MinecraftColorsConstants.GOLD_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.GRAY_COLOR_CODE,
            MinecraftColorsConstants.GRAY_COLOR_NAME,
            MinecraftColorsConstants.GRAY_COLOR_FG,
            MinecraftColorsConstants.GRAY_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.DARK_GREY_COLOR_CODE,
            MinecraftColorsConstants.DARK_GREY_COLOR_NAME,
            MinecraftColorsConstants.DARK_GREY_COLOR_FG,
            MinecraftColorsConstants.DARK_GREY_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.BLUE_COLOR_CODE,
            MinecraftColorsConstants.BLUE_COLOR_NAME,
            MinecraftColorsConstants.BLUE_COLOR_FG,
            MinecraftColorsConstants.BLUE_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.GREEN_COLOR_CODE,
            MinecraftColorsConstants.GREEN_COLOR_NAME,
            MinecraftColorsConstants.GREEN_COLOR_FG,
            MinecraftColorsConstants.GREEN_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.AQUA_COLOR_CODE,
            MinecraftColorsConstants.AQUA_COLOR_NAME,
            MinecraftColorsConstants.AQUA_COLOR_FG,
            MinecraftColorsConstants.AQUA_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.RED_COLOR_CODE,
            MinecraftColorsConstants.RED_COLOR_NAME,
            MinecraftColorsConstants.RED_COLOR_FG,
            MinecraftColorsConstants.RED_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.LIGHT_PURPLE_COLOR_CODE,
            MinecraftColorsConstants.LIGHT_PURPLE_COLOR_NAME,
            MinecraftColorsConstants.LIGHT_PURPLE_COLOR_FG,
            MinecraftColorsConstants.LIGHT_PURPLE_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.YELLOW_COLOR_CODE,
            MinecraftColorsConstants.YELLOW_COLOR_NAME,
            MinecraftColorsConstants.YELLOW_COLOR_FG,
            MinecraftColorsConstants.YELLOW_COLOR_BG));
        COLORS.add(new MinecraftColors(
            MinecraftColorsConstants.WHITE_COLOR_CODE,
            MinecraftColorsConstants.WHITE_COLOR_NAME,
            MinecraftColorsConstants.WHITE_COLOR_FG,
            MinecraftColorsConstants.WHITE_COLOR_BG));
    }

    public static final MinecraftColors getColorFromCode(char code) {
        MinecraftColors result = null;

        for (MinecraftColors colors : COLORS) {
            if (colors.getCode() == code) {
                result = colors;
                break;
            }
        }

        return result;
    }

    public static final List<MinecraftColors> getColors() {
        return COLORS;
    }
}
