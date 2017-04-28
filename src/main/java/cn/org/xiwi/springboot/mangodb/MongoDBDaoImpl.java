package cn.org.xiwi.springboot.mangodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/*
 * mongodb数据库链接池
 */
public class MongoDBDaoImpl implements MongoDBDao {
	private MongoClient mongoClient = null;
	private static final MongoDBDaoImpl mongoDBDaoImpl = new MongoDBDaoImpl();// 饿汉式单例模式

	private MongoDBDaoImpl() {
		if (mongoClient == null) {
			MongoClientOptions.Builder buide = new MongoClientOptions.Builder();
			buide.connectionsPerHost(100);// 与目标数据库可以建立的最大链接数
			buide.connectTimeout(1000 * 60 * 20);// 与数据库建立链接的超时时间
			buide.maxWaitTime(100 * 60 * 5);// 一个线程成功获取到一个可用数据库之前的最大等待时间
			buide.threadsAllowedToBlockForConnectionMultiplier(100);
			buide.maxConnectionIdleTime(0);
			buide.maxConnectionLifeTime(0);
			buide.socketTimeout(0);
			buide.socketKeepAlive(true);

			MongoClientOptions myOptions = buide.build();

			// 连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
			// ServerAddress()两个参数分别为 服务器地址 和 端口
			ServerAddress serverAddress = new ServerAddress("10.10.152.26", 27017);
			List<ServerAddress> addrs = new ArrayList<ServerAddress>();
			addrs.add(serverAddress);

			// MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
			MongoCredential credential = MongoCredential.createScramSha1Credential("xiwi", "mongo_test",
					"xiwi123".toCharArray());
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);

			mongoClient = new MongoClient(addrs, credentials, myOptions);
		}
	}

	public static MongoDBDaoImpl getMongoDBDaoImpl() {
		return mongoDBDaoImpl;
	}

	@Override
	public DB getDb(String dbName) {
		return mongoClient.getDB(dbName);
	}

	@Override
	public DBCollection getCollection(String dbName, String collectionName) {
		DB db = mongoClient.getDB(dbName);
		return db.getCollection(collectionName);
	}

	@Override
	public boolean inSert(String dbName, String collectionName, String keys, Object values) {
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);
		long num = dbCollection.count();
		BasicDBObject doc = new BasicDBObject();
		doc.put(keys, values);
		dbCollection.insert(doc);
		if (dbCollection.count() - num > 0) {
			System.out.println("添加数据成功！！！");
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(String dbName, String collectionName, String keys, Object values) {
		WriteResult writeResult = null;
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);
		BasicDBObject doc = new BasicDBObject();
		doc.put(keys, values);
		writeResult = dbCollection.remove(doc);
		if (writeResult.getN() > 0) {
			System.out.println("删除数据成功!!!!");
			return true;
		}
		return false;
	}

	@Override
	public ArrayList<DBObject> find(String dbName, String collectionName, int num) {
		int count = num;
		ArrayList<DBObject> list = new ArrayList<DBObject>();
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);
		DBCursor dbCursor = dbCollection.find();
		if (num == -1) {
			while (dbCursor.hasNext()) {
				list.add(dbCursor.next());
			}
		} else {
			while (dbCursor.hasNext()) {
				if (count == 0)
					break;
				list.add(dbCursor.next());
				count--;
			}
		}
		return list;
	}

	@Override
	public boolean update(String dbName, String collectionName, DBObject oldValue, DBObject newValue) {
		WriteResult writeResult = null;
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);
		writeResult = dbCollection.update(oldValue, newValue);
		if (writeResult.getN() > 0) {
			System.out.println("数据更新成功");
			return true;
		}
		return false;
	}

	public void name(String dbName, String collectionName, DBObject oldValue, DBObject newValue) {
		MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
		try {
	        MongoCollection<Document> animals = mongoDatabase.getCollection(collectionName);

	        Document animal = new Document("animal", "monkey");

	        animals.insertOne(animal);
	        animal.remove("animal");
	        animal.append("animal2", "cat");
	        animals.insertOne(animal);
	        animal.remove("animal2");
	        animal.append("animal", "lion");
	        animals.insertOne(animal);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@Override
	public boolean isExit(String dbName, String collectionName, String key, Object value) {
		DB db = mongoClient.getDB(dbName);
		DBCollection dbCollection = db.getCollection(collectionName);
		BasicDBObject doc = new BasicDBObject();
		doc.put(key, value);
		if (dbCollection.count(doc) > 0) {
			return true;
		}
		return false;
	}

	public static void main(String args[]) {
		MongoDBDaoImpl mongoDBDaoImpl = MongoDBDaoImpl.getMongoDBDaoImpl();
		ArrayList<DBObject> list = new ArrayList<DBObject>();
		list = mongoDBDaoImpl.find("mongo_test", "runoob", -1);
		System.out.println(list.get(0));
		System.out.println(list.get(0).get("name"));
		System.out.println(list.get(0).get("_id"));
		mongoDBDaoImpl.inSert("mongo_test", "runoob", "hello", "hello value");
		mongoDBDaoImpl.name("mongo_test", "runoob", null, null);
	}
}
