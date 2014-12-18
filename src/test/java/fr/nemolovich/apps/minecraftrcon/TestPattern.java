/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        .compile("-{7,}\\s.+:\\s.+\\s\\(\\d+/(?<nbPages>\\d+)\\)\\s-{7,}.*");
    private static final Pattern SERVER_CUSTOM_COMMAND_PATTERN = Pattern
        .compile("\n(?<cmd>\\w+(-\\w+)*):\\s");

    private static final String HELP_MSG_1 = "\u00A7e--------- \u00A7fHelp: Index (1/6) \u00A7e---------------------\n"
        + "\u00A77A?>;L795B5 /help [=><5@ AB@0=8FK] GB>1K ?5@5<5I0BLAO?>\n"
        + "\u00A77AB@0=8F0<.\n"
        + "\u00A76Aliases: \u00A7f!?8A>: :><0=4-75@:0;\n"
        + "\u00A76Bukkit: \u00A7fA5 :><0=4K Bukkit\n"
        + "\u00A76ClearLag: \u00A7fA5 :><0=4K ClearLag\n"
        + "\u00A76NoSpawnChunks: \u00A7fA5 :><0=4K NoSpawnChunks\n"
        + "\u00A76/ban: \u00A7f0?@5I05B 8A?>;L7>20=85 A5@25@0 >?@545;5==K< 83@>:><\n"
        + "\u00A76/ban-ip: \u00A7f0?@5I05B 8A?>;L7>20=85 A5@25@0 83@>:0< A >?@55;5==>3> IP 04@5A0.\n"
        + "\u00A76/banlist: \u00A7f@>A<>B@ 701;>:8@>20==KE 83@>:>2 8 IP 04@5A>2\n";
    private static final String HELP_MSG_2 = "\u00A7e--------- \u00A7f><>IL: !?8A>: (2/6) \u00A7e------------------\n"
        + "\u00A76/clear: \u00A7fG8I05B 8=25=B0@L 83@>:0. >6=> C:070BL D8;LB@ ?> ?@54<5BC 8 53> 40==K<.\n"
        + "\u00A76/defaultgamemode: \u00A7fKAB02;O5B 83@>2>9 @568< ?> C<>;G0=8N\n"
        + "\u00A76/deop: \u00A7fB7K205B C C:070==>3> 83@>:0 AB0BCA >?5@0B>@0\n"
        + "\u00A76/difficulty: \u00A7fKAB02;O5B A;>6=>ABL 83@K\n"
        + "\u00A76/enchant: \u00A7f>102;O5B 70G0@>20=85 =0 ?@54<5B :>B>@K9 A59G0A 45@68B 83@>:. #:068B5 7=0G5=85 0 5A;8\n"
        + "\u00A7fE>B8B5 C1@0BL 70G0@>20=85. A?>;L7C9B5 force GB>1K\n"
        + "\u00A7f83=>@8@>20BL ?@028;0 70G0@>20=89.\n"
        + "\u00A76/gamemode: \u00A7f7<5=O5B 83@>2>9 @568< C:070==>3> 83@>:0\n"
        + "\u00A76/gamerule: \u00A7fKAB02;O5B ?@028;0 A5@25@0\n";
    private static final String HELP_MSG_3 = "\u00A7e--------- \u00A7f><>IL: !?8A>: (3/6) \u00A7e------------------\n"
        + "\u00A76/give: \u00A7fK405B 83@>:C 7040==>5 :>;8G5AB2> ?@54<5B>2\n"
        + "\u00A76/help: \u00A7f>:07K205< <5=N ?><>I8\n"
        + "\u00A76/kick: \u00A7fK1@0AK205B 83@>:0 A A5@25@0\n"
        + "\u00A76/kill: \u00A7fkills the server cold....in its tracks!\n"
        + "\u00A76/lagg: \u00A7fClearlag base command!\n"
        + "\u00A76/list: \u00A7f>:07K205B A?8A>: 83@>:>2 >=;09=\n"
        + "\u00A76/me: \u00A7fK?>;=O5B C:070==>5 459AB28O 2 G0B5\n"
        + "\u00A76/op: \u00A7f@54>AB02;O5B 7040==><C 83@>:C AB0BCA >?5@0B>@0\n"
        + "\u00A76/pardon: \u00A7f>72>;O5B C:070==><C 83@>:C 70E>48BL =0 A5@25@\n";
    private static final String HELP_MSG_4 = "\u00A7e--------- \u00A7f><>IL: !?8A>: (4/6) \u00A7e------------------\n"
        + "\u00A76/pardon-ip: \u00A7f 07@5H05B 83@>:0< 70E>48BL 2 83@C A C:070==>3> IP 04@5A0.\n"
        + "\u00A76/plugins: \u00A7f>:07K205B A?8A>: ?;038=>2 A5@25@0\n"
        + "\u00A76/reload: \u00A7fReloads the server and every plugin!\n"
        + "\u00A76/restart: \u00A7f5@5703@C605B A5@25@\n"
        + "\u00A76/save-all: \u00A7f!>E@0=O5B <8@ =0 48A:.\n"
        + "\u00A76/save-off: \u00A7fB:;NG05B 02B><0B8G5A:>5 A>E@0=5=85 <8@0 =0 48A:\n"
        + "\u00A76/save-on: \u00A7f:;NG05B 02B><0B8G5A:>5 A>E@0=5=85 <8@0 =0 48A:\n"
        + "\u00A76/say: \u00A7f8H5B A>>1I5=85 2 G0B >B 8<5=8 :>=A>;8\n"
        + "\u00A76/seed: \u00A7f>:07K205B A84 <8@0\n";
    private static final String HELP_MSG_5 = "\u00A7e--------- \u00A7f><>IL: !?8A>: (5/6) \u00A7e------------------\n"
        + "\u00A76/spawnpoint: \u00A7f#:07K205B B>G:C A?02=0 83@>:>2\n"
        + "\u00A76/stop: \u00A7fStops the server cold....in its tracks!\n"
        + "\u00A76/tell: \u00A7fBAK;05B ;8G=>5 A>>1I5=85 C:070==><C 83@>:C.\n"
        + "\u00A76/time: \u00A7f#:07K205B 2@5<O 4;O 2A5E <8@>2\n"
        + "\u00A76/timings: \u00A7f0?8AK205B B09<8=38 2A5E A>1KB89 ?;038=>2\n"
        + "\u00A76/toggledownfall: \u00A7f:;NG05B 8;8 2K:;NG05B 4>64L.\n"
        + "\u00A76/tp: \u00A7f\"5;5?>@B8@C5B 83@>:0 : 4@C3><C 83@>:C 8;8 =0 7040=CN B>G:C.\n"
        + "\u00A76/tps: \u00A7fGets the current ticks per second for the server\n"
        + "\u00A76/version: \u00A7f>:07K205B 25@A8N A5@25@0 8;8 C:070==>3> ?;038=0.\n";
    private static final String HELP_MSG_6 = "\u00A7e--------- \u00A7f><>IL: !?8A>: (6/6) \u00A7e------------------\n"
        + "\u00A76/weather: \u00A7f5=O5B ?>3>4C\n"
        + "\u00A76/whitelist: \u00A7f#?@02;O5B 15;K< A?8A:>< A5@25@0\n"
        + "\u00A76/wstats: \u00A7fshow stats\n"
        + "\u00A76/xp: \u00A7fK405B C:070==><C 83@>:C C:070=>5 :>;8G5AB2> >?KB0. #:07K209B5 <:>;8G5AB2>>L 4;O 2K40G8 C@>2=59. A;8 7=0G5=85 >B@8G0B5;L=>5, B> >?KB A=8<05BAO.\n";
    private static final String ALIASES_MSG = "\u00A7e--------- \u00A7f><>IL: Aliases \u00A7e-----------------------\n"
        + "\u00A76/about: \u00A7f\u00A7eAlias for \u00A7f/version\n"
        + "\u00A76/pl: \u00A7f\u00A7eAlias for \u00A7f/plugins\n"
        + "\u00A76/rl: \u00A7f\u00A7eAlias for \u00A7f/reload\n"
        + "\u00A76/ver: \u00A7f\u00A7eAlias for \u00A7f/version\n";
    private static final String BUKKIT_MSG_1 = "\u00A7e--------- \u00A7f><>IL: Bukkit (1/5) \u00A7e------------------\n"
        + "\u00A77865 C:070=K 2A5 :><0=4K Bukkit:\n"
        + "\u00A76/ban: \u00A7f0?@5I05B 8A?>;L7>20=85 A5@25@0 >?@545;5==K<83@>:><\n"
        + "\u00A7f\n"
        + "\u00A76/ban-ip: \u00A7f0?@5I05B 8A?>;L7>20=85 A5@25@0 83@>:0< A\n"
        + "\u00A7f>?@55;5==>3> IP 04@5A0.\n"
        + "\u00A76/banlist: \u00A7f@>A<>B@ 701;>:8@>20==KE 83@>:>2 8 IP 04@5A>2\n"
        + "\u00A76/clear: \u00A7fG8I05B 8=25=B0@L 83@>:0. >6=> C:070BL D8;LB@ ?>\n"
        + "\u00A7f?@54<5BC 8 53> 40==K<.\n"
        + "\u00A76/defaultgamemode: \u00A7fKAB02;O5B 83@>2>9 @568< ?> C<>;G0=8N\n";

    private static final String PLAYER_NAME_PATTERN
        = "(?<playerName>[^\\n]+)";
    private static final String PLAYER_IP_PATTERN
        = "(?<playerIP>\\[\\d{1,3}(\\.\\d{1,3}){3}\\])";
    private static final Pattern PLAYER_IP_CLEANER
        = Pattern.compile("(?<otherChar>[^\\d\\.])");
    private static final Pattern PLAYERS_LIST_IP_PATTERN = Pattern
        .compile(String.format("(?:(?<line>%s\\s+%s*)\\n)",
                PLAYER_NAME_PATTERN, PLAYER_IP_PATTERN));
    private static final String PLAYERS_LIST_IP_EMPTY = "§fThere are no players online\n";
    private static final String PLAYERS_LIST_IP = "§fThere are §a1§f/§b4§f players online:\n"
        + "§aPlayer1        §e[§b192.168.1.101§e]\n"
        + "§aPlayer2        §e[§b192.168.1.102§e]\n"
        + "§aPlayer3        §e[§b192.168.1.103§e]\n";
    private static final Pattern PLAYERS_LIST_PATTERN = Pattern
        .compile("(?:(?<line>[^\\n]*)\\n)");
    private static final String PLAYERS_LIST_EMPTY = "There are 0/4 players online:\n\n";
    private static final String PLAYERS_LIST = "There are 1/4 players online:\n"
        + "Player1\n"
        + "Player2\n"
        + "Player3\n";

    @Test
    public void test1() throws FileNotFoundException, IOException {
        assertTrue(parseServerCommands(HELP_MSG_1).size() > 0);
    }

    @Test
    public void test2() throws FileNotFoundException, IOException {
        assertTrue(parsePlayersList(PLAYERS_LIST_EMPTY).isEmpty());
        assertTrue(parsePlayersList(PLAYERS_LIST).size() == 3);
    }

    @Test
    public void test3() throws FileNotFoundException, IOException {
        assertTrue(parsePlayersWithIPList(PLAYERS_LIST_IP_EMPTY).isEmpty());
        assertTrue(parsePlayersWithIPList(PLAYERS_LIST_IP).size() == 3);
    }

    private List<String> parsePlayersList(String msg) {
        List<String> result = new ArrayList<>();

        if (msg.contains("\n")) {
            String resp = parseColorString(msg.substring(msg.indexOf("\n") + 1));
            Matcher matcher = PLAYERS_LIST_PATTERN.matcher(resp);

            String playerName;
            while (matcher.find()) {
                playerName = matcher.group("line");
                if (!playerName.isEmpty() && !playerName.equalsIgnoreCase("\n")) {
                    result.add(playerName);
                }
            }
        }
        return result;
    }

    private Map<String, String> parsePlayersWithIPList(String msg) {
        Map<String, String> result = new HashMap<>();

        if (msg.contains("\n")) {
            String resp = parseColorString(msg.substring(msg.indexOf("\n") + 1));
            Matcher matcher = PLAYERS_LIST_IP_PATTERN.matcher(resp);

            String playerName;
            String playerIP;
            while (matcher.find()) {
                if (!matcher.group("line").isEmpty()) {
                    playerName = matcher.group("playerName").trim();
                    playerIP = matcher.group("playerIP").trim();
                    Matcher m = PLAYER_IP_CLEANER.matcher(playerIP);
                    while (m.find()) {
                        playerIP = playerIP.replace(m.group("otherChar"), "");
                    }
                    if (!playerName.isEmpty()
                        && !playerName.equalsIgnoreCase("\n")) {
                        result.put(playerName, playerIP);
                    }
                }
            }
        }
        return result;
    }

    private List<String> parseServerCommands(String msg) {
        String content = parseColorString(msg);
        List<String> commands = getBasicCommands(content);
        commands.addAll(getCustomCommands(content));
        return commands;
    }

    private static String parseColorString(String msg) {
        return msg.replaceAll("\u00A7(\\d|[a-f])", "");
    }

    private List<String> getBasicCommands(String msg) {
        return getBasicCommands(msg, false);
    }

    private List<String> getBasicCommands(String msg, boolean skipPagination) {
        List<String> commands = new ArrayList();
        Matcher matcher = SERVER_BASIC_COMMAND_PATTERN.matcher(msg);

        while (matcher.find()) {
            commands.add(matcher.group("cmd"));
        }
        if (!skipPagination) {
            Matcher multiPage = SERVER_COMMAND_PAGES_PATTERN.matcher(msg
                .replaceAll("\\n", "\\\\n"));
            if (multiPage.matches()) {
                int nbPages = Integer.valueOf(multiPage.group("nbPages"));
                if (nbPages > 1) {
                    for (int i = 2; i <= nbPages; i++) {
                        commands.addAll(getBasicCommands(
                            parseColorString(getNextHelp(i)), true));
                    }
                }
            }
        }
        return commands;
    }

    private List<String> getCustomCommands(String msg) {
        List<String> commands = new ArrayList();
        Matcher matcher = SERVER_CUSTOM_COMMAND_PATTERN.matcher(msg);

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
