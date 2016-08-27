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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
	private JTable RankTab; // tabela rankingowa graczy;
	private JTable TablesTab; // tabela stołów utworzonych przez graczy
	private byte GameState; // flaga określająca w jakim trybie jest okno 0-pierwsze wejście, 1-ranking, 2-szachownica, 3-dostępne stoły
	private BufferedImage[] WhitePawnImgSet; // zestaw obrazków dla białych pionków
	private BufferedImage[] BlackPawnImgSet; // zestaw obrazków dla czarnych pionków
	private boolean GameStarted; // flaga określająca czy rozpoczęła się gra
	private Board MyBoard; // obiekt szachownicy
	private boolean InvertedFlag; // flaga okeslająca czy tło szachownicy było odwrócone
	private Pawn SelectedPawn; //obiekt zaznaczonego pionka

	
	public GraphPanel(int w, int h, GameFrame frm){
		this.Frame = frm;
		this.setPreferredSize(new Dimension(w,h));
		this.setLayout(new FlowLayout());
		this.setOpaque(false);
		this.InvertedFlag = false;

		
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
				
				this.GameState = 0;
				this.Scroll = new JScrollPane(this.RankTab);
				this.Scroll.setPreferredSize(new Dimension(w-10,h-10));
				this.Scroll.setVisible(false);
				this.Scroll2 = new JScrollPane(this.TablesTab);
				this.Scroll2.setPreferredSize(new Dimension(w-10,h-10));
				this.Scroll2.setVisible(false);
				this.add(this.Scroll);
				this.add(this.Scroll2);
		
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
		this.Scroll.setVisible(true);
		
		this.repaint();	
	}
	
	/**
	 * Metoda pokazuje Dostępne stoły na panelu graficznym w postaci tabeli
	 * @param players
	 */
	public void showTables(Player[] players){
		if(players==null)return;
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
		this.Scroll2.setVisible(true);
		this.Scroll.setVisible(false);
		this.repaint();	
	}
	
	
	/**
	 * Metoda przugotowująca panel graficzny do gry
	 */
	public void prepareTableToGame(){
		this.Scroll.setVisible(false);
		this.Scroll2.setVisible(false);
		this.GameState = 2;
		this.repaint();
	}

	protected void paintComponent(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		if(GameState == 2){
			g2d.drawImage(this.BackgroundBoard,null,0,0);
			if(this.GameStarted){ // jeżeli gra rozpoczeta
				showActualBoard(g2d);
			}
		}else if(GameState == 1 || GameState==3){
			g2d.clearRect(0,0,this.getWidth(),this.getHeight());
			this.updateUI();
		}else{
			g2d.drawImage(this.Background,null,0,0);
		}
	}

	
	/**
	 * Metoda rysująca na panelu graficznym aktualny stan pionków
	 */
	private void showActualBoard(Graphics2D g2d){
		Pawn p=null;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(this.MyBoard.getMyBoard()[i][j]!=null){
					p=this.MyBoard.getMyBoard()[i][j];
					if(p.isActive()){
						p.calcCordstoGraph();
						g2d.drawImage(p.getImg(),null,p.getGraphCordX(),p.getGraphCordY());
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
		this.MyBoard = new Board(this,bottom);
		this.prepareTableToGame();
		this.GameStarted = true;
		this.repaint();
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
		Pack pck = null;
		int x = arg0.getX();
		int y = arg0.getY();
		if(SelectedPawn==null){
			if(this.MyBoard!=null){
				this.SelectedPawn = this.MyBoard.checkClicked(x,y);
			}
		}else{
			if(this.MyBoard.makeMove(this.SelectedPawn,x,y)){
				//TODO this.MyBoard.lockAllPawns();
				pck = new Pack("MAKE_MOVE");
				pck.setPawnId(this.SelectedPawn.getId());
				pck.setX(this.SelectedPawn.getX());
				pck.setY(this.SelectedPawn.getY());
				this.Frame.getMyClient().sendPack(pck);
			}
			this.SelectedPawn = null;
		}
		this.repaint();	
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



}
