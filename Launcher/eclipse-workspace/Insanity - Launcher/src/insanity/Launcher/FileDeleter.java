package insanity.Launcher;

import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class FileDeleter {
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void check() {
        log("Request server files");
        try {
            byte[] bytes = requestURL(new URL("http://www.insanitymc.dyjix.fr/s-update/insanity/ListFiles/MD5%20Check%20Method"));
            log("Parsing received json");
            JsonArray jsonArray = gson.fromJson(new String(bytes),JsonArray.class);
            ArrayList<String> mods = new ArrayList<>();
            for (JsonElement jsonElement:jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String filename = jsonObject.get("fileRelativePath").getAsString();
                if (!filename.startsWith("/mods/"))
                    continue;
                String file = filename.substring(6);
                if (file.contains("/"))
                    continue;
                mods.add(file);
            }
            log("Checking Files");
            for (File file:new File(Launcher.IF_DIR,"mods").listFiles()) {
                if ((!file.isDirectory())&&!mods.contains(file.getName())) {
                    log("Deleting /mods/"+file.getName());
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("Delete files failed");
        }
    }

    public static void log(String message) {
        System.out.println("[FileDeleter]: "+message);
    }

    public static byte[] requestURL(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        urlConnection.connect();
        return readAllBytes(urlConnection.getInputStream());
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        byte[] buf = new byte[8192];
        int capacity = buf.length;
        int nread = 0;
        int n;
        for (;;) {
            // read to EOF which may read more or less than initial buffer size
            while ((n = inputStream.read(buf, nread, capacity - nread)) > 0)
                nread += n;

            // if the last call to read returned -1, then we're done
            if (n < 0)
                break;

            // need to allocate a larger buffer
            if (capacity <= MAX_BUFFER_SIZE - capacity) {
                capacity = capacity << 1;
            } else {
                if (capacity == MAX_BUFFER_SIZE)
                    throw new OutOfMemoryError("Required array size too large");
                capacity = MAX_BUFFER_SIZE;
            }
            buf = Arrays.copyOf(buf, capacity);
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }

    public static void main(String[] args) {
        check();
    }
}
