package cz.vutbr.web.css;

import cz.vutbr.web.css.RuleBlock.Priority;


/**
 * Acts as collection of Rules. Remembers last priority used 
 * in style sheet to allow incremental parsing. The style sheet
 * origin is tracked as well for later rule priority evaluation.
 * The default style sheet origin is "Author". 
 *
 * @author kapy
 */
public interface StyleSheet extends Rule<RuleBlock<?>>{
  
    /**
     * Marks last priority used in this style-sheet
     * @param last Priority mark
     */
    public void markLast(Priority last);
    
    /**
     * Gets last mark priority
     * @return Priority mark
     */
    public Priority getLastMark();

    /**
     * Sets the stylesheet origin.
     * @param o The origin to be set
     */
    public void setOrigin(Origin o);
    	
    /**
     * Gets the origin of the stylesheet.
     * @return the origin of the stylesheet.
     */
    public Origin getOrigin();
    
    /**
     * The origin of the style sheet (user agent, user, or author). 
     * @author radek
     */
    public enum Origin { AUTHOR, AGENT, USER };
}
