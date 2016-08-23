package Game;

import java.awt.Color;

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
	private GraphPanel Graph; //ramka, która wywołała szachownicę
	private int CheckPawnID; // identyfikator pionka szachującego
	private boolean Context; // flaga sprawdzenia kontekstu sprawdzania przeciwnika jeżeli true to sprawdzamy jako my, jeżeli false to jako przeciwnik
	private boolean Check; // flaga oznaczona na true jeżeli wystąpił szach
	private int[][] SimulationBoard; // plansza symulacyjna dozwolonych ruchów;
	private boolean SimulationFlag; // flaga trawania symulacji 
	
	public Board(GraphPanel graph){
		this.LogicBoard = new int[8][8];
		this.MarkedFieldBoard = new int[8][8];
		this.WhitePawnSet = new Pawn[16];
		this.SimulationBoard = new int[8][8];
		this.BlackPawnSet = new Pawn[16];
		this.Graph = graph;
		this.CheckPawnID =-1;
		this.Context = true;
		this.Check = false;
		this.SimulationFlag = false;
		
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.SimulationBoard[i][j]=0;
			}
		}
		
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
	 * @param check
	 * 			Flaga wystapienia szacha
	 * @param checkpawnid
	 * 			Identyfikator pionka szachującego
	 * @param x
	 * 			Współrzedna X
	 * @param y
	 * 			Współrzędna Y
	 */
	public void oponentMove(int id,boolean check,int checkpawnid,int x, int y){
		Pawn[] set;
		Pawn[] set2;
		this.unlockPawns();
		if(this.Bottom==Board.BLACK_ON_BOTTOM){
			set = this.WhitePawnSet;
		}else{
			set = this.BlackPawnSet;
		}
		if(this.Bottom==Board.BLACK_ON_BOTTOM){
			set2 = this.BlackPawnSet;
		}else{
			set2 = this.WhitePawnSet;
		}

		this.LogicBoard[set[id].getX()][set[id].getY()]=-1;
		set[id].setCords(this.calculateOponentCord(x,false),this.calculateOponentCord(y,true));
		if(this.LogicBoard[set[id].getX()][set[id].getY()]!=-1){
			for(int i=0;i<set.length;i++){
				if(this.LogicBoard[set[id].getX()][set[id].getY()]==set2[i].getId()){
					set2[i].setActive(false);
					break;
				}
			}
		}
		this.LogicBoard[set[id].getX()][set[id].getY()]=set[id].getId();
		if(check){//akcja jeżeli wystapił szach
			if(this.checkMate(checkpawnid)){ // wywołanie metody sprawdzającej mata
				this.Graph.getFrame().setMsg("Szach Mat",Color.RED);
			}else{
				this.Graph.getFrame().setMsg("Szach",Color.RED);
			}
		}
	}
	
	/**
	 * Metoda wykonuje ruch pionka którym gra ten gracz
	 * @param id
	 * 			Identyfikator pionka
	 * @param x
	 * 			Współrzędna X
	 * @param y
	 * 			Współrzędna Y
	 * @return 
	 */
	public boolean MyMove(int id, int x,int y){
		Pawn[] set;
		Pawn[] set2;
		if(this.Bottom==Board.WHITE_ON_BOTTOM){
			set = this.WhitePawnSet;
		}else{
			set = this.BlackPawnSet;
		}
		if(this.MarkedFieldBoard[x][y]==GraphPanel.GREEN_DOT || this.MarkedFieldBoard[x][y]==GraphPanel.RED_DOT ){
			this.LogicBoard[set[id].getX()][set[id].getY()]=-1;
			set[id].setMoveCounter(set[id].getMoveCounter()+1);
			if(this.MarkedFieldBoard[x][y]==GraphPanel.RED_DOT){
				if(this.Bottom==Board.WHITE_ON_BOTTOM){
					set2 = this.BlackPawnSet;
				}else{
					set2 = this.WhitePawnSet;
				}
				for(int i=0;i<set2.length;i++){
					if(set2[i].getId()==this.LogicBoard[x][y]){
						set2[i].setActive(false);
						break;
					}
				}
			}
			set[id].setCords(x, y);
			this.LogicBoard[x][y]=set[id].getId();
			this.resetMarkedFieldBoard();

			
			return true;
		}
		return false;
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
		if(p.isActive()){
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
		if(!this.SimulationFlag){ // sprawdzmy czy już trwa symulacja, żeby nie przepełnić stosu
			if(p.getStatus()!=Pawn.KING){
				this.checkAllowMoves(p);
			}
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
								this.resetSimulationBoard();
								p.setActive(false);
								this.LogicBoard[p.getX()][p.getY()] = -1;
								this.checkIfKingAllow();
								p.setActive(true);
								this.LogicBoard[p.getX()][p.getY()]=p.getId();
								if(this.SimulationBoard[x][y]!=GraphPanel.GREEN_DOT){
									this.MarkedFieldBoard[x][y]=GraphPanel.GREEN_DOT;
								}
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
			if(this.Context){
				y=p.getY()+j;
			}else{
				y=p.getY()-j;
			}
			if(x>=0 && x<8 && y>=0 && y<8){
				if(i==0){
					if(this.LogicBoard[x][y]==-1){
						if(j==-2){
							if(this.LogicBoard[x][y+1]!=-1)return;
							this.MarkedFieldBoard[x][y+1]=GraphPanel.GREEN_DOT;
						}
						this.MarkedFieldBoard[x][y]=GraphPanel.GREEN_DOT;
					}else{
						if(j==-2){
							if(this.LogicBoard[x][y+1]==-1){
								this.MarkedFieldBoard[x][y+1]=GraphPanel.GREEN_DOT;
							}
						}
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
			if(this.Context){
				set = this.BlackPawnSet;
			}else{
				set = this.WhitePawnSet;
			}
		}else{
			if(this.Context){
				set = this.WhitePawnSet;
			}else{
				set = this.BlackPawnSet;
			}
		}
		for(int i=0;i<set.length;i++){
			if(set[i].getId()==id){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Metoda sprawdzająca wystąpienie szacha
	 * @param context
	 * 				Flaga ustalająca kontekst sprawdzenia w jakim została wywołana metoda
	 * 				Ustawiamy na true jeżeli wywołujemy w kontekście po wykonaniu ruchu lub false jeżeli w kontekście sprawdzenia mata
	 * @return
	 * 			Zwraca true jeżeli wystapił szach
	 */
	public boolean checkCheck(boolean context){
		System.out.println("Weszło w spr Szacha");
		Pawn[] set;
		Pawn[] set2;
			if(this.Bottom==Board.WHITE_ON_BOTTOM){
				if(context){
					set = this.WhitePawnSet;
					set2 = this.BlackPawnSet;
				}else{
					set = this.BlackPawnSet;
					set2 = this.WhitePawnSet;
				}
			}else{
				if(context){
					set= this.BlackPawnSet;
					set2 =  this.WhitePawnSet;
				}else{
					set= this.WhitePawnSet;
					set2 =  this.BlackPawnSet;
				}
			}
		for(int i=0;i<set.length;i++){
			if(set[i].isActive()){
				switch(set[i].getStatus()){
					case Pawn.HORSE :
						this.checkHorseMove(set[i]);
					break;
					case Pawn.KING :
						this.checkKingMove(set[i]);
					break;
					case Pawn.PAWN :
						this.checkPawnMove(set[i]);
					break;
					case Pawn.ROCK :
						this.checkRockMove(set[i]);
					break;
					case Pawn.BISHOP:
						this.checkBishopMove(set[i]);
					break;
					case Pawn.QUEEN:
						this.checkQueenMove(set[i]);
					break;
				}
			}
				for(int j=0;j<set.length;j++){
					if(set2[j].isActive() && set2[j].getStatus()==Pawn.KING){
						if(this.MarkedFieldBoard[set2[j].getX()][set2[j].getY()]==GraphPanel.RED_DOT){
							this.resetMarkedFieldBoard();
							this.CheckPawnID = set[i].getId();
							this.Check = true;
							return true;
						}
					}
				}
		}
		
		this.resetMarkedFieldBoard();
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
	 * Metoda przelicza koordynat logiczny na rzeczywisty na panelu graficznym
	 * @param cord
	 * 				Koordynat Logiczny
	 * @return
	 * 				Zwraca koordynat rzeczywisty
	 */
	public int calcLogicToGraph(int cord){
		return cord*62+50;
	}
	
	/**
	 * Metoda blokuje możliwość ruchu pionków(używana w przypadku wystapienia szacha)
	 * @param king
	 * 			Flaga ustawiona na true powoduje zablokowanie także króla
	 */
	public void blockPawns(boolean king) {
		Pawn [] set;
		if(this.Bottom == Board.WHITE_ON_BOTTOM){
			set = this.WhitePawnSet;
		}else{
			set = this.BlackPawnSet;
		}
		for(int i=0;i<set.length;i++){
			if(set[i].getStatus()!=Pawn.KING){
				set[i].setAllowMove(false);
			}else{
				if(king) set[i].setAllowMove(false);
			}
		}
	}
	/**
	 * Metoda odblokowuje ruch pionków
	 */
	public void unlockPawns(){
		Pawn [] set;
		if(this.Bottom == Board.WHITE_ON_BOTTOM){
			set = this.WhitePawnSet;
		}else{
			set = this.BlackPawnSet;
		}
		for(int i=0;i<set.length;i++){
				set[i].setAllowMove(true);
		}
	}
	
	/**
	 * Metoda sprawdza czy wystapił mat (jeżeli tak to wywołuje z panelu graficznego przekazanie informacji o tym zdarzeniu)
	 * @param checkpawnid
	 * 				Identyfikator pionka szachującego
	 */
	private boolean checkMate(int checkpawnid){
		this.resetMarkedFieldBoard();
		Pawn[] set;
		Pawn[] set2;
		Pawn[] allowpawn = new Pawn[16];
		for(int i=0;i<allowpawn.length;i++){
			allowpawn[i]=null;
			
		}
		if(this.Bottom==Board.WHITE_ON_BOTTOM){
			set = this.WhitePawnSet;
			set2 = this.BlackPawnSet;
		}else{
			set2 = this.WhitePawnSet;
			set = this.BlackPawnSet;
		}
		Pawn p=null;
		for(int i=0;i<set2.length;i++){
			if(set2[i].getId()==checkpawnid){
				p = set2[i];
				break;
			}
		}
		Pawn king = null;
		for(int i=0;i<set.length;i++){
			set[i].setAllowMove(false);
			if(set[i].getStatus()==Pawn.KING){
				king = set[i];
			}
		}
		
		int prevx =0;// początkowa pozycja x piona
		int prevy=0;// początkowa pozycja y piona
		int mt2[][] = new int[8][8];

			//sprawdzenie po kolei
		for(int nr=0;nr<set.length;nr++){
			for(int i=0; i<mt2.length;i++){
				for(int j=0;j<mt2[0].length;j++){
					mt2[i][j]=0;
				}
			}
			if(set[nr].isActive()){
					prevx = set[nr].getX();
					prevy = set[nr].getY();
				switch(set[nr].getStatus()){
					case Pawn.HORSE :
						this.checkHorseMove(set[nr]);
					break;
					case Pawn.KING :
						this.checkKingMove(set[nr]);
					break;
					case Pawn.PAWN :
						this.checkPawnMove(set[nr]);
					break;
					case Pawn.ROCK :
						this.checkRockMove(set[nr]);
					break;
					case Pawn.BISHOP:
						this.checkBishopMove(set[nr]);
					break;
					case Pawn.QUEEN:
						this.checkQueenMove(set[nr]);
					break;
				}
					for(int i=0; i<mt2.length;i++){
						for(int j=0;j<mt2[0].length;j++){
							mt2[i][j]=this.MarkedFieldBoard[i][j];
						}
					}
					for(int x=0;x<mt2.length;x++){
						for(int y=0;y<mt2[0].length;y++){
						   if(mt2[x][y]==GraphPanel.GREEN_DOT){ //jeżeli dozwolone wejście na pole
							   set[nr].setX(x);
							   set[nr].setY(y);
							   this.LogicBoard[prevx][prevy]=-1;
							   this.LogicBoard[x][y]=set[nr].getId();
							   this.resetMarkedFieldBoard();
							   //sprawdzenie ruchu pionka szachującego
							   this.Context = false;
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
										System.out.println("Zgadza sie królowa");
										this.checkQueenMove(p);
									break;
								}
							   this.Context = true;
							   if(this.MarkedFieldBoard[king.getX()][king.getY()]!=GraphPanel.RED_DOT){
								  allowpawn[nr]= set[nr];
								  allowpawn[nr].setAllowMove(true);
							   }	
							   this.resetMarkedFieldBoard();
							   this.LogicBoard[set[nr].getX()][set[nr].getY()]=-1;
							   set[nr].setX(prevx);
							   set[nr].setY(prevy);
							   this.LogicBoard[prevx][prevy]=set[nr].getId();
							   
						   }
						   if(allowpawn[nr]!=null)break;
						}
						if(allowpawn[nr]!=null)break;
					}
			}
		}
		for(int i=0;i<allowpawn.length;i++){
			if(allowpawn[i]!=null){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * metoda sprawdza czy król będzie mógł ruszyć się na dane pole
	 */
	private void checkIfKingAllow(){
		int [][] tmp = new int[8][8];
		Pawn[] set;
		if(this.Bottom==Board.WHITE_ON_BOTTOM){
			set=this.BlackPawnSet;
		}else{
			set = this.WhitePawnSet;
		}
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				tmp[i][j]=this.MarkedFieldBoard[i][j];
			}
		}
		this.Context = false;
		for(int i=0;i<16;i++){
				if(set[i].isActive()){
					switch(set[i].getStatus()){
						case Pawn.HORSE :
							this.checkHorseMove(set[i]);
						break;
						//case Pawn.KING :
						//	this.checkKingMove(set[i]);
						//break;
						case Pawn.PAWN :
							this.checkPawnMove(set[i]);
						break;
						case Pawn.ROCK :
							this.checkRockMove(set[i]);
						break;
						case Pawn.BISHOP:
							this.checkBishopMove(set[i]);
						break;
						case Pawn.QUEEN:
							this.checkQueenMove(set[i]);
						break;
					}
				}
		}
		this.Context=true;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.SimulationBoard[i][j]=this.MarkedFieldBoard[i][j];
			}
		}
		
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.MarkedFieldBoard[i][j]=tmp[i][j];
			}
		}
	}
	
	/**
	 * Metoda resetująca tablicę symulacji
	 */
	private void resetSimulationBoard(){
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.SimulationBoard[i][j]=0;
			}
		}
	}
	
	/**
	 * Metoda sprawdza czy pionek może się ruszyć nie odsłaniając króla
	 * @param p
	 * 		  Referencja na pionek do sprawdzenia
	 */
	private void checkAllowMoves(Pawn p){
		this.SimulationFlag = true;
		int [][] tmp = new int[8][8];
		Pawn[] set;
		Pawn[] set2;
		Pawn king = null;
		if(this.Bottom==Board.WHITE_ON_BOTTOM){
			set=this.BlackPawnSet;
			set2 = this.WhitePawnSet;
		}else{
			set = this.WhitePawnSet;
			set2 = this.BlackPawnSet;
		}
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				tmp[i][j]=this.MarkedFieldBoard[i][j];
			}
		}
		
		for(int i=0;i<16;i++){
			if(set2[i].getStatus()==Pawn.KING){
				king = set2[i];
			}
		}
		Pawn oponentpawn = null;
		int prevx = p.getX();
		int prevy = p.getY();
		int oponentpawnid=0;
		int oponentpawnx=0;
		int oponentpawny=0;
		for(int x=0;x<8;x++){
			for(int y=0;y<8;y++){
				if(tmp[x][y]==GraphPanel.GREEN_DOT){
					   p.setX(x);
					   p.setY(y);
					   this.LogicBoard[prevx][prevy]=-1;
					   this.LogicBoard[x][y]=p.getId();
					   this.resetMarkedFieldBoard();
					   this.Context = false;
						for(int i=0;i<16;i++){
								this.resetMarkedFieldBoard();
								if(set[i].isActive()){
									switch(set[i].getStatus()){
										case Pawn.HORSE :
											this.checkHorseMove(set[i]);
										break;
										//case Pawn.KING :
										//	this.checkKingMove(set[i]);
										//break;
										case Pawn.PAWN :
											this.checkPawnMove(set[i]);
										break;
										case Pawn.ROCK :
											this.checkRockMove(set[i]);
										break;
										case Pawn.BISHOP:
											this.checkBishopMove(set[i]);
										break;
										case Pawn.QUEEN:
											this.checkQueenMove(set[i]);
										break;
									}
								}
							if(this.MarkedFieldBoard[king.getX()][king.getY()]==GraphPanel.RED_DOT){
								tmp[p.getX()][p.getY()]=0;
							}	
						}
						this.Context=true;
						this.LogicBoard[p.getX()][p.getY()]=-1;
						p.setX(prevx);
						p.setY(prevy);
						this.LogicBoard[prevx][prevy]=p.getId();
						this.resetMarkedFieldBoard();
				}else if(tmp[x][y]==GraphPanel.RED_DOT){
					   p.setX(x);
					   p.setY(y);
					   oponentpawnid=this.LogicBoard[x][y];
					   for(int z=0;z<16;z++){
						   if(set[z].getId()==oponentpawnid){
							   set[z].setActive(false);
							   oponentpawn = set[z];
						   }
					   }
					   this.LogicBoard[prevx][prevy]=-1;
					   this.LogicBoard[x][y]=p.getId();
					   this.resetMarkedFieldBoard();
					   this.Context = false;
						for(int i=0;i<16;i++){
								this.resetMarkedFieldBoard();
								if(set[i].isActive()){
									switch(set[i].getStatus()){
										case Pawn.HORSE :
											this.checkHorseMove(set[i]);
										break;
										//case Pawn.KING :
										//	this.checkKingMove(set[i]);
										//break;
										case Pawn.PAWN :
											this.checkPawnMove(set[i]);
										break;
										case Pawn.ROCK :
											this.checkRockMove(set[i]);
										break;
										case Pawn.BISHOP:
											this.checkBishopMove(set[i]);
										break;
										case Pawn.QUEEN:
											this.checkQueenMove(set[i]);
										break;
									}
								}
							if(this.MarkedFieldBoard[king.getX()][king.getY()]==GraphPanel.RED_DOT){
								tmp[p.getX()][p.getY()]=0;
							}	
						}
						this.Context=true;
						
						this.LogicBoard[p.getX()][p.getY()]=oponentpawn.getId();
						oponentpawn.setActive(true);
						p.setX(prevx);
						p.setY(prevy);
						this.LogicBoard[prevx][prevy]=p.getId();
						this.resetMarkedFieldBoard();
				}
			}
		}

		
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.SimulationBoard[i][j]=this.MarkedFieldBoard[i][j];
			}
		}
		
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.MarkedFieldBoard[i][j]=tmp[i][j];
			}
		}
		this.SimulationFlag = false;
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

	public int getCheckPawnID() {
		return CheckPawnID;
	}

	public void setCheckPawnID(int checkPawnID) {
		CheckPawnID = checkPawnID;
	}

	public boolean isCheck() {
		return Check;
	}

	public void setCheck(boolean check) {
		Check = check;
	}

	public int[][] getSimulationBoard() {
		return SimulationBoard;
	}

	public void setSimulationBoard(int[][] simulationBoard) {
		SimulationBoard = simulationBoard;
	}
}
