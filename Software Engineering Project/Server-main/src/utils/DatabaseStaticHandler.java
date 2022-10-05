package utils;

import game.Story;

public class DatabaseStaticHandler {

    private static DatabaseQueries querier;

    static void init(final DatabaseQueries databaseQuerier) {
        querier = databaseQuerier;
    }

    public static final Story getStory(final Integer storyId) {
        return querier.getStory(storyId);
    }

}
