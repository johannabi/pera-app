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
	
	protected void init(String initText) {
		logger.info("Initialize text output...");
		
		setMargin();
		setLayoutData();

		setColors();		
		configText(true, initText);
	}
	
	private void setColors() {
		setBackground(parent.getBackground()/*Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA)*/);
		setForeground(parent.getForeground());
	}
	
	private void configText(boolean justify, String initText) {
		setEditable(false);
		setJustify(justify);		
		setFont(parent.getFont(/*SWT.ITALIC*/SWT.NORMAL));	// TODO font style
		setText(initText);
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

	

}
