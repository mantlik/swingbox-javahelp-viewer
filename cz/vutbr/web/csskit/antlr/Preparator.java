package cz.vutbr.web.csskit.antlr;

import java.util.List;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.css.RuleBlock.Priority;

/**
 * Prepares StyleSheet blocks in parser. Allows switching strategy in runtime,
 * for example modify behavior for embedded, file and inline rules
 * 
 * @author kapy
 *
 */
public interface Preparator {

	/**
	 * Creates base block of rules. Most usually, it will be RuleSet, but when
	 * parser is inside of imported file with medium and wrap condition is {@code true}, then
	 * RuleSet is wrapped by media into RuleMedia 
	 * @param cslist List of CombinedSelector for this rule block
	 * @param dlist List of Declaration for this rule block
	 * @param wrap Wrap condition, it {@code true}, rule set is wrapped into rule media
	 * @param media List of medias used to wrap 
	 * @return Either RuleSet containing selectors and declarations, or RuleMedia, wrapped
	 * version of RuleSet
	 */
	public RuleBlock<?> prepareRuleSet(List<CombinedSelector> cslist,
			List<Declaration> dlist, boolean wrap, List<String> media);
	
	/**
	 * Creates block of rules as result of parsing in-line declaration.
	 * @param dlist List of Declaration
	 * @param pseudos List of pseudo page identifiers
	 * @return RuleSet
	 */
	public RuleBlock<?> prepareInlineRuleSet(List<Declaration> dlist,
			List<Selector.PseudoPage> pseudos);
	
	/**
	 * Creates RuleMedia, block of rules with assigned medias. Uses mark to change priority of rules,
	 * that is priority of this rule is set to mark 
	 * @param mark Priority mark
	 * @param rules Rules encapsulated by this RuleMedia
	 * @param media List of media assigned to rule
	 * @return RuleMedia 
	 */
	public RuleBlock<?> prepareRuleMedia(Priority mark, List<RuleSet> rules, List<String> media);
	
	/**
	 * Creates RulePage, block of rules associated with specific page 
	 * @param decl List of declarations
	 * @param pseudo Name, or pseudo name of page
	 * @return RulePage
	 */
	public RuleBlock<?> prepareRulePage(List<Declaration> decl, String pseudo);
	
	/**
	 * Marks priority 
	 * @return Mark
	 */
	public Priority markPriority();
}
