import java.io.File;
import java.sql.SQLException;
import java.util.zip.ZipFile;

public class DatabaseFactory {

    /**
     * @return a new Database which is located in the MartaDatabase.db file
     * @throws SQLException
     */
    public static Database createEmptyDatabase() throws SQLException {
        Database database = new SQLiteDatabase();
        database.clear();
        return database;
    }

    /**
     * @param file in which to create a new empty Database
     * @return a new empty Database using the given file
     * @throws SQLException
     */
    public static Database createEmptyDatabase(File file) throws SQLException {
        Database database = new SQLiteDatabase(file);
        database.clear();
        return database;
    }

    /**
     * @param file that contains the Database
     * @return the Database based on the given file
     * @throws SQLException
     */
    public static Database createDatabaseFromDb(File file) throws SQLException {
        return new SQLiteDatabase(file);
    }

    /**
     * @param zipFile containing GTFS data
     * @return a Database located in the MartaDatabase.db file that is populated with the GTFS data
     * @throws SQLException
     */
    public static Database createDatabaseFromGtfs(ZipFile zipFile) throws SQLException {
        Database database = new SQLiteDatabase();
        (new GtfsParser(database, zipFile)).parse();
        return database;
    }
}
