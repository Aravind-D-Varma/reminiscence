package my.project.nostalgia.database;

public class MemoryDbSchema {
    public static final class memoryTable{
        public static final String NAME = "memories";

        public static final class Columns{
            public static final String UUID = "ID";
            public static final String TITLE = "Title";
            public static final String DATE = "Date";
            public static final String EVENT = "Event";
            public static final String DETAIL = "Details";
            public static final String MEDIAPATH = "MediaPaths";
        }

    }

}
