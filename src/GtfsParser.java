import java.util.zip.ZipFile;

class GtfsParser extends Parser {


    GtfsParser(Database database, ZipFile zipFile) {
        super(database, zipFile);
    }

    @Override
    public void parse() {
        // TODO (5/24/20): read the file, parse it, and add all information to the database
    }
}
