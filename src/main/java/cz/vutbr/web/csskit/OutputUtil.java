package cz.vutbr.web.csskit;

import java.util.List;

import cz.vutbr.web.css.PrettyOutput;

/**
 * Helper class for generation output for given CSS rules
 * @author kapy
 *
 */
public class OutputUtil {

	public static final String EMPTY_DELIM = "";
	public static final String SPACE_DELIM = " ";
	public static final String DEPTH_DELIM = "\t";
	public static final String RULE_OPENING = " {\n";
	public static final String RULE_CLOSING = "}\n";
	public static final String MEDIA_DELIM = ", ";
	public static final String SELECTOR_DELIM = ", ";
	public static final String IMPORT_KEYWORD = "@import ";
	public static final String URL_OPENING = "url('";
	public static final String URL_CLOSING = "')";
	public static final String LINE_CLOSING = ";\n";
	public static final String NEW_LINE = "\n";
	public static final String MEDIA_KEYWORD = "@media ";
	public static final String RULE_DELIM = "\n";
	public static final String CHARSET_KEYWORD = "@charset ";
	public static final String CHARSET_OPENING = "\"";
	public static final String CHARSET_CLOSING = "\";\n";
	public static final String PROPERTY_OPENING = ": ";
	public static final String PROPERTY_CLOSING = ";\n";
	public static final String IMPORTANT_KEYWORD = "!important";
	public static final String PAGE_KEYWORD = "@page";
	public static final String PAGE_OPENING = ":";
	public static final String PAGE_CLOSING = "";
    public static final String VIEWPORT_KEYWORD = "@viewport";
    public static final String FONT_FACE_KEYWORD = "@font-face";
	public static final String FUNCTION_OPENING = "(";
	public static final String FUNCTION_CLOSING = ")";
	public static final String STRING_OPENING = "'";
	public static final String STRING_CLOSING = "'";
	public static final String ATTRIBUTE_OPENING = "[";
	public static final String ATTRIBUTE_CLOSING = "]";
	public static final String PERCENT_SIGN = "%";
	public static final String HASH_SIGN = "#";
	public static final String MARGIN_AREA_OPENING = "@";
	
	
	
	
	/**
	 * Appends string multiple times to buffer
	 * @param sb StringBuilder to be modified
	 * @param append String to be added
	 * @param times Number of times <code>append</code> is added 
	 * @return Modified StringBuilder <code>sb</code> to allow chaining
	 */
	public static StringBuilder appendTimes(StringBuilder sb, String append, int times) {
		
		for(;times>0; times--)
			sb.append(append);
		
		return sb;
	}
	
	/**
	 * Appends all elements of <code>list</code> to buffer, separated by delimiter
	 * @param <T> Type of elements stored in <code>list</code>
	 * @param sb StringBuilder to be modified
	 * @param list List of elements
	 * @param delimiter Delimiter to separate elements
	 * @return Modified <code>sb</code> to allow chaining
	 */
	public static <T> StringBuilder appendList(StringBuilder sb, List<T> list, String delimiter) {
		
		boolean firstRun = true;
		
		for(T elem: list) {
			if(!firstRun) 
				sb.append(delimiter);
			else 
				firstRun = false;
			
			sb.append(elem.toString());
		}
		
		return sb;
		
	}
	
	/**
	 * Appends of elements of <code>list</code> to list, separater by delimiter.
	 * Uses depth parameter to make output nicer for each element
	 * @param <T> List of elements, which implements <code>Rule</code>
	 * @param sb StringBuilder to be modified
	 * @param list List of elements
	 * @param delimiter Delimeter between elements
	 * @param depth Depth of each element
	 * @return Modified <code>sb</code> to allow chaining
	 */
	public static <T extends PrettyOutput> StringBuilder appendList(
			StringBuilder sb, List<T> list, 
			String delimiter, int depth) {
		
		boolean firstRun = true;
		
		for(T elem: list) {
			if(!firstRun)
				sb.append(delimiter);
			else
				firstRun = false;
			
			sb.append(elem.toString(depth));
		}
		
		return sb;
	}
	
}
