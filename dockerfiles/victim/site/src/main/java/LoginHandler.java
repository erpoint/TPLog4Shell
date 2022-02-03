import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements HttpHandler {

    @Override

    public void handle(HttpExchange httpExchange) throws IOException {
        if("GET".equals(httpExchange.getRequestMethod())) {
            handleGetRequest(httpExchange);
            return;

        }

        if("POST".equals(httpExchange.getRequestMethod())) {
            handlePost(httpExchange);
            return;
        }
    }


    private void handleGetRequest(HttpExchange httpExchange) {
        try {
            OutputStream outputStream = httpExchange.getResponseBody();
            String content = IOUtils.toString(getClass().getResourceAsStream("/static/index.html"));

            httpExchange.sendResponseHeaders(200, content.length());

            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePost(HttpExchange httpExchange) {
        try {
            OutputStream outputStream = httpExchange.getResponseBody();

            String body = IOUtils.toString(httpExchange.getRequestBody());
            HashMap<String, String> params = new HashMap<>();
            Arrays.stream(body.split("&"))
                    .map(str -> str.split("="))
                    .forEach(entry -> {
                        params.put(entry[0], entry[1]);
                    });

            if(!params.containsKey("username") || !params.containsKey("password")) {
                String response = "Missing param";
                httpExchange.sendResponseHeaders(422, response.length());

                outputStream.write(response.getBytes());
                outputStream.flush();
                outputStream.close();
                return;
            }

            // FLEMME C'EST L'HEURE DE SE COUCHER
            String username = params.get("username")
            .replaceAll("%24", "\\$")
            .replaceAll("%7B", "{")
            .replaceAll("%3A", ":")
            .replaceAll("%2F", "/")
            .replaceAll("%7D", "}");

            Main.logger.info(username + " a essayé de se login");


            try (Connection con = DataSource.getConnection();
                 PreparedStatement stmt = con.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
                )
            {
                stmt.setString(1, params.get("username"));
                stmt.setString(2, params.get("password"));

                ResultSet result = stmt.executeQuery();

                if(!result.next()) {
                    String content = IOUtils.toString(getClass().getResourceAsStream("/static/mauvaisTuEs.html"));

                    httpExchange.sendResponseHeaders(400, content.length());

                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    return;
                }

                if(result.getBoolean("isAdmin")) {
                    String content = IOUtils.toString(getClass().getResourceAsStream("/static/bravoAdmin.html"));

                    httpExchange.sendResponseHeaders(200, content.length());

                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    return;
                }
                else {
                    String content = IOUtils.toString(getClass().getResourceAsStream("/static/bravo.html"));

                    httpExchange.sendResponseHeaders(200, content.length());

                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    return;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {

        OutputStream outputStream = httpExchange.getResponseBody();

        StringBuilder htmlBuilder = new StringBuilder();

        //redirection

        htmlBuilder.append("<html>").

                append("<body>").

                append("<h1>").

                append("Hello ")

                .append(requestParamValue)

                .append("</h1>")

                .append("</body>")

                .append("</html>");

        // encode HTML content

        String htmlResponse = htmlBuilder.toString();



        // this line is a must

        httpExchange.sendResponseHeaders(200, htmlResponse.length());

        outputStream.write(htmlResponse.getBytes());

        outputStream.flush();

        outputStream.close();

    }

}
