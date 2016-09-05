package CommunicationModel;

import java.io.Serializable;

import Game.Pawn;

/**
 * Klasa reprezen tująca obiekt zapisanej gry
 *
 */
public class GameSaved implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int Id;
	private int AllowPlay; // flaga ustawiona na 1, że obaj gracze uczestniczący są aktywni i można rozpocząć
	private String Nick1; // nick pierwszego gracza
	private String Nick2; // nick drugiego gracza
	private int Color;	// kolor jakim grał gracz pierwszy
	private boolean Move; // flaga ustanowiona na true oznacza, że pierwszy gracz miał ruch w czasie zapisu
	private Pawn[][] Board; // ustawienie pionków na szachownicy
	
	public GameSaved(int id,String n1, String n2, int color,boolean move,Pawn[][] board,int allow){
		this.AllowPlay = allow;
		this.Id = id;
		this.Nick1 = n1;
		this.Nick2 = n2;
		this.Color = color;
		this.Board = new Pawn[8][8];
		this.Move = move;
		this.Board = new Pawn[8][8];
		for(int i=0; i<8; i++){
			for(int j=0;j<8;j++){
				this.Board[i][j]=board[i][j];
			}
		}
		
		
	}
	
	public String getNick1() {
		return Nick1;
	}
	public void setNick1(String nick1) {
		this.Nick1 = nick1;
	}
	public String getNick2() {
		return Nick2;
	}
	public void setNick2(String nick2) {
		this.Nick2 = nick2;
	}
	public int getColor() {
		return Color;
	}
	public void setColor(int color) {
		Color = color;
	}
	public Pawn[][] getBoard() {
		return Board;
	}
	public void setBoard(Pawn[][] board) {
		this.Board = new Pawn[8][8];
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				Board[i][j] = board[i][j];
			}
		}
	}
	public int isAllowPlay() {
		return AllowPlay;
	}
	public void setAllowPlay(int allowPlay) {
		AllowPlay = allowPlay;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public boolean isMove() {
		return Move;
	}

	public void setMove(boolean move) {
		Move = move;
	}
	
}
