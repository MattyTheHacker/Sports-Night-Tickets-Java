import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class FileHandler {
    public static void saveHashMapToJSON(ConcurrentHashMap<String, String> map, String fileName) throws IOException, JsonProcessingException {
        // convert the map to a json string
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);

        // write the json string to a file
        FileWriter writer = new FileWriter(fileName);
        writer.write(json);
        writer.close();
    }
}
