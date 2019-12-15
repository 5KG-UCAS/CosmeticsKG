package KGAQ;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.mining.word.WordInfo;
import com.hankcs.hanlp.model.CRFSegmentModel;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubGraphQA {

    public static String format(String s){
        String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    public static void main(String[] args) throws IOException {

        /*
        * 1.实体链接
        *   1.1分词
        *   1.2链接
        * 2.抽象实体标注
        * 3.句法分析
        * 4.句法树转换
        * 5.CCG
        * */
//        String text = "和纪梵希禁忌之吻N4的颜色相同的YSL口红有哪些？";
        String text= "有哪些YSL口红和纪梵希禁忌之吻N4的颜色相同？";
        //---1实体链接
        //去符号
        text = format(text);
        
        CoNLLSentence coNLLSentence = HanLP.parseDependency(text);
        List<CoNLLWord> words = new ArrayList<CoNLLWord>(coNLLSentence.getWordArray().length);
        HashMap<String,String> str2ent = new HashMap();
        for (CoNLLWord word : coNLLSentence) {
            System.out.println(word.LEMMA);
            if (word.CPOSTAG.equals("ent")){
                str2ent.put(word.LEMMA,"ent");
            }
            if (word.LEMMA.equals("YSL")){
                str2ent.put(word.LEMMA,"vent:b");
            }
            if (word.LEMMA.equals("口红")){
                str2ent.put(word.LEMMA,"vent:kh");
            }
            if (word.LEMMA.equals("颜色")){
                str2ent.put(word.LEMMA,"attr:color");
            }
            if (word.LEMMA.equals("相同")){
                str2ent.put(word.LEMMA,"v:same");
            }
            if (word.LEMMA.equals("哪些")){
                str2ent.put(word.LEMMA,"qword");
            }
        }
        System.out.println(str2ent);
        for (CoNLLWord word : coNLLSentence){
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
            if(str2ent.keySet().contains(word.LEMMA)){
                if (!str2ent.keySet().contains(word.HEAD.LEMMA)){
                    word.HEAD = word.HEAD.HEAD;
                }
                words.add(word);
            }
        }
        System.out.println("simple:");
        // 可以方便地遍历它
        for (CoNLLWord word : words)
        {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
//        // 也可以直接拿到数组，任意顺序或逆序遍历
//        CoNLLWord[] wordArray = sentence.getWordArray();
//        for (int i = wordArray.length - 1; i >= 0; i--)
//        {
//            CoNLLWord word = wordArray[i];
//            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
//        }
//        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
//        CoNLLWord head = wordArray[12];
//        while ((head = head.HEAD) != null)
//        {
//            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
//            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
//        }
    }
}

