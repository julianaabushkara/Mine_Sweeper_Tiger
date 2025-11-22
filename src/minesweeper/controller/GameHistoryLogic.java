package minesweeper.controller;

import minesweeper.model.GameHistory;
import minesweeper.model.Difficulty;

// NEW (correct for 1.1.1)
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameHistoryLogic {

    private static GameHistoryLogic instance;
    private final List<GameHistory> historyList = new ArrayList<>();

    private GameHistoryLogic() {}

    public static GameHistoryLogic getInstance() {
        if (instance == null)
            instance = new GameHistoryLogic();
        return instance;
    }

    public List<GameHistory> getAllHistory() {
        loadHistoryFromJSON("src/minesweeper/data/history.json");
        return historyList;
    }


    // ============================
    // LOAD HISTORY FROM JSON FILE
    // ============================
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
                historyArray = (JSONArray) doc.get("History");
            } else {
                // or if JSON *is* just [ ... ]
                historyArray = (JSONArray) root;
            }

            for (Object item : historyArray) {
                JSONObject obj = (JSONObject) item;

                String difficultyStr = (String) obj.get("difficulty");
                Difficulty difficulty = Difficulty.valueOf(difficultyStr);

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
    }

}
