package org.gestern.gringotts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.gestern.gringotts.Util.versionAtLeast;

public class UtilTest {

    @Test
    public void versionAtLeast1() {
        Assertions.assertFalse(versionAtLeast("0", "1"));
    }

    @Test
    public void versionAtLeast2() {
        Assertions.assertFalse(versionAtLeast("0.1", "1"));
    }

    @Test
    public void versionAtLeast3() {
        Assertions.assertFalse(versionAtLeast("0.1", "1.2.1"));
    }

    @Test
    public void versionAtLeast4() {
        Assertions.assertFalse(versionAtLeast("0.0.0.0.1", "0.0.0.0.2"));
    }

    @Test
    public void versionAtLeast5() {
        Assertions.assertFalse(versionAtLeast("3.6", "4.6.1"));
    }

    @Test
    public void versionAtLeast6() {
        Assertions.assertTrue(versionAtLeast("3.6-b23", "3.6"));
    }

    @Test
    public void versionAtLeast6a() {
        Assertions.assertTrue(versionAtLeast("3.6", "3.6-b23"));
    }

    @Test
    public void versionAtLeast7() {
        Assertions.assertTrue(versionAtLeast("1.2.3", "1.2.3"));
    }

    @Test
    public void versionAtLeast8() {
        Assertions.assertTrue(versionAtLeast("one", "two"));
    }

    @Test
    public void versionAtLeast9() {
        Assertions.assertTrue(versionAtLeast("3.6.1", "2.6"));
    }

    @Test
    public void versionAtLeast10() {
        Assertions.assertTrue(versionAtLeast("3.6.1", "3.6"));
    }

    @Test
    public void versionAtLeast10a() {
        Assertions.assertFalse(versionAtLeast("3.6", "3.6.1"));
    }

    @Test
    public void versionAtLeast11() {
        Assertions.assertTrue(versionAtLeast("88.6", "3.6.99-b3"));
    }

    @Test
    public void versionAtLeast12() {
        Assertions.assertTrue(versionAtLeast("88.6", "3-b3"));
    }

    @Test
    public void versionAtLeast13() {
        Assertions.assertFalse(versionAtLeast("88.6", "99-b3"));
    }

}
