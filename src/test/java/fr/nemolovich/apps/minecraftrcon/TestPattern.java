/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Nemolovich
 */
public class TestPattern {

    private static final Pattern SERVER_BASIC_COMMAND_PATTERN = Pattern
        .compile("\n(?<cmd>/\\w+(-\\w+)*):\\s");
    private static final Pattern SERVER_COMMAND_PAGES_PATTERN = Pattern
        .compile("^.*-{4,}\\s.+:\\s\\(\\d+/(?<nbPages>\\d+)\\)\\s-{4,}.*");
    private static final Pattern SERVER_CUSTOM_COMMAND_PATTERN = Pattern
        .compile("\n(?<cmd>\\w+(-\\w+)*):\\s");
    private static final String HELP_MSG_1 = "§e--------- §f><>IL: !?8A>: (1/6) §e------------------\n"
        + "§7A?>;L795B5 /help [=><5@ AB@0=8FK] GB>1K ?5@5<5I0BLAO?>\n"
        + "§7AB@0=8F0<.\n"
        + "§6Aliases: §f!?8A>: :><0=4-75@:0;\n"
        + "§6Bukkit: §fA5 :><0=4K Bukkit\n"
        + "§6ClearLag: §fA5 :><0=4K ClearLag\n"
        + "§6NoSpawnChunks: §fA5 :><0=4K NoSpawnChunks\n"
        + "§6/ban: §f0?@5I05B 8A?>;L7>20=85 A5@25@0 >?@545;5==K< 83@>:><\n"
        + "§6/ban-ip: §f0?@5I05B 8A?>;L7>20=85 A5@25@0 83@>:0< A >?@55;5==>3> IP 04@5A0.\n"
        + "§6/banlist: §f@>A<>B@ 701;>:8@>20==KE 83@>:>2 8 IP 04@5A>2\n";
    private static final String HELP_MSG_2 = "§e--------- §f><>IL: !?8A>: (2/6) §e------------------\n"
        + "§6/clear: §fG8I05B 8=25=B0@L 83@>:0. >6=> C:070BL D8;LB@ ?> ?@54<5BC 8 53> 40==K<.\n"
        + "§6/defaultgamemode: §fKAB02;O5B 83@>2>9 @568< ?> C<>;G0=8N\n"
        + "§6/deop: §fB7K205B C C:070==>3> 83@>:0 AB0BCA >?5@0B>@0\n"
        + "§6/difficulty: §fKAB02;O5B A;>6=>ABL 83@K\n"
        + "§6/enchant: §f>102;O5B 70G0@>20=85 =0 ?@54<5B :>B>@K9 A59G0A 45@68B 83@>:. #:068B5 7=0G5=85 0 5A;8\n"
        + "§fE>B8B5 C1@0BL 70G0@>20=85. A?>;L7C9B5 force GB>1K\n"
        + "§f83=>@8@>20BL ?@028;0 70G0@>20=89.\n"
        + "§6/gamemode: §f7<5=O5B 83@>2>9 @568< C:070==>3> 83@>:0\n"
        + "§6/gamerule: §fKAB02;O5B ?@028;0 A5@25@0\n";
    private static final String HELP_MSG_3 = "§e--------- §f><>IL: !?8A>: (3/6) §e------------------\n"
        + "§6/give: §fK405B 83@>:C 7040==>5 :>;8G5AB2> ?@54<5B>2\n"
        + "§6/help: §f>:07K205< <5=N ?><>I8\n"
        + "§6/kick: §fK1@0AK205B 83@>:0 A A5@25@0\n"
        + "§6/kill: §fkills the server cold....in its tracks!\n"
        + "§6/lagg: §fClearlag base command!\n"
        + "§6/list: §f>:07K205B A?8A>: 83@>:>2 >=;09=\n"
        + "§6/me: §fK?>;=O5B C:070==>5 459AB28O 2 G0B5\n"
        + "§6/op: §f@54>AB02;O5B 7040==><C 83@>:C AB0BCA >?5@0B>@0\n"
        + "§6/pardon: §f>72>;O5B C:070==><C 83@>:C 70E>48BL =0 A5@25@\n";
    private static final String HELP_MSG_4 = "§e--------- §f><>IL: !?8A>: (4/6) §e------------------\n"
        + "§6/pardon-ip: §f 07@5H05B 83@>:0< 70E>48BL 2 83@C A C:070==>3> IP 04@5A0.\n"
        + "§6/plugins: §f>:07K205B A?8A>: ?;038=>2 A5@25@0\n"
        + "§6/reload: §fReloads the server and every plugin!\n"
        + "§6/restart: §f5@5703@C605B A5@25@\n"
        + "§6/save-all: §f!>E@0=O5B <8@ =0 48A:.\n"
        + "§6/save-off: §fB:;NG05B 02B><0B8G5A:>5 A>E@0=5=85 <8@0 =0 48A:\n"
        + "§6/save-on: §f:;NG05B 02B><0B8G5A:>5 A>E@0=5=85 <8@0 =0 48A:\n"
        + "§6/say: §f8H5B A>>1I5=85 2 G0B >B 8<5=8 :>=A>;8\n"
        + "§6/seed: §f>:07K205B A84 <8@0\n";
    private static final String HELP_MSG_5 = "§e--------- §f><>IL: !?8A>: (5/6) §e------------------\n"
        + "§6/spawnpoint: §f#:07K205B B>G:C A?02=0 83@>:>2\n"
        + "§6/stop: §fStops the server cold....in its tracks!\n"
        + "§6/tell: §fBAK;05B ;8G=>5 A>>1I5=85 C:070==><C 83@>:C.\n"
        + "§6/time: §f#:07K205B 2@5<O 4;O 2A5E <8@>2\n"
        + "§6/timings: §f0?8AK205B B09<8=38 2A5E A>1KB89 ?;038=>2\n"
        + "§6/toggledownfall: §f:;NG05B 8;8 2K:;NG05B 4>64L.\n"
        + "§6/tp: §f\"5;5?>@B8@C5B 83@>:0 : 4@C3><C 83@>:C 8;8 =0 7040=CN B>G:C.\n"
        + "§6/tps: §fGets the current ticks per second for the server\n"
        + "§6/version: §f>:07K205B 25@A8N A5@25@0 8;8 C:070==>3> ?;038=0.\n";
    private static final String HELP_MSG_6 = "§e--------- §f><>IL: !?8A>: (6/6) §e------------------\n"
        + "§6/weather: §f5=O5B ?>3>4C\n"
        + "§6/whitelist: §f#?@02;O5B 15;K< A?8A:>< A5@25@0\n"
        + "§6/wstats: §fshow stats\n"
        + "§6/xp: §fK405B C:070==><C 83@>:C C:070=>5 :>;8G5AB2> >?KB0. #:07K209B5 <:>;8G5AB2>>L 4;O 2K40G8 C@>2=59. A;8 7=0G5=85 >B@8G0B5;L=>5, B> >?KB A=8<05BAO.\n";
    private static final String ALIASES_MSG = "§e--------- §f><>IL: Aliases §e-----------------------\n"
        + "§6/about: §f§eAlias for §f/version\n"
        + "§6/pl: §f§eAlias for §f/plugins\n"
        + "§6/rl: §f§eAlias for §f/reload\n"
        + "§6/ver: §f§eAlias for §f/version\n";
    private static final String BUKKIT_MSG_1 = "§e--------- §f><>IL: Bukkit (1/5) §e------------------\n"
        + "§7865 C:070=K 2A5 :><0=4K Bukkit:\n"
        + "§6/ban: §f0?@5I05B 8A?>;L7>20=85 A5@25@0 >?@545;5==K<83@>:><\n"
        + "§f\n"
        + "§6/ban-ip: §f0?@5I05B 8A?>;L7>20=85 A5@25@0 83@>:0< A\n"
        + "§f>?@55;5==>3> IP 04@5A0.\n"
        + "§6/banlist: §f@>A<>B@ 701;>:8@>20==KE 83@>:>2 8 IP 04@5A>2\n"
        + "§6/clear: §fG8I05B 8=25=B0@L 83@>:0. >6=> C:070BL D8;LB@ ?>\n"
        + "§f?@54<5BC 8 53> 40==K<.\n"
        + "§6/defaultgamemode: §fKAB02;O5B 83@>2>9 @568< ?> C<>;G0=8N\n";

    @Test
    public void test1() {
        assertTrue(parseServerCommands(HELP_MSG_1).size() > 0);
    }

    private static String parseColorString(String msg) {
        return msg.replaceAll("§(\\d|[a-f])", "");
    }

    private List<String> parseServerCommands(String msg) {
        String content = parseColorString(msg);
        List<String> commands = getCustomCommands(content);
        commands.addAll(getBasicCommands(content, true));
        return commands;
    }

    private List<String> getBasicCommands(String msg) {
        return this.getBasicCommands(msg, false);
    }

    private List<String> getBasicCommands(String msg, boolean recursiveSearch) {
        List<String> commands = new ArrayList();
        Matcher matcher = SERVER_BASIC_COMMAND_PATTERN.matcher(msg);

        while (matcher.find()) {
            commands.add(matcher.group("cmd"));
        }

        if (recursiveSearch) {
            Matcher multiPage = SERVER_COMMAND_PAGES_PATTERN.matcher(
                msg.replaceAll("\\n", "\\\\n"));
            if (multiPage.matches()) {
                int nbPages = Integer.valueOf(multiPage.group("nbPages"));
                if (nbPages > 1) {
                    for (int i = 2; i <= nbPages; i++) {
                        commands.addAll(
                            getBasicCommands(parseColorString(getNextHelp(i))));
                    }
                }
            }
        }
        return commands;
    }

    private List<String> getCustomCommands(String msg) {
        List<String> commands = new ArrayList();
        String content = parseColorString(msg);
        Matcher matcher = SERVER_CUSTOM_COMMAND_PATTERN.matcher(content);

        while (matcher.find()) {
            commands.add(matcher.group("cmd"));
        }
        return commands;
    }

    private String getNextHelp(int index) {
        String result = null;
        if (index == 2) {
            result = HELP_MSG_2;
        } else if (index == 3) {
            result = HELP_MSG_3;
        } else if (index == 4) {
            result = HELP_MSG_4;
        } else if (index == 5) {
            result = HELP_MSG_5;
        } else if (index == 6) {
            result = HELP_MSG_6;
        }
        return result;
    }
}
