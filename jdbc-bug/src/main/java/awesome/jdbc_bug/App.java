package awesome.jdbc_bug;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;

public class App extends AbstractVerticle {
	
	public void start() throws Exception {
		JsonObject JDBC_CONFIG = new JsonObject().put("url", "jdbc:oracle:thin:@<MYHOST>:1521:<MYDB>").put("driver_class", "oracle.jdbc.OracleDriver").put("user","<MYUSER>").put("password", "<MYPASSWORD>").put("max_idle_time", 360).put("max_pool_size", 30);
		
		JDBCClient oracle = JDBCClient.createShared(Vertx.vertx(), JDBC_CONFIG);
		
		oracle.rxGetConnection().flatMap(conn ->  conn.setOptions(new SQLOptions())
			.rxQuery("SELECT * FROM <MYTABLE>")
			.doOnError(err -> System.out.println(err))
			.doAfterTerminate(conn::close)
			.doFinally(conn::close)
		).map(resultSet -> resultSet.toJson().getJsonArray("rows"))
		.subscribe(jsonArray -> System.out.println(jsonArray.encodePrettily()));
		
		
	}
	
}