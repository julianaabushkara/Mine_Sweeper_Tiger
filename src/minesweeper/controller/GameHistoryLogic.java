package minesweeper.controller;

import minesweeper.model.GameHistory;

// NEW (correct for 1.1.1)
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import minesweeper.model.GameSession;
import minesweeper.model.HistoryFilterStrategy.HistoryFilterStrategy;
import minesweeper.model.HistoryFilterStrategy.HistorySortStrategy;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryLogic {

    private static GameHistoryLogic instance;
    private final List<GameHistory> historyList = new ArrayList<>();
    private static final Path HISTORY_FILE = getHistoryPath();

    private GameHistoryLogic() {}

    public static GameHistoryLogic getInstance() {
        if (instance == null)
            instance = new GameHistoryLogic();
        return instance;
    }

    public List<GameHistory> getHistoryFilteredSorted(
            HistoryFilterStrategy filter,
            HistorySortStrategy sort
    ) {
        List<GameHistory> all = getAllHistory();

        var stream = all.stream();
        if (filter != null) {
            stream = stream.filter(filter::matches);
        }
        if (sort != null) {
            return stream.sorted(sort.comparator()).toList();
        }
        return stream.toList();
    }

    // Helper method to get history file path
    private static Path getHistoryPath() {
        try {
            Path userHome = Paths.get(System.getProperty("user.home"));
            Path appDir = userHome.resolve(".minesweeper");
            Files.createDirectories(appDir);
            Path historyFile = appDir.resolve("history.json");

            // If file doesn't exist, try to copy from classpath
            if (!Files.exists(historyFile)) {
                try (InputStream is = GameHistoryLogic.class.getResourceAsStream("/minesweeper/Data/history.json")) {
                    if (is != null) {
                        Files.copy(is, historyFile);
                    } else {
                        // Create empty history file with proper structure
                        JSONObject root = new JSONObject();
                        root.put("users", new JSONObject());
                        Files.write(historyFile, root.toJSONString().getBytes());
                    }
                } catch (IOException e) {
                    // Create empty file if copy fails
                    JSONObject root = new JSONObject();
                    root.put("users", new JSONObject());
                    Files.write(historyFile, root.toJSONString().getBytes());
                }
            }
            return historyFile;
        } catch (IOException e) {
            throw new RuntimeException("Cannot create history data directory", e);
        }
    }

    /*
    public List<GameHistory> getAllHistory() {
        loadHistoryFromJSON(HISTORY_FILE);
        return historyList;
    }*/
    public List<GameHistory> getAllHistory() {
        return loadHistoryFromJSON();
    }


    // ============================
// SAVE HISTORY TO JSON FILE OLD VERSION
// ============================
    /*
    public void saveHistory(GameHistory history, String path) {
        JSONParser parser = new JSONParser();
        JSONArray historyArray;

        try {
            File file = new File(path);

            // Load existing history (or create new)
            if (file.exists() && file.length() > 0) {
                Object root = parser.parse(new FileReader(file));
                if (root instanceof JSONObject) {
                    historyArray = (JSONArray) ((JSONObject) root).get("history");
                } else {
                    historyArray = (JSONArray) root;
                }
            } else {
                historyArray = new JSONArray();
            }

            // Convert GameHistory → JSON
            JSONObject obj = new JSONObject();
            obj.put("difficulty", history.getDifficulty().name());
            obj.put("player1", history.getPlayer1());
            obj.put("player2", history.getPlayer2());
            obj.put("finalScore", history.getFinalScore());
            obj.put("coopWin", history.isCoopWin());
            obj.put("duration", history.getDuration());
            obj.put("dateTime", history.getDateTime().toString());

            historyArray.add(obj);

            // Write back to file
            JSONObject root = new JSONObject();
            root.put("history", historyArray);

            java.nio.file.Files.write(
                    file.toPath(),
                    root.toJSONString().getBytes()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void saveHistoryForUser(String username, GameHistory history) {

        JSONParser parser = new JSONParser();
        JSONObject root;
        JSONObject usersObject;

        try {
            File file = HISTORY_FILE.toFile();

            // 1. Load or create root
            if (file.exists() && file.length() > 0) {
                root = (JSONObject) parser.parse(new FileReader(file));
            } else {
                root = new JSONObject();
            }

            // 2. Load or create "users"
            usersObject = (JSONObject) root.get("users");
            if (usersObject == null) {
                usersObject = new JSONObject();
                root.put("users", usersObject);
            }

            // 3. Load or create user object
            JSONObject userObject = (JSONObject) usersObject.get(username);
            if (userObject == null) {
                userObject = new JSONObject();
                userObject.put("history", new JSONArray());
                usersObject.put(username, userObject);
            }

            // 4. Append history
            JSONArray historyArray = (JSONArray) userObject.get("history");

            JSONObject obj = new JSONObject();
            obj.put("difficulty", history.getDifficulty().name());
            obj.put("player1", history.getPlayer1());
            obj.put("player2", history.getPlayer2());
            obj.put("finalScore", history.getFinalScore());
            obj.put("coopWin", history.isCoopWin());
            obj.put("duration", history.getDuration());
            obj.put("dateTime", history.getDateTime().toString());

            historyArray.add(obj);

            // 5. Write back to file
            java.nio.file.Files.write(
                    file.toPath(),
                    root.toJSONString().getBytes()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // ============================
    // LOAD HISTORY FROM JSON FILE OLD VERSION
    // ============================
    /*
    public void loadHistoryFromJSON(String path) {
        historyList.clear();

        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(new File(path))) {
            // parse root
            Object root = parser.parse(reader);

            JSONArray historyArray;

            // if your JSON is { "History": [ ... ] }
            if (root instanceof JSONObject) {
                JSONObject doc = (JSONObject) root;
                historyArray = (JSONArray) doc.get("history");


            } else {
                // or if JSON *is* just [ ... ]
                historyArray = (JSONArray) root;
            }

            for (Object item : historyArray) {
                JSONObject obj = (JSONObject) item;

                String difficultyStr = ((String) obj.get("difficulty")).trim().toUpperCase();
                GameSession.Difficulty difficulty = GameSession.Difficulty.valueOf(difficultyStr);


                String player1  = (String) obj.get("player1");
                String player2  = (String) obj.get("player2");
                long   score    = (long)   obj.get("finalScore");
                boolean coopWin = Boolean.TRUE.equals(obj.get("coopWin"));
                String duration = (String) obj.get("duration");
                String dateTimeStr = (String) obj.get("dateTime");

                LocalDateTime dateTime =
                        LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                GameHistory h = new GameHistory(
                        difficulty,
                        player1,
                        player2,
                        (int) score,
                        coopWin,
                        duration,
                        dateTime
                );

                historyList.add(h);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }*/

    public List<GameHistory> loadHistoryFromJSON() {

        List<GameHistory> allHistories = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(new FileReader(HISTORY_FILE.toFile()));

            JSONObject usersObject = (JSONObject) root.get("users");
            if (usersObject == null) {
                return allHistories; // no users yet
            }

            // iterate over ALL users
            for (Object userKey : usersObject.keySet()) {
                String username = (String) userKey;
                JSONObject userObject = (JSONObject) usersObject.get(username);

                if (userObject == null) continue;

                JSONArray historyArray = (JSONArray) userObject.get("history");
                if (historyArray == null) continue;

                // iterate over this user's history
                for (Object obj : historyArray) {
                    JSONObject h = (JSONObject) obj;
                    allHistories.add(parseHistoryObject(h, username));
                }
                System.out.println("Loading history from: " + HISTORY_FILE.toAbsolutePath());
                System.out.println("Loaded rows: " + allHistories.size());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return allHistories;
    }


    private GameHistory parseHistoryObject(JSONObject h, String username) {

        return new GameHistory(
                GameSession.Difficulty.valueOf((String) h.get("difficulty")),
                username, // the username
                (String) h.get("player1"),
                (String) h.get("player2"),
                ((Long) h.get("finalScore")).intValue(),
                (boolean) h.get("coopWin"),
                (String) h.get("duration"),
                LocalDateTime.parse((String) h.get("dateTime"))
        );
    }



}