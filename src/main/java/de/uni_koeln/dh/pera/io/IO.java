package de.uni_koeln.dh.pera.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.uni_koeln.dh.pera.data.story.Choice;
import de.uni_koeln.dh.pera.data.story.Element;
import de.uni_koeln.dh.pera.data.story.Node;
import de.uni_koeln.dh.pera.data.story.Wrapper;



public class IO {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public static List<Node> readJson(File json) throws IOException {
		BufferedReader bfr = getReader(json);

		StringBuilder sb = new StringBuilder();
		String curLine;
		
		while ((curLine = bfr.readLine()) != null) 
			sb.append(curLine);
	
		bfr.close();
		
		String content = sb.toString();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Wrapper wrapper = mapper.readValue(content, Wrapper.class);
		
		List<Node> nodes = wrapper.getList();

		return nodes;
	}
	
	
	private static BufferedReader getReader(File file) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		return new BufferedReader(isr);
	}

}
