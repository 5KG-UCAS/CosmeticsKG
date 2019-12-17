package QuestionGraph;


import KGQA.SubGraphQA;

import java.util.ArrayList;
import java.util.HashMap;

import static KGQA.SubGraphQA.Label.Unknow;

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
        return cyphers.toString();
    }

    public CypherItem createCypherItem(){
        //如果是叶子节点
        if (this.childs.size()==0){
            switch (this.type){
                case entity:
                    String varName = "V"+this.getID();
                    this.cypherItem = new MatchCypher(varName,null,0,null,
                            Integer.valueOf(this.value),null,varName);
                    this.cyphers.add(this.cypherItem.toCypher());
                    return this.cypherItem;
                case label:
                    String varName2 = "V"+this.getID();
                    this.cypherItem = new MatchCypher(varName2, SubGraphQA.Label.valueOf(this.value),0,null,
                            -1,null,varName2);
                    this.cyphers.add(this.cypherItem.toCypher());
                    return this.cypherItem;
                case value:
                    //TODO
                    break;
                default:
                    return null;
            }
        }else {
            for (QGItem child :
                    childs) {
                CypherItem childItem = child.createCypherItem();
                this.cyphers.addAll(child.cyphers);
                switch (this.type){
                    case attr:
                        switch (child.type){
                            case value:
                                break;//TODO
                            case entity:
                                ((MatchCypher)childItem).setReturnName(((MatchCypher)childItem).getReturnName()+"."+this.value);
                                this.cyphers.remove(this.cyphers.size()-1);
                                return childItem;
                            case label:
                                ((MatchCypher)childItem).setReturnName(((MatchCypher)childItem).getReturnName()+"."+this.value);
                                this.cyphers.remove(this.cyphers.size()-1);
                                return childItem;
                        }
                    case label:
                        switch (child.type){
                            case attr:
                                //TODO
                                break;
                            case entity:
                                String varName = "V"+this.getID();
                                CypherItem thisItem = new MatchCypher(varName, SubGraphQA.Label.valueOf(this.value),2,
                                        ((MatchCypher)child.cypherItem).getReturnName(),-1,null,varName);
                                this.cyphers.add(thisItem.toCypher());

                        }
                }

            }
            //TODO 融合
            return this.childs.get(0).createCypherItem();
        }
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
