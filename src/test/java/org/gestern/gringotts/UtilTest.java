package org.gestern.gringotts;

import static org.junit.Assert.*;
import static org.gestern.gringotts.Util.*;
import org.junit.Test;



public class UtilTest {

    @Test
    public void versionAtLeast1() {
        assertFalse(versionAtLeast("0", "1"));
    }
    @Test
    public void versionAtLeast2() {
        assertFalse(versionAtLeast("0.1", "1"));
    }
    @Test
    public void versionAtLeast3() {
        assertFalse(versionAtLeast("0.1", "1.2.1"));
    }
    @Test
    public void versionAtLeast4() {
        assertFalse(versionAtLeast("0.0.0.0.1", "0.0.0.0.2"));
    }
    @Test
    public void versionAtLeast5() {
        assertFalse(versionAtLeast("3.6", "4.6.1"));
    }
    @Test
    public void versionAtLeast6() {
        assertTrue(versionAtLeast("3.6-b23", "3.6"));
    }
    @Test
    public void versionAtLeast6a() {
        assertTrue(versionAtLeast("3.6", "3.6-b23"));
    }
    @Test
    public void versionAtLeast7() {
        assertTrue(versionAtLeast("1.2.3", "1.2.3"));
    }
    @Test
    public void versionAtLeast8() {
        assertTrue(versionAtLeast("one", "two"));
    }
    @Test
    public void versionAtLeast9() {
        assertTrue(versionAtLeast("3.6.1", "2.6"));
    }
    @Test
    public void versionAtLeast10() {
        assertTrue(versionAtLeast("3.6.1", "3.6"));
    }
    @Test
    public void versionAtLeast10a() {
        assertFalse(versionAtLeast("3.6", "3.6.1"));
    }
    @Test
    public void versionAtLeast11() {
        assertTrue(versionAtLeast("88.6", "3.6.99-b3"));
    }
    @Test
    public void versionAtLeast12() {
        assertTrue(versionAtLeast("88.6", "3-b3"));
    }
    @Test
    public void versionAtLeast13() {
        assertFalse(versionAtLeast("88.6", "99-b3"));
    }

}
