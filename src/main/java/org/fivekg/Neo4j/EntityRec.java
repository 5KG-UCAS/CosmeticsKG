package org.fivekg.Neo4j;

import com.hankcs.hanlp.dictionary.CustomDictionary;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
@Component
public class EntityRec {

    private static SessionCreater sessionCreater;

    public enum Label{
        Brand,Country,Lipstick,LipstickType,Perfume,PriceRange,Color,Comment
    }
    public enum Attr{
        chiName,code,price,size
    }
    public enum Return{
        count,entity,attr
    }

    private static HashMap<String,Long> string2Entity = new HashMap<String, Long>();
    public static HashMap<String, Return> question_word = new HashMap<>();
    public static HashMap<String, Label> label_word = new HashMap<>();
    public static HashMap<String, Attr> attr_word = new HashMap<>();

    static {
        question_word.put("哪些",Return.entity);
        question_word.put("什么",Return.attr);
        question_word.put("多少",Return.count);

        label_word.put("口红", Label.Lipstick);
        label_word.put("香水", Label.Perfume);
        label_word.put("系列", Label.LipstickType);
        label_word.put("国家", Label.Country);
        label_word.put("品牌", Label.Brand);
        label_word.put("价格区间", Label.PriceRange);
        label_word.put("色系", Label.Color);
        label_word.put("颜色", Label.Color);
        label_word.put("评价", Label.Comment);
        label_word.put("印象", Label.Comment);


        attr_word.put("名字", Attr.chiName);
        attr_word.put("色号", Attr.code);
        attr_word.put("价格", Attr.price);
        attr_word.put("容量", Attr.size);

        Session session = SessionCreater.getSession();
        int count = 0;
        for (Label l : Label.values()){
            String c = String.format("match (n:%s) return n",l.name());
            StatementResult result =session.run(c);
            while (result.hasNext()){
                Record r = result.next();
                string2Entity.put(r.get("n").get("name").asString(), r.get("n").asNode().id());
                if(l!=Label.PriceRange)
                    CustomDictionary.insert(r.get("n").get("name").asString(), "N 1000");

                if(l==Label.Brand||l==Label.Country||l==Label.PriceRange)
                    CustomDictionary.insert(r.get("n").get("name").asString(), "ADJ 800");
                if(l==Label.Brand) {
                    string2Entity.put(r.get("n").get("engName").asString(), r.get("n").asNode().id());
                    CustomDictionary.insert(r.get("n").get("engName").asString(), "N 1000");
                    CustomDictionary.insert(r.get("n").get("engName").asString(), "ADJ 800");
                }
                count++;
            }
        }
        System.out.println("add word "+count);
        System.out.println(string2Entity.size());
    }

    public static boolean hasEntity(String name){
        return string2Entity.containsKey(name);
    }

    public static long NER(String name){
        return string2Entity.get(name);
    }

    public static HashMap<String, Return> getQuestion_word() {
        return question_word;
    }

    public static HashMap<String, Label> getLabel_word() {
        return label_word;
    }

    public static HashMap<String, Attr> getAttr_word() {
        return attr_word;
    }

}
