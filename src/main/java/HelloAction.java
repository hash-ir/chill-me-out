import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class HelloAction extends AnAction {
    int prv,prv_2=0, counter=0;
    double alpha = 1.0;
    boolean state = FALSE, wasAngry=FALSE;
    Thread workerThread;
    Process p;
    private static final int STRESS_THRESHOLD = 5;
    private static final String FILE_PATH = "C:\\Users\\h80054936\\Documents\\intellibuddy\\src\\stress.txt";
    public static final NotificationGroup GROUP_DISPLAY_ID_INFO =
            new NotificationGroup("My notification group",
                    NotificationDisplayType.BALLOON, true);

    public HelloAction() {
        super("IntelliBuddy");
    }

    public void actionPerformed(AnActionEvent event) {
        if (state == FALSE) {
            // Enable
            System.out.println("State: 0");
            workerThread = new Thread() {
                public void run() {
                    executePythonScriptAsync();
                    showFirstNotification(event);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startTimer();
                }
            };
            workerThread.start();
            state = TRUE;
        } else {
            // Disable
            System.out.println("State: 1");
            workerThread.stop();
            state = FALSE;
            p.destroy();
        }
    }

    private void executePythonScriptAsync() {
        new Thread(() -> {
            //ProcessBuilder pb = new ProcessBuilder("python","\"python C:\\\\Users\\\\h80054936\\\\Documents\\\\intellibuddy\\\\src\\\\model.py\"");
            System.out.println("In thread async");
            executePythonScript();
            //Process p = pb.start();
            // more code to capture the output here
        }).start();
    }

    private void startTimer() {
        TimerTask task = new FileWatcher(new File(FILE_PATH)) {
            protected void onChange(File file) {
                // here we code the action on a change
                System.out.println("File " + file.getName() + " have change !");
                int stressValue = getStressValue();
                // Tn+1 = a*Tn + (1-a)Tn-1
                double T;
                T=alpha*stressValue+(1-alpha)*prv_2;
                prv_2=(int)T;
                int diff=  Math.abs(stressValue - prv_2);
                if (stressValue >= 65) {
                    wasAngry=TRUE;
                    //p.destroy();
                    showStressNotification(stressValue);
                    counter=0;
                    System.out.println("========================================================");
                    System.out.println(prv_2);
                    System.out.println("========================================================");
                    //this.cancel();
                } else {
                    if(wasAngry==TRUE) {
                        showChillNotification();

                    }
                    wasAngry=FALSE;
                }

            }
        };

        Timer timer = new Timer();
        // repeat the check every second
        timer.schedule(task, new Date(), 4000);

    }

    private void showStressNotification(int stressValue) {

        try {
            JsonElement rootobj = getRandomProgrammerJoke();
            showMyMessage("Hello Anand , you are " + stressValue + " % stressed. Take a break!\n" + rootobj.getAsJsonObject().get("setup").getAsString() + "\n" + rootobj.getAsJsonObject().get("punchline"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonElement getRandomProgrammerJoke() throws IOException {
        String sURL = "https://official-joke-api.appspot.com/jokes/programming/random"; //just a string

        // Connect to the URL using java's native library
        URL url = new URL(sURL);
        URLConnection request = url.openConnection();
        request.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonArray rootArr = root.getAsJsonArray(); //May be an array, may be an object.
        JsonElement rootobj = rootArr.get(0);
        //String question = rootobj.get("setup").getAsString();
        System.out.println(rootobj.getAsJsonObject().get("setup").getAsString());
        System.out.println(rootobj.getAsJsonObject().get("punchline").getAsString());
        return rootobj;
    }


    public void showFirstNotification(AnActionEvent event) {
        showChillNotification();
    }

    void executePythonScript() {

        String s = null;

        try {

            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            p = Runtime.getRuntime().exec("python C:\\Users\\h80054936\\Documents\\intellibuddy\\src\\model.py");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            //System.exit(0);
        } catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    private void showChillNotification() {
        showMyMessage("Hello Anand , you are cool !  Keep Chilling");
    }

    private int getStressValue() {
        File file = new File(FILE_PATH);
        String st = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (!((st = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(st);
            break;
        }
        if (st != null) {
            prv = Integer.parseInt(st);
            return prv;
        } else {
            return prv;
        }
    }

    void showMyMessage(String message) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Notification notification = GROUP_DISPLAY_ID_INFO.createNotification(message, NotificationType.INFORMATION);
                Project[] projects = ProjectManager.getInstance().getOpenProjects();
                Notifications.Bus.notify(notification, projects[0]);
            }
        });
    }
}