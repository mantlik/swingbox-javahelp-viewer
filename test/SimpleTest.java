package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vutbr.web.css.CSSException;
import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.TermURI;

public class SimpleTest {
	private static Logger log = LoggerFactory.getLogger(SimpleTest.class);
	
	public static final TermFactory tf = CSSFactory.getTermFactory();
	
	public static final String TEST_STRING1 = 
		"BODY { display: block;}";
	
	public static final String TEST_CHARSET_STRING1 = 
		"@charset \"UTF-8\";";
	
	public static final String TEST_CHARSET_STRING2 = 
		"@charset \"ISO-8859-1\";\n" +
		"\n" +
		"BODY { color: blue;}";
	
	public static final String TEST_HASH_COLOR1 =
		"BODY { color: #00AA85;}";
	
	public static final String TEST_HASH_COLOR2 =
		"DIV, P { color: #CCC;}";
	
	public static final String TEST_RGBFUNCTION1 =
		"BODY { color: rgb(192,64,32);}";
	
	public static final String TEST_RGBFUNCTION2 =
		"BODY { color: rgb(50%,128,30%);} ";
	
	public static final String TEST_UNIT =
		"BODY { margin: 5em; }";
	
	public static final String TEST_STRING2 = 
		"BODY {\n" +
		"	background-color: #EEE;\n" +
		"	color: red;\n" +
		"}\n" +
		"\n" +
		"H1.prvni+P {\n" +
		"	color: #00A;\n" +
		"}\n" +
		"\n" +
		"H1 {\n"+
		"	font-size: 20px;\n" +
		"	color: #000;\n" +
		"}\n" +
		"\n" +
		"H1.prvni {\n" +
		"	color: #00A;\n" +
		"}\n" +		
		"\n" +
		".AA {\n" +
		"font-weight: bold;\n" +
		"}\n" +
		"\n" +
		".BB {\n" +
		"	color: #0F0;}\n" +
		"\n";
	
	public static String TEST_URI1 = "BODY { background-image: url(image.jpg); } ";

	
	@BeforeClass
	public static void init()  {
		log.info("\n\n\n == SimpleTest test at {} == \n\n\n", new Date());
	}
	
	
	@Test
	public void testCharsets() throws IOException, CSSException   {
		
		StyleSheet ss;
		
		ss = CSSFactory.parse(TEST_CHARSET_STRING1);
		
		assertEquals("No rules are defined", 0, ss.size());

		ss = CSSFactory.parse(TEST_CHARSET_STRING2);
		
		assertEquals("One rule is set", 1, ss.size());
		
		RuleSet rule = (RuleSet) ss.get(0);				
		
		assertEquals("Rule contains one selector BODY ", 
				SelectorsUtil.createSelectors("BODY"), 
				rule.getSelectors());
		
		assertEquals("Rule contains one declaration { color: blue;}",
				DeclarationsUtil.appendDeclaration(null, "color", 
						tf.createColor(0,0,255)),
				rule.asList());	
	}
	
	
	@Test 
	public void testString1() throws IOException, CSSException   {
		
		StyleSheet ss = CSSFactory.parse(TEST_STRING1);
		
		assertEquals("One rule is set", 1, ss.size());
		
		RuleSet rule = (RuleSet) ss.get(0);				
		
		assertEquals("Rule contains one selector BODY ", 
				SelectorsUtil.createSelectors("BODY"), 
				rule.getSelectors());
		
		assertEquals("Rule contains one declaration {display:block;}",
				DeclarationsUtil.appendDeclaration(null, "display", 
						tf.createIdent("block")),
				rule.asList());
							
	}
	
	
	
	@Test 
	public void testRGBFunction1() throws IOException, CSSException   {
		
		StyleSheet ss = CSSFactory.parse(TEST_RGBFUNCTION1);
		assertEquals("One rule is set", 1, ss.size());
		
		RuleSet rule = (RuleSet) ss.get(0);				
		
		assertEquals("Rule contains one selector BODY ", 
				SelectorsUtil.createSelectors("BODY"), 
				rule.getSelectors());
		
		assertEquals("Rule contains one declaration {color: #00aa85;}",
				DeclarationsUtil.appendDeclaration(null, "color", 
						tf.createColor(192, 64, 32)),
				rule.asList());
		
	}
	
	@Test 
	public void testRGBFunction2() throws IOException, CSSException   {
		
		StyleSheet ss = CSSFactory.parse(TEST_RGBFUNCTION2);
		assertEquals("One rule is set", 1, ss.size());
		
		RuleSet rule = (RuleSet) ss.get(0);				
		
		assertEquals("Rule contains one selector BODY ", 
				SelectorsUtil.createSelectors("BODY"), 
				rule.getSelectors());
		
		assertEquals("Rule contains one declaration {color: rgb(50%,128,30%);}",
				DeclarationsUtil.appendDeclaration(null, "color", 
						tf.createColor(127, 128, 76)),
				rule.asList());
	}
	
	@Test
	public void testHashColor1() throws IOException, CSSException   {

		StyleSheet ss = CSSFactory.parse(TEST_HASH_COLOR1);
		assertEquals("One rule is set", 1, ss.size());
		
		RuleSet rule = (RuleSet) ss.get(0);				
		
		assertEquals("Rule contains one selector BODY ", 
				SelectorsUtil.createSelectors("BODY"), 
				rule.getSelectors());
		
		assertEquals("Rule contains one declaration {color: #00aa85;}",
				DeclarationsUtil.appendDeclaration(null, "color", 
						tf.createColor(0, 170, 133)),
				rule.asList());
		
	}
	
	@Test
	public void testHashColor2() throws IOException, CSSException   {
		
		StyleSheet ss = CSSFactory.parse(TEST_HASH_COLOR2);
		assertEquals("One rule is set", 1, ss.size());
		
		final RuleSet rule = (RuleSet) ss.get(0);				
		
		assertEquals("Rule contains two selectors DIV, P", 
				SelectorsUtil.createSelectors("DIV", "P"), 
				rule.getSelectors());
		
		assertEquals("Rule contains one declaration {color: #CCC;}",
				DeclarationsUtil.appendDeclaration(null, "color", 
						tf.createColor(204,204,204)),
				rule.asList());
	}
	
	@Test
	public void testUnit() throws IOException, CSSException {
		StyleSheet ss = CSSFactory.parse(TEST_UNIT);
		
		assertEquals("There is one rule", 1, ss.size());
		
	}
	
	@Test
	public void testString2() throws IOException, CSSException   {
		
		StyleSheet ss = CSSFactory.parse(TEST_STRING2);
		assertEquals("Six rules are set", 6, ss.size());
	}
	
	@Test
	public void testURI1() throws IOException, CSSException   {
		
		StyleSheet ss = CSSFactory.parse(TEST_URI1);
		assertEquals("There is one rule", 1, ss.size());
		
		Declaration dec = (Declaration) ss.get(0).get(0);
		assertEquals("There is one declaration", 1, ss.get(0).size());
		
		Object term = dec.get(0);
		assertTrue("Term value is URI", term instanceof TermURI);
		
		TermURI uri = (TermURI) term;
		assertEquals("URI has proper value", "image.jpg", uri.getValue());
	}	
}
