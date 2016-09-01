package Game;

import java.io.Serializable;

/**
 * 
 * Klasa reprezentująca pionek na szachownicy
 *
 */
public class Pawn implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
			// stałe
		//kolor pionka
	   		public static final int BLACK = 1;
	   		public static final int WHITE = 2;
	   	//status pionka
	   		public static final int PAWN = 0;
	   		public static final int ROCK = 1;
	   		public static final int HORSE = 2;
	   		public static final int BISHOP = 3;
	   		public static final int KING = 4;
	   		public static final int QUEEN = 5;
	   		
	private int Id; // identyfikator pionka
	private int X; // współrzędna X na szachownicy
	private int Y;//współrzędna Y na szachownicy
	private int GraphCordX;// współrzędna X obrazka pionka w panelu graficznym
	private int GraphCordY;// współrzędna Y obrazka pionka w panelu graficznym
	private int Status; // wskazuje na status pionka 0 - pionek,1-wieża,2-konik,3-biskup,4-król,5-królowa
	private int Color; // wskazuje na kolor pionka
	private boolean Active; // flaga aktywności pionka w grze (jeżeli false to został zbity)
	private int MoveCounter; // licznik ruchów pionka
	private boolean AllowMove; // flaga monitorująca czy pionek może się poruszać
	
	public Pawn(int id,int status,int color,int cx,int cy){
		this.Id = id;
		this.setStatus(status);
		this.Color=color;
		this.X = cx;
		this.Y = cy;
		this.GraphCordX = 0;
		this.GraphCordY = 0;
		this.Active = true;
		this.MoveCounter = 0;
		this.AllowMove=true;
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getX() {
		return X;
	}
	public void setX(int cordX) {
		X = cordX;
	}
	public int getY() {
		return Y;
	}
	public void setY(int cordY) {
		Y = cordY;
	}
	public int getGraphCordX() {
		return GraphCordX;
	}
	public void setGraphCordX(int graphCordX) {
		GraphCordX = graphCordX;
	}
	public int getGraphCordY() {
		return GraphCordY;
	}
	public void setGraphCordY(int graphCordY) {
		GraphCordY = graphCordY;
	}
	
	public void setCords(int x,int y){
		this.X = x;
		this.Y = y;
		this.calcCordstoGraph();
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getColor() {
		return Color;
	}

	public void setColor(int color) {
		Color = color;
	}	
	
	/**
	 * Metoda przelicza współrzędne logiczne pionka na rzeczywiste współrzędne obrazka na grafice (wyniki zapisywane są do pól lokalnych)
	 */
	public void calcCordstoGraph(){
		this.GraphCordX = this.X*62+50;
		this.GraphCordY = this.Y*62+50;
	}
	
	
	/**
	 * Metoda przelicza współrzędną klikniętą w panelu graficznym na logiczną współrzędną szachownicy;
	 * @param c
	 * 			Współrzędna na grafie
	 * @return
	 * 			Zwraca współrzedną logiczną na szachownicy
	 */
	public int getLogicCord(int c){
		if(c<50) c= 60;
		if(c>470) c=470;
		
		String s = Integer.toString((int)((c-50)/60));
		System.out.println("Koordynat logiczny "+s);
		return (int)((c-50)/60);
	}

	public boolean isActive() {
		return Active;
	}

	public void setActive(boolean active) {
		Active = active;
	}

	public int getMoveCounter() {
		return MoveCounter;
	}

	public void setMoveCounter(int moveCounter) {
		MoveCounter = moveCounter;
	}

	public boolean isAllowMove() {
		return AllowMove;
	}

	public void setAllowMove(boolean allowMove) {
		AllowMove = allowMove;
	}
}
