import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

public class FileHandler {
    public static void saveHashMapToJSON(ConcurrentHashMap<String, String> map, String fileName) throws IOException {
        // convert the map to a json string
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);

        // write the json string to a file
        FileWriter writer = new FileWriter(fileName);
        writer.write(json);
        writer.close();
    }

    public static void saveSortedCodesToJSON(SortedMap<String, String> codes, String fileName) throws IOException {
        // convert the map to a json string
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(codes);

        // write the json string to a file
        FileWriter writer = new FileWriter(fileName);
        writer.write(json);
        writer.close();
    }
}
