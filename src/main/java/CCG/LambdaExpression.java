package CCG;
/**
 * Lambda 入-算子
 * 可自动解析字符串
 * huchuan 19/12/15
 */
enum LambdaType{
    CONS,VAR,FUC,APP,FUCBODY
}
enum Var{
    x,y
}
enum AON{
    and,or,not
}
public class LambdaExpression {
    //表达式类型
    private LambdaType type;

    //常数值
    private String value;

    //变量
    private Var var;

    //函数变量集合
    private Var[] vars;

    //函数内容
    private LambdaExpression func;

    //函数体名
    private String funcName;

    //函数体第一形参
    private LambdaExpression x;

    //函数体第二形参
    private LambdaExpression y;

    //应用左侧表达式
    private LambdaExpression left;

    //应用的操作符
    private AON ope;

    //应用右侧表达式
    private LambdaExpression right;

    //常数构造函数
    public LambdaExpression(String value) {
        this.type = LambdaType.CONS;
        this.value = value;
    }

    //变量构造函数
    public LambdaExpression(Var var) {
        this.type = LambdaType.VAR;
        this.var = var;
    }

    //函数构造函数
    public LambdaExpression(Var[] vars, LambdaExpression func) {
        this.type = LambdaType.FUC;
        this.vars = vars;
        this.func = func;
    }

    //函数体
    public LambdaExpression(String funcName, LambdaExpression x, LambdaExpression y) {
        this.type = LambdaType.FUCBODY;
        this.funcName = funcName;
        this.x = x;
        this.y = y;
    }

    //应用
    public LambdaExpression(LambdaExpression left, AON ope, LambdaExpression right) {
        this.type = LambdaType.APP;
        this.left = left;
        this.ope = ope;
        this.right = right;
    }


}

class Constant extends LambdaExpression{
    public Constant(String value) {
        super(value);
    }
}

class Variable extends LambdaExpression{
    public Variable(Var var) {
        super(var);
    }
}

class Function extends LambdaExpression{
    public Function(Var[] vars, LambdaExpression func) {
        super(vars, func);
    }
}

class FunctionBody extends LambdaExpression{
    public FunctionBody(String funcName, LambdaExpression x, LambdaExpression y) {
        super(funcName, x, y);
    }
}

class Application extends LambdaExpression{
    public Application(LambdaExpression left, AON ope, LambdaExpression right) {
        super(left, ope, right);
    }
}



