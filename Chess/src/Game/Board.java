package Game;

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
	private Pawn[] WhitePawnSet; // zestaw białych pionków
	private Pawn[] BlackPawnSet; // zestaw czarnych pionków
	private int Bottom; //flaga wskazująca któore pionki na dole
	
	public Board(){
		this.LogicBoard = new int[8][8];
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
		this.LogicBoard[set[id].getX()][set[id].getY()]=id;
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
		this.LogicBoard[x][y]=id;
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
	
	
}
