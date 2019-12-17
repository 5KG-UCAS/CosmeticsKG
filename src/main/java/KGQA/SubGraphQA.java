package KGQA;

import QuestionGraph.QGItem;
import QuestionGraph.QGItem.QGItemType;
import QuestionGraph.QuestionGraph;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import java.io.IOException;
import java.util.*;


public class SubGraphQA {

    public enum Label{
        Unknow,Brand,Country,Lipstick,LipstickType,Perfume,PriceRange
    }
    public enum Attr{
        chiName,code,price,size
    }
    public enum Return{
        count,entity,attr,error
    }

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

        attr_word.put("名字", Attr.chiName);
        attr_word.put("色号", Attr.code);
        attr_word.put("价格", Attr.price);
        attr_word.put("容量", Attr.size);
    }

    public static String format(String s){
        String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    /**
     * @param question 自然语言问题
     * @return 搜索结果
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
    public static String QA(String question){
        //TODO NER
        HashMap<String,Integer> NER = new HashMap<>();
        NER.put("纪梵希禁忌之吻N4",3506);
        NER.put("YSL",1);
        NER.put("300左右",2);

        question = format(question);
        CoNLLSentence coNLLSentence = HanLP.parseDependency(question);
        for (CoNLLWord word : coNLLSentence){
            //实体链接
//            System.out.printf("%s------%s",word.LEMMA,word.HEAD.LEMMA);
            if (NER.containsKey(word.LEMMA)){
                word.LEMMA = NER.get(word.LEMMA).toString();
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
        graph.printGraph();
        return "";
    }

    public static void main(String[] args) throws IOException {

        List<String> questions = new ArrayList<String>();
        questions.add("和纪梵希禁忌之吻N4的价格区间相同的YSL口红有哪些？");
        questions.add("纪梵希禁忌之吻N4的品牌是什么？");
        questions.add("纪梵希禁忌之吻N4的价格区间是什么");
        questions.add("纪梵希禁忌之吻N4的价格是什么");
        questions.add("YSL的口红一共有多少");
        questions.add("YSL的口红系列一共有多少");
        questions.add("300左右的YSL口红有哪些");
        questions.add("有哪些YSL口红在300左右");
        questions.add("YSL口红在300左右的有哪些");
        for (String question : questions){
            SubGraphQA.QA(question);
        }



    }
}

