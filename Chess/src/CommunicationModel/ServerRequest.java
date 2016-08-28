package CommunicationModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Game.Pawn;
import Game.Player;

/**
 * 
 * Klasa Reprezentująca wątek komunikacji z pojedynczym klientem, który nawiązał połączenie z serwerem
 *
 */
public class ServerRequest extends Thread {
	private static byte END_CONTEXT_MAT = 0; //  stała zakończenia matem
	private static byte END_CONTEXT_WALKOVER = 1;  //stała zakończenia walkowerem
	private static byte END_CONTEXT_DRAW = 2; // stała zakończenia remisem
	private Player Client; // obiekt gracza
	private int ClientID; // identyfikator klienta 
	private int OponentID; // identyfikator przeciwnika
	private ObjectOutputStream out; // strumień wyjściowy
	private ObjectInputStream in; // strumień wejściowy
	private boolean Running; // flaga nasłuch na klienta 
	private Socket ClientSck; // gniazdo klienta
	private Server Serv; // referencja na serwer, który wywołał obiekt obsługi klienta
	
	/**
	 * Konstruktor nowej wymiany danych między klientem a serwerem
	 * @param s
	 * 			Referencja na obiekt serwera, który wywołał ten wątek
	 * @param sck
	 * 			Referencja na gniazdo, na któ©ym odbywa się wymiana danych
	 * @param id
	 * 			Identyfikator klienta w tablicy wątków klienckich serwera
	 */
	public ServerRequest(Server s,Socket sck,int id){
		this.ClientID = id;
		this.Serv = s;
		this.ClientSck = sck;
		this.Running = true;
		this.Client = null;
		this.OponentID = -1;
		try {
			out = new ObjectOutputStream(ClientSck.getOutputStream());
			in = new ObjectInputStream(ClientSck.getInputStream());
		} catch (IOException e) {
			//TODO - obsługa błędu wejścia 
		}
	}
	
	
	/**
	 * Metoda wykonuje się po uruchomieniu wątku. Nasłuchuje w pętli na komunikaty od klienta i odpowiednio na nie reaguje w zależności
	 * od wartości komunikatu
	 */
	@Override
	public void run() {
	 Pack pck= null;
	 Pack outpck = new Pack("SUCCESS"); // wysłanie komunikatu o poprawnym połączeniu poprawnym połączeniu
		// czytanie wejścia w pętli
	 Pack pack = null; // pakiet do wysyłania
	 try{
		while(this.Running){
			pck =(Pack)in.readObject();
			if(pck!=null){
				switch(pck.getMessage()){
					case "HELLO": // wiadomość powitalna
						this.out.writeObject(outpck);
					break;
					case "REG_ME": // prośba o rejestrację
						System.out.println("Odebrano komunikat o rejestracji "+pck.getPlayer().getNick());
						if(this.Serv.registerPlayer(pck.getPlayer())){
							this.sentPack(new Pack("REG_SUCCESS")); // wysłanie komunikatu o poprawnym zarejestrowaniu
						}else{
							this.sentPack(new Pack("REG_FAILED"));	// wysłanie komunikatu o porażce rejestracji
						}
					break;
					case "LOG_ME" : // prośba o zalogowanie
						Player p;
						if((p=this.loginPlayer(pck.getPlayer()))!=null){
							 pack = new Pack("LOG_SUCCESS"); // wysłanie komuniaktu o poprawnym zalogowaniu
							 this.Client = p;
							this.Serv.getClientsActivity()[this.ClientID][0] = true;
							pack.setPlayer(p);
							this.sentPack(pack);
							System.out.println("Login klienta: "+this.Client.getNick());
						}else{
							this.sentPack(new Pack("LOG_FAILED"));//wysłanie komuniaktu o błędzie logowania
						}
					break;
					case "GIVE_RANK" : //prośba o ranking
						pack = new Pack("RESP_RANK");// wysłanie rankingu
						pack.setPlayers(this.getRank());
						this.sentPack(pack);
					break;
					case "NEW_TABLE" : //prośba o ranking
						this.Serv.getClientsActivity()[this.ClientID][1] = true;
						pack = new Pack("NEW_TABLE_CONFIRM");// wysłanie rankingu
						this.sentPack(pack);
					break;
					case "GIVE_TAB_LIST" : //prośba o listę graczy, którzy założyli nowy stół
						pack = new Pack("TAB_LIST_RESP");// wysłanie listy graczy posiadających stół
						pack.setPlayers(this.giveTabList());
						this.sentPack(pack);
					break;
					case "SELECT_OPONENT" : //prośba o skojarzenie z wybranym przeciwnikiem
						String nick = pck.getPlayer().getNick();
						System.out.println("Wybrany przeciwnik na serwie "+nick);
						if(this.findOponent(nick)){
							pack = new Pack("START_GAME");
							pack.setColor(Pawn.BLACK);
							this.sentPack(pack);
							pack = new Pack("OPONENT_CHOSE_YOU");
							pack.setColor(Pawn.WHITE);
							this.Serv.getClientsThr()[this.OponentID].sentPack(pack);
						}else{
							pack = new Pack("OPONENT_SELECT_FAILED");
							this.sentPack(pack);
						}
					break;
					case "CLOSE_TABLE": // Prośba o zamknięcie stołu (jeżeli prowadziliśmy grę to przegrana walkowerem)
						this.exitProcedure();
						pack = new Pack("TABLE_CLOSED"); // inforamcja o zamknięciu stołu
						this.sentPack(pack);
					break;
					case "EXIT_ME" : //prośba o wylogowanie
						this.exitProcedure();
						pack = new Pack("EXIT_SUCCESS");
						this.Serv.getClientsActivity()[this.ClientID][0] = false;
						this.Serv.getClientsThr()[this.ClientID] = null;
						this.Running = false;
						this.sentPack(pack);
					break;
					case "MAKE_MOVE" : // informacja o wykonaniu ruchu, musi zostać przekazana do przeciwnika
						if(OponentID<50 && OponentID>-1){
							if(pck.getCheck()==Pack.MATE || pck.getCheck()==Pack.DRAW_YES ){ // jeżeli wykrylismy mata to ustawiamy punkty zwycięzcy i przegranego
								if(pck.getCheck()==Pack.MATE){
									saveGameResults(ServerRequest.END_CONTEXT_MAT);
								}else{
									saveGameResults(ServerRequest.END_CONTEXT_DRAW);
								}
								this.Serv.getClientsActivity()[this.ClientID][1] = false;
								if(OponentID<50 && OponentID>-1){
									if(this.Serv.getClientsThr()[this.OponentID]!=null){
										this.Serv.getClientsThr()[this.OponentID].setOponentID(-1);	
										this.Serv.getClientsActivity()[this.OponentID][1]=false;	
									}
								}
								System.out.println("Wejście w zapis do rankingu");
							}
							this.Serv.getClientsThr()[this.OponentID].sentPack(pck);
							if(pck.getCheck()==Pack.MATE || pck.getCheck()==Pack.DRAW_YES)this.OponentID = -1;
						}
					break;
				}
			}
			pck = null;
		}
		// do usunięcia
		System.out.println("Koniec nasłuchu na klienta : "+this.ClientID);
		    
	  }catch(Exception e){
		e.printStackTrace();
		this.Serv.closeClient(this.ClientID);
		this.close();
	  }
	}
	
	/**
	 * Metoda ustawiająca działanie nasłuchu
	 * @param running	
	 * 				Ustawia stan nasłuchu
	 */
	public void setRunning(boolean running){
		this.Running = running;
	}
	
	/**
	 * Metoda zamykająca strumienie IO i socket
	 */
	public void close(){
		try {
			this.out.close();
			this.in.close();
			this.ClientSck.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * Metoda wywoływana przy przerwaniu gry przez jednego z graczy (rozłącza graczy z pary i ustawia walkower)
	 */
	private void exitProcedure(){
		Pack pack;
		this.Serv.getClientsActivity()[this.ClientID][1] = false;
		if(OponentID<50 && OponentID>-1){
			this.saveGameResults(ServerRequest.END_CONTEXT_WALKOVER);
			pack = new Pack("OPONENT_EXITED");
			if(this.Serv.getClientsThr()[this.OponentID]!=null){
				this.Serv.getClientsThr()[this.OponentID].setOponentID(-1);
				this.Serv.getClientsThr()[this.OponentID].sentPack(pack);
				
			}
			this.OponentID = -1;
		}
	}
	
	/**
	 * Metoda uzyskiwania rankingu graczy
	 * @return
	 * 		Zwraca posortowaną tablicę graczy do rankingu
	 */
	public Player[] getRank(){
		Player[] regs = new Player[this.Serv.getRegisteredPlayers().length]; 
		for(int i=0;i<regs.length;i++){
			regs[i]=this.Serv.getRegisteredPlayers()[i];
		}
		Player[] sort = new Player[regs.length];
		for(int i=0;i<regs.length;i++){
			sort[i]=regs[i];
		}
		Player play;
		int i = 0;
		int j = 0;
		while(i<regs.length){
			j=0;
			if(sort[i]!=null){
					while(j<regs.length){
						if(sort[j]!=null){
								if(sort[i].getWins()>sort[j].getWins()){
									play = sort[i];
									sort[i]=sort[j];
									sort[j]=play;
								}
						}
						j++;
					}
			}
			i++;
		}		
		System.out.println("Weszło do metody gromadzenia rankingu");
		return sort;
	}
	
	/**
	 * Metoda wysyłająca pakiet do klienta
	 * @param pck
	 * 			Pakiet do wysłania
	 */
	public void sentPack(Pack pck){
		try {
			this.out.writeObject(pck);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Metoda kompletująca listę graczy, którzy założyli nowy stół
	 * @return
	 * 		Zwraca listę graczy, którzy założyli stół
	 */
	public Player[] giveTabList(){
		Player[] apt = new Player[this.Serv.getMaxActiveClients()]; //tablica graczy 
		int i = 0;
		int j =0;
		while(i<this.Serv.getMaxActiveClients()){
			if(this.Serv.getClientsActivity()[i][0]){
				if(this.Serv.getClientsActivity()[i][1]){
					if(this.Serv.getRegisteredPlayers()[i]!=null){
						apt[j]=this.Serv.getClientsThr()[i].getClient();
						j++;
					}
				}
			}
			i++;
		}
		return apt;
	}
	
	/**
	 * Metoda zwracająca id przeciwnika
	 * @return
	 * 		Zwraca identyfikator przeciwnika
	 */
	public int getOponentID() {
		return OponentID;
	}
	
	/**
	 * Metoda ustawia identyfikator przeciwnika
	 * @param oponentId
	 * 				Identyfikator przeciwnika
	 */
	public void setOponentID(int oponentId) {
		OponentID = oponentId;
	}
	
	
	
	public int getClientID() {
		return ClientID;
	}


	public void setClientID(int clientID) {
		ClientID = clientID;
	}

	

	public Player getClient() {
		return Client;
	}


	public void setClient(Player client) {
		Client = client;
	}


	/**
	 * 
	 * @param play
	 * 			Gracz do zalogowania
	 * @return
	 * 		Zwraca zarejestrowanego gracza jeżeli ten kwalifikuje się do zalogowania
	 */
	public  Player loginPlayer(Player play){
		int i = 0;
		while(this.Serv.getRegisteredPlayers()[i]!=null){
			if(this.Serv.getRegisteredPlayers()[i].getNick().equals(play.getNick())){
				if(this.Serv.getRegisteredPlayers()[i].getPassword().toString().equals(play.getPassword().toString())){
					for(int j=0;j<this.Serv.getMaxActiveClients();j++){
						if(this.Serv.getClientsThr()[j]!=null){
							if(this.Serv.getClientsThr()[j].getClient()!=null){
								if(this.Serv.getClientsThr()[j].getClient().getNick().equals(play.getNick())){return null;}
							}
						}
					}
					this.Serv.getRegisteredPlayers()[i].getNick();
					return this.Serv.getRegisteredPlayers()[i];
					
				}
			}
			i = i+1;
		}
		return null;
	}
	
	/**
	 * Metoda wyszukuje gracza który złożył stół po nicku i kojarzy przeciwników
	 * @param nick
	 * 			Nick gracza, z którym chcemy zagrać
	 * @return
	 * 			Zwraca true jeżeli akcja powiodła się
	 */
	private boolean findOponent(String nick){
		ServerRequest[] reqtab = this.Serv.getClientsThr();
		for(int i=0;i<this.Serv.getMaxActiveClients();i++){
			if(reqtab[i]!=null){
				if(reqtab[i].getClient().getNick().equals(nick)){
					if(this.Serv.getClientsActivity()[i][0] && this.Serv.getClientsActivity()[i][1]){
						this.Serv.getClientsActivity()[i][1]=false;
						this.setOponentID(reqtab[i].getClientID());
						reqtab[i].setOponentID(this.ClientID);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Metoda zapisująca wynik wyniki dla graczy w tabeli rankingowej
	 * @param walkower
	 * 			Flaga ustawiona na stałą zakończenia gry w zalezności od kontekstu wywołania
	 */
	private void saveGameResults(byte end_context){
		int i = 0;
		if(this.Client==null)return;
		if(this.OponentID==-1)return;
		System.out.println("Przeszło pierwszy etap");
		while(i<this.Serv.getRegisteredPlayers().length && this.Serv.getRegisteredPlayers()[i]!=null){
			if(this.Serv.getClientsThr()[this.OponentID].getClient().getNick().equals(Serv.getRegisteredPlayers()[i].getNick())){
				if(end_context==ServerRequest.END_CONTEXT_WALKOVER || end_context==ServerRequest.END_CONTEXT_MAT ){
					Serv.getRegisteredPlayers()[i].setWins(Serv.getRegisteredPlayers()[i].getWins()+1);
				}else if(end_context==ServerRequest.END_CONTEXT_DRAW){
					Serv.getRegisteredPlayers()[i].setDraw(Serv.getRegisteredPlayers()[i].getDraw()+1);
				}
			}
			if(this.Client.getNick().equals(Serv.getRegisteredPlayers()[i].getNick())){
				if(end_context==ServerRequest.END_CONTEXT_WALKOVER || end_context==ServerRequest.END_CONTEXT_MAT){
					Serv.getRegisteredPlayers()[i].setLoses(Serv.getRegisteredPlayers()[i].getLoses()+1);
				}else if(end_context==ServerRequest.END_CONTEXT_DRAW){
					Serv.getRegisteredPlayers()[i].setDraw(Serv.getRegisteredPlayers()[i].getDraw()+1);
				}
			}
			i = i+1;
		}
		this.Serv.saveRegisteredPlayers();
		try { // uśpienie wątku na czas wykonania zadania
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.Serv.uploadRegisteredPlayers();
	}
	
	
	
}
