package pl.javastart.library.io.file;

import pl.javastart.library.exception.DataExportEception;
import pl.javastart.library.exception.DataImportException;
import pl.javastart.library.exception.InvalidDataException;
import pl.javastart.library.model.*;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

public class CsvFileManager implements FileManager {
    private static final String FILE_NAME = "Library.csv";
    private static final String USERS_FILE_NAME = "Users.csv";

    @Override
    public Library importData() {
        Library library = new Library();
        importPublications(library);
        importUsers(library);

        return library;
    }
    @Override
    public void exportData(Library library) {
        exportPublications(library);
        exportUsers(library);

    }
    private void exportPublications(Library library) {
        Collection<Publication> publications = library.getPublications().values();
        exportToCsv(publications, FILE_NAME);

    }

    private void importUsers(Library library) {

        try (Scanner scanner = new Scanner(new File(USERS_FILE_NAME))) {
            scanner.tokens()
                    .map(this::createUserFromString)
                    .forEach(library::addUser);

        } catch (FileNotFoundException e) {
            throw new DataImportException("Brak pliku" + USERS_FILE_NAME);
        }
    }

    private LibraryUser createUserFromString(String line) {
        String[] split = line.split(";");
        String firstName = split[0];
        String lastName = split[1];
        String pesel = split[2];
        return new LibraryUser(firstName, lastName, pesel);

    }

    private void importPublications(Library library) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME))){
            bufferedReader.lines()
                    .map(this::createObjectFromString)
                    .forEach(library::addPublication);
        } catch (FileNotFoundException e){
            throw new DataImportException("Brak pliku" + FILE_NAME);

        } catch (IOException e){
            throw new DataImportException("Błąd odczytu pliku " + FILE_NAME);
        }


    }

    private Publication createObjectFromString(String line) {
        String[] split = line.split(";");
        String type = split[0];
        if (Book.TYPE.equals(type)) {
            return createBook(split);

        } else if (Magazine.TYPE.equals(type)) {
            return createMagazine(split);
        }
        throw new InvalidDataException("Nieznany typ pliku" + type);

    }

    private Magazine createMagazine(String[] data) {
        String title = data[1];
        String publisher = data[2];
        int year = Integer.parseInt(data[3]);
        int month = Integer.parseInt(data[4]);
        int day = Integer.parseInt(data[5]);
        String language = data[6];
        return new Magazine(title, publisher, year, month, day, language);
    }

    private Book createBook(String[] data) {
        String title = data[1];
        String author = data[2];
        int year = Integer.valueOf(data[3]);
        int pages = Integer.valueOf(data[4]);
        String publisher = data[5];
        String isbn = data[6];
        return new Book(title, author, year, pages, publisher, isbn);
    }



    private void exportUsers(Library library) {
        Collection<LibraryUser> users = library.getUsers().values();
        exportToCsv(users, USERS_FILE_NAME);

    }



    private <T extends CsvConvertible> void exportToCsv(Collection<T> collection, String fileName) {

        try (FileWriter fileWriter = new FileWriter(fileName);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        ) {
            for (T element : collection) {
                bufferedWriter.write(element.toCsv());
                bufferedWriter.newLine();
            }

        } catch (IOException e) {
            throw new DataExportEception("Błąd zapisu danych do pliku" + fileName);
        }

    }
}

