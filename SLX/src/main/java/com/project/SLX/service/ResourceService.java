package com.project.SLX.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ResourceService {
    private final Path resourceDirectory;

    @Autowired
    public ResourceService() {
        this.resourceDirectory = Paths.get("src", "main", "resources", "static", "cache");
    }

    public String getCSS(String cssName) {
        String cssFile = "";

        // Retrieve cached css
        ClassPathResource cssCache = new ClassPathResource("/static/cache/" + cssName + ".css");
        // Retrieve css config
        ClassPathResource cssConfig = new ClassPathResource("/static/config/" + cssName + ".json");

        // Check if cache already exists
        if (cssCache.exists()) {
            // If cache already exists, check if any of the files have been modified after the cached was created
            // If true, create new css file and rebuild cache
            try {
                long lastModifiedCache = cssCache.getFile().lastModified();
                File file2 = new File(this.resourceDirectory.toString() + "/" + cssName + ".css");

                if (cssConfig.exists() && cssConfig.lastModified() > lastModifiedCache) {
                    cssFile = createCSS(cssConfig);
                    cssCache
                            .getFile()
                            .delete();
                    file2.delete();
                    createCacheFile(cssFile, cssName);

                    return cssFile;
                }

                File file;

                file = new ClassPathResource("/static/css/libs/bootstrap.min.css").getFile();

                if (file.lastModified() > lastModifiedCache) {
                    cssFile = createCSS(cssConfig);
                    cssCache
                            .getFile()
                            .delete();
                    file2.delete();
                    createCacheFile(cssFile, cssName);

                    return cssFile;
                }

                if (cssConfig.exists()) {
                    JSONObject configObj = getJSONFromFile(cssConfig);

                    if (configObj.has("libs")) {
                        JSONArray libs = configObj.getJSONArray("libs");

                        for (int i = 0; i < libs.length(); i++) {
                            file = new ClassPathResource("/static/css/libs/" + libs.getString(0) + ".css").getFile();

                            if (file.lastModified() > lastModifiedCache) {
                                cssFile = createCSS(cssConfig);
                                cssCache
                                        .getFile()
                                        .delete();
                                file2.delete();
                                createCacheFile(cssFile, cssName);

                                return cssFile;
                            }
                        }
                    }

                    file = new ClassPathResource("/static/css/main.css").getFile();

                    if (file.lastModified() > lastModifiedCache) {
                        cssFile = createCSS(cssConfig);
                        cssCache
                                .getFile()
                                .delete();
                        file2.delete();
                        createCacheFile(cssFile, cssName);

                        return cssFile;
                    }

                    if (configObj.has("own")) {
                        JSONArray own = configObj.getJSONArray("own");

                        for (int i = 0; i < own.length(); i++) {
                            file = new ClassPathResource("/static/css/" + own.getString(0) + ".css").getFile();

                            if (file.lastModified() > lastModifiedCache) {
                                cssFile = createCSS(cssConfig);
                                cssCache
                                        .getFile()
                                        .delete();
                                file2.delete();
                                createCacheFile(cssFile, cssName);

                                return cssFile;
                            }
                        }
                    }

                    return getFileContents(cssCache);
                } else {
                    file = new ClassPathResource("/static/css/main.css").getFile();

                    if (file.lastModified() > lastModifiedCache) {
                        cssFile = createCSS(cssConfig);
                        cssCache
                                .getFile()
                                .delete();
                        file2.delete();
                        createCacheFile(cssFile, cssName);

                        return cssFile;
                    }

                    return getFileContents(cssCache);
                }
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();

                return cssFile;
            }
        } else {
            // If cached doesn't exist, create the css file and the cache file
            try {
                cssFile = createCSS(cssConfig);
                createCacheFile(cssFile, cssName);
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();

                return cssFile;
            }
        }

        return cssFile;
    }

    // Helper methods
    private void createCacheFile(String cssFile, String cssName) throws IOException {
        File file = new File(
    new ClassPathResource("application.properties")
                .getFile()
                .getParent() +
                "/static/cache/" +
                cssName +
                ".css"
        );

        if (file.createNewFile()) {
            // Creates it only if it doesn't exist
            FileOutputStream is = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write(cssFile);
            w.close();
        }

        // Create the file in the src as well
        File file2 = new File(this.resourceDirectory.toString() + "/" + cssName + ".css");
        if (file2.createNewFile()) {
            // Creates it only if it doesn't exist
            FileOutputStream is = new FileOutputStream(file2);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write(cssFile);
            w.close();
        }
    }

    private String createCSS(ClassPathResource cssConfig) {
        StringBuilder cssFile = new StringBuilder();

        // Always includes
        cssFile.append(getFileContents(new ClassPathResource("/static/css/libs/bootstrap.min.css")));
        // End always includes

        if (cssConfig.exists()) {
            JSONObject configObj = getJSONFromFile(cssConfig);

            if (configObj.has("libs")) {
                JSONArray libs = configObj.getJSONArray("libs");

                for (int i = 0; i < libs.length(); i++) {
                    cssFile.append(getFileContents(new ClassPathResource("/static/css/libs/" + libs.getString(0) + ".css")));
                }
            }

            cssFile.append(getFileContents(new ClassPathResource("/static/css/main.css")));

            if (configObj.has("own")) {
                JSONArray own = configObj.getJSONArray("own");

                for (int i = 0; i < own.length(); i++) {
                    cssFile.append(getFileContents(new ClassPathResource("/static/css/" + own.getString(0) + ".css")));
                }
            }
        } else {
            cssFile.append(getFileContents(new ClassPathResource("/static/css/main.css")));
        }

        return cssFile.toString();
    }

    private String getFileContents(ClassPathResource resource) {
        StringBuilder contents = new StringBuilder();
        String line;

        try (InputStream inputStream = resource.getInputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = bufferedReader.readLine()) != null) {
                contents.append(line);
            }
        } catch (IOException ignored) {
            log.info(ignored.getMessage());

            return "";
        }

        return contents.toString();
    }

    private JSONObject getJSONFromFile(ClassPathResource resource) {
        String fileContents = getFileContents(resource);
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(fileContents);
        } catch (JSONException e) {
            log.info(e.getMessage());

            return new JSONObject();
        }

        return jsonObject;
    }
}
