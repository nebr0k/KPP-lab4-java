import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

class Node<T> implements Serializable {
    T data;
    Node<T> next;

    Node(T data) {
        this.data = data;
        this.next = null;
    }
}

class MyLinkedList<T> implements Iterable<T>, Serializable {
    private Node<T> head = null;

    public void add(T item) {
        Node<T> newNode = new Node<>(item);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;
        }
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            public boolean hasNext() {
                return current != null;
            }

            public T next() {
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    public void removeIf(java.util.function.Predicate<T> predicate) {
        if (head == null) return;

        while (head != null && predicate.test(head.data)) {
            head = head.next;
        }

        Node<T> current = head;
        while (current != null && current.next != null) {
            if (predicate.test(current.next.data)) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }
    }
}

class MyListUtilities {
    public static <T> void printList(MyLinkedList<T> list) {
        for (T item : list) {
            System.out.println(item);
        }
    }
}

class Store implements Serializable {
    private String name;
    private String address;
    private List<String> phones = new ArrayList<>();
    private String specialization;
    private String workingHours;

    public Store(String name, String address, String specialization, String workingHours) {
        this.name = name;
        this.address = address;
        this.specialization = specialization;
        this.workingHours = workingHours;
    }

    public String getName() {
        return name;
    }

    public void addPhone(String phone) {
        phones.add(phone);
    }

    @Override
    public String toString() {
        return "Store{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phones=" + phones +
                ", specialization='" + specialization + '\'' +
                ", workingHours='" + workingHours + '\'' +
                '}';
    }
}

public class Main {
    public static void main(String[] args) {
        MyLinkedList<Store> list = loadList();

        if (args.length > 0 && args[0].equals("-auto")) {
            list.add(new Store("AutoStore", "AutoAddress", "AutoSpecialization", "24/7"));
            MyListUtilities.printList(list);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stores.dat"))) {
                oos.writeObject(list);
                System.out.println("Data successfully saved to file stores.dat");
            } catch (IOException e) {
                System.err.println("Error saving data to file: " + e.getMessage());
            }
            System.out.println("Program terminated.");
        } else {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Menu:");
                System.out.println("1. Add a new store");
                System.out.println("2. View list of stores");
                System.out.println("3. Delete a store by name");
                System.out.println("4. Exit program");
                System.out.print("Select an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Store name: ");
                        String name = scanner.nextLine();
                        System.out.print("Address: ");
                        String address = scanner.nextLine();
                        System.out.print("Specialization: ");
                        String specialization = scanner.nextLine();
                        System.out.print("Working hours: ");
                        String workingHours = scanner.nextLine();

                        Store newStore = new Store(name, address, specialization, workingHours);

                        System.out.print("Add phone number (Y/N)? ");
                        String addPhoneChoice = scanner.nextLine();
                        while (addPhoneChoice.equalsIgnoreCase("Y")) {
                            System.out.print("Phone number: ");
                            String phone = scanner.nextLine();
                            newStore.addPhone(phone);
                            System.out.print("Add another phone number (Y/N)? ");
                            addPhoneChoice = scanner.nextLine();
                        }

                        list.add(newStore);
                        System.out.println("Store added!");
                        break;

                    case 2:
                        MyListUtilities.printList(list);
                        break;

                    case 3:
                        System.out.print("Enter store name to delete: ");
                        String storeName = scanner.nextLine();
                        list.removeIf(store -> store.getName().equalsIgnoreCase(storeName));
                        System.out.println("Store(s) with name " + storeName + " deleted (if found).");
                        break;

                    case 4:
                        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("stores.dat"))) {
                            oos.writeObject(list);
                            System.out.println("Data successfully saved to file stores.dat");
                        } catch (IOException e) {
                            System.err.println("Error saving data to file: " + e.getMessage());
                        }
                        System.out.println("Program terminated.");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                        break;
                }
            }
        }
    }

    private static MyLinkedList<Store> loadList() {
        MyLinkedList<Store> list;
        File file = new File("stores.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                list = (MyLinkedList<Store>) ois.readObject();
                System.out.println("Data loaded successfully from stores.dat");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data from stores.dat. Starting with an empty list.");
                list = new MyLinkedList<>();
            }
        } else {
            System.out.println("stores.dat not found. Starting with an empty list.");
            list = new MyLinkedList<>();
        }
        return list;
    }
}
