package github.tornaco.configs.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class PropsManager {
    private final static String EXPECTED_TOKEN = "=";

    private String rawConfigString;

    private Map<String, String> configMap;

    public PropsManager(String rawConfigString) {
        this.rawConfigString = rawConfigString;
    }

    public PropsManager(File configFile) throws IOException {
        Preconditions.checkNotNull(configFile);
        Preconditions.checkArgument(configFile.exists());
        StringBuilder content = new StringBuilder();
        Files.asCharSource(configFile, Charset.defaultCharset())
                .copyTo(content);
        this.rawConfigString = content.toString();
    }

    private boolean hasAnyConfigInput() {
        return !Strings.isNullOrEmpty(rawConfigString);
    }

    private synchronized void initLines() throws IOException {
        if (!hasAnyConfigInput() || configMap != null) {
            return;
        }

        configMap = new HashMap<String, String>();

        // Read lines from input.
        BufferedReader br = new BufferedReader(new StringReader(rawConfigString));
        String line;
        while ((line = br.readLine()) != null) {
            // Split.
            if (!Strings.isNullOrEmpty(line)) {
                String[] pair = line.split(EXPECTED_TOKEN, 2); // Limit to 2, key and value.
                int count = pair.length;
                if (count == 2) { // Key and value.
                    // Found valid token.
                    String key = pair[0];
                    String value = pair[1];
                    boolean isAValidConfig =
                            key != null && value != null
                                    && !Strings.isNullOrEmpty(key.trim())
                                    && !Strings.isNullOrEmpty(value.trim());
                    if (!isAValidConfig) {
                        LogManager.getLogger().warn("Found invalid config line: " + line);
                        continue;
                    }
                    // Save.
                    configMap.put(key.trim(), value.trim());
                }
            }
        }
    }

    public boolean has(String key) throws IOException {
        Preconditions.checkNotNull(key, "Key is null");
        initLines();
        return configMap.containsKey(key);
    }


    public String get(String key) throws IOException {
        initLines();
        return configMap.get(key);
    }

    public String getStringOrThrow(String key) throws IOException {
        return Preconditions.checkNotNull(getString(key), "Missing config for key: " + key);
    }

    public String getString(String key) throws IOException {
        return getString(key, null);
    }

    public String getString(String key, String defValue) throws IOException {
        Preconditions.checkNotNull(key, "Key is null");
        initLines();
        if (!has(key)) {
            return defValue;
        }
        return configMap.get(key);
    }

    public int getInt(String key, int defValue) throws IOException, BadConfigsException {
        Preconditions.checkNotNull(key, "Key is null");
        initLines();
        if (!has(key)) {
            return defValue;
        }
        try {
            return Integer.parseInt(configMap.get(key));
        } catch (NumberFormatException e) {
            throw new BadConfigsException("Bad config, check your config for this test", e);
        }
    }

    public long getLong(String key, long defValue) throws IOException, BadConfigsException {
        Preconditions.checkNotNull(key, "Key is null");
        initLines();
        if (!has(key)) {
            return defValue;
        }
        try {
            return Long.parseLong(configMap.get(key));
        } catch (NumberFormatException e) {
            throw new BadConfigsException("Bad config, check your config for this test", e);
        }
    }

    public static class BadConfigsException extends Exception {
        public BadConfigsException(String message) {
            super(message);
        }

        public BadConfigsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
