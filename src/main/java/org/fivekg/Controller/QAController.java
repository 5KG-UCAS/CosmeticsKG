package org.fivekg.Controller;

import org.fivekg.KGQA.SubGraphQA;
import org.fivekg.Neo4j.SessionCreater;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QAController {

    private SubGraphQA subGraphQA;
    @Value("${neo4j.uri}")
    private String uri;

    @RequestMapping(value = "/question",method = RequestMethod.POST,produces="application/json")
    @ResponseBody()
    public String Login(@RequestParam("question")  String question){
        JSONObject jsonObject = new JSONObject();
        String cypher = SubGraphQA.Question2Cypher(question);

        if (cypher.equals("error")){
            jsonObject.put("success",false);
            return jsonObject.toString();
        }
        Session session = SessionCreater.getSession();
        jsonObject.put("success",true);
        String type = "";
        JSONArray data = new JSONArray();
        StatementResult result = session.run(cypher);
        while ( result.hasNext() ) {
            Record record = result.next();
            if(record.get("n").type().name().equals("NODE")){
                Node node = record.get("n").asNode();
                if(node.hasLabel("Lipstick")){
                    type = "NODE";
                    JSONObject ls = new JSONObject();
                    ls.put("name",node.get("name").asString());
                    ls.put("brand",node.get("brand").asString());
                    ls.put("price",node.get("price").asString());
                    data.put(ls);
                }else {
                    type = "STRING";
                    data.put(record.get("n").get("name").asString());
                }
            }else if (record.get("n").type().name().equals("STRING")){
                type = "STRING";
                data.put(record.get("n").asString());
            }else if(record.get("n").type().name().equals("INTEGER")){
                type = "INTEGER";
                data.put(record.get("n").asInt());
            }else {
                jsonObject.put("success",false);
                return jsonObject.toString();
            }
        }
        jsonObject.put("type",type);
        jsonObject.put("data",data);
        return jsonObject.toString();
    }
}
