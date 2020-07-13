import java.util.zip.ZipFile;

abstract class Parser {
    Database database;
    ZipFile zipFile;

    Parser(Database database, ZipFile zipFile) {
        this.database = database;
        this.zipFile = zipFile;
    }

    abstract void parse();
}
