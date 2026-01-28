import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class helloworld {
    
    // Jenkins configuration
    private static final String JENKINS_URL = "http://localhost:8080";
    private static final String JENKINS_USER = "admin";
    private static final String JENKINS_TOKEN = "your-api-token";
    
    // Jenkins Pipeline Builder
    public static class JenkinsPipeline {
        private String jobName;
        private List<String> stages;
        
        public JenkinsPipeline(String jobName) {
            this.jobName = jobName;
            this.stages = new ArrayList<>();
        }
        
        public JenkinsPipeline addStage(String stageName) {
            this.stages.add(stageName);
            return this;
        }
        
        public String generatePipeline() {
            StringBuilder pipeline = new StringBuilder();
            pipeline.append("pipeline {\n");
            pipeline.append("    agent any\n");
            pipeline.append("    stages {\n");
            
            for (String stage : stages) {
                pipeline.append("        stage('").append(stage).append("') {\n");
                pipeline.append("            steps {\n");
                pipeline.append("                echo 'Running ").append(stage).append("...'\n");
                pipeline.append("            }\n");
                pipeline.append("        }\n");
            }
            
            pipeline.append("    }\n");
            pipeline.append("}\n");
            return pipeline.toString();
        }
    }
    
    // Jenkins API Client
    public static class JenkinsClient {
        
        public static String triggerBuild(String jobName) throws Exception {
            String apiUrl = JENKINS_URL + "/job/" + jobName + "/buildWithParameters";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("POST");
            connection.setBasicAuth(JENKINS_USER, JENKINS_TOKEN);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                return "Build triggered successfully for: " + jobName;
            } else {
                return "Failed to trigger build. Response code: " + responseCode;
            }
        }
        
        public static String getBuildStatus(String jobName, int buildNumber) throws Exception {
            String apiUrl = JENKINS_URL + "/job/" + jobName + "/" + buildNumber + "/api/json";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setBasicAuth(JENKINS_USER, JENKINS_TOKEN);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return response.toString();
        }
    }
    
    // Main method - Demo
    public static void main(String[] args) {
        // Create and display Jenkins pipeline
        JenkinsPipeline pipeline = new JenkinsPipeline("MyProject")
            .addStage("Checkout")
            .addStage("Build")
            .addStage("Test")
            .addStage("Deploy");
        
        System.out.println("=== Generated Jenkins Pipeline ===");
        System.out.println(pipeline.generatePipeline());
        
        // Example: Trigger build
        try {
            String result = JenkinsClient.triggerBuild("MyProject");
            System.out.println("\n=== Build Trigger Result ===");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
