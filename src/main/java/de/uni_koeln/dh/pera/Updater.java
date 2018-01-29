package de.uni_koeln.dh.pera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_koeln.dh.pera.data.Player;
import de.uni_koeln.dh.pera.data.story.Access;
import de.uni_koeln.dh.pera.data.story.Choice;
import de.uni_koeln.dh.pera.data.story.Element;
import de.uni_koeln.dh.pera.data.story.Node;
import de.uni_koeln.dh.pera.gui.core.img.ImgComposite;
import de.uni_koeln.dh.pera.gui.core.text.TextComposite;
import de.uni_koeln.dh.pera.gui.core.text.TextInput;
import de.uni_koeln.dh.pera.gui.core.text.TextOutput;
import de.uni_koeln.dh.pera.util.Serializer;
/**
 * this class manages the updating of all field that react on an
 * input in the text field (text output, text input (map))
 * @author Johanna
 *
 */
public class Updater {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private TextComposite txtComp; // TODO delete?
	private ImgComposite imgComp;
	private TextOutput output;
	private TextInput input;
	private Map<Integer, Node> nodes = null;
	private Player player;

	public Updater(TextComposite txtComp, ImgComposite imgComp, TextOutput output) {
		this.txtComp = txtComp;
		this.imgComp = imgComp;
		this.output = output;
		this.nodes = txtComp.getNodes();
		this.player = txtComp.getPlayer();
	}

	public void setInput(TextInput input) {
		this.input = input;
	}

	/**
	 * reads the value of the text input and creates the 
	 * referred output (e.g. text output of the next node)
	 */
	public void updateFields() {

		try {
			// game status before text input
			int currentID = txtComp.getCurrentID();
			Node node = nodes.get(currentID);
			String currentChapter = node.getChapter();
			Boolean notifyInventory = false; // variable is set to true every time the inventory is updated (added,
												// deleted,...)
			if (node.getClearinventory()) // all things in inventory are deleted
				notifyInventory = deleteElements();
			if (!node.getElements().isEmpty()) // node contains elements to collect
				notifyInventory = addElements(node);
			String in = input.getText().toLowerCase().trim();

			//// proof text input ////
			// special cases
			if (in.equals("help")) {
				String currentText = buildNodeText(node);
				String help = "save: speichert am letzten Kapitel und schließt das Spiel\n"
						+ "inventory: zeigt das Inventar an\n"
						+ "about: zeigt das Impressum an\n\n";
				output.setText(help + currentText);
				input.setText("");
				return;
			}
			
			if (in.equals("demo")) {
				player = Serializer.deserializeDemo(player);
				currentID = player.getCurrentChapterNode();

				node = nodes.get(currentID);
				currentChapter = node.getChapter();
				notifyInventory = false; // variable is set to true every time the inventory is updated (added,
													// deleted,...)
				if (node.getClearinventory()) // all things in inventory are deleted
					notifyInventory = deleteElements();
				if (!node.getElements().isEmpty()) // node contains elements to collect
					notifyInventory = addElements(node);

				output.setText(buildNodeText(node));
				input.setText("");
				return;
			}

			if (in.equals("save")) {
				// config-file will not be deleted when closing application
				System.exit(0);
			}

			if (in.equals("about")) {
				String currentText = buildNodeText(node);
				String credits = "Credits\n"
						+ "Johanna Binnewitt\n"
						+ "Stefan Krause\n"
						+ "Anne-K. Pietsch\n"
						+ "Universität zu Köln\n\n";
				output.setText(credits + currentText);
				input.setText("");
				return;
			}

			if (in.equals("inventory")) { // show inventory
				String newText = getInventory(output.getText(), player);
				output.setText(newText);
				input.setText("");
				return;
			}

			Node updateNode = null;
			// regular cases
			if (node.getOpenanswer()) { // behandelt freie Antwort

				String correct = node.getChoices().get(0).getChoice();
				if (correct.equals(in)) {
					currentID = node.getChoices().get(0).getNext();
					updateNode = nodes.get(currentID);
					output.setText(buildNodeText(updateNode));
				} else {
					String prevText = buildNodeText(node);
					prevText = "Da musst du wohl noch mal genauer schauen!\n\n" + prevText;
					output.setText(prevText);
				}
			} else { // behandelt Auswahl-Antwort

				Integer input = Integer.parseInt(in);
				int nextID = node.getChoices().get(input - 1).getNext();
				List<Access> accesses = node.getChoices().get(input - 1).getAccesses();
				if (!accesses.isEmpty()) { // if elements are required for choice
					List<String> inventory = player.getInventory();
					for (Access access : accesses) {
						inventory.remove(access.getAccess());
					}
					player.setInventory(inventory);
					notifyInventory = true;
				}
				if (nextID == 999) { // exit
					Serializer.deleteFiles();
					System.exit(0);
				}
				if (currentID > nextID) { // try again
					player = Serializer.deserialize(player);
					txtComp.setPlayer(player);// TODO delete?
					imgComp.setPlayer(player);
					logger.info("Deserialized game status " + currentChapter);
				}
				currentID = nextID;
				imgComp.setCurrentID(currentID);
				txtComp.setCurrentID(currentID);
				updateNode = nodes.get(currentID);
				output.setText(buildNodeText(updateNode));

			}
			
			if(notifyInventory) {
				String inventory = getInventory(buildNodeText(updateNode), player);
				output.setText("Inventar aktualisiert: \n" + inventory);
			}

			String nextChapter = nodes.get(currentID).getChapter();
			if (!currentChapter.equals(nextChapter)) {
				Serializer.serialize(currentID, player);
				logger.info("Serialized game status " + currentChapter);
			}

			imgComp.updateAll();

		} catch (NumberFormatException e) {
			String prevText = buildNodeText(nodes.get(txtComp.getCurrentID()));
			prevText = "Bitte gib nur die voranstehende Zahl ein.\n\n" + prevText;
			output.setText(prevText);
		} catch (IndexOutOfBoundsException e) {
			String prevText = buildNodeText(nodes.get(txtComp.getCurrentID()));
			prevText = "Die eingegebene Auswahl ist nicht vorhanden.\n\n" + prevText;
			output.setText(prevText);
		} catch (IOException e) {
			e.printStackTrace();
		}
		input.setText("");

	}

	/**
	 * creates the text for the first node (might be the
	 * saved node, too)
	 * @param player
	 * @return
	 */
	public String getInitialText(Player player) {

		Node node = nodes.get(player.getCurrentChapterNode());
		return buildNodeText(node);
		
	}

	private String buildNodeText(Node node) {
		List<Choice> choices = node.getChoices();
		StringBuilder sb = new StringBuilder();

		sb.append(node.getText() + "\n\n");
		if (!node.getOpenanswer()) {
			Choice currentChoice;
			for (int i = 0; i < choices.size(); i++) {
				currentChoice = choices.get(i);
				if (currentChoice.getAccesses().isEmpty())
					sb.append((i + 1) + ": " + currentChoice.getChoice() + "\n");
				else {
					List<Access> accesses = currentChoice.getAccesses();
					boolean hasAccess = true;
					for (Access access : accesses) {
						if (!player.getInventory().contains(access.getAccess()))
							hasAccess = false; // if user doesn't have all elements that are required

					}
					if (hasAccess)
						sb.append((i + 1) + ": " + currentChoice.getChoice() + "\n");
				}
			}
		}

		return sb.toString();
	}

	private String getInventory(String currentText, Player player) {
		if (!player.getInventory().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String icon : player.getInventory()) {
				sb.append(icon + "\n"); //TODO format inventory output
			}
			sb.append("\n\n" + currentText);
			return sb.toString();
		} else {

			return "Dein Inventar ist leer!\n\n" + currentText;
		}
	}

	private Boolean addElements(Node node) {
		List<Element> elements = node.getElements();
		for (Element element : elements) {
			player.addIcon(element.getElement());
		}	
		return true;
	}

	private Boolean deleteElements() {
		player.setInventory(new ArrayList<String>());

		return true;
	}

}
