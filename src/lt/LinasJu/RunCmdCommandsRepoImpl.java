package lt.LinasJu;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class RunCmdCommandsRepoImpl {
    private static String SUMO_HOME_PATH = "C:\\Program Files (x86)\\Eclipse\\Sumo";

    public void callAndWaitForFinish() {
        try {
            Runtime rt = Runtime.getRuntime();
            //Process pr = rt.exec("cmd /c dir");
            Process pr = rt.exec(CmdCommands.CD + " /c dir");
//            Process pr = rt.exec("c:\\helloworld.exe");

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line;

            while((line=input.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = pr.waitFor();
            System.out.println("Exited with error code "+exitVal);

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
}
