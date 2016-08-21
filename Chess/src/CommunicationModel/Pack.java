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
	private static final long serialVersionUID = 1L;
	private String Message;
	private Player player;
	private Player[] players; // tablica graczy używana w wyświetlaniu rankingu
	private int Color; // kolor pionków
	private int PawnId; // identyfikator pionka
	private int X; // współrzędna przesunięcia x
	private int Y; // współrzedna przesunięcia Y
	
	public Pack(String msg){
		this.Message = msg;
		this.player = null;
		this.setPlayers(null);
		this.setColor(0);
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
		this.players = players;
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
	
	

}
