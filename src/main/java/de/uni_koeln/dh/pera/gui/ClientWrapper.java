package de.uni_koeln.dh.pera.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.data.Player;
import de.uni_koeln.dh.pera.data.story.Node;
import de.uni_koeln.dh.pera.gui.core.img.ImgComposite;
import de.uni_koeln.dh.pera.gui.core.text.TextComposite;
import de.uni_koeln.dh.pera.io.IO;
import de.uni_koeln.dh.pera.util.Calc;
import de.uni_koeln.dh.pera.util.Serializer;

public class ClientWrapper {

		private Logger logger = LoggerFactory.getLogger(getClass());
	
		private static final int H_HEIGHT_PCT = 60;
	
		// parent == shell
		private Composite parent = null;
		
		private Player player = null;
		private Map<Integer, Node> nodes = null;
	
	protected ClientWrapper(Composite parent) {
		this.parent = parent;
		try {
			List<Node> nodeList = IO.readJson(new File("src/main/resources/textadventure/textadventure.json"));
			nodes = new HashMap<Integer, Node>();
			for (Node node : nodeList) {
				nodes.put(node.getId(), node);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		player = Serializer.deserialize(player);
	}
	
	protected void wrap() {
		logger.info("Initialize client area...");
		
		// parent.getClientArea() == inner area
		final int height = parent.getClientArea().height,
				imgCompHeight = (int) Calc.getValByPct(height, H_HEIGHT_PCT),
				txtCompHeight = height - imgCompHeight;			// 40%

		ImgComposite imgComp = new ImgComposite(parent, nodes, player);
		imgComp.init(imgCompHeight);
		
		if (imgComp.isInitialized()) {
			imgComp.addMapComponents();
		
			TextComposite txtComp = new TextComposite(parent, imgComp, nodes, player);
			txtComp.init(txtCompHeight);
			if (txtComp.isInitialized()) txtComp.addTexts();
		}
	}
	
}
