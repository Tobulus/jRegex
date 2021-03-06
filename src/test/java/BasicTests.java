import org.junit.Test;

import static org.junit.Assert.*;

public class BasicTests {
    @Test
    public void simpleCharacters() {
        assertTrue(Pattern.compile("a").test("a"));
        assertTrue(Pattern.compile("test").test("test"));
    }

    @Test
    public void simpleSplit() {
        assertTrue(Pattern.compile("a|b").test("a"));
        assertTrue(Pattern.compile("a|b").test("b"));
        Matcher dogCat = Pattern.compile("cat|dog");
        assertTrue(dogCat.test("cat"));
        assertTrue(dogCat.test("dog"));
    }

    @Test
    public void cycleSplit() {
        Matcher m = Pattern.compile("a*|b*");
        assertTrue(m.test("a"));
        assertTrue(m.test("aa"));
        assertTrue(m.test("b"));
        assertTrue(m.test("bb"));
        assertFalse(m.test("c"));
        assertFalse(m.test("ab"));

        m = Pattern.compile("cat*|dog*");
        assertTrue(m.test("cat"));
        assertTrue(m.test("catt"));
        assertTrue(m.test("dog"));
        assertTrue(m.test("dogg"));
    }

    @Test
    public void groupedCycleSplit() {
        Matcher m = Pattern.compile("(cat)*|(dog)*");
        assertTrue(m.test(""));
        assertTrue(m.test("cat"));
        assertTrue(m.test("catcat"));
        assertTrue(m.test("dog"));
        assertTrue(m.test("dogdog"));
    }

    @Test
    public void nestedGroups() {
        Matcher m = Pattern.compile("(cat|(dog|doggy))*|bird");
        assertTrue(m.test("bird"));
        assertFalse(m.test("birdbird"));
        assertTrue(m.test("cat"));
        assertTrue(m.test("catcat"));
        assertTrue(m.test("dog"));
        assertTrue(m.test("dogdog"));
        assertTrue(m.test("doggydoggy"));
    }

    @Test
    public void simpleCycle() {
        Matcher aStar = Pattern.compile("a*");
        assertTrue(aStar.test(""));
        assertTrue(aStar.test("a"));
        assertTrue(aStar.test("aa"));
        assertTrue(aStar.test("aaa"));
        assertTrue(Pattern.compile("a*b").test("aaab"));
        assertTrue(Pattern.compile("a*b").test("ab"));
        assertTrue(Pattern.compile("ab*").test("ab"));
        assertTrue(Pattern.compile("ab*").test("abb"));

        Matcher aPlus = Pattern.compile("a+");
        assertFalse(aPlus.test(""));
        assertTrue(aPlus.test("a"));
        assertTrue(aPlus.test("aa"));
    }

    @Test
    public void multiOr() {
        Matcher m = Pattern.compile("a|b|c");
        assertTrue(m.test("a"));
        assertTrue(m.test("b"));
        assertTrue(m.test("c"));
        assertFalse(m.test(""));

        m = Pattern.compile("cat|dog|bird");
        assertTrue(m.test("cat"));
        assertTrue(m.test("dog"));
        assertTrue(m.test("bird"));
        assertFalse(m.test(""));
    }

    @Test
    public void exclusiv() {
        Matcher m = Pattern.compile("a?");
        assertTrue(m.test(""));
        assertTrue(m.test("a"));

        m = Pattern.compile("tes?t");
        assertTrue(m.test("test"));
        assertTrue(m.test("tet"));

        m = Pattern.compile("a?a?a*");
        assertTrue(m.test("a"));
        assertTrue(m.test("aa"));
        assertTrue(m.test("aaa"));
        assertTrue(m.test("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        assertFalse(m.test("aaaaaaaaaaaaaaaaaaaaaaaaaaaaab"));
    }

    @Test
    public void exclusivGroup() {
        Matcher m = Pattern.compile("(test)?");
        assertTrue(m.test(""));
        assertTrue(m.test("test"));

        m = Pattern.compile("((cat)?|dog*)");
        assertTrue(m.test(""));
        assertTrue(m.test("cat"));
        assertFalse(m.test("catcat"));
        assertTrue(m.test("dog"));
        assertTrue(m.test("dogg"));
    }

    @Test
    public void dot() {
        Matcher m = Pattern.compile("test.*");
        assertTrue(m.test("test"));
        assertTrue(m.test("teste"));
        assertTrue(m.test("tester"));

        m = Pattern.compile(".+@.+\\.com");
        assertTrue(m.test("user@test.com"));
        assertFalse(m.test("@test.com"));
    }

    @Test
    public void escaping() {
        Matcher m = Pattern.compile("\\*");
        assertTrue(m.test("*"));
        m = Pattern.compile("\\.");
        assertTrue(m.test("."));
    }

    @Test
    public void characterClass() {
        Matcher m = Pattern.compile("[abc]");
        assertTrue(m.test("a"));
        assertTrue(m.test("b"));
        assertTrue(m.test("c"));

        m = Pattern.compile("[abc]+");
        assertFalse(m.test(""));
        assertTrue(m.test("a"));
        assertTrue(m.test("ab"));
        assertTrue(m.test("aa"));
        assertTrue(m.test("ca"));
        assertTrue(m.test("cc"));

        m = Pattern.compile("([abc]+|d)ef");
        assertTrue(m.test("aef"));
        assertTrue(m.test("def"));
        assertTrue(m.test("aaef"));
        assertTrue(m.test("abcef"));
        assertFalse(m.test("abdef"));

        m = Pattern.compile("[\\.,]+");
        assertTrue(m.test(".,."));
        assertFalse(m.test("a"));
    }

    @Test
    public void negateCharacterClass() {
        Matcher m = Pattern.compile("[^abc]");
        assertFalse(m.test("a"));
        assertFalse(m.test("b"));
        assertFalse(m.test("c"));
        assertTrue(m.test("d"));
    }

    @Test
    public void minMax() {
        Matcher m = Pattern.compile("[^abc]{1,3}");
        assertFalse(m.test(""));
        assertTrue(m.test("d"));
        assertTrue(m.test("dd"));
        assertTrue(m.test("ddd"));
        assertFalse(m.test("dddd"));
        assertTrue(m.test("e"));

        m = Pattern.compile("(dog){1,}");
        assertFalse(m.test(""));
        assertTrue(m.test("dog"));
        assertTrue(m.test("dogdog"));
    }

    @Test
    public void prepare() {
        assertEquals("abc", new Optimizer().prepare("abc"));
        assertEquals("ab*c", new Optimizer().prepare("ab*c"));
        assertEquals("(abc)", new Optimizer().prepare("(abc)"));
        assertEquals("(abc)+|(def)*", new Optimizer().prepare("(abc)+|(def)*"));
        assertEquals("aaa?", new Optimizer().prepare("a{2,3}"));
        assertEquals("aaa*", new Optimizer().prepare("a{2,}"));
        assertEquals("a?a?a?", new Optimizer().prepare("a{0,3}"));
        assertEquals("(dog)?(dog)?(dog)?", new Optimizer().prepare("(dog){0,3}"));
        assertEquals("(dog)(dog)?(dog)?", new Optimizer().prepare("(dog){1,3}"));
        assertEquals("(dog)(dog)*", new Optimizer().prepare("(dog){1,}"));
        assertEquals("((1)\\.)((1)\\.)((1)\\.)", new Optimizer().prepare("((1)\\.){3}"));
    }

    @Test
    public void metaCharacters() {
        Matcher m = Pattern.compile("\\d");
        assertFalse(m.test("a"));
        assertTrue(m.test("1"));
        assertFalse(m.test("11"));

        m = Pattern.compile("\\d*");
        assertTrue(m.test(""));
        assertTrue(m.test("1"));
        assertTrue(m.test("12"));
        assertTrue(m.test("123"));

        m = Pattern.compile("\\w+");
        assertFalse(m.test(""));
        assertTrue(m.test("a"));
        assertTrue(m.test("ab"));
        assertTrue(m.test("abc"));
    }

    @Test
    public void complex() {
        Matcher email = Pattern.compile("[\\w\\.+-]+@[\\w\\.-]+\\.[\\w\\.-]+");
        assertTrue(email.test("admin@domain.com"));
        assertTrue(email.test("cooldude-42@gmail.net"));
        assertFalse(email.test("@gmail.net"));

        Matcher ipv4 = Pattern.compile("((25[0-5]|2[0-4]\\d|[01]?\\d\\d)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d)");
        assertTrue(ipv4.test("255.201.199.255"));

        Matcher uri = Pattern.compile("[\\w]+://[^/\\s?#]+[^\\s?#]+(\\?[^\\s#]*)?(#[^\\s]*)?");
        assertTrue(uri.test("http://google.de"));
        assertFalse(uri.test("http://wr ong.de"));
        assertFalse(uri.test("://google.de"));
        assertTrue(uri.test("http://google.de?query=test"));
    }

    @Test
    public void range() {
        Matcher m = Pattern.compile("[4-7]{2}");
        assertTrue(m.test("54"));
        assertTrue(m.test("76"));
        assertTrue(m.test("54"));
        assertFalse(m.test("15"));
        assertFalse(m.test("98"));

        m = Pattern.compile("[b-m]{2,3}");
        assertTrue(m.test("bhi"));
        assertTrue(m.test("gkl"));
        assertTrue(m.test("cde"));
        assertFalse(m.test("abc"));
        assertFalse(m.test("Bde"));
    }
}
