package satd_detector.core.test;

import org.apache.commons.cli.*;
import satd_detector.core.utils.SATDDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static satd_detector.core.utils.FileUtil.readLinesFromFile;
import static satd_detector.core.utils.FileUtil.writeLinesToFile;

public class Test {
    private static String prompt = ">";

    public static void main(String[] args) {
        test(args);
    }

    public static void test(String[] args) {
        Options opts = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cl;
        String modelDir;
        String commentFile;
        List<String> comments;
        try {
            cl = parser.parse(opts, args);
            modelDir = cl.getOptionValue("model_dir");
            commentFile = cl.getOptionValue("comment_file");
        } catch (ParseException e) {
            // e.printStackTrace();
            formatter.printHelp("test", opts);
            return;
        }

        if (cl.hasOption("h")) {
            formatter.printHelp("test", opts);
            return;
        }
        modelDir = cl.getOptionValue("model_dir");
        if (modelDir != null) {
            File modelFile = new File(modelDir);
            if (!modelFile.isDirectory()) {
                formatter.printHelp("test", opts);
            }
        }
        commentFile = cl.getOptionValue("comment_file");
        if (commentFile != null) {
            File cFile = new File(commentFile);
            if (!cFile.isDirectory()) {
                formatter.printHelp("test", opts);
            }
        }

        SATDDetector detector = new SATDDetector(modelDir);
        if (commentFile == null) {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                try {
                    System.out.print(prompt);
                    String comment = br.readLine();
                    if (comment.equals("/exit")) {
                        System.out.println("bye!");
                        break;
                    }
                    if (detector.isSATD(comment))
                        System.out.println("SATD");
                    else
                        System.out.println("Not SATD");
                    // System.out.println(comment);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } else {
            List<String> satdResults = new ArrayList<>();
            comments = readLinesFromFile(commentFile);
            for (String comment : comments) {
                if (detector.isSATD(comment)) satdResults.add("SATD");
                else satdResults.add("not");
            }
            writeLinesToFile(satdResults, commentFile + ".result");
        }
    }

    private static Options createOptions() {
        Options opts = new Options();
        opts.addOption("h", false, "Show help message");
        opts.addOption("model_dir", true,
                "Dir which stores all the models. Using build-in models if not specified.");
        opts.addOption("comment_file", true,
                "File which has all the comments.");
        return opts;
    }
}
