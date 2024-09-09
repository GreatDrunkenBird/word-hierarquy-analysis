import com.google.gson.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3 || !args[0].equals("analyze")) {
            System.out.println("Usage: java -jar cli.jar analyze --depth <n> [--verbose] \"{phrase}\"");
            return;
        }

        int depth = -1;
        boolean verbose = false;
        StringBuilder phraseBuilder = new StringBuilder();

        // Parse arguments
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--depth":
                    if (i + 1 < args.length) {
                        try {
                            depth = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid depth value.");
                            return;
                        }
                    } else {
                        System.out.println("Depth value is missing.");
                        return;
                    }
                    break;
                case "--verbose":
                    verbose = true;
                    break;
                default:
                    if (!phraseBuilder.isEmpty()) {
                        phraseBuilder.append(" ");
                    }
                    phraseBuilder.append(args[i]);
                    break;
            }
        }

        String phrase = phraseBuilder.toString();

        if (depth < 0 || phrase.isEmpty()) {
            System.out.println("Usage: java -jar cli.jar analyze --depth <n> [--verbose] \"{phrase}\"");
            return;
        }

        long startTime = System.currentTimeMillis();
        File jsonFile = findJsonFile("dicts");
        if (jsonFile == null) {
            System.out.println("Nenhum arquivo JSON encontrado em /dicts.");
            return;
        }

        JsonObject jsonObject = loadJsonTree(jsonFile);
        long loadTime = System.currentTimeMillis() - startTime;

        if (verbose) {
            System.out.println("Tempo de carregamento dos parâmetros: " + loadTime + " ms");
        }

        startTime = System.currentTimeMillis();
        Map<String, Map<String, Integer>> resultMap = analyzePhrase(jsonObject, depth, phrase);
        long checkTime = System.currentTimeMillis() - startTime;

        if (verbose) {
            System.out.println("Tempo de verificação da frase: " + checkTime + " ms");
        }

        if (resultMap.isEmpty()) {
            System.out.println("Na frase não existe nenhum filho do nível"+ depth +"e nem o nível 5 possui os termos especificados.\n");
        } else {
            for (Map.Entry<String, Map<String, Integer>> entry : resultMap.entrySet()) {
                String nodeName = entry.getKey();
                Map<String, Integer> wordCounts = entry.getValue();
                if (!wordCounts.isEmpty()) {
                    System.out.print(nodeName + " = ");
                    int totalCount = wordCounts.values().stream().mapToInt(Integer::intValue).sum();
                    System.out.print(totalCount);
                }
            }
        }
    }

    private static File findJsonFile(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return null;
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        return files != null && files.length > 0 ? files[0] : null;
    }

    private static JsonObject loadJsonTree(File jsonFile) {
        try (FileReader reader = new FileReader(jsonFile)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            System.err.println("Erro ao carregar o arquivo JSON: " + e.getMessage());
            System.exit(1);
            return null; // unreachable
        }
    }

    private static Map<String, Map<String, Integer>> analyzePhrase(JsonObject jsonObject, int depth, String phrase) {
        Map<String, Map<String, Integer>> resultMap = new HashMap<>();
        String[] words = phrase.split("\\s+");

        // Percorrer a árvore até o nível de profundidade especificado
        countWordsAtDepth(jsonObject, depth, 0, words, resultMap);

        return resultMap;
    }

    private static void countWordsAtDepth(JsonObject jsonObject, int targetDepth, int currentDepth, String[] words, Map<String, Map<String, Integer>> resultMap) {
        if (currentDepth == targetDepth) {
            // Estamos na camada desejada, contar as palavras na camada abaixo
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String nodeName = entry.getKey();
                JsonElement element = entry.getValue();
                Map<String, Integer> subNodeWordCounts = new HashMap<>();

                if (element.isJsonObject()) {
                    // Contar as palavras na camada abaixo
                    countWordsInSubLayer(element.getAsJsonObject(), words, subNodeWordCounts);
                } else if (element.isJsonArray()) {
                    countWordsInArray(element.getAsJsonArray(), words, subNodeWordCounts);
                }

                if (!subNodeWordCounts.isEmpty()) {
                    resultMap.put(nodeName, subNodeWordCounts);
                }
            }
        } else {
            // Continuar descendo na árvore até o nível alvo
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                JsonElement element = entry.getValue();
                if (element.isJsonObject()) {
                    countWordsAtDepth(element.getAsJsonObject(), targetDepth, currentDepth + 1, words, resultMap);
                }
            }
        }
    }

    private static void countWordsInSubLayer(JsonObject jsonObject, String[] words, Map<String, Integer> subNodeWordCounts) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonElement element = entry.getValue();
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String nodeWord = element.getAsString();
                for (String word : words) {
                    if (nodeWord.equalsIgnoreCase(word)) {
                        subNodeWordCounts.merge(nodeWord, 1, Integer::sum);
                    }
                }
            } else if (element.isJsonObject()) {
                countWordsInSubLayer(element.getAsJsonObject(), words, subNodeWordCounts);
            } else if (element.isJsonArray()) {
                countWordsInArray(element.getAsJsonArray(), words, subNodeWordCounts);
            }
        }
    }

    private static void countWordsInArray(JsonArray jsonArray, String[] words, Map<String, Integer> subNodeWordCounts) {
        for (JsonElement element : jsonArray) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String nodeWord = element.getAsString();
                for (String word : words) {
                    if (nodeWord.equalsIgnoreCase(word)) {
                        subNodeWordCounts.merge(nodeWord, 1, Integer::sum);
                    }
                }
            } else if (element.isJsonObject()) {
                countWordsInSubLayer(element.getAsJsonObject(), words, subNodeWordCounts);
            }
        }
    }
}
