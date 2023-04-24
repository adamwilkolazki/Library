package pl.javastart.library.model;

import pl.javastart.library.exception.PublicationAlreadyExistException;
import pl.javastart.library.exception.UserAlreadyExistException;

import java.io.Serializable;
import java.util.*;

public class Library implements Serializable {


    private Map<String, Publication> publications = new HashMap<>();
    private Map<String, LibraryUser> users = new HashMap<>();

    public Collection<Publication> getSortedPublication(Comparator<Publication> comparator)
    {
        Collection<Publication> values = publications.values();
        ArrayList<Publication> list = new ArrayList<>(this.publications.values());
        list.sort(comparator);
        return list;
    }
    public Optional<Publication> findPublicationByTitle(String title){
        return Optional.ofNullable(publications.get(title));
    }
    public Collection<LibraryUser> getSortedUsers(Comparator<LibraryUser> comparator){
        ArrayList<LibraryUser> libraryUsers = new ArrayList<>(users.values());
        libraryUsers.sort(comparator);
        return libraryUsers;
    }
    public Map<String, Publication> getPublications() {
        return publications;
    }

    public Map<String, LibraryUser> getUsers() {
        return users;
    }

    private int booksNumber = 0;
    private int magazinesNumber = 0;

    public void addBook(Book book) {
        addPublication(book);
    }

    public void addMagazine(Magazine magazine) {
        addPublication(magazine);
    }

    public void addPublication(Publication publication) {
        if (publications.containsKey(publication.getTitle())) {
            throw new PublicationAlreadyExistException("Publikacja o takim tytule już istnieje.");
        }
        publications.put(publication.getTitle(), publication);


    }

    public void addUser(LibraryUser user) {
        if (users.containsKey(user.getPesel())) {
            throw new UserAlreadyExistException("Użytkownik o takim peselu już istnieje");
        } else
            users.put(user.getPesel(), user);
    }

    public boolean removePublication(Publication publication) {
        if (publications.containsValue(publication)) {
            publications.remove(publication.getTitle());
            return true;
        } else
            return false;


    }

}




