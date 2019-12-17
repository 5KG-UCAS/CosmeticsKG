package QuestionGraph;

import KGQA.SubGraphQA.Label;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class CypherItem {
		enum CypherType{
				match,condiction,key,value
		}
		private CypherType type;

		public CypherItem(CypherType type) {
				this.type = type;
		}

		public abstract String toCypher();

}
