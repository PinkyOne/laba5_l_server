import com.google.gson.Gson;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class Account {
    private String accountName = "Ivanov";
    private long accountNumber = 10000;
    private int pin = 1111;

    Account() {
        // no-args constructor
    }

    Account(String accountName, long accountNumber, int pin) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.pin = pin;
    }
}

class Response {
    String data;
    String error;
}

class LoginResponse {
    String token;
    String error;
}

class BalanceResponse {
    int accountNumber;
    long balanceCheck;
    long balanceSaving;
}

public class ControllerTest {

    @Test
    public void loginSuccessTest() {
        Account obj = new Account();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        String response = executePost("http://localhost:9010/teller/gettoken/1", json);
        LoginResponse loginResp = gson.fromJson(response, LoginResponse.class);

        assertTrue(loginResp.token != null);
    }

    @Test
    public void loginFailedTest() {
        Account obj = new Account("Petrov", 10001, 1234);
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        String response = executePost("http://localhost:9010/teller/gettoken/1", json);
        LoginResponse loginResp = gson.fromJson(response, LoginResponse.class);

        assertTrue(response, loginResp.error != null);
    }

    @Test
    public void withdrawCashTest() {
        Account obj = new Account();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        String response = executePost("http://localhost:9010/teller/gettoken/1", json);
        LoginResponse loginResp = gson.fromJson(response, LoginResponse.class);
        String token = loginResp.token;

        response = executeGet("http://localhost:9010/teller/checkbalance/" +
                token + "/" +
                "10000");
        BalanceResponse balance = gson.fromJson(response, BalanceResponse.class);
        assertEquals(response,10000, balance.accountNumber);



        response = executeGet("http://localhost:9010/teller/withdraw/" +
                token + "/" +
                "10000/10/0");
        Response resp = gson.fromJson(response, Response.class);
        assertTrue(response,resp.data != null);

        response = executeGet("http://localhost:9010/teller/checkbalance/" +
                token + "/" +
                "10000");
        BalanceResponse balance1 = gson.fromJson(response, BalanceResponse.class);
        assertEquals(response,balance.balanceCheck - 10, balance1.balanceCheck);

        response = executeGet("http://localhost:9010/teller/withdraw/" +
                token + "/" +
                "10000/10/1");
        Response resp2 = gson.fromJson(response, Response.class);
        assertTrue(response,resp2.data != null);

        response = executeGet("http://localhost:9010/teller/checkbalance/" +
                token + "/" +
                "10000");
        BalanceResponse balance2 = gson.fromJson(response, BalanceResponse.class);
        assertEquals(response,balance.balanceSaving - 10, balance2.balanceSaving);



        response = executeGet("http://localhost:9010/teller/withdraw/" +
                token + "/" +
                "10000/110/0");
        Response respFailed = gson.fromJson(response, Response.class);
        assertTrue(response,respFailed.error != null);

        response = executeGet("http://localhost:9010/teller/checkbalance/" +
                token + "/" +
                "10000");
        BalanceResponse balance3 = gson.fromJson(response, BalanceResponse.class);
        assertEquals(response,balance.balanceSaving - 10, balance3.balanceSaving);
        assertEquals(response,balance.balanceCheck - 10, balance3.balanceCheck);

    }

    @Test
    public void transferCashTest() {
        Account obj = new Account();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        String response = executePost("http://localhost:9010/teller/gettoken/1", json);
        LoginResponse loginResp = gson.fromJson(response, LoginResponse.class);
        String token = loginResp.token;

        response = executeGet("http://localhost:9010/teller/checkbalance/" +
                token + "/" +
                "10000");
        BalanceResponse balance = gson.fromJson(response, BalanceResponse.class);
        assertEquals(response,10000, balance.accountNumber);

        response = executeGet("http://localhost:9010/teller/transfer/" +
                token + "/" +
                "10000/100");
        Response resp = gson.fromJson(response, Response.class);
        assertTrue(resp.data != null);

        response = executeGet("http://localhost:9010/teller/checkbalance/" +
                token + "/" +
                "10000");
        BalanceResponse balance1 = gson.fromJson(response, BalanceResponse.class);
        assertEquals(response,balance.balanceCheck - 100, balance1.balanceCheck);
        assertEquals(response,balance.balanceSaving + 100, balance1.balanceSaving);

    }

    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String executeGet(String targetURL) {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
