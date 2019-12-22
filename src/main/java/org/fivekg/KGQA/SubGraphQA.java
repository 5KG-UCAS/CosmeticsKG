package org.fivekg.KGQA;

import org.fivekg.Neo4j.EntityRec;
import org.fivekg.QuestionGraph.QGItem;
import org.fivekg.QuestionGraph.QGItem.QGItemType;
import org.fivekg.QuestionGraph.QuestionGraph;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class SubGraphQA {


    public static String format(String s){
        String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    /**
     * @param question 自然语言问题
     * @return 查询语句
     * 步骤：
     *      0.句法分析
     *      1.实体链接
     *      2.抽象实体标注
     *      3.属性标注
     *      4.问题词语标注
     *      5.句法树删减
     *      6.问题图生成
     *      7.问题图删减
     *      8.查询语句转换
     */
    public static String Question2Cypher(String question, HashMap<String,Object> info){
        HashMap<String, EntityRec.Label> label_word = EntityRec.getLabel_word();
        HashMap<String, EntityRec.Attr> attr_word = EntityRec.getAttr_word();
        HashMap<String, EntityRec.Return> question_word = EntityRec.getQuestion_word();
        question = format(question);
        CoNLLSentence coNLLSentence = HanLP.parseDependency(question);
        for (CoNLLWord word : coNLLSentence){
            word.NAME = word.LEMMA;
            //实体链接
//            System.out.printf("%s------%s",word.LEMMA,word.HEAD.LEMMA);
            if (EntityRec.hasEntity(word.LEMMA)){
                word.LEMMA = Long.toString(EntityRec.NER(word.LEMMA));
                word.POSTAG = "entity";
                word.CPOSTAG = "1";
            }
            //抽象实体标注
            if (label_word.containsKey(word.LEMMA)){
                word.LEMMA = label_word.get(word.LEMMA).name();
                word.POSTAG = "label";
                word.CPOSTAG = "1";
            }
            //属性标注
            if (attr_word.containsKey(word.LEMMA)){
                word.LEMMA = attr_word.get(word.LEMMA).name();
                word.POSTAG = "attr";
                word.CPOSTAG = "1";
            }
            //疑问词标注
            if (question_word.containsKey(word.LEMMA)){
                word.LEMMA = question_word.get(word.LEMMA).name();
                word.POSTAG = "question";
                word.CPOSTAG = "1";
            }
            //其他词语
            if (word.CPOSTAG!="1"){
                word.POSTAG = "other";
            }
        }
        info.put("words",coNLLSentence);
        //问题图转换
        QuestionGraph graph = new QuestionGraph(coNLLSentence.getWordArray().length);
        //迭代添加直到不变
        int size = -1;
        while (graph.getSize()!=size) {
            size = graph.getSize();
            for (CoNLLWord word : coNLLSentence) {

                if (word.CPOSTAG == "1" || graph.hasNode(word.ID)) {
                    QGItem i1 = new QGItem(word.ID, QGItemType.valueOf(word.POSTAG), word.LEMMA);
                    graph.addNode(i1);
                    if (word.HEAD != CoNLLWord.ROOT) {
                        QGItem i2 = new QGItem(word.HEAD.ID, QGItemType.valueOf(word.HEAD.POSTAG), word.HEAD.LEMMA);
                        graph.addNode(i2);
                        graph.addEdge(i1, i2);
                    }
                }
            }
        }
        graph.reduce();
        JSONArray graphEdges = new JSONArray();
        int[][] max = graph.getGraph();
        for (int i = 0; i < graph.getSize(); i++) {
            for (int j = i; j < graph.getSize(); j++) {
                if (max[i][j]==1) {
                    JSONObject edge = new JSONObject();
                    edge.put("source",graph.getNodeIds().get(i));
                    edge.put("target",graph.getNodeIds().get(j));
                    graphEdges.put(edge);
                }
            }
        }
        info.put("graph",graphEdges);

        graph.printGraph();
        String cypher = graph.parseCypher();
        System.out.println(cypher);
        return cypher;


    }

//    public static void main(String[] args) throws IOException {
//
//
//
//        List<String> questions = new ArrayList<String>();
//        questions.add("和纪梵希禁忌之吻N4的价格区间相同的YSL口红有哪些？");
//        questions.add("纪梵希禁忌之吻N4的品牌是什么？");
//        questions.add("纪梵希禁忌之吻N4的价格区间是什么?");
//        questions.add("纪梵希禁忌之吻N4的价格是什么？");
//        questions.add("YSL的口红一共有多少？");
//        questions.add("YSL的口红系列一共有多少？");
//        questions.add("300左右的YSL口红有哪些？");
//        questions.add("有哪些YSL口红在300左右？");
//        questions.add("YSL口红在300左右的有哪些？");
//        for (String question : questions){
//            System.out.println(question);
//            System.out.println(SubGraphQA.QA(question, session));
//            System.out.println("");
//        }
//
//    }
}

