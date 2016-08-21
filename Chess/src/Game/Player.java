package Game;

import java.io.Serializable;

/**
 * 
 * Klasa odwzorowująca dane gracza z pliku
 *
 */

public class Player implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int Id;
	private String Nick;
	private String Password;
	private int Wins;
	private int Loses;
	private int Draw;
	
	public Player(String nick,String cs){
		
		this.Nick = nick;
		this.Password = cs;
		this.Wins = 0;
		this.Loses =0;
		this.Draw=0;
		this.Id = 0;
	}
	
	public String getNick() {
		return Nick;
	}
	public void setNick(String nick) {
		Nick = nick;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public int getWins() {
		return Wins;
	}
	public void setWins(int wins) {
		Wins = wins;
	}
	public int getLoses() {
		return Loses;
	}
	public void setLoses(int loses) {
		Loses = loses;
	}
	public int getDraw() {
		return Draw;
	}
	public void setDraw(int draw) {
		Draw = draw;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}
}
