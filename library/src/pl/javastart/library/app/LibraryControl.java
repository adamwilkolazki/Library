package pl.javastart.library.app;

import pl.javastart.library.exception.*;
import pl.javastart.library.io.ConsolePrinter;
import pl.javastart.library.io.DataReader;
import pl.javastart.library.io.file.FileManager;
import pl.javastart.library.io.file.FileManagerBuilder;
import pl.javastart.library.model.*;


import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Optional;

public class LibraryControl {


    private ConsolePrinter printer = new ConsolePrinter();

    private DataReader dataReader = new DataReader(printer);
    private FileManager fileManager;

    private Library library;

    public LibraryControl() {
        fileManager = new FileManagerBuilder(printer, dataReader).build();
        try {
            library = fileManager.importData();
            printer.printLine("Zaimportowano dane z pliku");
        } catch (DataImportException | InvalidDataException e) {
            printer.printLine(e.getMessage());
            printer.printLine("Zainicjowano nową bazę");
            library = new Library();
        }
    }

    void controlLoop() {
        Option option;
        do {
            printOptions();
            option = getOption();
            switch (option) {
                case ADD_BOOK:
                    addBook();
                    break;
                case ADD_MAGAZINE:
                    addMagazine();
                    break;
                case PRINT_BOOKS:
                    printBooks();
                    break;
                case PRINT_MAGAZINES:
                    printMagazines();
                    break;
                case DELETE_MAGAZINE:
                    deleteMagazine();
                    break;
                case DELETE_BOOK:
                    deleteBook();
                    break;
                case ADD_USER:
                    addUser();
                    break;
                case PRINT_USER:
                    printUsers();
                    break;
                case EXIT:
                    exit();
                    break;
                case FIND_BOOK:
                    findBook();
                    break;
                default:
                    printer.printLine("Nie ma takiej opcji, wybierz ponownie.");

            }

        } while (option != Option.EXIT);
    }

    private void findBook() {
        printer.printLine("Wpisz tytuł wyszukiwanej książki");
        String bookTitle = dataReader.getString();
        String notFound = "Brak wyszkiwanej pozycji w bibliotece.";
        library.findPublicationByTitle(bookTitle)
                .map(Publication::toString)
                .ifPresentOrElse(System.out::println,()-> System.out.println(notFound));



    }

    private void printUsers() {
         printer.printUsers(library.getSortedUsers(
          //       (o1, o2) -> o1.getLastName().compareTo(o2.getLastName())
         Comparator.comparing(User::getLastName,String.CASE_INSENSITIVE_ORDER)
         ));


    }

    private void addUser() {
        try {
            LibraryUser libraryUser = dataReader.createLibraryUser();
            library.addUser(libraryUser);
        }catch(UserAlreadyExistException e){
            printer.printLine(e.getMessage());
        }
    }


    private Option getOption() {
        boolean optionOK = false;
        Option option = null;
        while (!optionOK) {
            try {
                option = Option.createFromInt(dataReader.getInt());
                optionOK = true;
            } catch (NoSuchOptionException e) {
                printer.printLine(e.getMessage());
            } catch (InputMismatchException e) {
                printer.printLine("Wprowadzono wartość, która nie jest liczbą, podaj ponownie");
            }
        }
        return option;
    }


    private void exit() {
        try {
            fileManager.exportData(library);
            printer.printLine("Exporty danych do pliku zakończony powodzeniem..");
        } catch (DataExportEception e) {
            printer.printLine(e.getMessage());
        }

        printer.printLine("Koniec programu.");
        dataReader.close();
    }

    private void printBooks() {

        printer.printBooks(library.getSortedPublication(
               Comparator.comparing(Publication::getTitle,String.CASE_INSENSITIVE_ORDER)
        ));
    }


    private void printMagazines() {

        printer.printMagazines(library.getSortedPublication(Comparator.comparing(
                Publication::getTitle,String.CASE_INSENSITIVE_ORDER)
        ));
    }

    private void addBook() {
        try {
            Book book = dataReader.readAndCreateBook();
            library.addPublication(book);
        } catch (InputMismatchException e) {
            printer.printLine("Nie udało się utwrozyć książki,niepoprawne dane");
        } catch (ArrayIndexOutOfBoundsException e) {
            printer.printLine("Osiągnięto limit pojemności, nie można dodać kolejnej ksiązki");
        }
    }

    private void deleteBook() {
        try {
            Book book = dataReader.readAndCreateBook();
            if (library.removePublication(book)) printer.printLine("Usunięto książkę");
            else printer.printLine("Brak wskazanej książki");
        } catch (InputMismatchException e) {
            printer.printLine("Nie udało się utworzyć książki,niepoprawne dane");
        }
    }

    private void addMagazine() {
        try {
            Magazine magazine = dataReader.readAndCreateMagazine();
            library.addPublication(magazine);
        } catch (InputMismatchException e) {
            printer.printLine("Nie udało się utwrozyć magazynu,niepoprawne dane");
        } catch (ArrayIndexOutOfBoundsException e) {
            printer.printLine("Osiągnięto limit pojemności, nie można dodać kolejnego magazynu");
        }
    }

    private void deleteMagazine() {
        try {
            Magazine magazine = dataReader.readAndCreateMagazine();
            if (library.removePublication(magazine)) printer.printLine("Usunięto magazyn");
            else printer.printLine("Brak wskazanego magazynu");
        } catch (InputMismatchException e) {
            printer.printLine("Nie udało się utworzyć magazynu,niepoprawne dane");
        }
    }

    private void printOptions() {
        printer.printLine("Wybierz opcję:");
        for (Option option : Option.values()) {
            printer.printLine(option.toString());
        }


    }



    private enum Option {
        EXIT(0, "Wyjście z programu"),
        ADD_BOOK(1, "Dodanie nowej książki"),
        ADD_MAGAZINE(2, "Dodanie nowego magazynu"),
        PRINT_BOOKS(3, "Wyświetl dostępne książki"),
        PRINT_MAGAZINES(4, "Wyświetl dostępne magazyny"),
        DELETE_MAGAZINE(5, "Usuń magazyn"), DELETE_BOOK(6, "Usuń książkę"),
        ADD_USER(7, "Dodaj czytelnika"),
        PRINT_USER(8, "Wyświetl czytelników"),
        FIND_BOOK(9,"wyszukaj książkę");

        private final int value;
        private final String description;

        Option(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return value + " - " + description;
        }

        static Option createFromInt(int option) throws NoSuchOptionException {
            try {
                return Option.values()[option];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchOptionException("Brak opcji o id " + option);
            }
        }
    }
}
