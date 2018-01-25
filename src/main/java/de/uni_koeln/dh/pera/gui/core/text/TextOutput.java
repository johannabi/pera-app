package de.uni_koeln.dh.pera.gui.core.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.gui.misc.LayoutHelper;
import de.uni_koeln.dh.pera.util.Calc;

public class TextOutput extends StyledText {

		private Logger logger = LoggerFactory.getLogger(getClass());
	
		// TODO if fitting set to final
		private static int H_HTXTOUT_PCT = 80;
		
		private TextComposite parent = null;
		private int margin = 0;
	
	protected TextOutput(TextComposite parent) {
		super(parent, SWT.WRAP /*| SWT.V_SCROLL*/);
		this.parent = parent;
	} 
	
	protected void init() {
		logger.info("Initialize text output...");
		
		setMargin();
		setLayoutData();

		setColors();		
		configText(true);
	}
	
	private void setColors() {
		setBackground(parent.getBackground()/*Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA)*/);
		setForeground(parent.getForeground());
	}
	
	private void configText(boolean justify) {
		setEditable(false);
		setJustify(justify);		
		setFont(parent.getFont(/*SWT.ITALIC*/SWT.NORMAL));	// TODO font style
		setText(getDefaultText());
	}
	
	private void setMargin() {
		margin = parent.getTextMargin();
		LayoutHelper.setMargin(this, margin);
	}
		
	private void setLayoutData() {
		int outputWidth = parent.getWidth() - (2*margin),
				outputHeight = (int) Calc.getValByPct(parent.getHeight(), H_HTXTOUT_PCT);

		setLayoutData(LayoutHelper.getGridData(
				outputWidth, outputHeight, 
				true, false));
	}

	// TODO default text
	private String getDefaultText() {
		return "Willkommen meine Damen und Herren. Es ist eine Freude, Sie bei meiner Reise willkommen zu heißen. "
				+ "Mein Name ist Pero und ich bin stolzer Kastilier und enger Vertrauter unseres verehrten Königs "
				+ "Juans II. Außerdem bin ich ein direkter Nachfahre des berühmten Pedro Ruiz Tafur, welcher bei "
				+ "Cordoba unsere Truppen zum Sieg über die gefürchteten Mauren führte."
				+ "\n\n"
				+ "Auch ich beabsichtige meiner Familie und meinem Titel alle Ehre zu machen. Um dies zu erreichen,"
				+ " habe ich beschlossen, den Waffenstillstand zwischen Kastilien und Granada zu nutzen und bin im "
				+ "Jahr 1435 aufgebrochen, um die Fremde zu erkunden. Meine Abenteuer führten mich nach Italien, "
				+ "Palästina und Ägypten. Doch nun ist es an der Zeit, weiter zu reisen. Mein nächstes Ziel steht "
				+ "fest – Konstantinopel. Sie bekommen die einzigartige Gelegenheit, mich bei diesem Kapitel meiner"
				+ " Reise zu begleiten. Doch Vorsicht! Gefahren lauern an jeder Ecke, sodass jede falsche Entscheidung"
				+ " schnell zur letzten werden kann..."
				+ "\n\n"
				+ "Willkommen meine Damen und Herren. Es ist eine Freude, Sie bei meiner Reise willkommen zu heißen."
				+ " Mein Name ist Pero und ich bin stolzer Kastilier und enger Vertrauter unseres verehrten Königs "
				+ "Juans II. Außerdem bin ich ein direkter Nachfahre des berühmten Pedro Ruiz Tafur, welcher bei "
				+ "Cordoba unsere Truppen zum Sieg über die gefürchteten Mauren führte."
				+ "\n\n"
				+ "Auch ich beabsichtige meiner Familie und meinem Titel alle Ehre zu machen. Um dies zu erreichen,"
				+ " habe ich beschlossen, den Waffenstillstand zwischen Kastilien und Granada zu nutzen und bin im "
				+ "Jahr 1435 aufgebrochen, um die Fremde zu erkunden. Meine Abenteuer führten mich nach Italien, "
				+ "Palästina und Ägypten. Doch nun ist es an der Zeit, weiter zu reisen. Mein nächstes Ziel steht fest"
				+ " – Konstantinopel. Sie bekommen die einzigartige Gelegenheit, mich bei diesem Kapitel meiner Reise"
				+ " zu begleiten. Doch Vorsicht! Gefahren lauern an jeder Ecke, sodass jede falsche Entscheidung "
				+ "schnell zur letzten werden kann...";
	}

}
