package org.fivekg.QuestionGraph;


import org.fivekg.Neo4j.EntityRec;

import java.util.ArrayList;
import java.util.HashMap;

public class QGItem {

    public enum QGItemType{
        entity,label,attr,rel,value,other,question
    }

    private int ID;

    private QGItemType type;

    private String value;

    private CypherItem cypherItem;

    private ArrayList<String> cyphers = new ArrayList<>();

    private ArrayList<QGItem> childs;

    public QGItem(int ID, QGItemType type, String value) {
        this.ID = ID;
        this.type = type;
        this.value = value;
        this.childs = new ArrayList<QGItem>();
    }

    public String getCypher(){
        this.createCypherItem();
        for (int i = 1; i < this.cyphers.size()-1; i++) {
            int index = this.cyphers.get(i-1).lastIndexOf("with");
            this.cyphers.set(i,
                    this.cyphers.get(i)+","+this.cyphers.get(i-1).substring(index+5));
        }
        String cypher = "";
        for (String line : this.cyphers){
            cypher += line;
            cypher += "\n";
        }
        return cypher;
    }

    public CypherItem createCypherItem(){
        //如果是叶子节点
        if (this.childs.size()==0){
            switch (this.type){
                case entity:
                    String varName = "V"+this.getID();
                    HashMap<String,String> attrs = new HashMap<>();
                    attrs.put("id",this.value);
                    this.cypherItem = CypherItem.praseMatch(varName,null,null,attrs);
                    this.cyphers.add(this.cypherItem.cypher);
                    return this.cypherItem;
                case label:
                    String varName2 = "V"+this.getID();
                    this.cypherItem = CypherItem.praseMatch(varName2,this.value,null,null);
                    this.cyphers.add(this.cypherItem.cypher);
                    return this.cypherItem;
                case value:
                    this.cypherItem = CypherItem.praseValue(this.value);
                    break;
                default:
                    return null;
            }
        }
        else {//非叶子节点
            if (this.type==QGItemType.attr){
                String varName = "V"+this.getID();
                QGItem child = this.childs.get(0);
                CypherItem childItem = child.createCypherItem();
                this.cyphers.addAll(child.cyphers);
                switch (child.type){
                    case value:
                        this.cypherItem = CypherItem.praseAttr(this.value, child.cypherItem.returnVar);
                        return this.cypherItem;
                    case entity:
                    case label:
                        this.cypherItem = CypherItem.praseWith(childItem.returnVar+"."+this.value, varName);
                        this.cyphers.add(this.cypherItem.cypher);
                        return this.cypherItem;
                }
            }else if(this.type==QGItemType.entity){
                String varName = "V"+this.getID();
                HashMap<String,String> attrs = new HashMap<>();
                attrs.put("id",this.value);
                this.cypherItem = CypherItem.praseMatch(varName,null,null,attrs);
                this.cyphers.add(this.cypherItem.cypher);
                return this.cypherItem;
            } else if (this.type==QGItemType.label){
                String label = this.value;
                HashMap<String,Integer> ents = new HashMap<>();
                HashMap<String,String> attrs = new HashMap<>();
                String varName = "V"+this.getID();
                for (QGItem child : childs) {
                    CypherItem childItem = child.createCypherItem();
                    this.cyphers.addAll(child.cyphers);
                    switch (child.type){
                        case attr:
                            attrs.put(child.value,childItem.returnVar);
                            break;
                        case entity:
                        case label:
                            ents.put(childItem.returnVar,1);//TODO distance
                            break;
                    }
                }
                this.cypherItem = CypherItem.praseMatch(varName,label,ents,attrs);
                this.cyphers.add(this.cypherItem.cypher);
                return this.cypherItem;
            }else if (this.type==QGItemType.question){
                QGItem child = this.childs.get(0);
                CypherItem childItem = child.createCypherItem();
                String key = childItem.returnVar;
                this.cyphers.addAll(child.cyphers);
                switch (EntityRec.Return.valueOf(this.value)){
                    case count:
                        this.cyphers.add("return count( DISTINCT "+key + ") as n");
                        break;
                    case attr:
                    case entity:
                        this.cyphers.add("return DISTINCT "+key + " as n limit 10");
                        break;
                }

            }else {
                return null;
            }
        }
        return null;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public QGItemType getType() {
        return type;
    }

    public void setType(QGItemType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addChild(QGItem item){
        this.childs.add(item);
    }

    public ArrayList<QGItem> getChilds() {
        return childs;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",this.value, this.type.name());
    }


}
