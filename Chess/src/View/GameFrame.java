package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import CommunicationModel.Client;
import CommunicationModel.GameSaved;
import CommunicationModel.Pack;
import Game.Player;


/**
 * 
 * Klasa reprezentująca okno gracza, które widzi po poprawnym zalogowaniu
 *
 */
public class GameFrame extends JFrame implements Runnable, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Client myClient;
	private JButton NewBoardBTn;
	private JButton JoinGameBtn;
	private JButton RankBtn;
	private JButton ExitBtn;
	private JButton StartGameBtn; // przycisk rozpoczecia gry po dokonaniu wyboru stołu
	private JButton ExitTableBtn; // przycisk opuszczenia stołu
	private JButton DrawProposeBtn; // przycisk propozycji remisu
	private JButton SavedGamesBtn; // przycisk listy zapisanych gier
	private JButton SaveGameBtn; // przycisk zapisania gry
	private JLabel PlayerLab; // etykieta informacji o graczu
	private JLabel ColorLab;
	private JLabel OponentLab;
	private JLabel InfoLab; // etykieta powiadomień
	private GraphPanel SidePanel; // panel boczny (wyświetlacz)
	public GameFrame(Client cli){
		super("Chess Game");

		this.myClient = cli;
		// tło
		Dimension windowdim = new Dimension(850,680);
		BufferedImage img =null;
		try {
		    img = ImageIO.read(getClass().getResource("/img/background.jpg"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		Image dimg = img.getScaledInstance(windowdim.width,windowdim.height, Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(dimg);
		JLabel backg = new JLabel(imageIcon);
		backg.setPreferredSize(windowdim);
 		setContentPane(backg);
		
		//wstępne ustawienia okna
	    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) {
	            exitProcedure();
	        }

	    });
		this.setSize(windowdim);
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		

		
		//Etykieta powiadomień
		 this.InfoLab = new JLabel();
		 this.InfoLab.setPreferredSize(new Dimension(590,24));
		 this.setForeground(Color.WHITE);
		 
		
		// Panel Boczny
		 this.SidePanel= new GraphPanel(600,600,this);
		 
		 
		
		// inicjalizacja panelu przycisków, oraz samych przycisków
			Dimension btnpaneldim = new Dimension(200,600);   
			Dimension btndim = new Dimension(190,24);
			JPanel btnpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			btnpanel.setPreferredSize(btnpaneldim);
			btnpanel.setOpaque(false);
			 this.PlayerLab = new JLabel();
			 this.PlayerLab.setPreferredSize(btndim);
			 this.PlayerLab.setForeground(Color.WHITE);
			 
			 this.ColorLab =  new JLabel();
			 this.ColorLab.setPreferredSize(btndim);
			 this.ColorLab.setForeground(Color.WHITE);
			 
			 this.OponentLab =  new JLabel();
			 this.OponentLab.setPreferredSize(btndim);
			 this.OponentLab.setForeground(Color.WHITE);
			 
			 this.NewBoardBTn = new JButton("Utwórz stół");
			 this.NewBoardBTn.setPreferredSize(btndim);
			 this.NewBoardBTn.addActionListener(this);
			 this.NewBoardBTn.setFocusPainted(false);
			 this.NewBoardBTn.setBackground(Color.black);
			 this.NewBoardBTn.setForeground(Color.WHITE);

			 this.JoinGameBtn = new JButton("Dołącz do gry");
			 this.JoinGameBtn.setPreferredSize(btndim);
			 this.JoinGameBtn.addActionListener(this);
			 this.JoinGameBtn.setFocusPainted(false);
			 this.JoinGameBtn.setBackground(Color.black);
			 this.JoinGameBtn.setForeground(Color.WHITE);
			 
			 this.SavedGamesBtn = new JButton("Wczytaj grę");
			 this.SavedGamesBtn.setPreferredSize(btndim);
			 this.SavedGamesBtn.addActionListener(this);
			 this.SavedGamesBtn.setFocusPainted(false);
			 this.SavedGamesBtn.setBackground(Color.black);
			 this.SavedGamesBtn.setForeground(Color.WHITE);
			 
			 this.RankBtn = new JButton("Ranking");
			 this.RankBtn.setPreferredSize(btndim);
			 this.RankBtn.addActionListener(this);
			 this.RankBtn.setFocusPainted(false);
			 this.RankBtn.setBackground(Color.black);
			 this.RankBtn.setForeground(Color.WHITE);
			 
			 this.DrawProposeBtn = new JButton("Zaproponuj remis");
			 this.DrawProposeBtn.setPreferredSize(btndim);
			 this.DrawProposeBtn.addActionListener(this);
			 this.DrawProposeBtn.setFocusPainted(false);
			 this.DrawProposeBtn.setBackground(Color.black);
			 this.DrawProposeBtn.setForeground(Color.WHITE);
			 this.DrawProposeBtn.setEnabled(false);
			 
			 this.SaveGameBtn = new JButton("Zapisz grę");
			 this.SaveGameBtn.setPreferredSize(btndim);
			 this.SaveGameBtn.addActionListener(this);
			 this.SaveGameBtn.setFocusPainted(false);
			 this.SaveGameBtn.setBackground(Color.black);
			 this.SaveGameBtn.setForeground(Color.WHITE);
			 this.SaveGameBtn.setEnabled(false);
			 
			 this.ExitTableBtn = new JButton("Opuść stół");
			 this.ExitTableBtn.setPreferredSize(btndim);
			 this.ExitTableBtn.addActionListener(this);
			 this.ExitTableBtn.setFocusPainted(false);
			 this.ExitTableBtn.setBackground(Color.black);
			 this.ExitTableBtn.setForeground(Color.WHITE);
			 this.ExitTableBtn.setEnabled(false);
			 
			 
			 this.ExitBtn = new JButton("Wyjdź");
			 this.ExitBtn.setPreferredSize(btndim);
			 this.ExitBtn.addActionListener(this);
			 this.ExitBtn.setFocusPainted(false);
			 this.ExitBtn.setBackground(Color.black);
			 this.ExitBtn.setForeground(Color.WHITE);
			 
			 this.StartGameBtn = new JButton("Rozpocznij grę");
			 this.StartGameBtn.setPreferredSize(btndim);
			 this.StartGameBtn.addActionListener(this);
			 this.StartGameBtn.setFocusPainted(false);
			 this.StartGameBtn.setBackground(Color.black);
			 this.StartGameBtn.setForeground(Color.WHITE);
			 this.StartGameBtn.setEnabled(false);
			 this.StartGameBtn.setVisible(true);
			 
			 JLabel separatelabel = new JLabel();
			 separatelabel.setPreferredSize(new Dimension(btndim.width,30));
			 JLabel separatelabel2 = new JLabel();
			 separatelabel2.setPreferredSize(new Dimension(btndim.width,200));
			 
			 btnpanel.add(this.PlayerLab);
			 btnpanel.add(this.ColorLab);
			 btnpanel.add(this.OponentLab);
			 btnpanel.add(separatelabel);
			 btnpanel.add(this.NewBoardBTn);
			 btnpanel.add(this.JoinGameBtn);
			 btnpanel.add(this.SavedGamesBtn);
			 btnpanel.add(this.RankBtn);
			 btnpanel.add(this.StartGameBtn);
			 btnpanel.add(this.DrawProposeBtn);
			 btnpanel.add(this.SaveGameBtn);
			 btnpanel.add(this.ExitTableBtn);
			 btnpanel.add(separatelabel2);
			 btnpanel.add(this.ExitBtn);
			 btnpanel.setVisible(true);
			 
		 // dodanie elementów do okna głównego
			 
			 this.add(this.InfoLab);
			 this.add(this.SidePanel);
			 this.add(btnpanel);
			 this.setVisible(true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		boolean t= true;
		boolean f= false;
		Object obj = arg0.getSource();
		Pack pck; // pakiet do wysyłania
		if(obj == this.RankBtn){
			this.setBtnsAct(t,t,t,f,f,f,t,f);
			pck = new Pack("GIVE_RANK");
			this.myClient.sendPack(pck);
			System.out.println("Wsyłam");
		}else if(obj == this.NewBoardBTn){
			pck = new Pack("NEW_TABLE");
			this.setBtnsAct(f,f,f,f,f,t,t,f);
			this.myClient.sendPack(pck);
		}else if(obj == this.JoinGameBtn){
			pck = new Pack("GIVE_TAB_LIST");
			this.myClient.sendPack(pck);
			this.setBtnsAct(t,t,t,t,f,f,t,f);
		}else if(obj == this.StartGameBtn){
			if(this.SidePanel!=null){
				if(this.SidePanel.getGameState()==3){
					this.setBtnsAct(f,f,f,f,t,t,t,t);
					int row = this.SidePanel.getTablesTab().getSelectedRow();
					if(row == -1){row = 0;}
					Player p = new Player(this.SidePanel.getTablesTab().getModel().getValueAt(row,1).toString(),"");
					pck = new Pack("SELECT_OPONENT");
					pck.setPlayer(p);
					this.myClient.sendPack(pck);
				}else if(this.SidePanel.getGameState()==4){
					//TODO oprogramowanie prośby o wczytanie gry
				}
			}
		}else if(obj == this.ExitBtn){
			this.exitProcedure();
		}else if(obj == this.ExitTableBtn){
			pck = new Pack("CLOSE_TABLE");
			this.setBtnsAct(t,t,t,f,f,f,t,f);
			this.myClient.sendPack(pck);
		}else if(obj==this.DrawProposeBtn){
			this.setBtnsAct(f,f,f,f,f,t,t,f);
			setMsg("Zaproponowałeś remis",Color.WHITE);
			pck = new Pack("MAKE_MOVE");
			pck.setCheck(Pack.DRAW_PROPOSE);
			this.myClient.sendPack(pck);
		}else if(obj == this.SavedGamesBtn){
			this.setBtnsAct(t,t,t,t,f,f,t,f);
			pck = new Pack("GIVE_SAVED_GAMES");
			this.myClient.sendPack(pck);
		}else if(obj == this.SaveGameBtn){
			if(this.SidePanel.isGameStarted()){
				pck = new Pack("SAVE_GAME");
				pck.getSaves()[0]= new GameSaved(99,this.myClient.getMyPlayer().getNick(),this.OponentLab.getText().substring(13),this.SidePanel.getMyBoard().getMyColor(),this.SidePanel.getMyBoard().isMyMove(),this.SidePanel.getMyBoard().getMyBoard(),0);
				this.myClient.sendPack(pck);
			}
		}
	}

	/**
	 * Metoda ustawia aktywność poszczególnych przycisków
	 * @param b1
	 * 			Flaga przycisku "Utwórz stół"
	 * @param b2
	 * 			Flaga przycisku "Dołącz do gry"
	 * @param b3
	 * 			Flaga przycisku "Ranking"
	 * @param b4
	 * 			Flaga przycisku "Rozpocznij grę"
	 * @param b5
	 * 			Flaga przycisku "Zaproponuj remis"
	 * @param b6
	 * 			Flaga przycisku "Opusc stół"
	 * @param b7
	 * 			Flaga przycisku "Wyjdź"
	 */
	public void setBtnsAct(boolean b1, boolean b2 , boolean b3, boolean b4, boolean b5, boolean b6, boolean b7,boolean b8){ // b8 zapisz grę
		this.NewBoardBTn.setEnabled(b1);
		this.JoinGameBtn.setEnabled(b2);
		this.RankBtn.setEnabled(b3);
		this.StartGameBtn.setEnabled(b4);
		this.DrawProposeBtn.setEnabled(b5);
		this.ExitTableBtn.setEnabled(b6);
		this.ExitBtn.setEnabled(b7);
		this.SaveGameBtn.setEnabled(b8);
		
	}
	
	/**
	 * Metoda wyświetlająca okno propozycji remisu
	 */
	public void drawWindow(){
		boolean t = true;
		boolean f = false;
		Pack p;
		this.setBtnsAct(f,f,f,f,f,t,t,f);
		int selectedOption = JOptionPane.showConfirmDialog(null, 
                "Przeciwnik zaproponował remis. Zgadzasz się?", 
                "Choose", 
                JOptionPane.YES_NO_OPTION); 
			if (selectedOption == JOptionPane.YES_OPTION) {
				setMsg("zaakceptowałeś remis koniec gry",Color.WHITE);
				p = new Pack("MAKE_MOVE");
				p.setCheck(Pack.DRAW_YES);
				this.myClient.sendPack(p);
				this.setBtnsAct(t,t,t,f,f,f,t,f);
				if(this.SidePanel!=null){
					if(SidePanel.getMyBoard()!=null){
						this.SidePanel.getMyBoard().lockAllPawns();
					}
				}
			}else if(selectedOption == JOptionPane.NO_OPTION){
				setMsg("Odrzuciłeś propozycję remisu remis",Color.WHITE);
				p = new Pack("MAKE_MOVE");
				p.setCheck(Pack.DRAW_NO);
				this.myClient.sendPack(p);
			}
	}
	
	
	public JButton getDrawProposeBtn() {
		return DrawProposeBtn;
	}

	public void setDrawProposeBtn(JButton drawProposeBtn) {
		DrawProposeBtn = drawProposeBtn;
	}

	@Override
	public void run() {
			
	}

	public JLabel getPlayerLab() {
		return PlayerLab;
	}

	public void setPlayerLab(JLabel playerLab) {
		PlayerLab = playerLab;
	}

	public GraphPanel getSidePanel() {
		return SidePanel;
	}

	public void setSidePanel(GraphPanel sidePanel) {
		SidePanel = sidePanel;
	}

	public Client getMyClient() {
		return myClient;
	}

	public void setMyClient(Client myClient) {
		this.myClient = myClient;
	}

	/**
	 *  metoda przekazuje wiadomości do okna gry
	 * @param msg
	 * 			Treść wiadomości
	 * @param col
	 * 			Kolor wyświetlanego tekstu
	 */
	public void setMsg(String msg,Color col){
		this.InfoLab.setForeground(col);
		this.InfoLab.setText(msg);
		new Thread(new ResetLab(this.InfoLab,3000)).start();
	}
	/**
	 * Metoda wykonywana przy wyjściu okna gracza
	 */
	public void exitProcedure(){
		Pack p = new Pack("EXIT_ME");
		this.myClient.sendPack(p);
		this.myClient.getFrame().getMyClient()[this.myClient.getId()]=null;
		this.myClient.setRunning(false);
		this.dispose();
	}
	/**
	 * Metoda opuszczenia gry wywoływana jest z okna nadrzędnego (okno logowania)
	 */
	public void exitProcedure2(){
		Pack p = new Pack("EXIT_ME");
		this.myClient.setRunning(false);
		this.myClient.sendPack(p);
		this.dispose();
	}
	
	public JLabel getColorLab() {
		return ColorLab;
	}

	public void setColorLab(JLabel colorLab) {
		ColorLab = colorLab;
	}

	public JLabel getOponentLab() {
		return OponentLab;
	}

	public void setOponentLab(JLabel oponentLab) {
		OponentLab = oponentLab;
	}
	
}
