package ass2.executor;

import ass2.GuiInterface;
import ass2.MyGraph;

public class Main {

    public static void main(String[] args) {

        //Gui
        GuiInterface gui = new GuiInterface();

        //Graph
        MyGraph graph = new MyGraph(gui);

        //Wikipedia Client
        WikipediaClient wc = new WikipediaClient(graph);

        // WikipediaExecutor
        WikipediaExecutor we = new WikipediaExecutor(gui, wc, graph);

        //Faccio partire il primo executor
        we.mainExec();
        we.taskExec();

    }
}
