package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import CommunicationModel.GameSaved;
import CommunicationModel.Pack;
import Game.Board;
import Game.Pawn;
import Game.Player;

/**
 * 
 * Klasa reprezentująca panel graficzny, na którym odbywa się rozgrywka, oraz wyświetlane są elementy jak np. ranking
 *
 */
public class GraphPanel extends JPanel implements MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int GREEN_DOT = 1; // stała zielonej kropki oznaczającej
	public static final int RED_DOT = 2; // stała czerownej kropki oznaczającej
	private GameFrame Frame;
	private BufferedImage Background; // tło wejściowe
	private BufferedImage BackgroundBoardInvert; // odwrócone tło przedstawiające szachownicę
	private BufferedImage BackgroundBoard; // tło przedstawiające szachownicę
	private BufferedImage RedDotImg; // obrazek czerownej kropki
	private BufferedImage GreenDotImg; // obrazek zielonej kropki
	private JScrollPane Scroll; // panel scrollowany na tabelę z rankingiem 
	private JScrollPane Scroll2; // panel scrollowany na tabelę ze stołami 
	private JScrollPane Scroll3; // panel scrollowany na tabelę z zapisanymi grami
	private JTable RankTab; // tabela rankingowa graczy;
	private JTable TablesTab; // tabela stołów utworzonych przez graczy
	private JTable SavesTab;// tabela zapisanych gier
	private byte GameState; // flaga określająca w jakim trybie jest okno 0-pierwsze wejście, 1-ranking, 2-szachownica, 3-dostępne stoły, 4-zapsane gry
	private BufferedImage[] WhitePawnImgSet; // zestaw obrazków dla białych pionków
	private BufferedImage[] BlackPawnImgSet; // zestaw obrazków dla czarnych pionków
	private boolean GameStarted; // flaga określająca czy rozpoczęła się gra
	private Board MyBoard; // obiekt szachownicy
	private boolean InvertedFlag; // flaga okeslająca czy tło szachownicy było odwrócone
	private Pawn SelectedPawn; //obiekt zaznaczonego pionka
	private boolean WantedUpload; // flaga wskazuje, czy ten gracz chicał wczytać grę
	private int Kolor;//kolor po wczytaniu zapisanej gry
	private boolean Move;// ruch po wczytaniu



	
	public GraphPanel(int w, int h, GameFrame frm){
		this.Frame = frm;
		this.setPreferredSize(new Dimension(w,h));
		this.setLayout(new FlowLayout());
		this.setOpaque(false);
		this.InvertedFlag = false;
		this.WantedUpload = false;

		
		try {
		    this.Background = ImageIO.read(getClass().getResource("/img/background2.png"));
		    this.BackgroundBoard = ImageIO.read(getClass().getResource("/img/board.jpg"));
		    this.BackgroundBoardInvert = ImageIO.read(getClass().getResource("/img/board_invert.jpg"));
		    this.RedDotImg = ImageIO.read(getClass().getResource("/img/dot_red.png"));
		    this.GreenDotImg = ImageIO.read(getClass().getResource("/img/dot_green.png"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// inicjalizacja rankingowa
		
				String[] cn = {"Miejsce","Nick","Zwycięztwa","Porażki","Remisy"};
				String[][] cv = new String[100][5];
				for(int i =0;i<100;i++){
					for(int j =0;j<5;j++){
						cv[i][j]= "";
					}
				}
				this.RankTab = new JTable(cv,cn){
					private static final long serialVersionUID = 1L;

					@Override
					    public boolean isCellEditable(int row, int column) {
					        return false;
					    }
					
				};
				
		// inicjalizacja tabeli stołów 
				String[] cn2 = {" ","Nick"};
				String[][] cv2 = new String[50][2];
				for(int i =0;i<50;i++){
					for(int j =0;j<2;j++){
						cv2[i][j]= "";
					}
				}
				this.TablesTab = new JTable(cv2,cn2){
					private static final long serialVersionUID = 1L;

					@Override
					    public boolean isCellEditable(int row, int column) {
					        return false;
					    }
				};
		// inicjalizacja tabeli zapisanych gier
				String[] cn3 = {"","Status przeciwnika","Przeciwnik"};
				String[][] cv3 = new String[50][3];
				for(int i =0;i<50;i++){
					for(int j =0;j<3;j++){
						cv3[i][j]= "";
					}
				}
				this.SavesTab = new JTable(cv3,cn3){
					private static final long serialVersionUID = 1L;

					@Override
					    public boolean isCellEditable(int row, int column) {
					        return false;
					    }
				};
				
				this.GameState = 0;
				this.Scroll = new JScrollPane(this.RankTab);
				this.Scroll.setPreferredSize(new Dimension(w-10,h-10));
				this.Scroll.setVisible(false);
				this.Scroll2 = new JScrollPane(this.TablesTab);
				this.Scroll2.setPreferredSize(new Dimension(w-10,h-10));
				this.Scroll2.setVisible(false);
				this.Scroll3 = new JScrollPane(this.SavesTab);
				this.Scroll3.setPreferredSize(new Dimension(w-10,h-10));
				this.Scroll3.setVisible(false);
				this.add(this.Scroll);
				this.add(this.Scroll2);
				this.add(this.Scroll3);
		
		// wczytanie obrazków dla zestawu pionków
			 this.WhitePawnImgSet = new BufferedImage[16];
			 this.BlackPawnImgSet = new BufferedImage[16];
			try{ 
			 for(int i=0;i<8;i++){
				 this.WhitePawnImgSet[i]=ImageIO.read(getClass().getResource("/img/pawns/white_pawn.png"));
				 this.BlackPawnImgSet[i]=ImageIO.read(getClass().getResource("/img/pawns/black_pawn.png"));
			 }
			 this.WhitePawnImgSet[8]=ImageIO.read(getClass().getResource("/img/pawns/white_tower.png"));
			 this.BlackPawnImgSet[8]=ImageIO.read(getClass().getResource("/img/pawns/black_tower.png"));
			 this.WhitePawnImgSet[9]=ImageIO.read(getClass().getResource("/img/pawns/white_horse.png"));
			 this.BlackPawnImgSet[9]=ImageIO.read(getClass().getResource("/img/pawns/black_horse.png")); 
			 this.WhitePawnImgSet[10]=ImageIO.read(getClass().getResource("/img/pawns/white_bishop.png"));
			 this.BlackPawnImgSet[10]=ImageIO.read(getClass().getResource("/img/pawns/black_bishop.png")); 
			 this.WhitePawnImgSet[11]=ImageIO.read(getClass().getResource("/img/pawns/white_king.png"));
			 this.BlackPawnImgSet[11]=ImageIO.read(getClass().getResource("/img/pawns/black_king.png")); 
			 this.WhitePawnImgSet[12]=ImageIO.read(getClass().getResource("/img/pawns/white_queen.png"));
			 this.BlackPawnImgSet[12]=ImageIO.read(getClass().getResource("/img/pawns/black_queen.png")); 
			 this.WhitePawnImgSet[13]=ImageIO.read(getClass().getResource("/img/pawns/white_bishop.png"));
			 this.BlackPawnImgSet[13]=ImageIO.read(getClass().getResource("/img/pawns/black_bishop.png")); 
			 this.WhitePawnImgSet[14]=ImageIO.read(getClass().getResource("/img/pawns/white_horse.png"));
			 this.BlackPawnImgSet[14]=ImageIO.read(getClass().getResource("/img/pawns/black_horse.png")); 
			 this.WhitePawnImgSet[15]=ImageIO.read(getClass().getResource("/img/pawns/white_tower.png"));
			 this.BlackPawnImgSet[15]=ImageIO.read(getClass().getResource("/img/pawns/black_tower.png")); 

			}catch (IOException e) {
			    e.printStackTrace();
			}
			
			this.GameStarted = false;
			this.MyBoard = null;
			this.addMouseListener(this);
				
		this.repaint();
		this.Scroll.setVisible(false);
	}
	
	/**
	 * Metoda wyświetlająca tabelę rankingową na podstawie przekazanej tabeli graczy
	 * @param players
	 * 				Referencja na tablicę graczy
	 */
	public void showRanking(Player[] players){
		if(players==null)return;
		int i=0; 
		for(i=0;i<50;i++){
			this.RankTab.setValueAt("",i,0);
			this.RankTab.setValueAt("",i,1);
			this.RankTab.setValueAt("",i,2);
			this.RankTab.setValueAt("",i,3);
			this.RankTab.setValueAt("",i,4);
		}
		i=0;
		while(i<100 && players[i]!=null){
			if(players[i].getNick().equals(this.Frame.getMyClient().getMyPlayer().getNick())){
				this.RankTab.setSelectionBackground(Color.GREEN);
				this.RankTab.setRowSelectionInterval(i,i);
			}
			this.RankTab.setValueAt(Integer.toString(i+1),i,0);
			this.RankTab.setValueAt(players[i].getNick(),i,1);
			this.RankTab.setValueAt(Integer.toString(players[i].getWins()),i,2);
			this.RankTab.setValueAt(Integer.toString(players[i].getLoses()),i,3);
			this.RankTab.setValueAt(Integer.toString(players[i].getDraw()),i,4);
			i = i+1;
		}
		this.GameState = 1;
		this.Scroll2.setVisible(false);
		this.Scroll3.setVisible(false);
		this.Scroll.setVisible(true);
		
		this.repaint();	
	}
	
	/**
	 * Metoda pokazuje Dostępne stoły na panelu graficznym w postaci tabeli
	 * @param players
	 * 			Tablica graczy
	 */
	public void showTables(Player[] players){
		//if(players==null)return;
		int i=0; 
		for(i=0;i<50;i++){
			this.TablesTab.setValueAt("",i,0);
			this.TablesTab.setValueAt("",i,1);
		}
		i=0;
		while(i<50 && players[i]!=null){
			this.TablesTab.setValueAt(Integer.toString(i+1),i,0);
			this.TablesTab.setValueAt(players[i].getNick(),i,1);
			i = i+1;
		}
		this.GameState = 3;
		this.Scroll3.setVisible(false);
		this.Scroll2.setVisible(true);
		this.Scroll.setVisible(false);
		this.repaint();	
	}
	
	/**
	 * Metoda pokazuje zapisane gry na panelu graficznym w postaci tabeli
	 * @param games
	 * 			Tablica zapisanych gier
	 */
	public void showSaves(GameSaved[] games){
		String nick;
		int i=0; 
		for(i=0;i<50;i++){
			this.SavesTab.setValueAt("",i,0);
			this.SavesTab.setValueAt("",i,1);
			this.SavesTab.setValueAt("",i,2);
		}
		i=0;
		while(i<50 && games[i]!=null){
			this.SavesTab.setValueAt(Integer.toString(games[i].getId()),i,0);
			String availible = "dostępny";
			if(games[i].isAllowPlay()==0){
				availible = "niedostępny";
			}
			this.SavesTab.setValueAt(availible,i,1);
			nick = games[i].getNick1();
			if(nick.equals(this.Frame.getMyClient().getMyPlayer().getNick())){
				nick = games[i].getNick2();
			}
			this.SavesTab.setValueAt(nick,i,2);
			i = i+1;
		}
		this.GameState = 4;
		this.Scroll2.setVisible(false);
		this.Scroll.setVisible(false);
		this.Scroll3.setVisible(true);
		this.repaint();	
	}
	
	public JTable getSavesTab() {
		return SavesTab;
	}

	public void setSavesTab(JTable savesTab) {
		SavesTab = savesTab;
	}

	/**
	 * Metoda przugotowująca panel graficzny do gry
	 */
	public void prepareTableToGame(){
		this.Scroll.setVisible(false);
		this.Scroll2.setVisible(false);
		this.Scroll3.setVisible(false);
		this.GameState = 2;
		this.repaint();
	}

	/**
	 * Komponent graficzny (płótno, na którym odbywa się rysowanie planszy, rankingów etc.)
	 */
	protected void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		if(GameState == 2){
			g2d.drawImage(this.BackgroundBoard,null,0,0);
			if(this.GameStarted){ // jeżeli gra rozpoczeta
				showActualBoard(g2d);
			}
		}else if(GameState == 1 || GameState==3 || GameState==4){
			g2d.clearRect(0,0,this.getWidth(),this.getHeight());
		}else{
			g2d.drawImage(this.Background,null,0,0);
		}
		this.updateUI();
	}

	
	/**
	 * Metoda rysująca na panelu graficznym aktualny stan pionków
	 */
	private void showActualBoard(Graphics2D g2d){
		BufferedImage[] set;
		BufferedImage img = null;
		Pawn p=null;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(this.MyBoard.getMyBoard()[i][j]!=null){
					p=this.MyBoard.getMyBoard()[i][j];
					if(p.isActive()){
						p.calcCordstoGraph();
						if(p.getColor() == Pawn.BLACK){ set = this.BlackPawnImgSet; }else{ set = this.WhitePawnImgSet;}
						switch(p.getStatus()){
						case Pawn.PAWN:
							img = set[0];
						break;
						case Pawn.ROCK:
							img = set[8];
						break;
						case Pawn.HORSE:
							img = set[9];
						break;
						case Pawn.BISHOP:
							img = set[10];
						break;
						case Pawn.QUEEN:
							img = set[12];
						break;
						case Pawn.KING:
							img = set[11];
						break;
						
					
					}
						g2d.drawImage(img,null,p.getGraphCordX(),p.getGraphCordY());
					}
				}
				p=null;
			}
		}

		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(this.MyBoard.getLogicBoard()[i][j]==GraphPanel.GREEN_DOT){
					g2d.drawImage(this.GreenDotImg,null,this.MyBoard.getGraphCord(i),this.MyBoard.getGraphCord(j));
				}else if(this.MyBoard.getLogicBoard()[i][j]==GraphPanel.RED_DOT){
					g2d.drawImage(this.RedDotImg,null,this.MyBoard.getGraphCord(i),this.MyBoard.getGraphCord(j));
				}
			}
		}
	}
	
	/**
	 * 
	 * Metoda rozpoczęcia nowej gry
	 * @param bottom
	 * 			Flaga określająca który set ma się znaleźć na dole szachownicy
	 */
	public void startGame(int bottom){
		boolean mymove = false;
		if(bottom == Board.WHITE_ON_BOTTOM){
			this.Frame.getMoveLab().setText("<< Twój ruch >>");
			mymove = true;
		}else{
			this.Frame.getMoveLab().setText("<< Ruch przeciwnika >>");
		}
		this.MyBoard = new Board(this,bottom,mymove);
		this.prepareTableToGame();
		this.GameStarted = true;
		this.repaint();
		
	}
	
	/**
	 * Metoda cofająca poprzedni ruch
	 */
	public void backMove(){
		if(this.MyBoard!=null){
			this.MyBoard.backMove();
		}
	}
	
	
	public boolean isGameStarted() {
		return GameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		GameStarted = gameStarted;
	}


	/**
	 * Metoda podmieniająca obrazek szachownicy na odwrócony;
	 */
	public void invertBoard(){
		BufferedImage tmp;
		tmp = this.BackgroundBoard;
		this.BackgroundBoard = this.BackgroundBoardInvert;
		this.BackgroundBoardInvert = tmp;
		if(this.InvertedFlag){
			this.InvertedFlag = false;
		}else{
			this.InvertedFlag = true;
		}
	}
	

	
	/**
	 *  Metoda rysująca orazek kropki(znacznika pola) na panelu graficznym
	 * @param color	
	 * 			Kolor kropki green,red
	 * @param g2d
	 * 			Referencja płótna
	 * @param x
	 * 			współrzędna X
	 * @param y
	 * 			współrzędna Y
	 */
	public void putdot(int dotcolor,Graphics2D g2d,int x, int y){
		if(dotcolor==GraphPanel.GREEN_DOT){
			if(this.GreenDotImg!=null)
			g2d.drawImage(this.GreenDotImg,null, x, y);
		}else if(dotcolor==GraphPanel.RED_DOT){
			if(this.RedDotImg!=null)
			g2d.drawImage(this.RedDotImg,null, x, y);
		}
	}
	
	
	public Board getMyBoard() {
		return MyBoard;
	}

	public void setMyBoard(Board myBoard) {
		MyBoard = myBoard;
	}

	public JTable getTablesTab() {
		return TablesTab;
	}

	public void setTablesTab(JTable tablesTab) {
		TablesTab = tablesTab;
	}

	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(this.MyBoard!=null && this.MyBoard.isMyMove()){
			Pack pck = null;
			int x = arg0.getX();
			int y = arg0.getY();
			if(SelectedPawn==null){
				if(this.MyBoard!=null){
					this.SelectedPawn = this.MyBoard.checkClicked(x,y);
				}
			}else{
				if(this.MyBoard.makeMove(this.SelectedPawn,x,y)){
					pck = new Pack("MAKE_MOVE");
					pck.setPawnId(this.SelectedPawn.getId());
					pck.setX(this.SelectedPawn.getX());
					pck.setY(this.SelectedPawn.getY());
					this.Frame.getBackMoveBtn().setEnabled(true);
					this.Frame.getMyClient().sendPack(pck);
					if(this.SelectedPawn.getStatus()==Pawn.PAWN && this.SelectedPawn.getY()==0){
						this.swapPawn(this.SelectedPawn);
					}
				}
				this.SelectedPawn = null;
			}
			this.repaint();	
		}
	}
	
	/**
	 * Metoda zmieniająca zwykły pionek na dowolny inny po dotarciu nim do końca szachownicy
	 * Wybór na podstawie okna dialogowego wyboru
	 * @param p
	 * 			Referencja na pionek do zamiany
	 */
	private void swapPawn(Pawn p){
		if(p!=null){
			if(p.isActive() && p.getStatus()==Pawn.PAWN){
				if(this.MyBoard!=null){
					String[] options ={"Hetman","Wieża","Skoczek","Goniec"};
					int response = JOptionPane.showOptionDialog(null,"Oznacz figurę na którą chcesz zamienić pionek","Zmiana pionka",JOptionPane.OK_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,0);
					switch(response){
						case 0:
							p.setStatus(Pawn.QUEEN);
						break;
						case 1:
							p.setStatus(Pawn.ROCK);
						break;
						case 2:
							p.setStatus(Pawn.HORSE);
						break;
						case 3:
							p.setStatus(Pawn.BISHOP);
						break;
					}
					Pack pck = new Pack("SWAP_PAWN");
					pck.setPawnId(p.getId());
					pck.setStatus(p.getStatus());
					this.Frame.getMyClient().sendPack(pck);
					
				}
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean isInvertedFlag() {
		return InvertedFlag;
	}

	public void setInvertedFlag(boolean invertedFlag) {
		InvertedFlag = invertedFlag;
	}

	public GameFrame getFrame() {
		return Frame;
	}

	public void setFrame(GameFrame frame) {
		Frame = frame;
	}

	public byte getGameState() {
		return GameState;
	}

	public void setGameState(byte gameState) {
		GameState = gameState;
	}

	/**
	 * Metoda wczytuje zapisaną grę na podstawie otrzymanej planszy z pakietu
	 * @param gameSaved
	 * 				Zapisana plansza
	 */
	public void uploadGame(GameSaved gameSaved,Player[] play) {
		//TODO męczarnia z wczytankiem :(
		boolean t = true;
		boolean f= false;
		this.Frame.setBtnsAct(f,f,f,f,t,t,t,t,f,f);
		this.GameState=2;
		String opnick = play[0].getNick();// nick przeciwnika
		String mynick =play[1].getNick();
		if(this.Frame.getMyClient().getMyPlayer().getNick().equals(play[0].getNick())){
			opnick = play[1].getNick();
			mynick = play[0].getNick();
		}
        this.Kolor = gameSaved.getColor();
        this.Move= gameSaved.isMove();
        Pawn[][] b = gameSaved.getBoard();
        Pawn[][] b2 = new Pawn[8][8];
	    if(!gameSaved.getNick1().equals(mynick)){ // jeżeli gracz zapisał jako czarny
	    	if(this.Kolor==Board.WHITE_ON_BOTTOM){
	    		this.Kolor = Board.BLACK_ON_BOTTOM;
	    	}else{
	    		this.Kolor = Board.WHITE_ON_BOTTOM;
	    	}
	    	if(this.Move){
	    		this.Move = false;
	    	}else{
	    		this.Move = true;
	    	}
	    		
		    	for(int i =0;i<8;i++){
		    		for(int j=0;j<8;j++){
		    			b2[i][j]=null;
		    			if(b[i][j]!=null && b[i][j].isActive()){
		    				b[i][j].setX(Board.calculateOponentCord(b[i][j].getX(),false));
		    				b[i][j].setY(Board.calculateOponentCord(b[i][j].getY(),true));
		    			}
		    		}
		    	}
		    	for(int i =0;i<8;i++){
		    		for(int j=0;j<8;j++){
		    			if(b[i][j]!=null && b[i][j].isActive()){
		    				b2[b[i][j].getX()][b[i][j].getY()]=b[i][j];
		    			}
		    		}
		    	}
		    	
	    }else{
		    	for(int i =0;i<8;i++){
		    		for(int j=0;j<8;j++){
		    				b2[i][j]=b[i][j];
		    		}
		    	}
	    }
	    	
	    	this.MyBoard = new Board(this,this.Kolor,this.Move);
	        this.MyBoard.setMyBoard(b2);
	    	if(this.Kolor == Board.BLACK_ON_BOTTOM && !this.InvertedFlag){
	    		this.invertBoard();
	    	}	    
	    	
			this.Frame.getOponentLab().setText("Przeciwnik : "+opnick);
			if(this.Kolor==Pawn.WHITE){
				this.Frame.getColorLab().setText("Biały");
			}else{
				this.Frame.getColorLab().setText("Czarny");
			}
			if(this.Move){
				this.Frame.getMoveLab().setText("<< Twój ruch >>");
			}else{
				this.Frame.getMoveLab().setText("<< Ruch przeciwnika >>");
			}
		this.prepareTableToGame();
		this.GameStarted = true;
		this.repaint();	
	}

	public boolean isWantedUpload() {
		return WantedUpload;
	}

	public void setWantedUpload(boolean wantedUpload) {
		WantedUpload = wantedUpload;
	}

	public boolean getMove() {
		return Move;
	}

	public void setMove(boolean move) {
		Move = move;
	}

	public int getKolor() {
		return Kolor;
	}

	public void setKolor(int kolor) {
		Kolor = kolor;
	}



}
