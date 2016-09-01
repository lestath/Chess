package CommunicationModel;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import View.GameFrame;
import View.LoginFrame;
import Game.Pawn;
import Game.Player;


/**
 * 
 * Klasa reprezuntująca klienta łączącego się z serwerem
 *
 */
public class Client implements Runnable{
	
	private int Id;// identyfikator klienta w tablicy wątków
	private Player MyPlayer;
	private Player Oponent;
	private LoginFrame Frame; // ramka wywołująca klienta
	private GameFrame PlayerPanel; // ramka panelu gracza
	
	
	private int Port;
	private String Host;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean Running;
	private Socket Sck;
	private volatile Pack InPack;
	private volatile Pack OutPack;
	private boolean Ready; // flaga sprawdzająca czy klient jest gotów do wymiany informacji
	
	public Client(Player player,LoginFrame frm,String host, int port,int id){
		this.setId(id);
		this.MyPlayer = player;
		this.Oponent = null;
		this.Host = host;
		this.Port = port;
		this.Running = true;
		this.Frame = frm;
		this.setReady(false);
	}
	
	public Player getOponent() {
		return Oponent;
	}
	public void setOponent(Player oponent) {
		Oponent = oponent;
	}
	public Player getMyPlayer() {
		return MyPlayer;
	}
	public void setMyPlayer(Player myPlayer) {
		MyPlayer = myPlayer;
	}

	public boolean isRunning() {
		return Running;
	}

	public void setRunning(boolean running) {
		Running = running;
	}

	public ObjectOutputStream getOut() {
		return out;
	}

	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}

	public ObjectInputStream getIn() {
		return in;
	}

	public void setIn(ObjectInputStream in) {
		this.in = in;
	}

	@Override
	public void run() {
		boolean t = true;
		boolean f = false;
		// utworzenie i wysłanie pakietu powitalnego wraz z przedstawieniem gracza
		this.OutPack = new Pack("HELLO");
		try {
			this.Sck = new Socket(this.Host,this.Port);
			this.in = new ObjectInputStream(this.Sck.getInputStream());
			this.out = new ObjectOutputStream(this.Sck.getOutputStream());
			this.out.writeObject(this.OutPack);
			//nasłuch na pakiety w pętli 
			while(this.Running){
				this.InPack =(Pack)in.readObject();
				if(this.InPack!=null){
					switch(this.InPack.getMessage()){
						case "SUCCESS": // pomyslne połączenie
							this.Ready = true;
							this.Frame.setMsg("Połączono z serwerem",Color.GREEN);
							this.Frame.setConnectSucess(true);
							this.Frame.setOperarionSucess(false);
						break;
						case "REG_SUCCESS": //pomyślna rejestracja
							this.Frame.setMsg("Pomyślnie zarejestrowano",Color.GREEN);
							this.Frame.setConnectSucess(true);
							this.Frame.setOperarionSucess(false);
						break;
						case "REG_FAILED": //błąd rejestracji
							this.Frame.setMsg("Błąd rejestracji - spróbuj ponownie",Color.RED);
							this.Frame.setConnectSucess(true);
							this.Frame.setOperarionSucess(false);
						break;
						case "LOG_FAILED": //błąd logowania
							this.Frame.setMsg("Błąd logowania - spróbuj ponownie",Color.RED);
							this.Frame.setConnectSucess(true);
							this.Frame.setOperarionSucess(false);
						break;
						case "LOG_SUCCESS": // poprawne zalogowanie
							this.MyPlayer = this.InPack.getPlayer();
							this.Frame.setMsg("Poprawinie zalogowano",Color.GREEN);
							System.out.println("Pomyślnie zalogowano");
							System.out.println(this.MyPlayer.getId());
							this.Frame.setOperarionSucess(true);
							this.Frame.setConnectSucess(true);
							this.PlayerPanel = new GameFrame(this);
							this.PlayerPanel.getPlayerLab().setText(this.MyPlayer.getNick());
							//this.Frame.setVisible(false);
						break;
						case "TABLE_CLOSED": //błąd logowania
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.setMsg("Zamknięto stół",Color.GREEN);
									this.PlayerPanel.getSidePanel().setGameStarted(false);
									this.PlayerPanel.getSidePanel().setGameState((byte)0);
									this.PlayerPanel.getSidePanel().repaint();
									
								}
							}
						break;
						case "RESP_RANK" : // wiadomość o dostarczeniu rankingu
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.getSidePanel().showRanking(this.InPack.getPlayers()); // przekazanie pakietu z rankingiem do wyświetlenia w panelu graficznym
								}
							}
						break;
						case "NEW_TABLE_CONFIRM" : // wiadomość o otwarciu nowego stołu
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.getSidePanel().prepareTableToGame(); // przekazanie pakietu z rankingiem do wyświetlenia w panelu graficznym
									this.PlayerPanel.setMsg("Czekam na Przeciwnka",Color.WHITE);
								}
							}
						break;
						case "TAB_LIST_RESP" : // wiadomość o dostarczeniu listy stołów
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.getSidePanel().showTables(this.InPack.getPlayers()); // przekazanie pakietu z dostępnymi stołami do panelu graficznego
									this.PlayerPanel.setMsg("Dostępne stoły",Color.WHITE);
								}
							}
						break;
						case "SAVED_GAMES_RESP" : // wiadomość o dostarczeniu listy zapisanych gier 
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.getSidePanel().showSaves(this.InPack.getSaves()); // przekazanie pakietu z dostępnymi stołami do panelu graficznego
									this.PlayerPanel.setMsg("Zapisane gry",Color.WHITE);
								}
							}
							this.InPack.setSaves(null);
							this.InPack = null;
						break;
						case "OPONENT_SELECT_FAILED" : // wiadomość o niepowodzeniu wybrania przeciwnika
							if(this.PlayerPanel!=null){
									this.PlayerPanel.setMsg("Akcja nie powiodła się - spróbuj ponownie, lub wybierz kogoś innego",Color.RED);
									this.PlayerPanel.setBtnsAct(t,t,t,t,f,f,t,f);
							}
						break;
						case "START_GAME" : // gra rozpoczyna się a wybierającym jest ten klient
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.setMsg("Wybrałeś gracza- Gra rozpoczyna się",Color.GREEN);
									if(!this.PlayerPanel.getSidePanel().isInvertedFlag()){
										this.PlayerPanel.getSidePanel().invertBoard();
									}
								}
								this.PlayerPanel.setBtnsAct(f,f,f,f,t,t,t,t);
								this.PlayerPanel.getSidePanel().startGame(this.InPack.getColor());
								String mycolor = "Czarny";
								this.PlayerPanel.getColorLab().setText("Mój kolor : "+mycolor);
								Pack nickpack = new Pack("MY_NAME_IS");
								nickpack.setNick(this.MyPlayer.getNick());
								this.sendPack(nickpack);	
							}
						break;
						case "OPONENT_CHOSE_YOU" : // gra rozpoczyna się a ten klient oczekiwał z założonym stołem
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.setMsg("Gra rozpoczyna się",Color.GREEN);
									if(this.PlayerPanel.getSidePanel().isInvertedFlag()){
										this.PlayerPanel.getSidePanel().invertBoard();
									}
									this.PlayerPanel.setBtnsAct(f,f,f,f,t,t,t,t);
									this.PlayerPanel.getSidePanel().startGame(this.InPack.getColor());
									String mycolor = "Biały";
									this.PlayerPanel.getColorLab().setText("Mój kolor : "+mycolor);
									Pack nickpack = new Pack("MY_NAME_IS");
									nickpack.setNick(this.MyPlayer.getNick());
									this.sendPack(nickpack);								}
							}
						break;
						case "MY_NAME_IS" : // wiadomosc od przeciwnika (przedstawienie się)
							if(this.PlayerPanel!=null){
								this.PlayerPanel.getOponentLab().setText("Przeciwnik : "+this.InPack.getNick());
							}
						break;
						case "OPONENT_EXITED"  : // przeciwnik z którym gram odszedł
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.setMsg("Przeciwnik odchodzi Wygrywasz Walkowerem",Color.WHITE);
									this.PlayerPanel.getSidePanel().setGameStarted(false);
									this.PlayerPanel.getSidePanel().setGameState((byte)0);
									this.PlayerPanel.getSidePanel().repaint();
									this.PlayerPanel.setBtnsAct(t,t,t,f,f,f,t,f);
									this.PlayerPanel.getOponentLab().setText("");
									this.PlayerPanel.getColorLab().setText("");
								}
							}
						break;
						case "EXIT_SUCESS"  : // poprawne wylogowanie

						break;
						case "MAKE_MOVE" : // rozkaz wykonania ruchu
							Pack pck = null;
							if(this.PlayerPanel!=null){
								if(this.InPack.getCheck() == Pack.CHECK){
									this.PlayerPanel.setMsg("Szach",Color.GREEN);
								}else if(this.InPack.getCheck() == Pack.MATE){ // jeżeli wygrana
									this.PlayerPanel.setMsg("Szach mat Wygrałeś",Color.GREEN);
									this.PlayerPanel.getSidePanel().getMyBoard().lockAllPawns();
									this.PlayerPanel.setBtnsAct(t,t,t,f,f,f,t,f);
									this.PlayerPanel.getOponentLab().setText("");
									this.PlayerPanel.getColorLab().setText("");
								}else if(this.InPack.getCheck()==Pack.DRAW_PROPOSE){
									if(this.PlayerPanel!=null){
										System.out.println("Propozycja remisu");
										this.PlayerPanel.drawWindow(); // okno propozycji remisu
										
									}
								}else if(this.InPack.getCheck()==Pack.DRAW_NO){ // jeżeli przeciwnik odrzucił propozycję remisu
									if(this.PlayerPanel!=null){
											this.PlayerPanel.setMsg("Przeciwnik odrzucił propozycję remisu",Color.RED);
									}
								}else if(this.InPack.getCheck()==Pack.DRAW_YES){
									if(this.PlayerPanel!=null){
										this.PlayerPanel.setMsg("Gra zakończona remisem",Color.GREEN);
										this.PlayerPanel.setBtnsAct(t,t,t,f,f,f,t,f);
										this.PlayerPanel.getOponentLab().setText("");
										this.PlayerPanel.getColorLab().setText("");
										if(this.PlayerPanel.getSidePanel()!=null){
											if(this.PlayerPanel.getSidePanel().getMyBoard()!=null){
												this.PlayerPanel.getSidePanel().getMyBoard().lockAllPawns();
											}
										}
									}
								}else{
									if(this.PlayerPanel.getSidePanel()!=null){
										if(this.PlayerPanel.getSidePanel().getMyBoard()!=null){
											this.PlayerPanel.getSidePanel().getMyBoard().unlockAllPawns();
											this.PlayerPanel.getSidePanel().getMyBoard().makeOponentMove(this.InPack.getPawnId(),this.InPack.getX(),this.InPack.getY());
											if(this.PlayerPanel.getSidePanel().getMyBoard().checkCheck()){
												if(this.PlayerPanel.getSidePanel().getMyBoard().checkMate()){ // jeżeli przegrana
													this.PlayerPanel.setMsg("Szach Mat Przegrałeś",Color.RED);
													pck = new Pack("MAKE_MOVE");
													pck.setCheck(Pack.MATE);
													this.PlayerPanel.getSidePanel().getMyBoard().lockAllPawns();
													this.PlayerPanel.setBtnsAct(t,t,t,f,f,f,t,f);
													this.PlayerPanel.getOponentLab().setText("");
													this.PlayerPanel.getColorLab().setText("");
												}else{
													this.PlayerPanel.setMsg("Szach",Color.RED);
													pck = new Pack("MAKE_MOVE");
													pck.setCheck(Pack.CHECK);
												}
												this.sendPack(pck);
											}
										}
									}
								}
							}
						break;
						case "SWAP_PAWN": // informacja o zmianie statusu pionka przez gracza
						  if(this.PlayerPanel!=null){
							if(this.PlayerPanel.getSidePanel()!=null){
								if(this.PlayerPanel.getSidePanel().getMyBoard()!=null){
									Pawn swappawn;
									swappawn=this.PlayerPanel.getSidePanel().getMyBoard().getPawnById(this.InPack.getPawnId());
									swappawn.setStatus(this.InPack.getStatus());
									this.PlayerPanel.getSidePanel().repaint();
									this.PlayerPanel.setMsg("Przeciwnik zamienił pionek",Color.WHITE);
								}
							}
						  }
						break;
						case "GAME_SAVED" :
							  if(this.PlayerPanel!=null){
									if(this.PlayerPanel.getSidePanel()!=null){
											this.PlayerPanel.setMsg("Zapisano grę",Color.WHITE);
											//TODO decyzja co po zapisaniu gry okienko wychodzę lub gramy dalej
									}
								  }
						break;
					}
				}
				this.InPack = null;
			}
			System.out.println("Klient_odszedł");
		} catch (IOException | ClassNotFoundException e) {
			this.Frame.setMsg("Błąd Połączenia",Color.RED);
			this.Frame.setOperarionSucess(true);
			this.Frame.setConnectSucess(false);
			this.Running = false;
		}
		
	}
	

	public Pack getOutPack() {
		return OutPack;
	}

	public void setOutPack(Pack outPack) {
		OutPack = outPack;
	}
	
	public void sendPack(Pack p){
		 if(this.out!=null){
			try {
				this.out.writeObject(p);
				this.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 }
	}
	
	protected void finalize(){
		this.PlayerPanel.exitProcedure();
	}
	
	public boolean isReady() {
		return Ready;
	}

	public void setReady(boolean ready) {
		Ready = ready;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public LoginFrame getFrame() {
		return Frame;
	}

	public void setFrame(LoginFrame frame) {
		Frame = frame;
	}

	public GameFrame getPlayerPanel() {
		return PlayerPanel;
	}

	public void setPlayerPanel(GameFrame playerPanel) {
		PlayerPanel = playerPanel;
	}
}
