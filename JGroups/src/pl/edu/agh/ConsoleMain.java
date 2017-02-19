package pl.edu.agh;

import pl.edu.agh.dsrg.sr.ChannelJoinedException;
import pl.edu.agh.dsrg.sr.exceptions.ChannelDoNotExistsException;
import pl.edu.agh.dsrg.sr.exceptions.ChannelExistsException;
import pl.edu.agh.dsrg.sr.ChatManager;
import pl.edu.agh.dsrg.sr.exceptions.ChannelNotJoinedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ConsoleMain {

    private String login;
    private Scanner s;
    private ChatManager chatManager;

    public int showMenu() {
        System.out.println("1) Stworz nowy kanal");
        System.out.println("2) Dolacz do kanalu");
        System.out.println("3) Wyswietl liste kanalow");
        System.out.println("4) Czatuj na kanale");
        System.out.println("5) Opusc kanal");
        System.out.println("6) Wyjdz");
        if(s.hasNextInt()) {
            return s.nextInt();
        }
        return -1;
    }

    private int selectNumber() {
        int number = -1;
        while(number == -1) {
            System.out.println("Podaj numer kanalu: - nr od 1 do 200");
            if(s.hasNextInt()) {
                number = s.nextInt();
                if(number > 200 || number < 0) {
                    number = -1;
                }
            } else {
                number = -1;
            }
        }
        return number;
    }

    public void createChannel() {
        int number = selectNumber();
        try {
            chatManager.createChannel(number);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (ChannelExistsException e) {
            System.out.println("Kanal istnieje!");
        } catch (ChannelDoNotExistsException e) {
            System.out.println("Kanal nie istnieje!");
        } catch (ChannelJoinedException e) {
            System.out.println("Juz jestes zapisany do kanalu!");
        }
    }

    public void joinChannel() {
        int number = selectNumber();
        try {
            chatManager.joinChannel(number);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (ChannelDoNotExistsException e) {
            System.out.println("Kanal nie istnieje!");
        } catch (ChannelJoinedException e) {
            System.out.println("Juz jestes zapisany do kanalu!");
        }
    }

    public void showChannelsList() {
        System.out.println("Lista kanalow: ");
        chatManager.showChannels();
    }

    public void chat() {
        int number = selectNumber();
        System.out.println("Wpisz wiadomosc, zakoncz \"#koniec\": ");
        StringBuilder builder = new StringBuilder("");
        while(s.hasNextLine()) {
            String next = s.nextLine();
            if(next.equals("#koniec")) {
                break;
            }
            builder.append(next);
            builder.append("\n");
        }

        try {
            chatManager.chatMessage(number, builder.toString());
        } catch (ChannelNotJoinedException e) {
            System.out.println("Trzeba byc zapisanym na kanal, by w nim pisac.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectFromMenu(int option) {
        switch (option) {
            case 1:
                createChannel();
                break;
            case 2:
                joinChannel();
                break;
            case 3:
                showChannelsList();
                break;
            case 4:
                chat();
                break;
            case 5:
                leave();
            default:
                System.out.println(option);
                break;
        }
    }

    private void leave() {
        int r = selectNumber();

        System.out.println("Następuje wylogowanie");
        try {
            this.chatManager.leaveChannel(r);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (ChannelNotJoinedException e) {
            e.printStackTrace();
        }
    }

    public void loginUser() {
        System.out.println("Podaj login: ");
        this.login = s.next();
    }

    public void run() {
        s = new Scanner(System.in);
        loginUser();
        chatManager = new ChatManager(this.login);
        try {
            chatManager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int option = showMenu();
        while(option != 6) {
            selectFromMenu(option);
            option = showMenu();
        }
        System.out.println("Wyłączam...");
        chatManager.close();
        System.out.println("Koniec");
    }

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        new ConsoleMain().run();
    }
}
