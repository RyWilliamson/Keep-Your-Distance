public static synchronized RSSIDatabase getInstance( Context context ) {
    if ( instance == null ) {
        // Threading lock
        synchronized ( RSSIDatabase.class ) {
            // Data race check
            if ( instance == null ) {
                instance = Room.databaseBuilder( context.getApplicationContext(), RSSIDatabase.class, DB_NAME ).fallbackToDestructiveMigration().build();
            }
        }
    }
    return instance;
}