import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.LDAPException;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    private  static  final String LDAP_BASE = "dc=example,dc=com" ;

    public static void main(String[] args) throws LDAPException, UnknownHostException {
        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
        config.setListenerConfigs(new InMemoryListenerConfig(
                "listen" ,
                InetAddress.getByName( "0.0.0.0" ),
                9000,
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()
        ));

        config.addInMemoryOperationInterceptor(new OperationInterceptor());


        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
        System.out.println( "LDAP server listening on 0.0.0.0:" + 9000);
        ds.startListening();
    }
}
