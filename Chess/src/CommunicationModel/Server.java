package CommunicationModel;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Game.Player;

/**
 * 
 * Klasa reprezentująca serwer gry w szachy
 *
 */

public class Server implements Runnable{
	private ServerSocket Sck; // gniazdo serwera
	private ServerRequest[] ClientsThr; // tablica wątków klienckich
	private int Port; // numer portu
	private boolean Accept; // flaga akceptowania połączeń;
	private volatile boolean[][] ClientsActivity; // rejestr aktywności klientów	pierwszy wymiar to id klienta a drugi określa czy ten konkretny jest aktywny i założył stół
	private volatile ObjectInputStream Indata; // strumień danych wejściowych do zapisywania obiektów do poliku
	private volatile ObjectOutputStream Outdata; // strumień danych wyjściowych o odczytu danych z pliku
	private String Filepath;
	private volatile Player[] RegisteredPlayers; // tablica zarejestrowanych graczy pobierana z pliku
	private int MaxPlayers; // maksymalna ilość zarejestrowanych graczy
	private int MaxActiveClients; // maksymalna ilość aktywnych klientów
	/**
	 * Konstruktor serwera
	 * @param port
	 * 			numer portu, na którym prowadzony będzie nasłuch
	 */
	
	public Server(int port){
		this.MaxPlayers = 100;
		this.MaxActiveClients = 50;
		this.Port = port;
		this.ClientsActivity = new boolean[this.MaxActiveClients][2];
		this.ClientsThr = new ServerRequest[this.MaxActiveClients];
		this.RegisteredPlayers = new Player[this.MaxPlayers];
		for(int i=0;i<this.MaxActiveClients;i++){
			this.ClientsActivity[i][0]=false; // ustawienie parametru aktywności
			this.ClientsActivity[i][1]=false; //ustawienie parametru założenia stołu
		}
		this.Accept = true;
		this.Filepath = "./players.dat";
		File f = new File(this.Filepath);
		if(!f.exists()) { 
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Problem z utworzeniem pliku");
				e.printStackTrace();
			}
		}
		this.uploadRegisteredPlayers();
	}

	/**
	 * Metoda wykonuje się po uruchomieniu egzemplarza wątku serwera. Akceptuje w pętli przychodzących klientów
	 */
	@Override
	public void run(){
		// TODO Auto-generated method stub
		try {
			this.Sck = new ServerSocket(this.Port);
			System.out.println("Serwer nasłuch na porcie : "+this.Port);
			while(Accept){
				 Socket sck = this.Sck.accept();
				 System.out.println("przybył klient");
				 for(int i=0;i<this.MaxActiveClients;i++){
					 if(!this.ClientsActivity[i][0]){
						 this.ClientsThr[i] = new ServerRequest(this,sck,i);
						 this.ClientsThr[i].start();
						 this.ClientsActivity[i][0] = true;
						 System.out.println("Utworzono wątek dla klienta");
						 break;
					 }
				 }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda odczytująca plik z zarejestrowanymi graczami do lokalnego pola tablicowego
	 */
   public synchronized void uploadRegisteredPlayers(){
			try {
				this.Indata = new ObjectInputStream(new FileInputStream(this.Filepath));
				int i = 0;
				System.out.println("Zarejestrowani :");
				Player p;
				while((p = (Player)this.Indata.readObject())!=null && i<this.MaxPlayers){
					this.RegisteredPlayers[i] = p;
					System.out.println(this.RegisteredPlayers[i].getNick());
					i = i+1;
				}
				this.Indata.close();
			} catch (IOException | ClassNotFoundException e) {
				
			}
	}
   
   /**
    * Metoda Zapisuje stan tablicy lokalnego pola tablicowego zarejestrowanych graczy do pliku
    */
   public synchronized void saveRegisteredPlayers(){
	   int i=0;
	   try {
		   this.Outdata = new ObjectOutputStream(new FileOutputStream(this.Filepath));
		   	while(i<this.MaxPlayers && this.RegisteredPlayers[i]!=null){
		   		   this.RegisteredPlayers[i].setId(i);
		   		   this.Outdata.writeObject(this.RegisteredPlayers[i]); 
				   i=i+1;
			}
		   	this.Outdata.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
	
    /**
     * Metoda ustawiająca pary aktywnych graczy
     * @param searcherId
     * 				Identyfikator klienta poszukującego przeciwnika
     */
	public synchronized void searchOponent(int searcherId){
		for(int i =0;i<this.MaxActiveClients;i++){
			if(ClientsActivity[i][0] && i!=searcherId){
				if(!ClientsActivity[i][1]){
					this.ClientsThr[i].setOponentID(searcherId);
					this.ClientsThr[searcherId].setOponentID(i);
					this.ClientsActivity[i][1] = true;
					this.ClientsActivity[searcherId][1]=true;
					//TODO - uruchomić grę
				}
			}
		}
	}

	
	/**
	 * Metoda synchronizowana rejestrująca gracza 
	 * @param play
	 * 		Gracz do zarejestrowania
	 * @return
	 * 		Zwraca true jeżeli pomyślnie zarejestrowano
	 */
	public synchronized boolean registerPlayer(Player play){
			if(this.RegisteredPlayers[0]==null){
				this.uploadRegisteredPlayers();
			}
			int i=0;
			while(i<this.MaxPlayers && this.RegisteredPlayers[i]!=null){ 
					if(this.RegisteredPlayers[i].getNick().equals(play.getNick())){
						this.saveRegisteredPlayers();
						return false;
					}
					i++;
			}
			if(i<this.MaxPlayers-1){
				this.RegisteredPlayers[i] = play;
				this.saveRegisteredPlayers();
				return true;
			}
			return false;
	}
	
	
	/**
	 * Metoda czyszcząca erlementy w tablicy wątków klienckich. Wywyoływana gdy kończymy pracę z klientem o danym identyfikatorze
	 * @param id
	 * 			Identyfikator klienta, z którym kończy się wymiana danych
	 */
	public void closeClient(int id){
		if(id>-1 && id<this.MaxActiveClients){
		 this.ClientsActivity[id][0] = false;
		 this.ClientsActivity[id][1]= false;
		 this.ClientsThr[id].setRunning(false);
		 this.ClientsThr[id]=null;
		}
	}

	public Player[] getRegisteredPlayers() {
		return RegisteredPlayers;
	}

	public void setRegisteredPlayers(Player[] registeredPlayers) {
		RegisteredPlayers = registeredPlayers;
	}

	public int getMaxPlayers() {
		return MaxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		MaxPlayers = maxPlayers;
	}

	public boolean[][] getClientsActivity() {
		return ClientsActivity;
	}

	public void setClientsActivity(boolean[][] clientsActivity) {
		ClientsActivity = clientsActivity;
	}

	public int getMaxActiveClients() {
		return MaxActiveClients;
	}

	public void setMaxActiveClients(int maxActiveClients) {
		MaxActiveClients = maxActiveClients;
	}

	public ServerRequest[] getClientsThr() {
		return ClientsThr;
	}

	public void setClientsThr(ServerRequest[] clientsThr) {
		ClientsThr = clientsThr;
	}
	
	
	
	
	
	public static void main(String[] args){
		int port;
		try{
			if(args.length != 0){
				port = Integer.parseInt(args[0]);
			}else{
				port = 4448;
			}
			new Thread(new Server(port)).start();
		}catch(Exception pe){
			 pe.printStackTrace();
		}
	}
	
  
	
}
