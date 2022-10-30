package com.ejavier.simpleDM;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataMask {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String arg[]) {
		
		String record = "XXXXXXXXXXYYYYYYYYYY0000012345QQQQQQQQQQAAAAAAAAAA";
		
		
		System.out.println("INPUT RECORD:" + record);
		System.out.println("INPUT LENGTH:" + record.length());
		
		DataMask dm = new DataMask();
		HashMap<String, Object> map = dm.loadXML();
		
		ArrayList<DmField> list = (ArrayList)map.get("FIELDS");
		
		
		
		dm.loadData(record, list);
		
		dm.logDmFields(list);
		
		//dm.logData(map);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		
		
		dm.maskValue(list);
		
		dm.logDmFields(list);
		
		System.out.println("OUTPUT RECORD:" + dm.constructRecord(list));
		System.out.println("OUTPUT LENGTH:" + dm.constructRecord(list).length());
		
		
	}
	
	
	public String constructRecord(ArrayList<DmField> list) {
		String output = "";
		for(int ctr=0; ctr < list.size(); ctr++) {
			output += list.get(ctr).getValue();
		}
		return output;
	}
	
	
	public void logDmFields (ArrayList<DmField> list) {
		for (int ctr=0; ctr < list.size(); ctr++) {
			
			System.out.println("Field Name  : " + list.get(ctr).getFieldname());
			System.out.println("Length      : " +list.get(ctr).getLength());
			System.out.println("Replacement : " +list.get(ctr).getReplacement());
			System.out.println("Value       : " +list.get(ctr).getValue());
			System.out.println("====================================");
		}
	}

	@SuppressWarnings("rawtypes")
	public void logData (HashMap map) {
			System.out.println("Map  : " + map);
		
	}
	
	
	public HashMap<String, Object> loadXML() {
		
		File xmlFile = new File("/Users/ejavier/eclipse-workspace/simpleDM/src/rwac.xml");

		
		HashMap<String,Object> output = new HashMap<String,Object>(); 
		ArrayList<DmField> fieldsList = new ArrayList<DmField>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			 
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlFile);
			
			doc.getDocumentElement().normalize();
			
			NodeList filenameNode = doc.getElementsByTagName("filename");
			String filename = filenameNode.item(0).getTextContent();
			output.put("INPUT", filename);
			
			
			NodeList list = doc.getElementsByTagName("field");
			
			for (int ctr = 0; ctr < list.getLength(); ctr++) {
				
				Node node = list.item(ctr);
				
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element element = (Element) node;
					
					String fieldName = element.getAttribute("name");
					String fieldLength = element.getAttribute("length");
					String fieldStart = element.getAttribute("start");
					String fieldEnd = element.getAttribute("end");
					String fieldIsStatic = element.getAttribute("isStatic");
					String fieldReplacement = element.getAttribute("replacement");
					
					
					DmField df = new DmField();
					df.setFieldname(fieldName);
					df.setLength(Integer.valueOf(fieldLength));
					//df.setStart(Integer.valueOf(fieldStart));
					//df.setEnd(Integer.valueOf(fieldEnd));
					df.setStatic(Boolean.valueOf(fieldIsStatic));
					df.setReplacement(fieldReplacement);
					
					fieldsList.add(df);
					
				}
				
			}
			
			output.put("FIELDS", fieldsList);
			
			
		} catch (Exception e) {
				e.printStackTrace();
		}
		return output;
		
	}
	
	
	public ArrayList<DmField> loadData(String record, ArrayList<DmField> dmField){
		
		int currentPosition = 0;
		
		for (int ctr=0; ctr < dmField.size(); ctr++) {
			
			String fieldValue = record.substring(currentPosition, currentPosition += dmField.get(ctr).getLength());
			dmField.get(ctr).setValue(fieldValue);
			
		}
		
		return dmField;
	
	}
	
	@SuppressWarnings("rawtypes")
	public ArrayList maskValue(ArrayList dmFields){
		
		for (int ctr=0; ctr < dmFields.size(); ctr++) {
			
			String replacement = ((DmField)dmFields.get(ctr)).getReplacement();
			String originalValue = ((DmField)dmFields.get(ctr)).getValue();
			if(replacement!=null && !replacement.equals("")) {
				
				int sizeDiff = -1;
				
				if(replacement.length() <= originalValue.length())
					sizeDiff = originalValue.length() - replacement.length();
				
				
				if (sizeDiff >= 0) {
					
					((DmField)dmFields.get(ctr)).setValue(replacement + this.pad(sizeDiff, " "));
				}
			}
		}
		
		return dmFields;
	}
	
	public String pad(int len, String character) {
		String output = "";
		for (int ctr =0; ctr < len; ctr++)
			output+=character;
		return output;
	}
	
	
}

