package CCG;
public class Syntax {

    static final int LEFT = -1;
    static final int NULL =  0;
    static final int RIGHT=  1;

    private int combineDirt;

    private Syntax condition;

    private Syntax result;

    private SyntaxItem item;

    public Syntax(int combineDirt, Syntax condition, Syntax result) {
        this.combineDirt = combineDirt;
        this.condition = condition;
        this.result = result;
    }

    public Syntax(SyntaxItem item) {
        this.combineDirt = Syntax.NULL;
        this.item = item;
    }
    /**
     * CCG句法类型解析
     * eg:(S\NP)/NP
     * */
    public Syntax parse(String s) {

    }
}
