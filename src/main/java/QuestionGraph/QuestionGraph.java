package QuestionGraph;

import java.util.ArrayList;

import static QuestionGraph.QGItemType.*;

/**
 * 使用邻接矩阵存储问题图
 */
public class QuestionGraph {

    private int[][] graph;

    private String questionWord;

    private ArrayList<QGItem> nodes;

    private ArrayList<Integer> nodeIds;

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
            if (item.getType()==QGItemType.other){
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

    public void printGraph(){
        System.out.println(this.nodes);
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                System.out.print(this.graph[i][j]+"  ");
            }
            System.out.println();
        }
    }
}
