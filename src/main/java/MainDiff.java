import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.Generators;

import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.utils.Pair;
import org.eclipse.jgit.api.errors.GitAPIException;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainDiff {
    public static void main(String[] args) throws IOException, GitAPIException {

//        Run.initGenerators();
//        String file = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/JavaCodeAnalysisTool/src/main/java/GetRepoInfo.java";
//        TreeContext tc = Generators.getInstance().getTree(file); // retrieve the default generator for the file
//        ITree t = tc.getRoot(); // return the root of the tree
//        System.out.println(TreeIoUtils.toLisp(tc).toString()); // displays the tree in LISP syntax
//


//        System.setProperty("gt.pp.path", "/Users/malinda/Documents/RectrofitinMLtoCode/Python/PythonDiff/pythonparser");
//        Run.initGenerators();
//        String file1 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/JavaCodeAnalysisTool/src/main/java/GetRepoInfo.java";
//        String file2 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/JavaCodeAnalysisTool/src/GetRepoInfovc1.java";
//        String file1 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/PythonDiff/src/test/java/source.py";
//        String file2 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/PythonDiff/src/test/java/dest.py";
//        ITree src = Generators.getInstance().getTree(file1).getRoot();
//        ITree dst = Generators.getInstance().getTree(file2).getRoot();
//        Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
//        m.match();
//        ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
//        g.generate();
//        List<Action> actions = g.getActions(); // return the actions
//        for (Action action : actions) {
//            System.out.println(action);
//           // System.out.println(action.getNode().toShortString());
//        }
        Map<String, Pair<String, String>> twoFiles = getTwoFiles(101505285, "11a02750e2446b4ec26118154be67a6042fa2d65");
        Map<String, List<Action>> editActions = getEditActions(twoFiles);
        System.out.println(editActions.keySet());
        getEditActionOfAllProjects();

    }

    private static void getEditActionOfAllProjects()
    {
        BufferedReader reader;
        Map<String,Integer> Stats = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(
                    "./src/main/resources/ML_UPGRADE.txt"));
            String line = reader.readLine();
            System.out.println(line);
            while (line != null) {
                String[] pro_commit = line.split(",");
                Map<String, Pair<String, String>> twoFiles = getTwoFiles(Integer.parseInt(pro_commit[0]),pro_commit[1]);
                Map<String, List<Action>> editActions = getEditActions(twoFiles);

                for (Map.Entry<String, List<Action>> entry : editActions.entrySet()) {
                    for (Action action : entry.getValue()) {
                        int count = Stats.containsKey(action.getName()) ? Stats.get(action.getName()) : 0;
                        Stats.put(action.getName(), count + 1);
                    }
                }

                System.out.println(Stats);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }


    private static Map<String,List<Action>>  getEditActions(Map<String, Pair<String,String>> changes) throws IOException {
        Map<String,List<Action>> editActions = new HashMap<>();
        Iterator<Map.Entry<String, Pair<String,String>> > itr = changes.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, Pair<String,String>> entry = itr.next();
            System.setProperty("gt.pp.path", "/Users/malinda/Documents/RectrofitinMLtoCode/Python/PythonDiff/pythonparser");
            Run.initGenerators();

            final Path path_s = Files.createTempFile("SOURCE", ".py");
            final Path path_d = Files.createTempFile("DEST", ".py");

            //Writing data here
            byte[] buf_s = entry.getValue().first.getBytes();
            byte[] buf_d = entry.getValue().second.getBytes();
            Files.write(path_s, buf_s);
            Files.write(path_d, buf_d);
            try{
                ITree src = Generators.getInstance().getTree(path_s.toString()).getRoot();
                ITree dst = Generators.getInstance().getTree(path_d.toString()).getRoot();
                Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
                m.match();
                ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
                g.generate();
                List<Action> actions = g.getActions(); // return the actions
                if (actions.size()>0) {
                    editActions.put(entry.getKey(), actions);
                    path_s.toFile().deleteOnExit();
                    path_d.toFile().deleteOnExit();
                }
            }
            catch (NullPointerException x) {
                System.out.println(x);
            }
            catch (RuntimeException x){
                System.out.println(x);
            }


        }
        return editActions;
    }

    private static String getRepoPath(Integer projectID) throws FileNotFoundException {
        String RepoPy3 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/Pancakes_ML/";
        String RepoPy3v2 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/Pancakes_ML_3p6Set1/";
        String RepoPy2 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/Pancakes_ML_2point7_set1/";
        String RepoPy2v2 = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/Pancakes_ML_2point7_set2/";
        String winter = "/Users/malinda/Documents/RectrofitinMLtoCode/Python/Winter/";

        if (isValidPath(RepoPy3+projectID.toString()))
            return RepoPy3+projectID.toString();
        else if(isValidPath(RepoPy3v2+projectID.toString()))
            return RepoPy3v2+projectID.toString();
        else if(isValidPath(RepoPy2+projectID.toString()))
            return RepoPy2+projectID.toString();
        else if(isValidPath(RepoPy2v2+projectID.toString()))
            return RepoPy2v2+projectID.toString();
        else if(isValidPath(winter+projectID.toString()))
            return winter+projectID.toString();
        else
            throw new FileNotFoundException(projectID.toString());

    }

    public static boolean isValidPath(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    public static Map<String, Pair<String,String>> getTwoFiles(Integer projectID,String Hash) throws IOException, GitAPIException {
        GitRepo g = new GitRepo();
        g.init(getRepoPath(projectID));
        Map<String, Pair<String,String>>  files = g.getOldNewChangedFiles(Hash);
        return files;
    }
}
