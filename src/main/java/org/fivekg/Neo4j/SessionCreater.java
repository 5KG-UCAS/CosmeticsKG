package org.fivekg.Neo4j;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SessionCreater {

    private static Driver driver;

    private static Session session;

    static {
        InputStream path = SessionCreater.class.getResourceAsStream("/neo4j");
        BufferedReader reader = new BufferedReader(new InputStreamReader(path));
        String uri = "bolt://localhost/:7687";
        String username = "neo4j";
        String password = "neo4j";
        try {
            uri = reader.readLine();
            username = reader.readLine();
            password = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = GraphDatabase.driver( uri, AuthTokens.basic( username, password) );
        session = driver.session();
    }

    public static Session getSession(){
        return session;
    }

    public void close(){
        session.close();
        driver.close();
    }



}
