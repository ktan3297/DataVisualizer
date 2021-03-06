package allAlgo;

import algorithms.Classifier;
import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

    // currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    private ApplicationTemplate applicationTemplate;
    private boolean algoRunning = true;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClassifier(DataSet dataset, int maxIterations, int updateInterval, boolean tocontinue, ApplicationTemplate appTemplate) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate = appTemplate;
    }

    @Override
    public void run() {
//        System.out.println(tocontinue());
        ((AppUI) applicationTemplate.getUIComponent()).setThreadRunningBoolean(true);
        if(tocontinue()) {
            for (int i = 1; i <= maxIterations && tocontinue(); i++) {

                int xCoefficient = new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
                int yCoefficient = 10;
                int constant = RAND.nextInt(11);

                // this is the real output of the classifier
                output = Arrays.asList(xCoefficient, yCoefficient, constant);

                // everything below is just for internal viewing of how the output is changing
                // in the final project, such changes will be dynamically visible in the UI
                if (i % updateInterval == 0) {
//                    System.out.printf("Iteration number %d: ", i); //
                    flush();
                }
                if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
//                    System.out.printf("Iteration number %d: ", i);
                    flush();
                    break;
                }

//                System.out.println("----------------------------------");
                double random = RAND.nextDouble();
                if (i % updateInterval == 0 || (i > maxIterations * .6 && RAND.nextDouble() < 0.05)) {
                    Platform.runLater(() -> {
                        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
                        ((AppData) applicationTemplate.getDataComponent()).displayData(output);

                    });

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else{
            for (int i = 1; i <= maxIterations && !tocontinue(); i++) {

                int xCoefficient = new Long(-1 * Math.round((2 * RAND.nextDouble() - 1) * 10)).intValue();
                int yCoefficient = 10;
                int constant = RAND.nextInt(11);

                // this is the real output of the classifier
                output = Arrays.asList(xCoefficient, yCoefficient, constant);

                //everything below is just for internal viewing of how the output is changing
                //in the final project, such changes will be dynamically visible in the UI
                if (i % updateInterval == 0) {
//                    System.out.printf("Iteration number %d: ", i); //
                    flush();
                }
                if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
//                    System.out.printf("Iteration number %d: ", i);
                    flush();
                    break;
                }

//                System.out.println("----------------------------------");
                double random = RAND.nextDouble();
                if (i % updateInterval == 0 || (i > maxIterations * .6 && random < 0.05)) {

//                    System.out.println(i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> {
                        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
                        ((AppData) applicationTemplate.getDataComponent()).displayData(output);
                        ((AppUI) applicationTemplate.getUIComponent()).changeTextofRunButton("Continue");
                        ((AppUI) applicationTemplate.getUIComponent()).makeAlgorithmandRunButtonVisibleAgain();

//                        System.out.println("draw line");

                    });

                    synchronized (this){
                        try {
                            if(i > maxIterations * .6 && random < 0.05 || i > maxIterations){
                                break;
                            }
                            this.wait();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Platform.runLater(() -> ((AppUI) applicationTemplate.getUIComponent()).changeTextofRunButton("Run"));
        }
        ((AppUI) applicationTemplate.getUIComponent()).setThreadRunningBoolean(false);
        ((AppUI) applicationTemplate.getUIComponent()).makeAlgorithmandRunButtonVisibleAgain();

    }

    public synchronized void runagain(){
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // for internal viewing only
    protected void flush() {

        //System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

//    /** A placeholder main method to just make sure this code runs smoothly */
//    public static void main(String... args) throws IOException {
//        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
//        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true, new ApplicationTemplate());
//        classifier.run(); // no multithreading yet
//    }

}