package gui;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import models.Graph;
import models.Edge;
import models.Graph;
import models.Node;
public class LogActions {

    private Graph graph;

    private GraphPanel graphPanel;

    private  String shortestPath;

    private String algorithmName;

    private int maximumFlow;

    private int optDist;

    private boolean loggingEnabled;

    private static final Logger LOGGER = Logger.getLogger(LogActions.class.getName());
    private static boolean loggerConfigured = false;


    public LogActions(Graph graph, GraphPanel graphPanel, boolean loggingEnabled,
                      String shortestPath, String algorithmName, int optDist){
        this.graph = graph;
        this.graphPanel = graphPanel;
        this.shortestPath = shortestPath;
        this.algorithmName = algorithmName;
        this.optDist = optDist;
        this.loggingEnabled = loggingEnabled;
    }
    public LogActions(Graph graph, GraphPanel graphPanel, int maximumFlow){
        this.graph = graph;
        this.graphPanel = graphPanel;
        this.maximumFlow = maximumFlow;
    }

    public void log(){
        setupLogger();

        LocalDateTime currentDateTime = LocalDateTime.now();
        // Format the date and time if needed
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(dateTimeFormatter);
        LOGGER.info(algorithmName + "\t\t" + "Time: " + formattedDateTime + "\n");
        LOGGER.info("Node List: " + graph.getNodes().toString());
        LOGGER.info("Edge List: " + graph.getEdges().toString());
        if (algorithmName.equals("Dijkstra's Algorithm" ) || algorithmName.equals("Bellman Ford Algorithm") ||
                algorithmName.equals("Topological Ordering Algorithm") ||algorithmName.equals("Floyd Warshall Algorithm")){
            LOGGER.info("Shortest Path From " + graph.getSource().toString() + " to " +
                    graph.getDestination() + ": " + shortestPath + "              Total Distance: " + optDist);
        }
        LOGGER.info("Algorithm Completed \n\n");

        // Close the logger
        closeLogger();
    }
    private void setupLogger() {
        if(!loggerConfigured){
            try {
                // Set the log file location to the user's Documents folder
                String os = System.getProperty("os.name").toLowerCase();
                String userHome = System.getProperty("user.home");
                Path logFolderPath;

                if (os.contains("win")) {
                    logFolderPath = Paths.get(userHome, "Documents", "GraphAlgorithmsOnGUILog");
                } else if (os.contains("mac")) {
                    logFolderPath = Paths.get(userHome, "Documents", "GraphAlgorithmsOnGUILog");
                } else {
                    // For other operating systems, you may need to adjust the path accordingly
                    logFolderPath = Paths.get(userHome, "GraphAlgorithmsOnGUILog");
                }

                // Create log folder if it does not exist
                if (!Files.exists(logFolderPath)) {
                    Files.createDirectories(logFolderPath);
                }

                // Get the current date for the log file name
                LocalDateTime  currentDate = LocalDateTime.now();
                // Define the desired date format
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

                // Format the date using the specified format
                String formattedDate = currentDate.format(dateTimeFormatter);

                // Create log file
                Path logFilePath = logFolderPath.resolve("GraphAlgorithmsLog_" + formattedDate + ".txt");

                // Check if the log file already exists
                if (!Files.exists(logFilePath)) {
                    Files.createFile(logFilePath);
                }

                // Remove existing handlers
                Logger rootLogger = Logger.getLogger("");
                for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
                    rootLogger.removeHandler(handler);
                }

                // Create a file handler that appends log entries to the log file
                FileHandler fileHandler = new FileHandler(logFilePath.toString(), true);

                // Create a simple formatter for the log entries
                SimpleFormatter formatter = new SimpleFormatter();
                fileHandler.setFormatter(formatter);

                // Set the logging level
                LOGGER.setLevel(Level.INFO);

                // Add the file handler to the logger if logging is enabled
                if(loggingEnabled)
                    LOGGER.addHandler(fileHandler);

                loggerConfigured = true;


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void closeLogger() {
        // Close the logger
        Logger rootLogger = Logger.getLogger("");
        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
            handler.close();
        }
    }
}
