package com.example.criminalintent.database;

public class CrimeDbSchema {
    public static final class CrimeTable{
        public static final String NAME = "Crimes";

        public static final class Columns{
            public static final String UUID = "ID";
            public static final String TITLE = "Title";
            public static final String DATE = "Date";
            public static final String SOLVED = "Solved";
            public static final String SUSPECT = "Suspect";
            public static final String NUMBER = "Number";
            public static final String DETAIL = "Details";
        }

    }

}
