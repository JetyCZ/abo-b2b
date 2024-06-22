package cz.abo.b2b.web.bank;// Import the required packages
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cz.abo.b2b.web.bank.dto.Transaction;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

public class MTLSClient {

    public static String prettyPrintUsingGlobalSetting(String uglyJsonString) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        String prettyJson = null;
        try {
            Object jsonObject = mapper.readValue(uglyJsonString, Object.class);
            prettyJson = mapper.writeValueAsString(jsonObject);
            return prettyJson;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void run() {
    BankResponseParser bankResponseParser = new BankResponseParser();

    while (true) {
        ZonedDateTime from = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).minus(5, ChronoUnit.DAYS);
        ZonedDateTime to = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
        List<Transaction> transactions;

        String responseString = callTransactionApi(from, to);
        System.out.println(prettyPrintUsingGlobalSetting(responseString));
        if (responseString!=null) {
            transactions = bankResponseParser.parse(responseString);
        } else {
            transactions = new ArrayList<>();
        }
        System.out.println("Read transactions: " + transactions.size());
        for (Transaction transaction : transactions) {
            System.out.println();
            if (transactions.size()>1) {
                System.out.println("Current time: " + ZonedDateTime.now());
                System.out.println("Transaction time: " + transactions.get(0).getValueDate());
                break;
            }
// Current time:              2024-03-10T21:04:36.266244016+01:00[Europe/Prague]
// Transaction time (0):      2024-03-10T21:04:35.000+01:00
// Transaction time (1):      2024-03-10T21:01:10.000+01:00

        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

// Print the response to the console


}
    static SSLContext sslContext = prepareSslContext();

    private static String callTransactionApi(ZonedDateTime from, ZonedDateTime to) {

        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();

        String url = "https://api.rb.cz/rbcz/premium/api/accounts/2241315002/CZ/transactions";
        url += "?from=" + formatToBankApiDate(from);
        url += "&to=" + formatToBankApiDate(to);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("X-IBM-Client-Id", "yzFDuXc7pTqsqfr6VWHJu0zF9JDlzrpg");
        httpGet.setHeader("X-Request-Id", "ref-1");
        httpGet.setHeader("Content-Type", "application/json");

        String responseString = null;
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity==null) return null;
            responseString = EntityUtils.toString(entity);

            response.close();
            httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseString;
    }

    @NotNull
    private static String formatToBankApiDate(ZonedDateTime from) {
        return from.withZoneSameInstant(ZoneOffset.UTC).toString().replaceAll("Z", ":00.0Z");
    }

    @NotNull
    private static SSLContext prepareSslContext()  {
        File p12File = new File(MTLSClient.class.getResource("/bank/BankovniAPI.p12").getFile());
        char[] password = "rKolik0123AAA".toCharArray();
        SSLContext sslContext = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(p12File), password);
            sslContext = SSLContext.getInstance("TLS");


            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);

            keyManagerFactory.init(keyStore, password);

            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            sslContext.init(keyManagers, new TrustManager[] {new TrustAllManager()}, null);
        } catch (Exception e) {
            throw new IllegalStateException("Problem creating SSL context", e);
        }
        return sslContext;
    }

    // A custom TrustManager that trusts all certificates
static class TrustAllManager implements X509TrustManager {
public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
public X509Certificate[] getAcceptedIssuers() {return null;}
}
}
