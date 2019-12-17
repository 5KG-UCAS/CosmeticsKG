package QuestionGraph;

import KGQA.SubGraphQA;

public class MatchCypher extends CypherItem{

		private String varName;

		private SubGraphQA.Label label = null;

		private int relNum = 0;

		private String relVarName;

		private int id = -1;

		private ConditionCypher conditionCypher = null;

		private String returnName;

		public MatchCypher(String varName, SubGraphQA.Label label, int relNum, String relVarName, int id, ConditionCypher conditionCypher, String returnName) {
				super(CypherType.match);
				this.varName = varName;
				this.label = label;
				this.relNum = relNum;
				this.relVarName = relVarName;
				this.id = id;
				this.conditionCypher = conditionCypher;
				this.returnName = returnName;
		}

		@Override
		public String toCypher() {
				String labeltext = this.label==null?"":":"+this.label;
				String reltext = this.relNum==0?"":String.format("-[*..%d]-(%s)",relNum,relVarName);
				String conditiontext = "";
				if(this.id!=-1)
					conditiontext+=(" id("+this.varName+")="+this.id);
				if (this.conditionCypher!=null){
						//TODO 添加参数约束
				}
				if (conditiontext.length()!=0){
						conditiontext = "where"+conditiontext;
				}
				String returntext = "with "+returnName;
				String cypher = String.format("match (%s%s)%s %s %s",varName,labeltext,reltext,conditiontext,returntext);
				return cypher;
		}

		public String getVarName() {
				return varName;
		}

		public void setVarName(String varName) {
				this.varName = varName;
		}

		public SubGraphQA.Label getLabel() {
				return label;
		}

		public void setLabel(SubGraphQA.Label label) {
				this.label = label;
		}

		public int getRelNum() {
				return relNum;
		}

		public void setRelNum(int relNum) {
				this.relNum = relNum;
		}

		public String getRelVarName() {
				return relVarName;
		}

		public void setRelVarName(String relVarName) {
				this.relVarName = relVarName;
		}

		public int getId() {
				return id;
		}

		public void setId(int id) {
				this.id = id;
		}

		public QuestionGraph.ConditionCypher getConditionCypher() {
				return conditionCypher;
		}

		public void setConditionCypher(QuestionGraph.ConditionCypher conditionCypher) {
				this.conditionCypher = conditionCypher;
		}

		public String getReturnName() {
				return returnName;
		}

		public void setReturnName(String returnName) {
				this.returnName = returnName;
		}
}
