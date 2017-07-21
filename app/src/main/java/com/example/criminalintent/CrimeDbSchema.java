package com.example.criminalintent;

/**
 * Created by jarod on 6/1/2017.
 */

public class CrimeDbSchema {
    public static final class CrimeTable{
        public static final String NAME = "crime";
        public static final class COLS{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
