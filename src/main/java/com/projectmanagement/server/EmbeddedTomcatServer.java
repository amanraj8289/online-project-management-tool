package com.projectmanagement.server;

import com.projectmanagement.config.AppConfig;
import com.projectmanagement.config.DatabaseConnection;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Embedded Apache Tomcat 10.1 Server.
 * Allows running the application standalone with `mvn exec:java` or via IDE run configurations
 * without requiring external Tomcat server installation.
 */
public class EmbeddedTomcatServer {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedTomcatServer.class);

    public static void main(String[] args) throws Exception {
        int port = AppConfig.getInt("server.port", 8080);
        String webappDirLocation = "src/main/webapp/";

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Trigger connector creation

        // Initialize database pool
        try {
            logger.info("Initializing database connection pool...");
            DatabaseConnection.initialize();
        } catch (Exception e) {
            logger.warn("Database initialization warning: {}. The server will start and retry connection upon requests.", e.getMessage());
        }

        File webappDir = new File(webappDirLocation);
        if (!webappDir.exists()) {
            webappDir = new File(".");
        }

        String contextPath = "/project-management-tool";
        StandardContext ctx = (StandardContext) tomcat.addWebapp(contextPath, webappDir.getAbsolutePath());
        ctx.setParentClassLoader(EmbeddedTomcatServer.class.getClassLoader());
        ctx.setReloadable(true);

        // Configure class resources for JSP compilation and Servlet mapping
        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        logger.info("=================================================================");
        logger.info(" Starting Online Project Management Tool (Tomcat 10.1 Embedded)");
        logger.info(" Application URL: http://localhost:{}{}", port, contextPath);
        logger.info(" Login Page:      http://localhost:{}{}/login", port, contextPath);
        logger.info(" Default Admin:   admin@promanage.com / Admin@123");
        logger.info("=================================================================");

        tomcat.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down Embedded Tomcat...");
            try {
                tomcat.stop();
                DatabaseConnection.shutdown();
            } catch (Exception e) {
                logger.error("Error shutting down tomcat: {}", e.getMessage());
            }
        }));

        tomcat.getServer().await();
    }
}
