package helloworld;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String db   = System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");

        // まずはSSL無しで疎通確認（後でSSL推奨設定に変える）
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&allowPublicKeyRetrieval=true";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String body = String.format("{\"message\":\"DB CONNECT OK\",\"dbHost\":\"%s\",\"dbName\":\"%s\"}", host, db);
            return response.withStatusCode(200).withBody(body);
        } catch (Exception e) {
            // エラー切り分け用に最低限だけ返す（パスワードは絶対出さない）
            String body = String.format("{\"message\":\"DB CONNECT NG\",\"error\":\"%s\",\"detail\":\"%s\"}",
                    e.getClass().getSimpleName(),
                    (e.getMessage() == null ? "" : e.getMessage()).replace("\"", "'"));
            return response.withStatusCode(500).withBody(body);
        }
    }
}