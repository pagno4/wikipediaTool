package ass2.event;

import ass2.controller.Controller;
import ass2.model.classes.WikiLink;
import ass2.model.classes.mygraph.AssignmentGraph;
import ass2.model.classes.mygraph.SimpleGraph;
import ass2.model.services.WikiClient;
import ass2.view.MainFrame;

import java.util.Set;

import io.vertx.core.*;

import javax.swing.*;

public class EventController implements Controller {
    private final MainFrame view;
    public WikiClient wikiClient;
    private AssignmentGraph graph;
    private final Vertx vertx;

    public EventController() {
        // Generate the view.
        this.view = new MainFrame("Event programming", this);
        this.view.setVisible(true);

        this.vertx = Vertx.vertx();
        this.wikiClient = new WikiClient();
        // Generate the model.
        //this.graph = new SimpleGraph(this);
    }

    @Override
    public void fetchConcept(String concept, int entry) {
        // Crea il grafo
        this.reset();

        // Inizia la ricorsione
        this.startRecursion(concept, entry);
    }

    @Override
    public void modelUpdated(String from) {
        SwingUtilities.invokeLater(() -> {
            this.view.display(from);
            this.view.displayNumber(this.graph.getNodeNumber());
        });
    }

    @Override
    public void modelUpdated(String from, String to) {
        SwingUtilities.invokeLater(() -> {
            this.view.display(from, to);
            this.view.displayNumber(this.graph.getNodeNumber());
        });
    }

    private void reset() { this.graph = new SimpleGraph(this);}

    // Parse del concetto e crea nuovi executor per le successive ricorsioni
    private void startRecursion(String concept, int entry) {

        // 1- Termina ricorsione
        if (entry == -1) {
            return;

        // 2- Crea solo il primo nodo: entry == 0
        } else if (entry == this.view.getEntryView()) {

            // Creo il primo vertice per il concetto dato in input nella view
            this.graph.addNode(concept);
            this.log("Ho aggiunto il nodo: " + concept);
        }

        // 3- Parse e ricorsione
        if (entry != 0) {

            WorkerExecutor ex = this.vertx.createSharedWorkerExecutor(concept);
            ex.executeBlocking(promise -> {

                log(" sono un nuovo executor di vertx");

                // Parse
                Set<WikiLink> links = null;
                try {
                    links = this.wikiClient.parseURL(concept);
                } catch (Exception e) {
                    promise.fail(e.getMessage());
                }

                if (links == null) return;

                promise.complete(links);

            }, res -> {

                // Se ha successo
                if (res.succeeded()) {

                   Set<WikiLink> links = (Set<WikiLink>) res.result();

                    // Ricorsione per ogni riferimento trovato
                    for (WikiLink elem : links) {

                        try {
                            //Creo il vertice per il nuovo concetto
                            this.graph.addNode(elem.getText());
                            this.log("Ho aggiunto il nodo: " + elem.getText());

                            try {
                                //Creo l'arco e aggancio il vertice al grafo
                                this.graph.addEdge(concept, elem.getText());

                                // Parto con la ricorsione
                                this.startRecursion(elem.getText(), entry - 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (IllegalArgumentException e) {
                            this.log("Il concetto " + elem.getText() + " è già presente.");
                        }
                    }
                }else if(res.failed()){
                    log("Nessun riferimento per " + concept);
                }
            });
        }
    }

    private void log(String msg) {
        synchronized (System.out) {
            System.out.println("[" + Thread.currentThread().getName() + "]: " + msg);
        }
    }
}