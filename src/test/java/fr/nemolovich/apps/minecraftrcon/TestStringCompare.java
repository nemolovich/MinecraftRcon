/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.nemolovich.apps.minecraftrcon;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Nemolovich
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestStringCompare {

    private static final List<String> WORDS = Arrays.asList("ban", "banlist",
        "ban-ip");

    @Test
    public void test1() {
        String commonStart = null;
        for (String str : WORDS) {
            if (commonStart == null) {
                commonStart = str;
            }
            commonStart = StringUtils.getCommonStringStart(commonStart, str);
        }
        assertEquals(commonStart, "ban");
    }

}
