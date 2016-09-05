package Game;


import java.io.Serializable;

import View.GraphPanel;

/**
 * 
 * Klasa reprezentująca szachownicę
 *
 */
public class Board implements Serializable{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		public static final int BLACK_ON_BOTTOM=1; // stała oznaczająca, że gracz jest czarny
		public static final int WHITE_ON_BOTTOM=2;// stała oznaczająca, że gracz jest biały
		public static final int MY_CONTEXT = 0; // stała oznaczająca, że sprawdzamy coś z prespektywy gracza 
		public static final int OPONENT_CONTEXT = 1; // stała oznaczająca, że sprawdzamy coś z perspektywy przeciwnika
		public static final int MAT_CONTEXT = 1; // stała oznaczająca, że sprawdzamy coś z perspektywy sprawdzenia mata
		public static final int NO_MAT_CONTEXT = 0;//stała oznaczająca, że sprawdzamy ruch pionka bez perspektywy mata
		private GraphPanel Graph;// graf,który wywołał szachownicę
		private int MyColor;//kolor gracza
		private Pawn[][] MyBoard; // tablica pionków reprezentująca szachownicę 
		private int[][] LogicBoard; // tablica reprezentująca logiczną szachownicę przetrymująca oznaczone pola 
									// z możliwością ruchu aktualnie zaznaczonergo pionka
		private int[][] SimulationBoard; // tablica symulacji wykorzystywana przy sprawdzaniu ruchów przeciwnika
		private int[][] MySimulationBoard; // tablica symulacji wykorzystywana przy sprawdzaniu ruchów własnego pionka
		private int[][] SaveBoard;// tablica trzymająca dane z tablicy logicznej korzystamy z niej do zapisu/odczytu
		private boolean[][] NoMatBoard; //tablica przetrzymująca pozycje, po których nie ma mata 
		private int[][] AllowPawnMovesBoard; //tablica przetrzymująca pozycje pionka nieodsłaniające króla na mat
		private boolean[][] KingDanger;// tablica przetrzymuje pozycje zagrożenia króla szachem po przesunięciu na dane pole 
		private int Context; // pole kontekstu sprawdzania ruchu pionka 0 zwykły 1 w kontekście mata
		private boolean Lock; // zamek zapobiegający przejściu w rekurencję przy sprawdzaniu ruchów pionka
		private boolean MyMove; // flaga oznaczona na true oznacza ruch gracza
		
		public Board(GraphPanel graph,int colorflag,boolean mymove){
			this.MyColor=colorflag;
			this.Graph=graph;
			this.MyBoard = new Pawn[8][8];
			this.LogicBoard = new int[8][8];
			this.SimulationBoard = new int[8][8];
			this.SaveBoard = new int[8][8];
			this.MySimulationBoard = new int[8][8];
			this.NoMatBoard = new boolean[8][8];
			this.KingDanger = new boolean[8][8];
			this.AllowPawnMovesBoard = new int[8][8];
			this.Context = Board.NO_MAT_CONTEXT;
			this.Lock = true;
			this.MyMove = mymove;
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					this.MyBoard[i][j]=null;
					this.LogicBoard[i][j]=0;
					this.SimulationBoard[i][j]=0;
					this.SaveBoard[i][j]=0;
					this.MySimulationBoard[i][j]=0;
					this.AllowPawnMovesBoard[i][j]=0;
					this.NoMatBoard[i][j]=false;
					this.KingDanger[i][j]=false;
				}
			}
			int id = 0;
			if(this.MyColor==Board.WHITE_ON_BOTTOM){
				for(int i=0;i<8;i++){
						this.MyBoard[i][6]=new Pawn(id,Pawn.PAWN,Board.WHITE_ON_BOTTOM,i,6);
						id=id+1;
						this.MyBoard[i][1]=new Pawn(id,Pawn.PAWN,Board.BLACK_ON_BOTTOM,i,1);
						id=id+1;
					}
				this.MyBoard[0][7] = new Pawn(16,Pawn.ROCK,Pawn.WHITE,0,7);
				this.MyBoard[1][7] = new Pawn(17,Pawn.HORSE,Pawn.WHITE,1,7);
				this.MyBoard[2][7] = new Pawn(18,Pawn.BISHOP,Pawn.WHITE,2,7);
				this.MyBoard[3][7] = new Pawn(19,Pawn.QUEEN,Pawn.WHITE,3,7);
				this.MyBoard[4][7]= new Pawn(20,Pawn.KING,Pawn.WHITE,4,7);
				this.MyBoard[5][7] = new Pawn(21,Pawn.BISHOP,Pawn.WHITE,5,7);
				this.MyBoard[6][7] = new Pawn(22,Pawn.HORSE,Pawn.WHITE,6,7);
				this.MyBoard[7][7] = new Pawn(23,Pawn.ROCK,Pawn.WHITE,7,7);
				this.MyBoard[0][0]= new Pawn(24,Pawn.ROCK,Pawn.BLACK,0,0);
				this.MyBoard[1][0] = new Pawn(25,Pawn.HORSE,Pawn.BLACK,1,0);
				this.MyBoard[2][0] = new Pawn(26,Pawn.BISHOP,Pawn.BLACK,2,0);
				this.MyBoard[3][0] = new Pawn(27,Pawn.QUEEN,Pawn.BLACK,3,0);
				this.MyBoard[4][0] = new Pawn(28,Pawn.KING,Pawn.BLACK,4,0);
				this.MyBoard[5][0] = new Pawn(29,Pawn.BISHOP,Pawn.BLACK,5,0);
				this.MyBoard[6][0] = new Pawn(30,Pawn.HORSE,Pawn.BLACK,6,0);
				this.MyBoard[7][0] = new Pawn(31,Pawn.ROCK,Pawn.BLACK,7,0);
			}else{
				for(int i=0;i<8;i++){
					this.MyBoard[i][1]=new Pawn(id,Pawn.PAWN,Board.WHITE_ON_BOTTOM,i,1);
					id=id+1;
					this.MyBoard[i][6]=new Pawn(id,Pawn.PAWN,Board.BLACK_ON_BOTTOM,i,6);
					id=id+1;
				}
				this.MyBoard[0][0] = new Pawn(16,Pawn.ROCK,Pawn.WHITE,0,0);
				this.MyBoard[1][0] = new Pawn(17,Pawn.HORSE,Pawn.WHITE,1,0);
				this.MyBoard[2][0] = new Pawn(18,Pawn.BISHOP,Pawn.WHITE,2,0);
				this.MyBoard[3][0] = new Pawn(19,Pawn.QUEEN,Pawn.WHITE,3,0);
				this.MyBoard[4][0]= new Pawn(20,Pawn.KING,Pawn.WHITE,4,0);
				this.MyBoard[5][0] = new Pawn(21,Pawn.BISHOP,Pawn.WHITE,5,0);
				this.MyBoard[6][0] = new Pawn(22,Pawn.HORSE,Pawn.WHITE,6,0);
				this.MyBoard[7][0] = new Pawn(23,Pawn.ROCK,Pawn.WHITE,7,0);
				this.MyBoard[0][7]= new Pawn(24,Pawn.ROCK,Pawn.BLACK,0,7);
				this.MyBoard[1][7] = new Pawn(25,Pawn.HORSE,Pawn.BLACK,1,7);
				this.MyBoard[2][7] = new Pawn(26,Pawn.BISHOP,Pawn.BLACK,2,7);
				this.MyBoard[3][7] = new Pawn(27,Pawn.QUEEN,Pawn.BLACK,3,7);
				this.MyBoard[4][7] = new Pawn(28,Pawn.KING,Pawn.BLACK,4,7);
				this.MyBoard[5][7] = new Pawn(29,Pawn.BISHOP,Pawn.BLACK,5,7);
				this.MyBoard[6][7] = new Pawn(30,Pawn.HORSE,Pawn.BLACK,6,7);
				this.MyBoard[7][7] = new Pawn(31,Pawn.ROCK,Pawn.BLACK,7,7);
			}	

		}
		/**
		 * Metoda sprawdza kliknięcie przekazane z panelu graficznego
		 * @param x
		 * 			Współrzędna X pozycji kliknięcia
		 * @param y
		 * 			Współrzedna Y pozycji kliknięcia
		 * @return
		 * 			Zwraca obiekt zaznaczonego pionka
		 */
		public Pawn checkClicked(int x, int y){
			this.resetTab(this.LogicBoard);
			int lx = this.getLogicCord(x);
			int ly = this.getLogicCord(y);
			if(lx>=0 && lx<8 && ly>=0 && ly<8){
				if(this.MyBoard[lx][ly]!=null && this.MyBoard[lx][ly].getColor()==this.MyColor){
					this.checkMove(this.MyBoard[lx][ly],Board.MY_CONTEXT);
					return this.MyBoard[lx][ly];
				}
			}
			return null;
		}
		
		/**
		 * Metoda wykonująca dozwolony ruch pionka na wybraną pozycję 
		 * Metoda przlicza współrzędne Graficzne na logiczne wewnątrz
		 * @param p
		 * 			Referencja na pionek
		 * @return
		 * 			Zwraca true jeżeli ruch zstał wykonany
		 */
		public boolean makeMove(Pawn p,int x, int y){
			int lx=this.getLogicCord(x);
			int ly=this.getLogicCord(y);
			if(this.MyBoard[lx][ly]!=null){
				if(this.MyBoard[lx][ly].getStatus()==Pawn.KING)return false;
			}
			if(lx>=0 && lx<=8 && ly>=0 && ly<8){
				if(this.LogicBoard[lx][ly]==GraphPanel.GREEN_DOT || this.LogicBoard[lx][ly]==GraphPanel.RED_DOT){
					if(this.LogicBoard[lx][ly]==GraphPanel.RED_DOT){
						this.MyBoard[lx][ly].setActive(false);
						this.MyBoard[lx][ly].setAllowMove(false);
						this.MyBoard[lx][ly].setX(-1);
						this.MyBoard[lx][ly].setY(-1);;
					}
					this.resetTab(this.NoMatBoard);
					this.resetTab(this.KingDanger);
					this.MyBoard[p.getX()][p.getY()]=null;
					this.MyBoard[lx][ly]=p;
					p.setX(lx);
					p.setY(ly);
					p.setMoveCounter(p.getMoveCounter()+1);
					this.resetTab(this.LogicBoard);
					this.MyMove = false;
					this.Graph.getFrame().getMoveLab().setText("<< Ruch przeciwnika >>");
					return true;
				}
			}
			this.resetTab(this.LogicBoard);
			return false;
		}
		
		
		/**
		 * Metoda przeznaczona do ruchów przeciwnika 
		 * @param id
		 * 			Identyfikator pionka przeciwnika
		 */
		public void makeOponentMove(int id,int px,int py){
			this.resetTab(this.NoMatBoard);
			this.resetTab(this.KingDanger);
			Pawn enemy = null;
			Pawn p = this.getPawnById(id);
			if(p!=null){
				int x =Board.calculateOponentCord(px,false);
				int y =Board.calculateOponentCord(py,true);
				enemy = this.getPawnById(p.getId());
				if(this.MyBoard[x][y]!=null){
					this.MyBoard[x][y].setActive(false);
					this.MyBoard[x][y].setAllowMove(false);
					this.MyBoard[x][y].setX(-1);
					this.MyBoard[x][y].setY(-1);
				}
				this.MyBoard[enemy.getX()][enemy.getY()] = null;
				enemy.setX(x);
				enemy.setY(y);
				this.MyBoard[enemy.getX()][enemy.getY()] = enemy;
				this.MyMove = true;
				this.Graph.getFrame().getMoveLab().setText("<< Twój ruch >>");
				
			}
			this.Graph.repaint();
		}
		
		/**
		 * Metoda sprawdza wystapienie szacha
		 * @return
		 * 		Zwraca true jeżeli szach
		 */
		public boolean checkCheck(){
		  int kingid = -100; // zmienna identyfikatora króla
		  if(this.MyColor==Board.BLACK_ON_BOTTOM){
			kingid = 28;
		  }else{
			kingid = 20;  
		  }
		  
		  Pawn king = this.getPawnById(kingid);
		  this.simulateOponentMoves();
		  if(this.SimulationBoard[king.getX()][king.getY()]==GraphPanel.RED_DOT){
			  this.resetTab(this.SimulationBoard);
			  return true;
		  }
		 this.resetTab(this.SimulationBoard);
		 return false;	
		 
		}
		
		/**
		 * Metoda sprawdza wystapienie mata
		 * @return
		 * 		Zwraca true jeżeli mat
		 */
		public boolean checkMate(){
			this.Context = Board.MAT_CONTEXT;
			Pawn p=null;// zmienna pomocnicz do przechowania pionka zbijanego w sumulacji
			int counter = 0;
			for(int i=0;i<8;i++){ // iteracja po własnych pionkach
				for(int j=0;j<8;j++){
					if(MyBoard[i][j]!=null && MyBoard[i][j].getColor()==MyColor && MyBoard[i][j].isActive() && MyBoard[i][j].isAllowMove()){
						this.resetTab(this.MySimulationBoard);
						this.simulateMyMove(MyBoard[i][j]);
						for(int x=0;x<8;x++){ // iteracja po możliwch ruchach własnego pionka 
							for(int y=0;y<8;y++){
								this.Context = Board.MAT_CONTEXT;
								if(MySimulationBoard[x][y]!=0){ // jeżeli zielona lub czerwona kropka
									if(MySimulationBoard[x][y]==GraphPanel.RED_DOT){
										p=MyBoard[x][y];
										p.setX(-1);
										p.setY(-1);
										p.setActive(false);
										p.setAllowMove(false);
									}
									MyBoard[x][y]=MyBoard[i][j];
									MyBoard[x][y].setX(x);
									MyBoard[x][y].setY(y);
									MyBoard[i][j]=null;
									if(!this.checkCheck()){
										this.NoMatBoard[x][y]=true;
										counter = counter +1;
									}else{
										if(this.MyBoard[x][y].getStatus()==Pawn.KING){
											this.KingDanger[x][y]=true;
										}
									}
									
									MyBoard[i][j]=MyBoard[x][y];
									MyBoard[i][j].setX(i);
									MyBoard[i][j].setY(j);
									if(MySimulationBoard[x][y]==GraphPanel.RED_DOT){
										MyBoard[x][y]=p;
										p.setX(x);
										p.setY(y);
										p.setActive(true);
										p.setAllowMove(true);
									}else{
										MyBoard[x][y]=null;
									}
								}
							}
						}
					}
				}
			}
			this.Context = Board.NO_MAT_CONTEXT;
			//this.lockAllPawns();
			if(counter>0)return false;
			return true;
		}
		
		/**
		 * Metoda wypełnie tablicę dozwolonych ruchów pionka po symulacji (sprawdza czy ruch pionka nie odsłoni króla na szach)
		 * @param p
		 * 			Referencja na pionek sprawdzany 
		 */
		private void checkAllowMoves(Pawn pawn){
			if(pawn.getColor()!=this.MyColor)return;
			this.Lock = false;
			int prevx = pawn.getX();
			int prevy = pawn.getY();
			boolean control = false; // kontrolka wystapienia czerwonej kropki w sprawdzeniu symulacyjnym
			Pawn p= null; // poprzedni pionek 
			for(int i=0;i<8;i++)
				for(int j=0;j<8;j++)
						this.AllowPawnMovesBoard[i][j]=this.LogicBoard[i][j];
			
			for(int x=0;x<8;x++){ // iteracja po możliwch ruchach własnego pionka 
				for(int y=0;y<8;y++){
					this.Context = Board.MAT_CONTEXT;
					if(AllowPawnMovesBoard[x][y]!=0){ // jeżeli zielona lub czerwona kropka
						if(AllowPawnMovesBoard[x][y]==GraphPanel.RED_DOT){
							p=MyBoard[x][y];
							p.setX(-1);
							p.setY(-1);
							p.setActive(false);
							p.setAllowMove(false);
						}
						MyBoard[x][y]=MyBoard[prevx][prevy];
						MyBoard[x][y].setX(x);
						MyBoard[x][y].setY(y);
						MyBoard[prevx][prevy]=null;
						if(AllowPawnMovesBoard[x][y]==GraphPanel.RED_DOT)control = true;
						if(this.checkCheck()){
							AllowPawnMovesBoard[x][y]=0;
						}
						MyBoard[prevx][prevy]=MyBoard[x][y];
						MyBoard[prevx][prevy].setX(prevx);
						MyBoard[prevx][prevy].setY(prevy);
						if(control){
							MyBoard[x][y]=p;
							p.setX(x);
							p.setY(y);
							p.setActive(true);
							p.setAllowMove(true);
							control = false;
						}else{
							MyBoard[x][y]=null;
						}
					}
				}
			}
			
			for(int i=0;i<8;i++)
				for(int j=0;j<8;j++)
					this.LogicBoard[i][j]=this.AllowPawnMovesBoard[i][j];
			Lock = true;
			
		}
		
		/**
		 * Metoda symulująca ruchy przeciwników po jej wykonaniu mamy wszelkie możliwe ruchy obecnego stanu zapisane w tablicy symulacji
		 */
		private void simulateOponentMoves(){
			this.resetTab(this.SimulationBoard);
			this.resetTab(this.LogicBoard);
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(this.MyBoard[i][j]!=null){
						if(this.MyBoard[i][j].getColor()!=this.MyColor && this.MyBoard[i][j].isActive() && this.MyBoard[i][j].isAllowMove() ){
							this.checkMove(this.MyBoard[i][j],Board.OPONENT_CONTEXT);
							this.rewriteSimulation();
							this.resetTab(this.LogicBoard);
						}
					}
				}
			}
		}
		/**
		 * Metoda symulująca ruch własnego pionka po jej wykonaniu uzyskujemy wypełnioną tablicę symulacji pionka
		 * @param p
		 * 			Referencja na pionek do symulacji
		 */
		private void simulateMyMove(Pawn p){
			this.resetTab(this.LogicBoard);
			this.checkMove(p,Board.MY_CONTEXT);
			this.rewriteMySimulation();
		}
		
		/**
		 * Metoda blokująca ruch własnych pionków
		 */
		public void lockAllPawns(){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(MyBoard[i][j]!=null && MyBoard[i][j].getColor()==MyColor && MyBoard[i][j].isActive()){
						MyBoard[i][j].setAllowMove(false);
					}
				}
			}
		}
		
		/**
		 * Metoda odblokowuje ruchy wszystkich pionków
		 */
		public void unlockAllPawns(){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(MyBoard[i][j]!=null && MyBoard[i][j].getColor()==MyColor && MyBoard[i][j].isActive()){
						MyBoard[i][j].setAllowMove(true);
					}
				}
			}
		}

		/**
		 * Metoda przeliczająca podaną współrzedną logiczną na wymiar w panelu graficznym
		 * @param cord
		 * 			Pozycja logiczna do przeliczenia
		 * @return
		 * 			Zwraca przliczoną pozycję dla panelu graficznego
		 */
		public int getGraphCord(int cord){
			if(cord>=0 && cord <8){
				return 50+(cord*62);
			}else{
				return 0;
			}
		}
		
		/**
		 * Metoda przliczająca koordynat z panelu graficznego na koordynat logiczny planszy
		 * @param cord
		 * 			Koordynat graficzny 
		 * @return
		 * 		 Zwraca koordynat logiczny
		 */
		public int getLogicCord(int cord){
			return (int)((cord - 50)/62);
		}
		
		/**
		 * Metoda wypełniająca tablicę dwuwymiarową zerami
		 * @param tab
		 * 			Referencja na tablicę
		 */
		public void resetTab(int[][] tab){
			for(int i=0;i<tab.length;i++){
				for(int j=0;j<tab[0].length;j++){
					tab[i][j]=0;
				}
			}
		}
		
		/**
		 * Metoda wypełniająca tablicę dwuwymiarową wartością FALSE
		 * @param tab
		 * 			Referencja na tablicę
		 */
		public void resetTab(boolean[][] tab){
			for(int i=0;i<tab.length;i++){
				for(int j=0;j<tab[0].length;j++){
					tab[i][j]=false;
				}
			}
		}
		
		private boolean isTabFalse(boolean[][] tab){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(tab[i][j]!=false) return false;
				}
			}
			return true;
		}
		
		/**
		 * Metoda sprawdzająca ruch pionka
		 * @param p
		 * 		Referencja na pionek
		 * @param context
		 * 		Stała perspektywy sprawdzania
		 */
		public void checkMove(Pawn p,int context){
			if(p!=null){
				if(p.isActive() && p.isAllowMove()){
					switch(p.getStatus()){
						case Pawn.PAWN :
							this.checkPawnMove(p,context);
						break;
						case Pawn.HORSE :
							this.checkHorseMove(p);
						break;
						case Pawn.QUEEN :
							this.checkQueenMove(p);
						break;
						case Pawn.BISHOP :
							this.checkBishopMove(p);
						break;
						case Pawn.ROCK :
							this.checkRockMove(p);
						break;
						case Pawn.KING :
							//if(this.Context==Board.MAT_CONTEXT && context==Board.MY_CONTEXT)this.Context=NO_MAT_CONTEXT;
							this.checkKingMove(p);
						break;
					}
				
					if(this.Lock){
						this.checkAllowMoves(p);
					}
	
					if(!this.isTabFalse(this.NoMatBoard) && this.Context != Board.MAT_CONTEXT){
						for(int i=0;i<8;i++){
							for(int j=0;j<8;j++){
								if(this.LogicBoard[i][j]==GraphPanel.GREEN_DOT || this.LogicBoard[i][j]==GraphPanel.RED_DOT ) 
									if(this.NoMatBoard[i][j]==false)this.LogicBoard[i][j]=0;
							}
						}
					}
					
					if(p.getStatus()==Pawn.KING && !this.isTabFalse(KingDanger)){
						for(int i=0;i<8;i++){
							for(int j=0;j<8;j++){
								if(this.LogicBoard[i][j]==GraphPanel.GREEN_DOT || this.LogicBoard[i][j]==GraphPanel.RED_DOT ) 
									if(this.KingDanger[i][j]==true)this.LogicBoard[i][j]=0;
							}
						}
					}
			  }
			}
		}
		
		
		/**
		 * Metoda sprawdzająca możliwośc ruchu pionka (zwykły pion)
		 * @param p
		 * 		Referencja na pionek
		 * @param context
		 * 		Stała perspektywy sprawdzania
		 * 
		 */
		private void checkPawnMove(Pawn p,int context){
			int y = p.getY();
			int x = p.getX();
				if(context==Board.MY_CONTEXT){
						if(p.getMoveCounter()==0){
							if(y-1>=0){
								if(this.MyBoard[x][y-1]==null){
									this.LogicBoard[x][y-1] = GraphPanel.GREEN_DOT;
									if(y-2>=0){
										if(this.MyBoard[x][y-2]==null){
											this.LogicBoard[x][y-2] = GraphPanel.GREEN_DOT;
										}
									}
								}
							}
						}else{
							if(y-1>=0){
								if(this.MyBoard[x][y-1]==null){
									this.LogicBoard[x][y-1] = GraphPanel.GREEN_DOT;
								}
							}
						}
						if(x-1>=0 && y-1>=0){
							if(this.MyBoard[x-1][y-1]!=null && this.MyBoard[x-1][y-1].getColor()!=p.getColor()){
								this.LogicBoard[x-1][y-1] = GraphPanel.RED_DOT;
							}
						}
						if(x+1<8 && y-1>=0){
							if(this.MyBoard[x+1][y-1]!=null && this.MyBoard[x+1][y-1].getColor()!=p.getColor()){
								this.LogicBoard[x+1][y-1] = GraphPanel.RED_DOT;
							}
						}
			 }else if(context == Board.OPONENT_CONTEXT){
					if(p.getMoveCounter()==0){
						if(y+1<8){
							if(this.MyBoard[x][y+1]==null){
								this.LogicBoard[x][y+1] = GraphPanel.GREEN_DOT;
								if(y+2<8){
									if(this.MyBoard[x][y+2]==null){
										this.LogicBoard[x][y+2] = GraphPanel.GREEN_DOT;
									}
								}
							}
						}
					}else{
						if(y+1<8){
							if(this.MyBoard[x][y+1]==null){
								this.LogicBoard[x][y+1] = GraphPanel.GREEN_DOT;
							}
						}
					}
					if(x-1>=0 && y+1<8){
						if(this.MyBoard[x-1][y+1]!=null && this.MyBoard[x-1][y+1].getColor()!=p.getColor()){
							this.LogicBoard[x-1][y+1] = GraphPanel.RED_DOT;
						}
					}
					if(x+1<8 && y+1<8){
						if(this.MyBoard[x+1][y+1]!=null && this.MyBoard[x+1][y+1].getColor()!=p.getColor()){
							this.LogicBoard[x+1][y+1] = GraphPanel.RED_DOT;
						}
					}
			 }
		}
		
		/**
		 * Metoda sprawdzająca ruch skoczka 
		 * @param p
		 * 			Referencja na pionek
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
							if(this.MyBoard[x][y]==null){
									this.LogicBoard[x][y]=GraphPanel.GREEN_DOT;
							}else{
								if(this.MyBoard[x][y].getColor()!=p.getColor()){
									this.LogicBoard[x][y]=GraphPanel.RED_DOT;
								}
							}
						}
					}
				}
			}
		}
		
		/**
		 * Metoda sprawdzająca ruchy króla
		 * @param p
		 * 			Referencja na pionek
		 */
		private void checkKingMove(Pawn p){
			int x,y;
			int i,j;
			for(i = -1;i<2;i++){
				for(j=-1;j<2;j++){
					if(!(j==0 && i==0)){
						x = p.getX()+i;
						y=p.getY()+j;
						if(x>=0 && x<8 && y>=0 && y<8){
							if(this.MyBoard[x][y]==null){
									this.LogicBoard[x][y]=GraphPanel.GREEN_DOT;
							}else{
								if(this.MyBoard[x][y].getColor()!=p.getColor()){
									this.LogicBoard[x][y]=GraphPanel.RED_DOT;
								}
							}
						}
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
				if(this.MyBoard[p.getX()][i]==null){
					this.LogicBoard[p.getX()][i]=GraphPanel.GREEN_DOT;
				}else if(this.MyBoard[p.getX()][i].getColor()!=p.getColor()){
					this.LogicBoard[p.getX()][i]=GraphPanel.RED_DOT;
					break;
				}else break;
			}
			
			for(i=p.getY()-1;i>=0;i--){ // sprawdzenie w górę szachownicy
				if(this.MyBoard[p.getX()][i]==null){
					this.LogicBoard[p.getX()][i]=GraphPanel.GREEN_DOT;
				}else if(this.MyBoard[p.getX()][i].getColor()!=p.getColor()){
					this.LogicBoard[p.getX()][i]=GraphPanel.RED_DOT;
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
				if(this.MyBoard[i][p.getY()]==null){
					this.LogicBoard[i][p.getY()]=GraphPanel.GREEN_DOT;
				}else if(this.MyBoard[i][p.getY()].getColor()!=p.getColor()){
					this.LogicBoard[i][p.getY()]=GraphPanel.RED_DOT;
					break;
				}else break;
			}
			
			for(i=p.getX()-1;i>=0;i--){ // sprawdzenie w lewą szachownicy
				if(this.MyBoard[i][p.getY()]==null){
					this.LogicBoard[i][p.getY()]=GraphPanel.GREEN_DOT;
				}else if(this.MyBoard[i][p.getY()].getColor()!=p.getColor()){
					this.LogicBoard[i][p.getY()]=GraphPanel.RED_DOT;
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
					if(this.MyBoard[x][y]==null){
						this.LogicBoard[x][y]=GraphPanel.GREEN_DOT;
					}else if(this.MyBoard[x][y].getColor()!=p.getColor()){
						this.LogicBoard[x][y]=GraphPanel.RED_DOT;
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
					if(this.MyBoard[x][y]==null){
						this.LogicBoard[x][y]=GraphPanel.GREEN_DOT;
					}else if(this.MyBoard[x][y].getColor()!=p.getColor()){
						this.LogicBoard[x][y]=GraphPanel.RED_DOT;
						break;
					}else break;
					if(j==1){x= x+1; y=y-1;}else{x=x-1;y=y+1;}
				}
				j++;
			}
		}
		
		

		/**
		 * Metoda wyszukująca pionek na szachownicy po jego identyfikatorze
		 * @param id
		 * 			Identyfikator pionka
		 * @return
		 * 			Zwraca znaleziony pionek
		 */
		public Pawn getPawnById(int id){
			Pawn p = null;
			for(int i=0;i<this.MyBoard.length;i++){
				for(int j=0;j<this.MyBoard[0].length;j++){
					if(this.MyBoard[i][j]!=null){
						if(this.MyBoard[i][j].getId()==id){
							return this.MyBoard[i][j];
						}
					}
				}
			}
			return p;
		}
		
		/**
		 * Metoda zrzucająca ruchy przeciwnika do tablicy symulacyjnej
		 */
		private void rewriteSimulation(){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(this.LogicBoard[i][j]==GraphPanel.GREEN_DOT || this.LogicBoard[i][j]==GraphPanel.RED_DOT){
						this.SimulationBoard[i][j] = this.LogicBoard[i][j];
					}
				}
			}
		}
		
		/**
		 * Metoda zrzucająca ruchy własnego pionka do tablicy symulacyjnej
		 */
		private void rewriteMySimulation(){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
						this.MySimulationBoard[i][j] = this.LogicBoard[i][j];
				}
			}
		}
		

		/**
		 * Metoda wyliczająca odwrotną pozycję pionka dla szachownicy przeciwnika
		 * @param cord
		 * 			Współrzędna do przeliczenia
		 * @param yflag
		 * 			Flaga ustawiona na true rozpoznaje współrzędną zmienną (w przypadku szachów współrzedna Y zmienia się dla szachów przeciwnika)
		 * @return
		 * 		Zwraca przeliczoną współrzędną
		 */
		public static int calculateOponentCord(int cord,boolean yflag){
			if(yflag)return ((-cord)+7);
			return cord;
		}

		public GraphPanel getGraph() {
			return Graph;
		}


		public void setGraph(GraphPanel graph) {
			Graph = graph;
		}


		public int getMyColor() {
			return MyColor;
		}


		public void setMyColor(int myColor) {
			MyColor = myColor;
		}


		public Pawn[][] getMyBoard() {
			return MyBoard;
		}


		public void setMyBoard(Pawn[][] myBoard) {
			if(this.MyBoard==null){this.MyBoard=new Pawn[8][8];}
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					this.MyBoard[i][j]=myBoard[i][j];
				}
			}
		}


		public int[][] getLogicBoard() {
			return LogicBoard;
		}


		public void setLogicBoard(int[][] logicBoard) {
			LogicBoard = logicBoard;
		}
		public boolean isMyMove() {
			return MyMove;
		}
		public void setMyMove(boolean myMove) {
			MyMove = myMove;
		}
		
		
	}


