package Server;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class Server {
    private static boolean hasChanged = false;
    private static String currentDatabase = "";
    public static void main(String args[]){

    //    MongoConnector connector = new MongoConnector();
        Timer timer = new Timer();
        TimerTask task = new FileWatcher(new File("Databases/TOSERVER.txt")){
            protected void onChange(File file) throws IOException, InterruptedException {

                int numberOfLines = countLinesInChanged();
                System.out.println(numberOfLines);
                if(numberOfLines == 1){
                    String command = "";
                    command = getDBcommand2();
                    if(command.equals("GET DATABASES")){

                        String[] files = (new File("Structures")).list();

                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        MongoCursor<String> dbsCursor = client.listDatabaseNames().iterator();
                          FileWriter writer = new FileWriter("Databases/TOCLIENT.txt", true);
                            //TODO send to server
                        for(String f : files) {
                            //System.out.println(dbsCursor.next());
                            writer.write(f.split("[.]")[0] + "\n" );
                        }
                        writer.close();
                    }
                }
                else if(numberOfLines == 2){
                    String dbName = "";
                    String command = "";
                    dbName = getDBname();
                    command = getDBcommand();
                    if(command.equals("CREATE DATABASE")){

                        try {
                            FileWriter writer = new FileWriter("Structures/" + dbName + ".txt",false);
                            writer.write("<DataBase dataBaseName=\"" + dbName + "\">\n<Tables>\n</Tables>\n</Databases>");
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection("default");
                        DBObject obj = new BasicDBObject();
                        obj.put("name","..");
                        coll.insert(obj);
                        BasicDBObject del = new BasicDBObject("name","..");
                        coll.remove(del);
                    }
                    else if(command.equals("DROP DATABASE")){
                        File fileToDelete = new File("Structures/" + dbName + ".txt");
                        fileToDelete.delete();
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        database.dropDatabase();

                    }
                    else if(command.equals("GET TABLES")){
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        Set<String> tables = database.getCollectionNames();
                        FileWriter writer = new FileWriter("Databases/TOCLIENT.txt",true);
                        for(String s : tables){
                            writer.write(s + "\n");
                        }
                        writer.close();
                    }


                }else if(numberOfLines == 3){
                    String dbName = "";
                    String command = "";
                    String tableName = "";
                    dbName = getDBname();
                    tableName = getDBTableName();
                    command = getDBcommandWithTable();

                    if(command.equals("CREATE TABLE")){

                        String content = "";
                        String newContent = "";

                        try {
                            content = readFile("Structures/" + dbName + ".txt", StandardCharsets.UTF_8);
                            int index = content.indexOf("<Tables>");
                            newContent = content.substring(0, index + 8) + "\n<Table tableName=\"" + tableName + "\">\n" + "<Structure>\n</Structure>\n</Table>\n" + content.substring(index + 8);
                            try {
                                FileWriter writer = new FileWriter("Structures/" + dbName + ".txt",false);
                                writer.write(newContent);
                                writer.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);
                        DBObject pers = new BasicDBObject();
                        pers.put("name","peter");
                        coll.insert(pers);
                        BasicDBObject del = new BasicDBObject("name","peter");
                        coll.remove(del);
                    }else if(command.equals("DROP TABLE")){

                        String content = readFile("Structures/" + dbName + ".txt", StandardCharsets.UTF_8);

                        int startIndexForIFile = content.indexOf("Table tableName=\"" + tableName) + 17;
                        int endIndexForIFile = content.indexOf("/Table>", startIndexForIFile + 1);
                        String content2 = content.substring(startIndexForIFile, endIndexForIFile);
                        int index = 0;
                        Vector<Integer> indexes = new Vector<Integer>();
                        while(index != -1){
                            index = content2.indexOf("indexName=\"", index);
                            if(index != -1){
                                indexes.add(index);
                                index++;
                            }
                        }
                        for(int i=0;i<indexes.size();i++){
                            indexes.set(i, indexes.get(i) + 11);
                        }
                        Vector<String> files = new Vector<String>();
                        for(int i=0;i<indexes.size();i++){
                            files.add(content2.substring(indexes.get(i), content2.indexOf("\"", indexes.get(i) + 1)));
                        }
                        for(String f : files){
                            System.out.println(f);
                            File fileToDelete = new File("IndexFiles/" + f);
                            fileToDelete.delete();
                        }

                        int startIndex = content.indexOf("Table tableName=\"" + tableName) - 2;
                        int endIndex = content.indexOf("/Table>",
                                content.indexOf("Table tableName=\"" + tableName) + 2) + 7;

                        content = content.substring(0, startIndex + 1) + content.substring(endIndex);

                        try {
                            FileWriter writer = new FileWriter("Structures/" + dbName + ".txt",false);
                            writer.write(content);
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        System.out.println(tableName);
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);
                        coll.drop();
                    }
                    else if(command.equals("GET RECORDS")){
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);
                        DBCursor cursor = coll.find();
                        Vector<String> keyList = new Vector<String>();
                        while(cursor.hasNext()) {
                            for (String key : cursor.next().keySet()) {
                                if(!keyList.contains(key)) {
                                    keyList.add(key);
                                }
                            }
                        }
                        String line ="";
                        for(String word : keyList){
                            line += word + " ";
                        }
                        try {
                            FileWriter writer = new FileWriter("Databases/TOCLIENT.txt", false);
                            writer.write(line);
                            writer.close();
                            //TODO send to server
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        System.out.println(keyList);
                    }
                }else if(numberOfLines == 4){
                    String dbName = "";
                    String command = "";
                    String tableName = "";
                    String[] fields = getAtributes();
                    dbName = getDBname();
                    tableName = getDBTableName();
                    command = getDBcommandWithTable();

                    if(command.equals("GENERATETABLE")){
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);
                        DBObject obj = new BasicDBObject();

                        String content = "";
                        String newContent = "";
                        String finalContent = "";
                        String pkOne = "";
                        Vector<String> fkOnes = new Vector<String>();
                        Vector<String> Uindexes = new Vector<String>();
                        Vector<String> NUindexes = new Vector<String>();
                        Vector<String> refTable = new Vector<String>();
                        Vector<String> refAtr = new Vector<String>();
                        for(String part : fields){
                            String[] splitted = part.split("#");
                            if(splitted[2].substring(0,1).equals("1")){
                                pkOne = splitted[0];
                            }
                            if(splitted[2].substring(1,2).equals("1")){
                                fkOnes.add(splitted[0]);
                                String[] combo = splitted[2].substring(splitted[2].indexOf("(") + 1 ,splitted[2].indexOf(")")).split("[.]");
                                refTable.add(combo[0]);
                                refAtr.add(combo[1]);
                                splitted[2] = splitted[2].substring(0,1) + "1" + splitted[2].substring(splitted[2].length()-2);

                            }
                            if(splitted[2].substring(2,3).equals("1")){
                                Uindexes.add(splitted[0]);
                            }
                            if(splitted[2].substring(3,4).equals("1")){
                                NUindexes.add(splitted[0]);
                            }

                            String type = "1";
                            if(splitted[1].equals("int")) {
                                obj.put(splitted[0], Integer.parseInt(type));
                            }else if(splitted[1].equals("varchar")){
                                obj.put(splitted[0], type);
                            }else if(splitted[1].equals("boolean")){
                                obj.put(splitted[0], Boolean.parseBoolean(type));
                            }else if(splitted[1].equals("double")){
                                obj.put(splitted[0], Double.parseDouble(type));
                            }


                            newContent += "<Attribute attributeName=\"" + splitted[0] + "\" type=\"" + splitted[1]
                                    + "\" specs=\"" + splitted[2] + "\"/>\n";
                        }

                        coll.insert(obj);
                        coll.createIndex(new BasicDBObject("key", 1), "key", true);
                        coll.remove(obj);

                        newContent += "<primaryKey>\n<pkAttribute>" + pkOne + "</pkAttribute>\n</primaryKey>\n<foreignKeys>\n";
                        String FKContent = "";
                        for(int i=0;i<fkOnes.size();i++){
                            FKContent += "<foreignKey>\n<fkAttribute>" + fkOnes.get(i) + "</fkAttribute>\n<references>\n<refTable>" + refTable.get(i) + "</refTable>\n" +
                                    "<refAttribute>" + refAtr.get(i) + "</refAttribute>\n</references>\n</foreignKey>\n";
                        }
                        newContent += FKContent + "</foreignKeys>\n<IndexFiles>\n";

                        for(int i=0;i<Uindexes.size();i++){
                            newContent += "<IndexFile indexName=\"" + dbName + tableName + Uindexes.get(i) + ".ind\" keyLength=\"3\" isUnique = \"1\" indexType=\"BTree\">\n" +
                                   "<IndexAttributes>\n<IAttribute>" + Uindexes.get(i) + "</IAttribute>\n</IndexAttributes>\n</IndexFile>\n";
                        }
                        for(int i=0;i<NUindexes.size();i++){
                            newContent += "<IndexFile indexName=\"" + dbName + tableName + NUindexes.get(i) + ".ind\" keyLength=\"3\" isUnique = \"0\" indexType=\"BTree\">\n" +
                                    "<IndexAttributes>\n<IAttribute>" + NUindexes.get(i) + "</IAttribute>\n</IndexAttributes>\n</IndexFile>\n";
                        }

                        newContent += "</IndexFiles>\n";



                        try {
                            content = readFile("Structures/" + dbName + ".txt", StandardCharsets.UTF_8);
                            int index = content.indexOf("tableName=\"" + tableName) + 11;
                            finalContent = content.substring(0, index + tableName.length() + 14) + "\n" + newContent +
                                            content.substring(index + tableName.length() + 14);
                            try {
                                FileWriter writer = new FileWriter("Structures/" + dbName + ".txt",false);
                                writer.write(finalContent);
                                writer.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }else if(command.equals("FILL")){
                        System.out.println("Filling started!");
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);
                        // THREAD KODRESZLET
                        ExecutorService executor = Executors.newFixedThreadPool(100);
                        int start = 1;
                        int limit = 1001;
                        int plus = 1000;
                        for (int i = 0; i < 100; i++) {
                            // GOTO FILLER CLASS
                            Runnable worker = new Fill100kPart1(start, limit, fields, coll);
                            start = limit;
                            limit += plus;
                            executor.execute(worker);
                        }
                        executor.shutdown();
                        while (!executor.isTerminated()) {
                        }
                        System.out.println("Finished all threads");
                        System.out.println("Whole filling done!");
                    }
                } else if(numberOfLines == 5){
                    System.out.println("5 sor");
                    String dbName = "";
                    String command = "";
                    String tableName = "";
                    String key = "";
                    String value = "";
                    dbName = getDBname();
                    tableName = getDBTableName();
                    command = getDBcommandWithTable();
                    key = getKey();
                    value = getValue();
                    String[] keyList = key.split(" ");
                    String[] valueList = value.split(" ");

                    if(command.equals("INSERT")){
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);
                        DBObject pers = new BasicDBObject();
                        Vector<String> uniqueKeys = new Vector<String>();
                        Vector<String> uniqueValues = new Vector<String>();
                        Vector<String> nuniqueKeys = new Vector<String>();
                        String keyField = "key";
                        String keyValue = valueList[0].substring(1);
                        String valueField = "value";
                        String valueValue = "";
                        for(int i=1;i<keyList.length;i++) {
                            valueValue += valueList[i].substring(1) + "#";
                        }
                        pers.put(keyField, keyValue);
                        pers.put(valueField, valueValue);
                        coll.insert(pers);

                    }
                    else if(command.equals("DELETE RECORDS")){
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);
                        System.out.println("DELETING RECORDS");

                        String content = readFile("Structures/" + dbName + ".txt", StandardCharsets.UTF_8);
                        System.out.println(key);
                        content = content.substring(content.indexOf("tableName=\"" + tableName), content.indexOf(
                                "<primaryKey>", content.indexOf("tableName=\"" + tableName) + 1
                        ));
                        Vector<Integer> startingIndexes = new Vector<Integer>();
                        int index = content.indexOf("attributeName=\"") + 15;
                        while (index >= 0) {
                            startingIndexes.add(index);
                            index = content.indexOf("attributeName=\"", index + 1);
                            if(index == -1)
                                break;
                            else index += 15;
                        }
                        Vector<String> attributes = new Vector<String>();
                        for(int i=0;i<startingIndexes.size();i++){
                            attributes.add(content.substring(startingIndexes.get(i), content.indexOf(
                                    "\"", startingIndexes.get(i) + 1
                            )));
                        }

                        int position = 0;
                        for(int i=0;i<attributes.size();i++){
                            if(attributes.get(i).equals(key)){
                                position = i;
                                break;
                            }
                        }

                        if(position == 0){
                            System.out.println("PRIMARY KEY");
                            coll.remove(new BasicDBObject("key", value));
                        }else{
                            String regex = "";
                            for(int i=0;i<attributes.size() - 1;i++){
                                if(i == position - 1){
                                    regex += value + "#";
                                }else {
                                    regex += "(.*)#";
                                }
                            }
                            DBObject del = new BasicDBObject();
                            del.put("value", java.util.regex.Pattern.compile(regex));
                            coll.remove(del);
                        }
                        System.out.println("DELETING RECORDS DONE");
                        FileWriter writer = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer.write("OK");
                        writer.close();

                    }else if(command.equals("CHECKFK")){
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);

                        List values = coll.distinct("key");
                        Vector<String> values2 = new Vector<String>();
                        for(Object val : values){
                            values2.add(val.toString());
                        }
                        Boolean isMatch = false;
                        for(String val : values2){
                            if(val.equals(value)){
                                isMatch = true;
                            }
                        }
                        String answer = "";
                        if(isMatch){
                            answer = "OK";
                        }else{
                            answer = "NOOK";
                        }

                        FileWriter writer = new FileWriter("Databases/TOCLIENT.txt",false);
                        writer.write(answer);
                        writer.close();
                    }else if(command.equals("BUILDNUI")){
                        System.out.println("Building NUI");
                        String DB = dbName;
                        String TB = tableName;
                        String NUIFIELD = key;
                        String PKFIELD = value;
                        File fileTmp = new File("IndexFiles/" + dbName + tableName + NUIFIELD + ".ind");
                        fileTmp.createNewFile();

                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(DB);
                        DBCollection coll = database.getCollection(TB);
                        DB database2 = client.getDB("Indexes");
                        DBCollection coll2 = database2.getCollection(DB + TB + NUIFIELD + ".ind");
                        coll2.drop();

                        System.out.println("IndexFiles/" + DB + TB + NUIFIELD + ".ind");

                        String content = readFile("Structures/" + dbName + ".txt", StandardCharsets.UTF_8);
                        System.out.println(key);
                        content = content.substring(content.indexOf("tableName=\"" + tableName), content.indexOf(
                                "<primaryKey>", content.indexOf("tableName=\"" + tableName) + 1
                        ));
                        Vector<Integer> startingIndexes = new Vector<Integer>();
                        int index = content.indexOf("attributeName=\"") + 15;
                        while (index >= 0) {
                            startingIndexes.add(index);
                            index = content.indexOf("attributeName=\"", index + 1);
                            if(index == -1)
                                break;
                            else index += 15;
                        }
                        Vector<String> attributes = new Vector<String>();
                        for(int i=0;i<startingIndexes.size();i++){
                            attributes.add(content.substring(startingIndexes.get(i), content.indexOf(
                                    "\"", startingIndexes.get(i) + 1
                            )));
                        }

                        int position = 0;
                        for(int i=0;i<attributes.size();i++){
                            if(attributes.get(i).equals(NUIFIELD)){
                                position = i;
                            }
                        }

                        DBCursor curs = coll.find();
                        HashMap<Integer, String> map = new HashMap<Integer, String>();
                        HashMap<String, Integer> counter = new HashMap<String, Integer>();
                        int count = 0;
                        while(curs.hasNext()){
                            String doc = curs.next().toString();
                            String needToSplit = doc.substring(doc.indexOf("value") + 9, doc.indexOf("\"",
                                    doc.indexOf("value") + 10));
                            String[] splitted = needToSplit.split("#");
                            String val = splitted[position - 1];
                            String keyy = doc.substring(doc.indexOf("key") + 7, doc.indexOf("\"",
                                    doc.indexOf("key") + 8));
                            if(counter.containsKey(val)){
                                map.put(counter.get(val), map.get(counter.get(val)) + "#" + keyy);
                            }else{
                                counter.put(val, count);
                                map.put(counter.get(val), val + "@" + keyy);
                                count++;
                            }
                        }

                        int offset = "key".length() + 4;
                        int limit = 0;
                        int plus = 0;
                        if(map.size() <= 10000){
                            limit = 1000;
                            plus = 1000;
                        }else if(map.size() > 10000 && map.size() <= 500000){
                            limit = 3000;
                            plus = 3000;
                        }else if(map.size() > 50000){
                            limit = 5000;
                            plus = 5000;
                        }
                        int start = 0;
                        ExecutorService executor = Executors.newFixedThreadPool(20);
                        Boolean isDone = false;
                        for (int i = 0; i < 20; i++) {
                            if(limit > map.size()){
                                limit = map.size();
                                isDone = true;
                            }
                            Runnable worker = new NUIBuilder(start, limit, offset, position, attributes, map, coll, coll2);
                            executor.execute(worker);
                            if(isDone) break;
                            start = limit;
                            limit += plus;
                        }
                        executor.shutdown();
                        while (!executor.isTerminated()) {
                        }
                        coll2.createIndex(new BasicDBObject("key", 1), "key", true);
                        client.close();
                        System.out.println("Done building NUI");
                        FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer2.write("OK");
                        writer2.close();

                    }else if(command.equals("BUILDUI")){
                        System.out.println("Building UI");
                        String DB = dbName;
                        String TB = tableName;
                        String UIFIELD = key;
                        File fileTmp = new File("IndexFiles/" + dbName + tableName + UIFIELD + ".ind");
                        fileTmp.createNewFile();

                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(DB);
                        DBCollection coll = database.getCollection(TB);
                        DB database2 = client.getDB("Indexes");
                        DBCollection coll2 = database2.getCollection(DB + TB + UIFIELD + ".ind");
                        coll2.drop();


                        String content = readFile("Structures/" + dbName + ".txt", StandardCharsets.UTF_8);
                        System.out.println(key);
                        content = content.substring(content.indexOf("tableName=\"" + tableName), content.indexOf(
                                "<primaryKey>", content.indexOf("tableName=\"" + tableName) + 1
                        ));
                        Vector<Integer> startingIndexes = new Vector<Integer>();
                        int index = content.indexOf("attributeName=\"") + 15;
                        while (index >= 0) {
                            startingIndexes.add(index);
                            index = content.indexOf("attributeName=\"", index + 1);
                            if(index == -1)
                                break;
                            else index += 15;
                        }
                        Vector<String> attributes = new Vector<String>();
                        for(int i=0;i<startingIndexes.size();i++){
                            attributes.add(content.substring(startingIndexes.get(i), content.indexOf(
                                    "\"", startingIndexes.get(i) + 1
                            )));
                        }

                        int position = 0;
                        for(int i=0;i<attributes.size();i++){
                            if(attributes.get(i).equals(UIFIELD)){
                                position = i;
                            }
                        }
                        System.out.println(position);
                        Vector<String> values = new Vector<String>();
                        Vector<String> pkList = new Vector<>();
                        if(position == 0){
                            List tmp = coll.distinct("key");
                            for(Object t : tmp){
                                values.add(t.toString());
                            }
                        }
                        else {
                            DBCursor curs = coll.find();
                            Boolean debugg = true;
                            while(curs.hasNext()){
                                String doc = curs.next().toString();
                                if(debugg)
                                    System.out.println(doc);
                                debugg = false;
                                String needToSplit = doc.substring(doc.indexOf("value") + 9, doc.indexOf("\"",
                                        doc.indexOf("value") + 10));
                                String[] splitted = needToSplit.split("#");
                                String val = splitted[position - 1];
                                String keyy = doc.substring(doc.indexOf("key") + 7, doc.indexOf("\"",
                                        doc.indexOf("key") + 8));
                                values.add(val);
                                pkList.add(keyy);
                            }
                        }
                        int offset = "key".length() + 4;
                        if(position != 0) {
                            ExecutorService executor = Executors.newFixedThreadPool(50);
                            int limit = 0;
                            int plus = 0;
                            if(values.size() <= 10000){
                                limit = 1000;
                                plus = 1000;
                            }else if(values.size() > 10000 && values.size() <= 500000){
                                limit = 3000;
                                plus = 3000;
                            }else if(values.size() > 50000){
                                limit = 5000;
                                plus = 5000;
                            }
                            int start = 0;
                            Boolean isDone = false;
                            for (int i = 0; i < 50; i++) {
                                if(limit > values.size()){
                                    limit = values.size();
                                    isDone = true;
                                }
                                Runnable worker = new UIBuilderNotPK(start, limit, offset, position, attributes, values, pkList, coll, coll2);
                                executor.execute(worker);
                                if(isDone) break;
                                start = limit;
                                limit += plus;
                            }
                            executor.shutdown();
                            while (!executor.isTerminated()) {
                            }
                        }
                        else{
                            ExecutorService executor = Executors.newFixedThreadPool(50);
                            int limit = 0;
                            int plus = 0;
                            if(values.size() <= 10000){
                                limit = 1000;
                                plus = 1000;
                            }else if(values.size() > 10000 && values.size() <= 500000){
                                limit = 3000;
                                plus = 3000;
                            }else if(values.size() > 50000){
                                limit = 5000;
                                plus = 5000;
                            }
                            int start = 0;
                            Boolean isDone = false;
                            for (int i = 0; i < 50; i++) {
                                if(limit > values.size()){
                                    limit = values.size();
                                    isDone = true;
                                }
                                Runnable worker = new UIBuilderPK(start, limit, values, coll2);
                                executor.execute(worker);
                                if(isDone) break;
                                start = limit;
                                limit += plus;
                            }
                            executor.shutdown();
                            while (!executor.isTerminated()) {
                            }
                        }
                        coll2.createIndex(new BasicDBObject("key", 1), "key", true);
                        client.close();
                        System.out.println("Done building UI");
                        FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer2.write("OK");
                        writer2.close();

                    }
                    else if(command.equals("CHECKPK")){
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);
                        DB database = client.getDB(dbName);
                        DBCollection coll = database.getCollection(tableName);

                        String content = readFile("Structures/" + dbName + ".txt", StandardCharsets.UTF_8);
                        System.out.println(key);
                        content = content.substring(content.indexOf("tableName=\"" + tableName), content.indexOf(
                                "<primaryKey>", content.indexOf("tableName=\"" + tableName) + 1
                        ));
                        Vector<Integer> startingIndexes = new Vector<Integer>();
                        int index = content.indexOf("attributeName=\"") + 15;
                        while (index >= 0) {
                            startingIndexes.add(index);
                            index = content.indexOf("attributeName=\"", index + 1);
                            if(index == -1)
                                break;
                            else index += 15;
                        }
                        Vector<String> attributes = new Vector<String>();
                        for(int i=0;i<startingIndexes.size();i++){
                            attributes.add(content.substring(startingIndexes.get(i), content.indexOf(
                                    "\"", startingIndexes.get(i) + 1
                            )));
                        }
                        Boolean isOK = true;
                        for(int i=0;i<attributes.size();i++){
                            System.out.println(attributes.get(i));
                            System.out.println(key);
                            if(attributes.get(i).equals(key) && i == 0){
                                List result = coll.distinct("key");
                                for (Object r : result) {
                                    if (r.toString().split("#")[0].equals(value)) {
                                        isOK = false;
                                        break;
                                    }
                                }
                                break;
                            }else{
                                List result = coll.distinct("value");
                                for(Object r : result){
                                    String[] splitted = r.toString().split("#");
                                    int position = 0;
                                    for(int j=0;j<attributes.size();j++){
                                        if(attributes.get(j).equals(key)){
                                            position = j;
                                            break;
                                        }
                                    }
                                    if(splitted[position - 1].equals(value)){
                                        isOK = false;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        String answer = "";
                        if(isOK){
                            answer = "OK";
                        }else{
                            answer = "NOOK";
                        }

                        FileWriter writer = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer.write(answer);
                        writer.close();
                    }
                }else if(numberOfLines == 10){
                    String command = getDBcommandWithTable();
                    if(command.equals("SIMPLEQUERY")){
                        Vector<String> allResults = new Vector<>();
                        System.out.println("QUERY STARTED!");
                        String DB = getDBname();
                        String TB = getDBTableName();
                        String[] conditions = getAtributes();
                        String[] operators = getOperators();
                        String[] projections = getProjections();
                        String groupByAttribute = getGroupByAttribute2();
                        String[] havingConditions = getHavingConditions2();
                        String[] sumFunctions = getSumFunctions2();
                        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                        MongoClientURI clientUri = new MongoClientURI(uri);
                        MongoClient client = new MongoClient(clientUri);

                        String cont = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
                        cont = cont.substring(cont.indexOf("tableName=\"" + TB), cont.indexOf(
                                "<primaryKey>", cont.indexOf("tableName=\"" + TB) + 1
                        ));
                        Vector<Integer> startingIndexes2 = new Vector<Integer>();
                        int ind = cont.indexOf("attributeName=\"") + 15;
                        while (ind >= 0) {
                            startingIndexes2.add(ind);
                            ind = cont.indexOf("attributeName=\"", ind + 1);
                            if(ind == -1)
                                break;
                            else ind += 15;
                        }
                        Vector<String> attributes2 = new Vector<String>();
                        for(int j=0;j<startingIndexes2.size();j++){
                            attributes2.add(cont.substring(startingIndexes2.get(j), cont.indexOf(
                                    "\"", startingIndexes2.get(j) + 1
                            )));
                        }

                        Boolean areAllInedxes = true;
                        Vector<String> indexCollections = new Vector<String>();
                        if(projections.length == 1 && projections[0].equals("*")){
                            for(int i=0;i<attributes2.size();i++){
                                if(!(new File("IndexFiles/" + DB + TB + attributes2.get(i) + ".ind").exists())){
                                    areAllInedxes = false;
                                    break;
                                }
                                indexCollections.add(DB + TB + attributes2.get(i) + ".ind");
                            }
                        }
                        for(int i=0;i<projections.length;i++){

                            if (!(new File("IndexFiles/" + DB + TB + projections[i]+ ".ind").exists()) &&
                            !projections[0].equals("*")){
                                areAllInedxes = false;
                                break;
                            }
                            if(!projections[i].equals("*")) {
                                indexCollections.add(DB + TB + projections[i] + ".ind");
                            }
                        }
                        Boolean goNext = true;
                        if(areAllInedxes){

                            MongoDatabase database = client.getDatabase(DB);
                            System.out.println("All projection fields have index!");
                            Vector<MongoCollection> collections = new Vector<>();
                            FindIterable<Document> findIterable = null;
                            if(!goNext) {
                                for (String str : indexCollections) {
                                    collections.add(database.getCollection(str));
                                }
                                System.out.println("got the needed index collections!");

                                Vector<String> allObjects = new Vector<>();
                                FindIterable<Document> findIterabl = collections.get(0).find();
                                for (Document doc : findIterabl) {
                                    String value = doc.toString().substring(doc.toString().indexOf("value=") + 6,
                                            doc.toString().indexOf("}", doc.toString().indexOf("value=") + 7));
                                    String[] splitted = value.split("#");
                                    for (String str : splitted) {
                                        allObjects.add(str);
                                    }
                                }
                                System.out.println("got all the unique pks!");
                                System.out.println(allObjects);
                                String result = "";
                                for (int k = 0; k < allObjects.size(); k++) {
                                    System.out.println(k);
                                    result += "{ ";
                                    for (int i = 0; i < indexCollections.size(); i++) {
                                        findIterable = collections.get(i).find();
                                        String key = "";
                                        for (Document doc : findIterable) {
                                            String value = doc.toString().substring(doc.toString().indexOf("value=") + 6,
                                                    doc.toString().indexOf("}", doc.toString().indexOf("value=") + 7));
                                            String[] splitted = value.split("#");
                                            Boolean found = false;
                                            for (String str : splitted) {
                                                if (str.equals(allObjects.get(k))) {
                                                    found = true;
                                                    break;
                                                }
                                            }
                                            if (found) {
                                                key = doc.toString().substring(doc.toString().indexOf("key=") + 4,
                                                        doc.toString().indexOf(",", doc.toString().indexOf("key=") + 5));
                                            }
                                        }
                                        String match = "";
                                        for (int p = 0; p < attributes2.size(); p++) {
                                            if (indexCollections.get(i).contains(attributes2.get(p))) {
                                                match = attributes2.get(p);
                                            }
                                        }
                                        result += match + ":" + key + ", ";
                                    }
                                    result += "}\n";
                                }
                                System.out.println("got all the results matching the conditions!");
                                System.out.println("formatting results after projections needed!");
                                String finalResult = "";
                                Vector<Integer> matched = new Vector<>();
                                Vector<Integer> neverUSe = new Vector<>();
                                if (!conditions[0].equals("")) {
                                    System.out.println("VAN KONDICIO");
                                    String[] lines = result.split("\n");
                                    for (int i = 0; i < conditions.length; i++) {
                                        int j = 0;
                                        for (String str : lines) {
                                            String part = str.substring(str.indexOf(conditions[i].split(operators[i])[0])
                                                    + conditions[i].split(operators[i])[0].length() + 1, str.indexOf(",",
                                                    str.indexOf(conditions[i].split(operators[i])[0])
                                                            + conditions[i].split(operators[i])[0].length() + 1));
                                            if (!isInt(part)) {
                                                if (part.equals(conditions[i].split(operators[i])[1])) {
                                                    if (!matched.contains(j)) {
                                                        if (!neverUSe.contains(j)) {
                                                            matched.add(j);
                                                        }
                                                    }
                                                } else {
                                                    if (matched.contains(j)) {
                                                        matched.remove(j);
                                                    }
                                                    neverUSe.add(j);
                                                }
                                            } else {
                                                System.out.println("INTEGER");
                                                if (operators[i].equals(">")) {
                                                    if ((Integer.parseInt(part) > Integer.parseInt(conditions[i].split(operators[i])[1]))) {
                                                        if (!matched.contains(j)) {
                                                            if (!neverUSe.contains(j)) {
                                                                matched.add(j);
                                                            }
                                                        }
                                                    } else {
                                                        if (matched.contains(j)) {
                                                            matched.remove(j);
                                                        }
                                                        neverUSe.add(j);
                                                    }
                                                } else if (operators[i].equals("<")) {
                                                    if ((Integer.parseInt(part) < Integer.parseInt(conditions[i].split(operators[i])[1]))) {
                                                        if (!matched.contains(j)) {
                                                            if (!neverUSe.contains(j)) {
                                                                matched.add(j);
                                                            }
                                                        }
                                                    } else {
                                                        if (matched.contains(j)) {
                                                            matched.remove(j);
                                                        }
                                                        neverUSe.add(j);
                                                    }
                                                } else if (operators[i].equals("=")) {
                                                    System.out.println("EGYENLOSEG");
                                                    if ((Integer.parseInt(part) == Integer.parseInt(conditions[i].split(operators[i])[1]))) {
                                                        if (!matched.contains(j)) {
                                                            if (!neverUSe.contains(j)) {
                                                                matched.add(j);
                                                            }
                                                        }
                                                    } else {
                                                        if (matched.contains(j)) {
                                                            matched.remove(j);
                                                        }
                                                        neverUSe.add(j);
                                                    }
                                                } else if (operators[i].equals("<=")) {
                                                    if ((Integer.parseInt(part) <= Integer.parseInt(conditions[i].split(operators[i])[1]))) {
                                                        if (!matched.contains(j)) {
                                                            if (!neverUSe.contains(j)) {
                                                                matched.add(j);
                                                            }
                                                        }
                                                    } else {
                                                        if (matched.contains(j)) {
                                                            matched.remove(j);
                                                        }
                                                        neverUSe.add(j);
                                                    }
                                                } else if (operators[i].equals(">=")) {
                                                    if ((Integer.parseInt(part) == Integer.parseInt(conditions[i].split(operators[i])[1]))) {
                                                        if (!matched.contains(j)) {
                                                            if (!neverUSe.contains(j)) {
                                                                matched.add(j);
                                                            }
                                                        }
                                                    } else {
                                                        if (matched.contains(j)) {
                                                            matched.remove(j);
                                                        }
                                                        neverUSe.add(j);
                                                    }
                                                }
                                            }
                                            j++;
                                        }
                                    }
                                    for (int i : matched) {
                                        finalResult += lines[i] + "\n";
                                    }
                                    for (int k : matched) {
                                        System.out.println(k);
                                    }
                                    FileWriter writer = new FileWriter("OUTPUT/output.txt", false);
                                    writer.write(finalResult);
                                    writer.close();
                                    System.out.println("QUERY DONE!");
                                    return;
                                }

                                for (int k : matched) {
                                    System.out.println(k);
                                }


                                FileWriter writer = new FileWriter("OUTPUT/output.txt", false);
                                writer.write(result);
                                writer.close();
                                System.out.println("QUERY DONE!");
                                return;
                            }
                        }
                        if(!areAllInedxes)
                        System.out.println("Not all projection fields have index!");



                    //--------------------------------------------------------------------------------------------------







                        Vector<String> tmp1 = new Vector<String>();
                        Vector<String> tmp2 = new Vector<String>();
                        Vector<String> result = new Vector<String>();
                        Boolean firstCondition = true;
                        if(conditions.length > 0 && !conditions[0].equals("")) {
                            for (int i = 0; i < conditions.length; i++) {
                                tmp1.clear();
                                result.clear();
                                System.out.println(DB + TB + conditions[i].split(operators[i])[0] + ".ind");
                                if ((new File("IndexFiles/" + DB + TB + conditions[i].split(operators[i])[0] + ".ind").exists())) {
                                    MongoDatabase database2 = client.getDatabase("Indexes");
                                    MongoCollection coll2 = database2.getCollection(DB + TB +
                                            conditions[i].split(operators[i])[0] + ".ind");
                                    FindIterable<Document> findIterable = null;
                                    if (!isInt(conditions[i].split(operators[i])[1])) {
                                        System.out.println("int");
                                        findIterable = coll2.find(Filters.eq("key", conditions[i].split(operators[i])[1]));
                                    } else {
                                        System.out.println("nem int");
                                    if (operators[i].equals("=")) {
                                        findIterable = coll2.find(Filters.eq("key", Integer.parseInt(conditions[i].split(operators[i])[1])));
                                    } else if (operators[i].equals("<=")) {
                                        findIterable = coll2.find(Filters.lte("key", Integer.parseInt(conditions[i].split(operators[i])[1])));
                                    } else if (operators[i].equals(">=")) {
                                        findIterable = coll2.find(Filters.gte("key", Integer.parseInt(conditions[i].split(operators[i])[1])));
                                    } else if (operators[i].equals("<")) {
                                        findIterable = coll2.find(Filters.lt("key", Integer.parseInt(conditions[i].split(operators[i])[1])));
                                    } else if (operators[i].equals(">")) {
                                        findIterable = coll2.find(Filters.gt("key", Integer.parseInt(conditions[i].split(operators[i])[1])));
                                    }
                                }
                                    for (Document doc : findIterable) {
                                        String tmp = doc.toString().substring(doc.toString().indexOf("value=") + 6,
                                                doc.toString().indexOf("}", doc.toString().indexOf("value=") + 6));
                                        String[] splitted = tmp.split("#");
                                        for (String sp : splitted) {
                                            tmp1.add(sp);
                                        }

                                    }
                                    if (firstCondition) {
                                        for (String tmp : tmp1) {
                                            tmp2.add(tmp);
                                        }
                                        for (String tmp : tmp2) {
                                            result.add(tmp);
                                        }
                                        firstCondition = false;
                                    } else {
                                        for (String tmp : tmp1) {
                                            if (tmp2.contains(tmp)) {
                                                result.add(tmp);
                                            }
                                        }
                                        tmp2.clear();
                                        for (String tmp : result) {
                                            tmp2.add(tmp);
                                        }
                                    }

                                }else{
                                    MongoDatabase database =  client.getDatabase(DB);
                                    MongoCollection coll = database.getCollection(TB);
                                    FindIterable<Document> findIterable = null;
                                    String results = "";

                                    String content = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
                                    content = content.substring(content.indexOf("tableName=\"" + TB), content.indexOf(
                                            "<primaryKey>", content.indexOf("tableName=\"" + TB) + 1
                                    ));
                                    Vector<Integer> startingIndexes = new Vector<Integer>();
                                    int index = content.indexOf("attributeName=\"") + 15;
                                    while (index >= 0) {
                                        startingIndexes.add(index);
                                        index = content.indexOf("attributeName=\"", index + 1);
                                        if(index == -1)
                                            break;
                                        else index += 15;
                                    }
                                    Vector<String> attributes = new Vector<String>();
                                    for(int j=0;j<startingIndexes.size();j++){
                                        attributes.add(content.substring(startingIndexes.get(j), content.indexOf(
                                                "\"", startingIndexes.get(j) + 1
                                        )));
                                    }
                                    int position = 0;
                                    for(int j=0;j<attributes.size();j++){
                                        if(attributes.get(j).equals(conditions[i].split(operators[i])[0])){
                                            position = j;
                                        }
                                    }
                                    String regex = "";
                                    for (int j = 0; j < attributes.size() - 1; j++) {
                                        if (j == position - 1) {
                                            regex += conditions[i].split(operators[i])[1] + "#";
                                        } else {
                                            regex += "(.*)#";
                                        }
                                    }
                                    if (!isInt(conditions[i].split(operators[i])[1])) {
                                        System.out.println("NON INT");
                                        DBObject obj = new BasicDBObject();
                                        obj.put("value", java.util.regex.Pattern.compile(regex));
                                        findIterable = coll.find(Filters.regex("value",
                                                regex));

                                    } else {
                                        if (operators[i].equals("=")) {
                                            findIterable = coll.find(Filters.eq(conditions[i].split(operators[i])[0],
                                                    Integer.parseInt(conditions[i].split(operators[i])[1])));
                                        } else if (operators[i].equals("<=")) {
                                            findIterable = coll.find(Filters.lte(conditions[i].split(operators[i])[0],
                                                    Integer.parseInt(conditions[i].split(operators[i])[1])));
                                        } else if (operators[i].equals(">=")) {
                                            findIterable = coll.find(Filters.gte(conditions[i].split(operators[i])[0],
                                                    Integer.parseInt(conditions[i].split(operators[i])[1])));
                                        } else if (operators[i].equals("<")) {
                                            findIterable = coll.find(Filters.lt(conditions[i].split(operators[i])[0],
                                                    Integer.parseInt(conditions[i].split(operators[i])[1])));
                                        } else if (operators[i].equals(">")) {
                                            findIterable = coll.find(Filters.gt(conditions[i].split(operators[i])[0],
                                                    Integer.parseInt(conditions[i].split(operators[i])[1])));
                                        }
                                    }

                                    for (Document doc : findIterable) {
                                        String tmp = doc.toString().substring(doc.toString().indexOf("=", 15) + 1,
                                                doc.toString().indexOf(",", doc.toString().indexOf("=", 15) + 1));
                                        tmp1.add(tmp);

                                    }
                                    if (firstCondition) {
                                        for (String tmp : tmp1) {
                                            tmp2.add(tmp);
                                        }
                                        for (String tmp : tmp2) {
                                            result.add(tmp);
                                        }
                                        firstCondition = false;
                                    } else {
                                        for (String tmp : tmp1) {
                                            if (tmp2.contains(tmp)) {
                                                result.add(tmp);
                                            }
                                        }
                                        tmp2.clear();
                                        for (String tmp : result) {
                                            tmp2.add(tmp);
                                        }
                                    }
                                }
                            }

                            System.out.println("EREDMENYY: ");
                            System.out.println(result);
                        }else{
                            MongoDatabase database =  client.getDatabase(DB);
                            MongoCollection coll = database.getCollection(TB);
                            FindIterable<Document> findIterable = null;
                            String results = "";
                            String content = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
                            content = content.substring(content.indexOf("tableName=\"" + TB), content.indexOf(
                                    "<primaryKey>", content.indexOf("tableName=\"" + TB) + 1
                            ));
                            Vector<Integer> startingIndexes = new Vector<Integer>();
                            int index = content.indexOf("attributeName=\"") + 15;
                            while (index >= 0) {
                                startingIndexes.add(index);
                                index = content.indexOf("attributeName=\"", index + 1);
                                if(index == -1)
                                    break;
                                else index += 15;
                            }
                            Vector<String> attributes = new Vector<String>();
                            for(int j=0;j<startingIndexes.size();j++){
                                attributes.add(content.substring(startingIndexes.get(j), content.indexOf(
                                        "\"", startingIndexes.get(j) + 1
                                )));
                            }
                            Vector<Integer> projectionIndexesToShow = new Vector<Integer>();
                            for(int i=0;i<attributes.size();i++){
                                for(int j=0;j<projections.length;j++){
                                    if(attributes.get(i).equals(projections[j])){
                                        projectionIndexesToShow.add(i);
                                    }
                                }
                            }
                            MongoCursor curs = coll.distinct("key", String.class).iterator();
                            Vector<String> keys = new Vector<String>();
                            while(curs.hasNext()){
                                keys.add(curs.next().toString());
                            }
                            System.out.println("got projections needed!");
                            FileWriter writer = new FileWriter("OUTPUT/output.txt", false);
                            writer.write("");
                            writer.close();
                            FileWriter writer2 = new FileWriter("OUTPUT/output.txt", true);
                            if(projections.length == 1 && projections[0].equals("*")) {

                                findIterable = coll.find();
                                System.out.println("got records");
                                int j = 0;
                                for(Document doc : findIterable){
                                    results = "";
                                    String values = doc.toString().substring(doc.toString().indexOf("value=") + 6,
                                            doc.toString().indexOf("}", doc.toString().indexOf("value=") + 7));
                                    String pk = doc.toString().substring(doc.toString().indexOf("key=") + 4,
                                            doc.toString().indexOf(",", doc.toString().indexOf("key=") + 5));
                                    String[] splittedValues = values.split("#");
                                    results += "{ " + TB + "." + attributes.get(0) + ":" + pk + ", ";
                                    for(int i=1;i<attributes.size();i++){
                                        results += TB + "." + attributes.get(i) + ":" + splittedValues[i - 1] + ", ";
                                    }
                                    j++;
                                    results +="}\n";
                                    //KIIRAT
                                    //writer2.write(results);
                                    allResults.add(results);
                                }
                                System.out.println("100%");
                            }else{
                                findIterable = coll.find();
                                for(Document doc : findIterable){
                                    results = "{ ";
                                    String values = doc.toString().substring(doc.toString().indexOf("value=") + 6,
                                            doc.toString().indexOf("}", doc.toString().indexOf("value=") + 7));
                                    String pk = doc.toString().substring(doc.toString().indexOf("key=") + 4,
                                            doc.toString().indexOf(",", doc.toString().indexOf("key=") + 5));
                                    String[] splittedValues = values.split("#");
                                    for(int i=0;i<attributes.size();i++){
                                        if(projectionIndexesToShow.contains(i)){
                                            if(i == 0){
                                                results += TB + "." + attributes.get(i) + ":" + pk + ", ";
                                            }else{
                                                results += TB + "." + attributes.get(i) + ":" + splittedValues[i - 1] + ", ";
                                            }
                                        }
                                    }
                                    results +="}\n";
                                    //writer2.write(results);
                                    allResults.add(results);
                                }
                            }
                            //KIIRAT
                            writer2.close();
                            GroupByFilterWithJoin groupByFilterWithJoin = new GroupByFilterWithJoin(allResults, groupByAttribute,
                                    havingConditions, sumFunctions, DB, TB);
                            groupByFilterWithJoin.writeResult("OUTPUT/output.txt");
                            System.out.println("QUERY DONE!");
                            return;

                        }
                        MongoDatabase database =  client.getDatabase(DB);
                        MongoCollection coll = database.getCollection(TB);
                        FindIterable<Document> curs = coll.find();
                        String PK = "";
                        System.out.println("formating the results!");
                        for(Document doc : curs){
                            PK = doc.toString().substring(doc.toString().indexOf(" ") + 1,
                                    doc.toString().indexOf("=", doc.toString().indexOf(" ") + 1));
                            break;
                        }
                        System.out.println("got pk");
                        String content = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
                        content = content.substring(content.indexOf("tableName=\"" + TB), content.indexOf(
                                "<primaryKey>", content.indexOf("tableName=\"" + TB) + 1
                        ));
                        Vector<Integer> startingIndexes = new Vector<Integer>();
                        int index = content.indexOf("attributeName=\"") + 15;
                        while (index >= 0) {
                            startingIndexes.add(index);
                            index = content.indexOf("attributeName=\"", index + 1);
                            if(index == -1)
                                break;
                            else index += 15;
                        }
                        System.out.println("got attribute indexes!");
                        Vector<String> attributes = new Vector<String>();
                        for(int j=0;j<startingIndexes.size();j++){
                            attributes.add(content.substring(startingIndexes.get(j), content.indexOf(
                                    "\"", startingIndexes.get(j) + 1
                            )));
                        }
                        Vector<Integer> projectionIndexesToShow = new Vector<Integer>();
                        for(int i=0;i<attributes.size();i++){
                            for(int j=0;j<projections.length;j++){
                                if(attributes.get(i).equals(projections[j])){
                                    projectionIndexesToShow.add(i);
                                }
                            }
                        }
                        System.out.println("got projections needed!");
                        FileWriter writer = new FileWriter("OUTPUT/output.txt", false);
                        writer.write("");
                        writer.close();
                        FileWriter writer2 = new FileWriter("OUTPUT/output.txt", true);
                        FindIterable findIterable;
                        String results = "";
                        System.out.println("kondicios");
                        Vector<String> docs = new Vector<>();
                        findIterable = coll.find();
                        //result;
                        for(Object obj : findIterable) {
                            int valPos = obj.toString().indexOf("value=");
                            int keyPos = obj.toString().indexOf("key=");
                            if(result.contains(obj.toString().substring(keyPos + 4,
                                    obj.toString().indexOf(",", keyPos + 5)))){
                                //docs.add(obj.toString());
                                results = "{ ";
                                if (projections.length == 1 && projections[0].equals("*")) {
                                    String values = obj.toString().substring(valPos + 6,
                                            obj.toString().indexOf("}", valPos + 7));
                                    String pk = obj.toString().substring(keyPos + 4,
                                            obj.toString().indexOf(",", keyPos + 5));
                                    String[] splittedValues = values.split("#");
                                    results += TB + "." + attributes.get(0) + ":" + pk + ", ";
                                    for (int j = 1; j < attributes.size(); j++) {
                                        results += TB + "." + attributes.get(j) + ":" + splittedValues[j - 1] + ", ";
                                    }

                                    results += "}\n";
                                    allResults.add(results);
                                    //writer2.write(results);
                                } else {
                                    String values = obj.toString().substring(valPos + 6,
                                            obj.toString().indexOf("}", valPos + 7));
                                    String pk = obj.toString().substring(keyPos + 4,
                                            obj.toString().indexOf(",", keyPos + 5));
                                    String[] splittedValues = values.split("#");
                                    for (int j = 0; j < attributes.size(); j++) {
                                        if (projectionIndexesToShow.contains(j)) {
                                            if (j == 0) {
                                                results += TB + "." + attributes.get(j) + ":" + pk + ", ";
                                            } else {
                                                results += TB + "." + attributes.get(j) + ":" + splittedValues[j - 1] + ", ";
                                            }
                                        }
                                    }
                                    results += "}\n";
                                    //writer2.write(results);
                                    allResults.add(results);
                                }
                            }
                        }
                        System.out.println("megvannak a dokokkk!");
                        GroupByFilterWithJoin groupByFilterWithJoin2 = new GroupByFilterWithJoin(allResults, groupByAttribute,
                                havingConditions, sumFunctions, DB, TB);
                        groupByFilterWithJoin2.writeResult("OUTPUT/output.txt");
                        //KIIRAT
                        writer2.close();
                        System.out.println("QUERY DONE!");
                    }
                }else if(numberOfLines ==7){
                    String db = "";
                    String tb = "";
                    String command = "";
                    String keyToDelete = "";
                    String valueToDelete = "";
                    String IFIELD = "";
                    String PKFIELD = "";
                    db = getDBname();
                    tb = getDBTableName();
                    command = getDBcommandWithTable();
                    keyToDelete = getKey();
                    valueToDelete = getValue();
                    IFIELD = getSixth();
                    PKFIELD = getSeventh();
                    if(command.equals("FKDELETE")){
                        System.out.println("FKDELETE BEGAN!");
                        String content = readFile("Structures/" + db + ".txt", StandardCharsets.UTF_8);
                        int startIndex = content.indexOf("tableName=\"" + tb) + 11;
                        int endIndex = content.indexOf("/Table", startIndex + 1);
                        content = content.substring(0, startIndex) + content.substring(endIndex);
                        System.out.println(content);

                        Vector<Integer> tableIndexes = new Vector<Integer>();
                        Vector<Integer> attributeIndexes = new Vector<Integer>();
                        System.out.println("finding possibilities!");
                        int index = 0;
                        while(index != -1){
                            index = content.indexOf("refTable>", index);
                            if(index != -1){
                                tableIndexes.add(index);
                                index++;
                            }
                        }

                        index = 0;
                        while(index != -1){
                            index = content.indexOf("refAttribute>", index);
                            if(index != -1){
                                attributeIndexes.add(index);
                                index++;
                            }
                        }
                        Boolean canDelete = true;
                        if(tableIndexes.size() != 0) {
                            for (int i = 0; i < tableIndexes.size(); i++) {
                                tableIndexes.set(i, tableIndexes.get(i) + 9);
                                attributeIndexes.set(i, attributeIndexes.get(i) + 13);
                            }

                            System.out.println("posibilities found!");
                            Vector<String> tables = new Vector<String>();
                            Vector<String> atrs = new Vector<String>();

                            for (int i = 0; i < tableIndexes.size(); i++) {
                                String table = content.substring(tableIndexes.get(i), content.indexOf("<", tableIndexes.get(i) + 1));
                                String atr = content.substring(attributeIndexes.get(i), content.indexOf("<", attributeIndexes.get(i) + 1));
                                tables.add(table);
                                atrs.add(atr);
                            }
                            System.out.println("tables: ");
                            System.out.println(tables);
                            System.out.println("attrs: ");
                            System.out.println(atrs);
                            String realTable = "";
                            String realAtr = "";
                            int index2 = -1;
                            for (int i = 0; i < atrs.size(); i++) {
                                if (tables.get(i).equals(tb) && atrs.get(i).equals(PKFIELD)) {
                                    realTable = tables.get(i);
                                    realAtr = atrs.get(i);
                                    index2 = i;

                                    break;
                                }
                            }
                            if(index2 == -1){
                                String answer = "OK";
                                FileWriter writer = new FileWriter("Databases/TOCLIENT.txt", false);
                                writer.write(answer);
                                writer.close();
                                return;
                            }
                            String sourceTable = content.substring(content.lastIndexOf("Table tableName=\"", tableIndexes.get(index2)) + 17,
                                    content.indexOf("\"", content.lastIndexOf("Table tableName=\"", tableIndexes.get(index2)) + 18));
                            System.out.println(realTable + " " + realAtr + " " + sourceTable);

                            String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                            MongoClientURI clientUri = new MongoClientURI(uri);
                            MongoClient client = new MongoClient(clientUri);
                            DB database = client.getDB(db);
                            DBCollection coll = database.getCollection(sourceTable);

                            String cont = readFile("Structures/" + db + ".txt", StandardCharsets.UTF_8);
                            cont = cont.substring(cont.indexOf("tableName=\"" + sourceTable), cont.indexOf(
                                    "<primaryKey>", cont.indexOf("tableName=\"" + sourceTable) + 1
                            ));
                            Vector<Integer> startingIndexes = new Vector<Integer>();
                            int ind = cont.indexOf("attributeName=\"") + 15;
                            while (ind >= 0) {
                                startingIndexes.add(ind);
                                ind = cont.indexOf("attributeName=\"", ind + 1);
                                if(ind == -1)
                                    break;
                                else ind += 15;
                            }
                            Vector<String> attributes = new Vector<String>();
                            for(int i=0;i<startingIndexes.size();i++){
                                attributes.add(cont.substring(startingIndexes.get(i), cont.indexOf(
                                        "\"", startingIndexes.get(i) + 1
                                )));
                            }
                            int position = 0;
                            for(int i=0;i<attributes.size();i++){
                                if(attributes.get(i).equals(realAtr)){
                                    position = i;
                                    break;
                                }
                            }



                            List elements = coll.distinct("value");
                            //valueToDelete
                            canDelete = true;
                            System.out.println(attributes);

                            for (Object elem : elements) {
                                String[] splitted = elem.toString().split("#");
                                if(splitted[position - 1].equals(valueToDelete)){
                                    canDelete = false;
                                    break;
                                }
                            }
                        }
                        String answer = "";
                        if (canDelete) {
                            answer = "OK";
                        } else {
                            answer = "NOOK";
                        }
                        System.out.println(answer);

                        FileWriter writer = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer.write(answer);
                        writer.close();
                    }
                }else if(numberOfLines == 13){
                    System.out.println("joinos");
                    String DB = getDBname();
                    String TB = getDBTableName();
                    String command = getDBcommandWithTable();
                    if(command.equals("JOINQUERY")){
                    String[] projections = getJoinProjections();
                    String[] joinConditions = getJoinJoinConditions();
                    String[] whereConditions = getJoinWhereConditions();
                    String[] whereOperators = getJoinWhereOperators();
                    String[] joinedTables = getJoinJoinedTables();
                    String[] attributesPerTable = getJoinAttributesPerTable();
                    String groupByAttribute = getGroupByAttribute();
                    String[] havingConditions = getHavingConditions();
                    String[] sumFunctions = getSumFunctions();
                    System.out.println(groupByAttribute);
                    for(String str : havingConditions){
                        System.out.println(str);
                    }
                        for(String str : sumFunctions){
                            System.out.println(str);
                        }
                    String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
                    MongoClientURI clientUri = new MongoClientURI(uri);
                    MongoClient client = new MongoClient(clientUri);
                    MongoDatabase database = client.getDatabase(DB);
                    Vector<Vector<String>> differentPKs = new Vector<Vector<String>>();
                    Vector<Vector<String>> resultVectors = new Vector<Vector<String>>();
                    Vector<Vector<String>> findResults = new Vector<Vector<String>>();
                    Vector<MongoCollection> collections = new Vector<>();
                    Vector<HashMap<String, String>> vectorMap = new Vector<HashMap<String, String>>();
                    for(String table : joinedTables){
                        HashMap<String, String> tmp = new HashMap<>();
                        vectorMap.add(tmp);
                        Vector<String> vect = new Vector<>();
                        differentPKs.add(vect);
                        Vector<String> vect2 = new Vector<>();
                        resultVectors.add(vect2);
                        Vector<String> vect3 = new Vector<>();
                        findResults.add(vect3);
                        MongoCollection coll = database.getCollection(table);
                        collections.add(coll);
                    }

                    Vector<String> result = new Vector<>();
                    ProjectionFieldAnalyzer projectionFieldAnalyzer = new ProjectionFieldAnalyzer(projections, joinedTables, DB,
                            attributesPerTable);
                    projectionFieldAnalyzer.analyze();
                    if (projectionFieldAnalyzer.areAllFieldsIndexed()) {
                        System.out.println("All projection attributes are indexed!");
                    } else {
                        System.out.println("Not all projection attributes are indexed!");
                    }
                    // JOIN KODRESZLET
                    JoinQueryNonIndexed joinQueryNonIndexed = new JoinQueryNonIndexed(DB, projections, joinConditions,
                            whereConditions, joinedTables, whereOperators);
                    result = joinQueryNonIndexed.getQueryResult();
                    for (String doc : result) {
                        String[] splittedPKs = doc.split("#");
                        for (int i = 0; i < splittedPKs.length; i++) {
                            Vector<String> tmp = differentPKs.get(i);
                            String[] splittedPK = splittedPKs[i].split("/");
                            String value = splittedPK[1];
                            tmp.add(value);

                        }
                    }
                    System.out.println("elotte");
                    for (int i = 0; i < differentPKs.size(); i++) {
                        FindIterable it = collections.get(i).find();
                        Vector<String> vect = findResults.get(i);
                        for (Object obj : it) {
                            vect.add(obj.toString());
                        }
                    }
                    System.out.println("doc lekerese kesz!");
                    for (int i = 0; i < findResults.size(); i++) {
                        for (String doc : findResults.get(i)) {
                            String key = doc.substring(doc.indexOf("key=") + 4, doc.indexOf(",",
                                    doc.indexOf("key=") + 5));
                            HashMap<String, String> tmp = vectorMap.get(i);
                            tmp.put(key, doc);
                        }
                    }
                    System.out.println("maps feltoltese kesz");

                    for (int i = 0; i < differentPKs.size(); i++) {
                        for (int j = 0; j < differentPKs.get(i).size(); j++) {
                            Vector<String> tmp = differentPKs.get(i);
                            HashMap<String, String> tmp2 = vectorMap.get(i);
                            tmp.set(j, tmp2.get(tmp.get(j)));
                        }
                    }
                    System.out.println("visszahelyettesites kesz!");

                    String[][] matrix = new String[differentPKs.get(0).size()][differentPKs.size()];
                    for (int i = 0; i < differentPKs.get(0).size(); i++) {
                        for (int j = 0; j < differentPKs.size(); j++) {
                            Vector<String> tmp = differentPKs.get(j);
                            matrix[i][j] = tmp.get(i);
                        }
                    }
                    System.out.println("matrix feltoltese kesz!");
                    PrintWriter out = new PrintWriter(new FileWriter("OUTPUT/output.txt"));

                    Vector<Vector<String>> attributesPerTable2 = new Vector<Vector<String>>();
                    for (int i = 0; i < joinedTables.length; i++) {
                        Vector<String> tmp = new Vector<String>();
                        tmp = getAttributesForTable(joinedTables[i], DB);
                        attributesPerTable2.add(tmp);
                    }
                    Vector<String> resultRows = new Vector<>();
                    System.out.println("atributumok megvannak!");

                    for (int i = 0; i < matrix.length; i++) {
                        String line = "";
                        String newLine = "";
                        for (int j = 0; j < matrix[0].length; j++) {
                            Vector<String> tmp = attributesPerTable2.get(j);
                            String doc = matrix[i][j];
                            String key = doc.substring(doc.indexOf("key=") + 4, doc.indexOf(",",
                                    doc.indexOf("key=") + 5));
                            String value = doc.substring(doc.indexOf("value=") + 6, doc.indexOf("}",
                                    doc.indexOf("value=") + 7));
                            newLine += "{ " + joinedTables[j] + "." + tmp.get(0) + ":" + key + ", ";
                            String[] splittedValue = value.split("#");
                            //newLine += String.join()
                            if (tmp.size() != 1) {
                                for (int k = 0; k < splittedValue.length; k++) {
                                    {
                                        newLine += joinedTables[j] + "." + tmp.get(k + 1) + ":" + splittedValue[k] + ", ";
                                    }
                                }
                            } else {
                                newLine += ", ";
                            }
                            newLine += "}";
                        }
                        line += newLine;
                        resultRows.add(line);
                        //out.write(line + "\n");
                    }

                    System.out.println("formazas kesz!");

                    if (!whereConditions[0].equals("")) {
                        for (int i = 0; i < whereConditions.length; i++) {
                            String[] splittedCondition = whereConditions[i].split(whereOperators[i]);

                            String tableInfo = splittedCondition[0];
                            String value = splittedCondition[1];

                            Vector<String> tmp = new Vector<>();
                            for (int j = 0; j < resultRows.size(); j++) {
                                String[] splittedLine = resultRows.get(j).split(",");
                                for (String str : splittedLine) {
                                    if (str.contains(tableInfo)) {
                                        //System.out.println(str);
                                        String[] splittedAtr = str.split(":");
                                        if (whereOperators[i].equals("=")) {
                                            if (isInt(splittedAtr[1])) {
                                                if (Integer.parseInt(splittedAtr[1]) == Integer.parseInt(value)) {
                                                    tmp.add(resultRows.get(j));
                                                    //System.out.println(resultRows.get(j));
                                                }
                                            } else {
                                                if (splittedAtr[1].equals(value)) {
                                                    tmp.add(resultRows.get(j));
                                                    //System.out.println(resultRows.get(j));
                                                }
                                            }
                                        } else if (whereOperators[i].equals("<")) {
                                            if (isInt(splittedAtr[1])) {
                                                if (Integer.parseInt(splittedAtr[1]) < Integer.parseInt(value)) {
                                                    //System.out.println(resultRows.get(j));
                                                    tmp.add(resultRows.get(j));
                                                }
                                            }
                                        } else if (whereOperators[i].equals(">")) {
                                            if (isInt(splittedAtr[1])) {
                                                if (Integer.parseInt(splittedAtr[1]) > Integer.parseInt(value)) {
                                                    //System.out.println(resultRows.get(j));
                                                    tmp.add(resultRows.get(j));
                                                }
                                            }
                                        } else if (whereOperators[i].equals("<=")) {
                                            if (isInt(splittedAtr[1])) {
                                                if (Integer.parseInt(splittedAtr[1]) <= Integer.parseInt(value)) {
                                                    //System.out.println(resultRows.get(j));
                                                    tmp.add(resultRows.get(j));
                                                }
                                            }
                                        } else if (whereOperators[i].equals(">=")) {
                                            if (isInt(splittedAtr[1])) {
                                                if (Integer.parseInt(splittedAtr[1]) >= Integer.parseInt(value)) {
                                                    //System.out.println(resultRows.get(j));
                                                    tmp.add(resultRows.get(j));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            resultRows = (Vector) tmp.clone();
                            tmp.clear();
                        }
                    }
                    //System.out.println(resultRows);
                    Vector<String> projectedresult = new Vector<>();
                    Vector<String> finalResult = new Vector<>();
                    Vector<String> resultsForGroupBy = new Vector<>();
                    if (!projections[0].equals("*")) {
                        for (int i = 0; i < resultRows.size(); i++) {
                            List<String> newRow = new ArrayList<String>();
                            for (int j = 0; j < projections.length; j++) {
                                String[] splittedRow = resultRows.get(i).split(",");
                                List<String> listSplittedRows = Arrays.asList(splittedRow);
                                //System.out.println(listSplittedRows);
                                for (String str : listSplittedRows) {
                                    if (str.contains(projections[j])) {
                                        //System.out.println(str);
                                        newRow.add(str.replace("{", ""));
                                    }
                                }
                            }
                            String newLine = "{ " + String.join(",", newRow) + " }";
                            finalResult.add(newLine);
                        }
                        for (String row : finalResult) {
                            out.write(row + "\n");
                            resultsForGroupBy.add(row);
                        }
                        out.close();
                        System.out.println("kiiras kesz!");
                    } else {
                        for (String row : resultRows) {
                            out.write(row + "\n");
                            resultsForGroupBy.add(row);
                        }
                        out.close();
                        System.out.println("kiiras kesz!");
                    }

                        GroupByFilterWithJoin groupByFilterWithJoin = new GroupByFilterWithJoin(resultsForGroupBy, groupByAttribute,
                                havingConditions, sumFunctions, DB, TB);
                        groupByFilterWithJoin.writeResult("OUTPUT/output.txt");
                    }

                }

              //  timer.cancel();
                //timer.purge();
                hasChanged = true;


            }
        };
        if(!hasChanged) {
            timer.schedule(task, new Date(), 1000);
        }

    }
    public static int countLinesInChanged() throws IOException {
        File file = new File("Databases/TOSERVER.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int lines = 0;
        while (reader.readLine() != null) {
            lines++;
        }
        reader.close();
        return lines;
    }

    public static String getDBname() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        reader.close();
        return dbName;
    }

    public static String getDBcommand() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String command = reader.readLine();
        reader.close();
        return command;
    }

    public static String getDBcommandWithTable() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        reader.close();
        return command;
    }

    public static String getDBTableName() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        reader.close();
        return tableName;
    }

    public static String getDBcommand2() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String command = reader.readLine();
        reader.close();
        return command;
    }

    public static String getKey() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String key = reader.readLine();
        reader.close();
        return key;
    }

    public static String getValue() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String key = reader.readLine();
        String value = reader.readLine();
        reader.close();
        return value;
    }

    public static String[] getAtributes() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        reader.close();
        return line.split(" ");

    }

    public static String[] getOperators() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        reader.close();
        return line2.split("#");

    }

    public static String[] getProjections() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        return line3.split(",");

    }

    public static String[] getJoinProjections() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        return line3.split("#");

    }

    public static String[] getJoinJoinConditions() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        return line3.split("#");

    }

    public static String[] getJoinWhereConditions() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        return line3.split("#");

    }

    public static String[] getJoinWhereOperators() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        return line3.split("#");

    }

    public static String[] getJoinJoinedTables() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        return line3.split("#");
    }

    public static String[] getJoinAttributesPerTable() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();
        String line000 = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        return line3.split("#");
    }

    public static String getGroupByAttribute() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();
        String line000 = reader.readLine();
        String line3 = reader.readLine();
        String groupBy = reader.readLine();
        reader.close();
        return groupBy;
    }

    public static String getGroupByAttribute2() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();

        reader.close();
        return line00;
    }



    public static String[] getHavingConditions() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();
        String line000 = reader.readLine();
        String line3 = reader.readLine();
        String groupBy = reader.readLine();
        String havingString = reader.readLine();
        reader.close();
        return havingString.replace(" ","").split("and");
    }

    public static String[] getHavingConditions2() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();
        String havingString = reader.readLine();
        reader.close();
        return havingString.replace(" ","").split("and");
    }

    public static String[] getSumFunctions() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();
        String line000 = reader.readLine();
        String line3 = reader.readLine();
        String groupBy = reader.readLine();
        String havingString = reader.readLine();
        String sumFunctions = reader.readLine();
        reader.close();
        return sumFunctions.split(",");
    }

    public static String[] getSumFunctions2() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String line0 = reader.readLine();
        String line00 = reader.readLine();
        String line000 = reader.readLine();
        String sumFunctions = reader.readLine();
        reader.close();
        return sumFunctions.split(",");
    }

    public static String getSixth() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String line2 = reader.readLine();
        String six = reader.readLine();
        reader.close();
        return six;
    }

    public static String getSeventh() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOSERVER.txt"));
        String dbName = reader.readLine();
        String tableName = reader.readLine();
        String command = reader.readLine();
        String line = reader.readLine();
        String six = reader.readLine();
        String line2 = reader.readLine();
        String seven = reader.readLine();
        reader.close();
        return seven;
    }

    public static boolean isDouble(String str) {
        try {
            double v = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }

    public static boolean isInt(String str) {
        try {
            int v = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }

    public static String getTableWithPK(String pk, String DB) throws IOException{
        String wholeStructure = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
        String tableTExt = wholeStructure.substring(wholeStructure.lastIndexOf("tableName=\"", wholeStructure.indexOf(
                "pkAttribute>" + pk)), wholeStructure.indexOf("pkAttribute>" + pk));
        String table = tableTExt.substring(tableTExt.indexOf("tableName=\"") + 11, tableTExt.indexOf("\"",
                tableTExt.indexOf("tableName=\"") + 12));
        return table;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static String getPKForTable(String table, String DB) throws  IOException{
        String whileStructure = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
        String tableText = whileStructure.substring(whileStructure.indexOf("tableName=\"" + table),
                whileStructure.indexOf("</Table>", whileStructure.indexOf("tableName=\"" + table) +1));
        String PK = tableText.substring(tableText.indexOf("pkAttribute>") + 12, tableText.indexOf("<",
                tableText.indexOf("pkAttribute>") + 13));

        return PK;
    }

    public static Vector<String> getAttributesForTable(String table, String DB) throws IOException{
        String whileStructure = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
        String tableText = whileStructure.substring(whileStructure.indexOf("tableName=\"" + table),
                whileStructure.indexOf("</Table>", whileStructure.indexOf("tableName=\"" + table) +1));
        Vector<Integer> startingIndexes = new Vector<Integer>();
        int index = tableText.indexOf("attributeName=\"") + 15;
        while (index >= 0) {
            startingIndexes.add(index);
            index = tableText.indexOf("attributeName=\"", index + 1);
            if(index == -1)
                break;
            else index += 15;
        }
        Vector<String> attributes = new Vector<String>();
        for(int j=0;j<startingIndexes.size();j++){
            attributes.add(tableText.substring(startingIndexes.get(j), tableText.indexOf(
                    "\"", startingIndexes.get(j) + 1
            )));
        }
        return attributes;

    }

}
