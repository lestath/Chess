package CommunicationModel;

import java.io.Serializable;

import Game.Player;

/**
 * 
 * Klasa reprezentująca pakiet przesyłany między klientem a serwerem
 *
 */
public class Pack implements Serializable{
	/**
	 * 
	 */
	public static final int CHECK = 1; // stała wystapienia szacha
	public static final int MATE = 2; // stała wystapienia mata
	public static final int DRAW_PROPOSE = 3; // stała propozycji remisu
	public static final int DRAW_YES = 4; // stała zgody na remis
	public static final int DRAW_NO = 5; // stała odmowy remisu
	public static final int DRAW = 6; // stała wystapienia remisu wynikająca z ruchów
	private static final long serialVersionUID = 1L;
	private String Message;
	private String Nick; // pole pseudonimów
	private Player player;
	private Player[] players; // tablica graczy używana w wyświetlaniu rankingu
	private GameSaved[] saves; // tablica zapisanych gier gracza używana przy pobraniu zapisów
	private int Color; // kolor pionków
	private int PawnId; // identyfikator pionka
	private int X; // współrzędna przesunięcia x
	private int Y; // współrzedna przesunięcia Y
	private int Check; // oznaczenie wystapienia zdarzenia szczególnego jak szach/mat
	private int CheckPawnId;// identyfikator pionka szachującego
	private int Status; // nowy status pionka jeżeli wiadomość o zmianie statusu

	
	public Pack(String msg){
		this.Message = msg;
		this.player = null;
		this.setPlayers(null);
		this.setColor(0);
		this.setCheck(0);
		this.CheckPawnId = -1;
		this.Nick = "";
		this.Status = -1;
		this.Check = -1;
		this.saves = new GameSaved[100];
		this.players = new Player[50];
	}
	
	
	public String getMessage() {
		return Message;
	}
	
	
	
	public void setMessage(String message) {
		Message = message;
	}
	public Player getPlayer() {
	return this.player;
	}
	public void setPlayer(Player player) {
		this.player = new Player(player.getNick(),player.getPassword());
		this.player.setId(player.getId());
		this.player.setLoses(player.getLoses());
		this.player.setWins(player.getWins());
		this.player.setDraw(player.getDraw());
	}

	/**
	 * Metoda ustawia wiadomości o pionku który został przsunięty
	 * @param id
	 * 			Identyfikator pionka
	 * @param x
	 * 			Współrzedna X
	 * @param y
	 * 			Współrzędna Y
	 */
	public void setMove(int id,int x, int y){
		this.setPawnId(id);
		this.setX(x);
		this.setY(y);
	}
	public Player[] getPlayers() {
		return players;
	}

	public void setPlayers(Player[] players) {
		if(players==null){
			this.players=null;
			return;
		}
		this.players = new Player[50];
		for(int i=0;i<players.length;i++){
			this.players[i]=players[i];
		}
	}


	public int getColor() {
		return Color;
	}


	public void setColor(int color) {
		Color = color;
	}


	public int getPawnId() {
		return PawnId;
	}


	public void setPawnId(int pawnId) {
		PawnId = pawnId;
	}


	public int getX() {
		return X;
	}


	public void setX(int x) {
		X = x;
	}


	public int getY() {
		return Y;
	}


	public void setY(int y) {
		Y = y;
	}



	public int getCheckPawnId() {
		return CheckPawnId;
	}


	public void setCheckPawnId(int checkPawnId) {
		CheckPawnId = checkPawnId;
	}


	public int getCheck() {
		return Check;
	}


	public void setCheck(int check) {
		Check = check;
	}


	public int getStatus() {
		return Status;
	}


	public void setStatus(int status) {
		Status = status;
	}


	public String getNick() {
		return Nick;
	}


	public void setNick(String nick) {
		Nick = nick;
	}


	public GameSaved[] getSaves() {
		return saves;
	}


	public void setSaves(GameSaved[] saves) {
		if(saves==null){
			this.saves=null;
			return;
		}
		this.saves = new GameSaved[100];
		for(int i =0;i<saves.length;i++){
			if(saves[i]!=null){
			this.saves[i]=new GameSaved(saves[i].getId(),saves[i].getNick1(),saves[i].getNick2(),saves[i].getColor(),saves[i].isMove(),saves[i].getBoard(),saves[i].isAllowPlay());
			}
		}
	}
}
