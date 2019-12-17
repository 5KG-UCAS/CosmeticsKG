package CCG;
enum SyntaxItem{
    S,N,NP,ADJ,PP
}
/**
 * Syntax  句法类型
 * 可以自动解析句法串
 * huchuan 19/12/15
 * */
public class Syntax {

    static final int LEFT = -1;
    static final int TERM =  0;
    static final int RIGHT=  1;

    private int combineDirt;

    private Syntax condition;

    private Syntax result;

    private SyntaxItem item;

    //非终结符
    public Syntax(int combineDirt, Syntax condition, Syntax result) {
        this.combineDirt = combineDirt;
        this.condition = condition;
        this.result = result;
    }

    //终结符
    public Syntax(SyntaxItem item) {
        this.combineDirt = Syntax.TERM;
        this.item = item;
    }

    static SyntaxItem prase(String s){
        return SyntaxItem.valueOf(s);
    }

    /**
     * CCG句法类型解析
     * 递归调用
     * eg:(S\NP)/NP
     * */
    public static Syntax parse(String s) {
        if(s.charAt(0)=='('&&s.charAt(s.length()-1)==')'){
            s = s.substring(1,s.length()-1);
        }
        if(s.contains("/")||s.contains("\\")){
            int r = s.lastIndexOf('/');
            int l = s.lastIndexOf('\\');
            int op = Integer.max(r,l);
            int combineDirt = r>l?Syntax.RIGHT:Syntax.LEFT;
            String s1 = s.substring(0,op);
            String s2 = s.substring(op+1);
            return new Syntax(combineDirt,parse(s2),parse(s1));
        }else {
            return new Syntax(prase(s));
        }
    }

    @Override
    public String toString() {
        if(this.combineDirt==Syntax.TERM) {
            return this.item.name();
        } else {
            return "("+this.result.toString()+(this.combineDirt==Syntax.LEFT?"\\":"/")+this.condition.toString()+")";
        }

    }
}
