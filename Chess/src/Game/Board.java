package Game;

import View.GraphPanel;

/**
 * 
 * Klasa reprezentująca szachownicę
 *
 */
public class Board {
	//stałe 
		public static final int WHITE_ON_BOTTOM=0; // stała "Białe na górze szachownicy"  potrzebne przy uruchomieniu metody rozstawiającej pionki
		public static final int BLACK_ON_BOTTOM=1;
		
	private int[][] LogicBoard; // tablica szachownicy
	private int[][] MarkedFieldBoard; // tablica do oznaczania czy pionek może ruszyć się na dane pole
	private Pawn[] WhitePawnSet; // zestaw białych pionków
	private Pawn[] BlackPawnSet; // zestaw czarnych pionków
	private int Bottom; //flaga wskazująca któore pionki na dole
	
	public Board(){
		this.LogicBoard = new int[8][8];
		this.MarkedFieldBoard = new int[8][8];
		this.WhitePawnSet = new Pawn[16];
		this.BlackPawnSet = new Pawn[16];
		
		
		for(int i=0;i<8;i++){
			this.WhitePawnSet[i]=new Pawn(i,Pawn.PAWN,Pawn.WHITE,i,1);
			this.BlackPawnSet[i]=new Pawn(i+16,Pawn.PAWN,Pawn.BLACK,i,6);
		}
		
			this.WhitePawnSet[8] = new Pawn(8,Pawn.ROCK,Pawn.WHITE,0,0);
			this.WhitePawnSet[9] = new Pawn(9,Pawn.HORSE,Pawn.WHITE,1,0);
			this.WhitePawnSet[10] = new Pawn(10,Pawn.BISHOP,Pawn.WHITE,2,0);
			this.WhitePawnSet[11] = new Pawn(11,Pawn.QUEEN,Pawn.WHITE,3,0);
			this.WhitePawnSet[12] = new Pawn(12,Pawn.KING,Pawn.WHITE,4,0);
			this.WhitePawnSet[13] = new Pawn(13,Pawn.BISHOP,Pawn.WHITE,5,0);
			this.WhitePawnSet[14] = new Pawn(14,Pawn.HORSE,Pawn.WHITE,6,0);
			this.WhitePawnSet[15] = new Pawn(15,Pawn.ROCK,Pawn.WHITE,7,0);
			
			this.BlackPawnSet[8] = new Pawn(24,Pawn.ROCK,Pawn.BLACK,0,7);
			this.BlackPawnSet[9] = new Pawn(25,Pawn.HORSE,Pawn.BLACK,1,7);
			this.BlackPawnSet[10] = new Pawn(26,Pawn.BISHOP,Pawn.BLACK,2,7);
			this.BlackPawnSet[11] = new Pawn(27,Pawn.QUEEN,Pawn.BLACK,3,7);
			this.BlackPawnSet[12] = new Pawn(28,Pawn.KING,Pawn.BLACK,4,7);
			this.BlackPawnSet[13] = new Pawn(29,Pawn.BISHOP,Pawn.BLACK,5,7);
			this.BlackPawnSet[14] = new Pawn(30,Pawn.HORSE,Pawn.BLACK,6,7);
			this.BlackPawnSet[15] = new Pawn(31,Pawn.ROCK,Pawn.BLACK,7,7);
			
	}

	/**
	 * Metoda przygotowująca szachownicę do gry
	 * @param whichonbtm
	 * 				Flaga wskazująca który kolor ma być na górze szachownicy
	 */
	public void initBoard(int whichonbtm){
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.LogicBoard[i][j]=-1;
				this.MarkedFieldBoard[i][j]=0;
			}
		}
		if(whichonbtm==Board.BLACK_ON_BOTTOM){
			this.Bottom = Board.BLACK_ON_BOTTOM;
			for(int i=0;i<8;i++){
				this.BlackPawnSet[i].setCords(i,6);
				this.WhitePawnSet[i].setCords(i,1);
				this.LogicBoard[BlackPawnSet[i].getX()][BlackPawnSet[i].getY()]=this.BlackPawnSet[i].getId();
				this.LogicBoard[WhitePawnSet[i].getX()][WhitePawnSet[i].getY()]=this.WhitePawnSet[i].getId();

			}
			
			for(int i=8;i<16;i++){
				this.BlackPawnSet[i].setCords(i-8,7);
				this.WhitePawnSet[i].setCords(i-8,0);
				this.LogicBoard[BlackPawnSet[i].getX()][BlackPawnSet[i].getY()]=this.BlackPawnSet[i].getId();
				this.LogicBoard[WhitePawnSet[i].getX()][WhitePawnSet[i].getY()]=this.WhitePawnSet[i].getId();

			}
		}else{
			this.Bottom = Board.WHITE_ON_BOTTOM;

			for(int i=0;i<8;i++){
				this.BlackPawnSet[i].setCords(i,1);
				this.WhitePawnSet[i].setCords(i,6);
				this.LogicBoard[BlackPawnSet[i].getX()][BlackPawnSet[i].getY()]=this.BlackPawnSet[i].getId();
				this.LogicBoard[WhitePawnSet[i].getX()][WhitePawnSet[i].getY()]=this.WhitePawnSet[i].getId();
			}
			for(int i=8;i<16;i++){
				this.BlackPawnSet[i].setCords(i-8,0);
				this.WhitePawnSet[i].setCords(i-8,7);
				this.LogicBoard[BlackPawnSet[i].getX()][BlackPawnSet[i].getY()]=this.BlackPawnSet[i].getId();
				this.LogicBoard[WhitePawnSet[i].getX()][WhitePawnSet[i].getY()]=this.WhitePawnSet[i].getId();
			}
		}
	}
	
	/**
	 * Metoda wykonuje ruch przeciwnika
	 * @param id
	 * 			Identyfikator pionka
	 * @param x
	 * 			Współrzedna X
	 * @param y
	 * 			Współrzędna Y
	 */
	public void oponentMove(int id, int x, int y){
		Pawn[] set;
		if(this.Bottom==Board.BLACK_ON_BOTTOM){
			set = this.WhitePawnSet;
		}else{
			set = this.BlackPawnSet;
		}
		this.LogicBoard[set[id].getX()][set[id].getY()]=-1;
		set[id].setCords(this.calculateOponentCord(x,false),this.calculateOponentCord(y,true));
		this.LogicBoard[set[id].getX()][set[id].getY()]=set[id].getId();
	}
	
	/**
	 * Metoda wykonuje ruch pionka którym gra ten gracz
	 * @param id
	 * 			Identyfikator pionka
	 * @param x
	 * 			Współrzędna X
	 * @param y
	 * 			Współrzędna Y
	 */
	public void MyMove(int id, int x,int y){
		Pawn[] set;
		if(this.Bottom==Board.WHITE_ON_BOTTOM){
			set = this.WhitePawnSet;
		}else{
			set = this.BlackPawnSet;
		}
		this.LogicBoard[set[id].getX()][set[id].getY()]=-1;
		set[id].setCords(x, y);
		this.LogicBoard[x][y]=set[id].getId();
		this.resetMarkedFieldBoard();
		set[id].setMoveCounter(set[id].getMoveCounter()+1);
	}
	
	/**
	 * Metoda sprawdzająca ruch pionka o o podanym identyfikatorze
	 * @param index
	 * 			Indeks pionka w zestawie
	 * 				
	 */
	public void checkMove(int index){
		this.resetMarkedFieldBoard(); // zresetowanie planszy oznaczającej
		Pawn p;
		if(this.Bottom==Board.WHITE_ON_BOTTOM){
			p = this.WhitePawnSet[index];
		}else{
			p= this.BlackPawnSet[index];
		}
		
		switch(p.getStatus()){
			case Pawn.HORSE :
				this.checkHorseMove(p);
			break;
			case Pawn.KING :
				this.checkKingMove(p);
			break;
			case Pawn.PAWN :
				this.checkPawnMove(p);
			break;
			case Pawn.ROCK :
				this.checkRockMove(p);
			break;
			case Pawn.BISHOP:
				this.checkBishopMove(p);
			break;
			case Pawn.QUEEN:
				this.checkQueenMove(p);
			break;
		}
	}
	
	/**
	 * Metoda resetująca tablicę dostępnych ruchów
	 */
	public void resetMarkedFieldBoard(){
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.MarkedFieldBoard[i][j]=0;
			}
		}
	}
	
	/**
	 * Metoda sprawdza ruch skoczka (sprawdzenie polega na oznaczeniu pól tablicy MarkedFieldBoard w odpowiedni sposób)
	 * @param p
	 * 			Referencja na pionek do sprawdzenia
	 */
	private void checkHorseMove(Pawn p){
		int x,y;
		int i,j;
		for(i = -2;i<3;i++){
			for(j=-2;j<3;j++){
				if( (i!=0) && (j!=0) && (Math.abs(i)!=Math.abs(j)) ){
					x = p.getX()+i;
					y=p.getY()+j;
					if(x>=0 && x<8 && y>=0 && y<8){
						if(this.LogicBoard[x][y]==-1){
								this.MarkedFieldBoard[x][y]=GraphPanel.GREEN_DOT;
						}else{
							if(this.isIdInOponentSet(this.LogicBoard[x][y])){
								this.MarkedFieldBoard[x][y]=GraphPanel.RED_DOT;
							}
						}
					}
				}
			}
		}
	}
	
	//TODO Sprawdzenie czy nie ma bicia na pole na które chce ruszyć król 
	/**
	 * Metoda sprawdzająca ruchy króla
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkKingMove(Pawn p){
		int x,y;
		for(int i = -1;i<2;i++ ){
			for(int j=-1;j<2;j++){
				if(!(j==0 && i==0)){
					x = p.getX()+i;
					y=p.getY()+j;
					if(x>=0 && x<8 && y>=0 && y<8){
						if(this.LogicBoard[x][y]==-1){
								this.MarkedFieldBoard[x][y]=GraphPanel.GREEN_DOT;
						}else{
							if(this.isIdInOponentSet(this.LogicBoard[x][y])){
								this.MarkedFieldBoard[x][y]=GraphPanel.RED_DOT;
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Metoda sprawdzająca ruch piona
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkPawnMove(Pawn p){
		int x,y;
		int j;
		if(p.getMoveCounter()!=0) j = -1; else j = -2; 
		for(int i=-1;i<2;i++){
			x = p.getX()+i;
			y=p.getY()+j;
			if(x>=0 && x<8 && y>=0 && y<8){
				if(i==0){
					if(this.LogicBoard[x][y]==-1){
						if(j==-2){
							if(this.LogicBoard[x][y+1]!=-1)return;
							this.MarkedFieldBoard[x][y+1]=GraphPanel.GREEN_DOT;
						}
						this.MarkedFieldBoard[x][y]=GraphPanel.GREEN_DOT;
					}
				}else{
					if(j==-2){y=y+1;}
					if(this.isIdInOponentSet(this.LogicBoard[x][y]))this.MarkedFieldBoard[x][y]=GraphPanel.RED_DOT;
				}
		    }
		}
	}
	
	/**
	 * Metoda sprawdzająca ruchy wieży
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkRockMove(Pawn p){
		this.checkVertical(p);
		this.checkHorizontal(p);
	}
	
	/**
	 * Metoda sprawdzająca ruchy gońca
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkBishopMove(Pawn p){
		this.checkLeftSlant(p);
		this.checkRightSlant(p);
	}
	
	/**
	 * Metoda sprawdzająca ruchy królowej(lub Hetman - zwał jak zwał :) )
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkQueenMove(Pawn p){
		this.checkHorizontal(p);
		this.checkVertical(p);
		this.checkLeftSlant(p);
		this.checkRightSlant(p);
	}
	
	/**
	 * Metoda podrzędna sprawdzająca ruch w pionie
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkVertical(Pawn p){
		int i;
		for(i=p.getY()+1;i<8;i++){ // sprawdzenie w dół szachownicy
			if(this.LogicBoard[p.getX()][i]==-1){
				this.MarkedFieldBoard[p.getX()][i]=GraphPanel.GREEN_DOT;
			}else if(isIdInOponentSet(this.LogicBoard[p.getX()][i])){
				this.MarkedFieldBoard[p.getX()][i]=GraphPanel.RED_DOT;
				break;
			}else break;
		}
		
		for(i=p.getY()-1;i>=0;i--){ // sprawdzenie w górę szachownicy
			if(this.LogicBoard[p.getX()][i]==-1){
				this.MarkedFieldBoard[p.getX()][i]=GraphPanel.GREEN_DOT;
			}else if(isIdInOponentSet(this.LogicBoard[p.getX()][i])){
				this.MarkedFieldBoard[p.getX()][i]=GraphPanel.RED_DOT;
				break;
			}else break;
		}
		
	}
	
	/**
	 * Metoda podrzędna sprawdzająca ruch w poziomie
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkHorizontal(Pawn p){
		int i;
		for(i=p.getX()+1;i<8;i++){ // sprawdzenie w prawą stronę szachownicy
			if(this.LogicBoard[i][p.getY()]==-1){
				this.MarkedFieldBoard[i][p.getY()]=GraphPanel.GREEN_DOT;
			}else if(isIdInOponentSet(this.LogicBoard[i][p.getY()])){
				this.MarkedFieldBoard[i][p.getY()]=GraphPanel.RED_DOT;
				break;
			}else break;
		}
		
		for(i=p.getX()-1;i>=0;i--){ // sprawdzenie w lewą szachownicy
			if(this.LogicBoard[i][p.getY()]==-1){
				this.MarkedFieldBoard[i][p.getY()]=GraphPanel.GREEN_DOT;
			}else if(isIdInOponentSet(this.LogicBoard[i][p.getY()])){
				this.MarkedFieldBoard[i][p.getY()]=GraphPanel.RED_DOT;
				break;
			}else break;
		}
	}
	
	/**
	 * Metoda podrzędna sprawdzająca ruch po lewym skosie
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkLeftSlant(Pawn p){
		int j = 0;
		int x,y;
		while(j<2){
			if(j==1){
				x=p.getX()-1;
				y=p.getY()-1;
			}else{
				x=p.getX()+1;
				y=p.getY()+1;
			}
			while((x<8 && x>=0)&& (y<8 && y>=0)){ 
				if(this.LogicBoard[x][y]==-1){
					this.MarkedFieldBoard[x][y]=GraphPanel.GREEN_DOT;
				}else if(isIdInOponentSet(this.LogicBoard[x][y])){
					this.MarkedFieldBoard[x][y]=GraphPanel.RED_DOT;
					break;
				}else break;
				if(j==1){x= x-1; y=y-1;}else{x=x+1;y=y+1;}
			}
			j++;
		}
		
	}
	
	/**
	 * Metoda podrzędna sprawdzająca ruch po prawym skosie
	 * @param p
	 * 			Referencja na pionek
	 */
	private void checkRightSlant(Pawn p){
		int j = 0;
		int x,y;
		while(j<2){
			if(j==1){
				x=p.getX()+1;
				y=p.getY()-1;
			}else{
				x=p.getX()-1;
				y=p.getY()+1;
			}
			while((x<8 && x>=0)&& (y<8 && y>=0)){ 
				if(this.LogicBoard[x][y]==-1){
					this.MarkedFieldBoard[x][y]=GraphPanel.GREEN_DOT;
				}else if(isIdInOponentSet(this.LogicBoard[x][y])){
					this.MarkedFieldBoard[x][y]=GraphPanel.RED_DOT;
					break;
				}else break;
				if(j==1){x= x+1; y=y-1;}else{x=x-1;y=y+1;}
			}
			j++;
		}
	}
	
	
	/**
	 * Metoda sprawdza czy pionek o podanym identyfikatorze znajduje się w zbiorze przeciwnika
	 * @param id
	 * 			Identyfikator pionka
	 * @return
	 * 			Zwraca true, jeżeli pionek o tym identyfikatorze należy do przeciwnika
	 */
	private boolean isIdInOponentSet(int id){
		Pawn[] set;
		if(this.Bottom==Board.WHITE_ON_BOTTOM){
			set = this.BlackPawnSet;
		}else{
			set = this.WhitePawnSet;
		}
		for(int i=0;i<set.length;i++){
			if(set[i].getId()==id){
				return true;
			}
		}
		return false;
	}

	/**
	 * Metoda wyliczająca odwrotną pozycję pionka dla szachownicy przeciwnika
	 * @param cord
	 * 			Współrzędna do przeliczenia
	 * @param yflag
	 * 			Flaga ustawiona na true rozpoznaje współrzędną zmienną (w przypadku szachów współrzedna Y zmienia się dla szachów przeciwnika)
	 * @return
	 * 		Zwraca przeliczoną współrzędną
	 */
	public int calculateOponentCord(int cord,boolean yflag){
		if(yflag)return ((-cord)+(this.LogicBoard.length-1));
		return cord;
	}
	
	/**
	 * MEtoda przlicza koordynat logiczny na rzeczywisty na panelu graficznym
	 * @param cord
	 * 				Koordynat Logiczny
	 * @return
	 * 				Zwraca koordynat rzeczywisty
	 */
	public int calcLogicToGraph(int cord){
		return cord*62+50;
	}
	
	
	public int[][] getLogicBoard() {
		return LogicBoard;
	}


	public void setLogicBoard(int[][] logicBoard) {
		LogicBoard = logicBoard;
	}


	public Pawn[] getWhitePawnSet() {
		return WhitePawnSet;
	}


	public void setWhitePawnSet(Pawn[] whitePawnSet) {
		WhitePawnSet = whitePawnSet;
	}


	public Pawn[] getBlackPawnSet() {
		return BlackPawnSet;
	}


	public void setBlackPawnSet(Pawn[] blackPawnSet) {
		BlackPawnSet = blackPawnSet;
	}

	public int getBottom() {
		return Bottom;
	}

	public void setBottom(int bottom) {
		Bottom = bottom;
	}

	public int[][] getMarkedFieldBoard() {
		return MarkedFieldBoard;
	}

	public void setMarkedFieldBoard(int[][] markedFieldBoard) {
		MarkedFieldBoard = markedFieldBoard;
	}
	
	
}
