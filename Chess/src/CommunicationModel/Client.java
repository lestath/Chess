package CommunicationModel;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import View.GameFrame;
import View.LoginFrame;
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
							//TODO - nowa ramka gry 
							this.Frame.setMsg("Poprawinie zalogowano",Color.GREEN);
							System.out.println("Pomyślnie zalogowano");
							System.out.println(this.MyPlayer.getId());
							this.Frame.setOperarionSucess(true);
							this.Frame.setConnectSucess(true);
							this.PlayerPanel = new GameFrame(this);
							this.PlayerPanel.getPlayerLab().setText(this.MyPlayer.getNick());
							//this.Frame.setVisible(false);
						break;
						case "RESP_RANK" : // wiadomość o dostarczeniu rankingu
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.getSidePanel().showRanking(this.InPack.getPlayers()); // przekazanie pakietu z rankingiem do wyświetlenia w panelu graficznym
								}
							}
						break;
						case "NEW_TABLE_CONFIRM" : // wiadomość o dostarczeniu rankingu
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
						case "OPONENT_SELECT_FAILED" : // wiadomość o niepowodzeniu wybrania przeciwnika
							if(this.PlayerPanel!=null){
									this.PlayerPanel.setMsg("Akcja nie powiodła się - spróbuj ponownie, lub wybierz kogoś innego",Color.RED);
							}
						break;
						case "START_GAME" : // gra rozpoczyna się a wybierającym jest ten klient
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.setMsg("Gra rozpoczyna się a ty wybrałeś",Color.GREEN);
									if(!this.PlayerPanel.getSidePanel().isInvertedFlag()){
										this.PlayerPanel.getSidePanel().invertBoard();
									}
									this.PlayerPanel.getSidePanel().startGame(this.InPack.getColor());
								}
							}
						break;
						case "OPONENT_CHOSE_YOU" : // gra rozpoczyna się a ten klient oczekiwał z założonym stołem
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.setMsg("Gra rozpoczyna się TY czekałeś",Color.GREEN);
									if(this.PlayerPanel.getSidePanel().isInvertedFlag()){
										this.PlayerPanel.getSidePanel().invertBoard();
									}
									this.PlayerPanel.getSidePanel().startGame(this.InPack.getColor());
								}
							}
						break;
						case "OPONENT_EXITED"  : // przeciwnik z którym gram odszedł
							if(this.PlayerPanel!=null){
								if(this.PlayerPanel.getSidePanel()!=null){
									this.PlayerPanel.setMsg("Przeciwnik odchodzi",Color.WHITE);
									this.PlayerPanel.getSidePanel().setGameStarted(false);
									this.PlayerPanel.getSidePanel().repaint();
									// TODO dodać walkower
								}
							}
						break;
						case "EXIT_SUCESS"  : // poprawne wylogowanie

						break;
						case "MAKE_MOVE" : // rozkaz wykonania ruchu
							if(this.PlayerPanel!=null){
								this.PlayerPanel.getSidePanel().getMyBoard().setCheck(false);
								if(this.PlayerPanel.getSidePanel()!=null){
									if( this.PlayerPanel.getSidePanel().getMyBoard() !=null){
										this.PlayerPanel.getSidePanel().getMyBoard().oponentMove(this.InPack.getPawnId(),this.InPack.isCheck(),this.InPack.getCheckPawnId(),this.InPack.getX(),this.InPack.getY());
										this.PlayerPanel.getSidePanel().repaint();
									}
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
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
}
