package org.fivekg.QuestionGraph;

import java.util.ArrayList;
import java.util.Stack;

import static org.fivekg.QuestionGraph.QGItem.QGItemType.*;

/**
 * 使用邻接矩阵存储问题图
 */
public class QuestionGraph {

    private int[][] graph;

    private ArrayList<QGItem> nodes;

    private ArrayList<Integer> nodeIds;

    private int questionWordIndex = -1;

    private int size;

    public QuestionGraph(int maxSize) {
        this.nodes = new ArrayList<QGItem>();
        this.nodeIds = new ArrayList<Integer>();
        this.graph = new int[maxSize][maxSize];
    }

    public void addNode(QGItem item){
        if (!this.nodeIds.contains(item.getID())) {
            this.nodes.add(item);
            this.nodeIds.add(item.getID());
            this.size++;
            if (item.getType()==question)
                this.questionWordIndex = this.nodeIds.indexOf(item.getID());
        }
    }

    public void addEdge(QGItem item1,QGItem item2){
        this.graph[nodeIds.indexOf(item1.getID())]
                [nodeIds.indexOf(item2.getID())] = 1;
        this.graph[nodeIds.indexOf(item2.getID())]
                [nodeIds.indexOf(item1.getID())] = 1;
    }

    public boolean hasNode(int id){
        return this.nodeIds.contains(id);
    }

    public int getSize() {
        return size;
    }

    /**
     * 删减无意义词
     */
    public void reduce(){
        for (QGItem item : this.nodes){
            int index = nodeIds.indexOf(item.getID());
            if (item.getType()==other){
                int degree = 0;
                for (int i = 0; i < size; i++) {
                    degree+=this.graph[index][i];
                }
                if (degree<=1){
                    //如果度为1直接删除
                    for (int i = 0; i < size; i++) {
                        this.graph[i][index] = 0;
                        this.graph[index][i] = 0;
                    }
                }else if (degree == 2){
                    //度为2则连接相邻
                    int indexs[] = new int[2];
                    int j = 0;
                    for (int i = 0; i < size; i++)
                        if(this.graph[index][i]==1)
                            indexs[j++] = i;
                    for (int i = 0; i < size; i++) {
                        this.graph[i][index] = 0;
                        this.graph[index][i] = 0;
                    }
                    this.graph[indexs[0]][indexs[1]] = 1;
                    this.graph[indexs[1]][indexs[0]] = 1;
                }else {
                    //加到label边上
                    int labelIndex = -1;
                    for (int i = 0; i < size; i++)
                        if(this.graph[index][i]==1&&this.nodes.get(i).getType()==label)
                            labelIndex = i;
                    for (int i = 0; i < size; i++) {
                        if (this.graph[i][index] == 1){
                            this.graph[i][index] = 0;
                            this.graph[index][i] = 0;
                            if (i!=labelIndex){//防止自向边
                                this.graph[i][labelIndex] = 1;
                                this.graph[labelIndex][i] = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public int[][] getGraph() {
        return graph;
    }

    public ArrayList<Integer> getNodeIds() {
        return nodeIds;
    }

    public void printGraph(){
        System.out.println(this.nodes);
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                System.out.print(this.graph[i][j]+"  ");
            }
            System.out.println();
        }
    }

    /**
     * 解析成cypher
     * 先转换成问题树
     * */
    public String parseCypher(){
        String cypher = "";
        System.out.println(this.questionWordIndex);
        if (this.questionWordIndex==-1){
            return "error";
        }
        //问题树，以疑问词为根节点
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(questionWordIndex);
        while (!stack.empty()){
            int i = stack.pop();
            QGItem item = this.nodes.get(i);
            for (int j = 0; j < size; j++) {
                if (this.graph[i][j]==1){
                    stack.push(j);
                    item.addChild(this.nodes.get(j));
                    this.graph[i][j] = 0;
                    this.graph[j][i] = 0;
                }
            }
        }
        //直接递归调用
        cypher = this.nodes.get(questionWordIndex).getCypher();
        return cypher;
    }

    public static void main(String[] args) {
//        "和纪梵希禁忌之吻N4的价格区间相同的YSL口红有哪些？"
        QuestionGraph graph = new QuestionGraph(10);
        QGItem item1 = new QGItem(1,entity,"3506");
        QGItem item2 = new QGItem(2,label,"PriceRange");
        QGItem item3 = new QGItem(3,label,"Lipstick");
        QGItem item4 = new QGItem(4,entity,"1");
        QGItem item5 = new QGItem(5,question,"entity");
        graph.addNode(item1);
        graph.addNode(item2);
        graph.addNode(item3);
        graph.addNode(item4);
        graph.addNode(item5);
        graph.addEdge(item1,item2);
        graph.addEdge(item2,item3);
        graph.addEdge(item3,item4);
        graph.addEdge(item3,item5);
        graph.reduce();
        graph.printGraph();
        graph.parseCypher();


    }
}
