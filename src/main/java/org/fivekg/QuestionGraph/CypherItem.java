package org.fivekg.QuestionGraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CypherItem{

	String cypher = "";

	String returnVar = "";

	public CypherItem(String cypher, String returnVar) {
		this.cypher = cypher;
		this.returnVar = returnVar;
	}

	public static CypherItem praseMatch(String returnVar, String label, HashMap<String,Integer> ents, HashMap<String,String> attrs){
		String labeltext = label==null?"":":"+label;
		String reltext = "";
		if (ents!=null){
			Iterator<Map.Entry<String,Integer>> entsI = ents.entrySet().iterator();
			while(entsI.hasNext()){
				Map.Entry entry = (Map.Entry) entsI.next();
				Object key = entry.getKey();
				Object num = entry.getValue();
				reltext += String.format("(%s%s)-[*..%d]-(%s),",returnVar,labeltext,num,key);
			}
		}

		String pattern = reltext.length()>0?
				reltext.substring(0,reltext.length()-1)
				:String.format("(%s%s)",returnVar,labeltext);

		String conditiontext = "";
		if (attrs!=null) {
			Iterator<Map.Entry<String, String>> attrsI = attrs.entrySet().iterator();
			while (attrsI.hasNext()) {
				Map.Entry entry = (Map.Entry) attrsI.next();
				Object attr = entry.getKey();
				Object value = entry.getValue();
				if (attr.equals("id")) {
					conditiontext += (" id(" + returnVar + ")=" + value);
				} else {
					conditiontext += String.format("%s.%s = %s", returnVar, attr, value);
				}
				conditiontext += " and ";
			}
		}
		if (conditiontext.length()>0){
			conditiontext = "where " + conditiontext.substring(0,conditiontext.length()-4);
		}

		String returntext = "with "+ returnVar;
		String cypher = String.format("match %s %s %s",pattern,conditiontext,returntext);
		return new CypherItem(cypher,returnVar);
	}

	public static CypherItem praseWith(String inVar,String returnVar){
		String cypher = String.format("with %s as %s",inVar,returnVar);
		return new CypherItem(cypher,returnVar);
	}

	public static CypherItem praseAttr(String key, String value){
		return new CypherItem(key,value);
	}

	public static CypherItem praseValue(String value){
		return new CypherItem("",value);
	}

	public static void main(String[] args) {
		System.out.println(CypherItem.praseMatch("A",null,null,null).cypher);
		HashMap<String,Integer> ents = new HashMap<>();
		HashMap<String,String> attrs = new HashMap<>();
		ents.put("A2",1);
		attrs.put("id","123");
		System.out.println(CypherItem.praseMatch("A","kouhong",ents,attrs).cypher);
		ents.put("A4",2);
		attrs.put("color","red");
		System.out.println(CypherItem.praseMatch("A","kouhong",ents,attrs).cypher);
	}



}
