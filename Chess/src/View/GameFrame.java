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
import javax.swing.JPanel;

import CommunicationModel.Client;
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
	private JLabel PlayerLab; // etykieta informacji o graczu
	private JLabel InfoLab; // etykieta powiadomień
	private GraphPanel SidePanel; // panel boczny (wyświetlacz)
	public GameFrame(Client cli){
		super("Chess Game");
		this.myClient = cli;
		Dimension windowdim = new Dimension(820,690);
		
		// tło
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
			 
			 this.RankBtn = new JButton("Ranking");
			 this.RankBtn.setPreferredSize(btndim);
			 this.RankBtn.addActionListener(this);
			 this.RankBtn.setFocusPainted(false);
			 this.RankBtn.setBackground(Color.black);
			 this.RankBtn.setForeground(Color.WHITE);
			 
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
			 this.StartGameBtn.setVisible(true);
			 
			 JLabel separatelabel = new JLabel();
			 separatelabel.setPreferredSize(new Dimension(btndim.width,100));
			 JLabel separatelabel2 = new JLabel();
			 separatelabel2.setPreferredSize(new Dimension(btndim.width,310));
			 
			 btnpanel.add(this.PlayerLab);
			 btnpanel.add(separatelabel);
			 btnpanel.add(this.NewBoardBTn);
			 btnpanel.add(this.JoinGameBtn);
			 btnpanel.add(this.RankBtn);
			 btnpanel.add(this.StartGameBtn);
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
		Object obj = arg0.getSource();
		Pack pck; // pakiet do wysyłania
		if(obj == this.RankBtn){
			pck = new Pack("GIVE_RANK");
			this.myClient.sendPack(pck);
			System.out.println("Wsyłam");
		}else if(obj == this.NewBoardBTn){
			pck = new Pack("NEW_TABLE");
			this.myClient.sendPack(pck);
		}else if(obj == this.JoinGameBtn){
			pck = new Pack("GIVE_TAB_LIST");
			this.myClient.sendPack(pck);
		}else if(obj == this.StartGameBtn){
			int row = this.SidePanel.getTablesTab().getSelectedRow();
			Player p = new Player(this.SidePanel.getTablesTab().getModel().getValueAt(row,1).toString(),"");
			System.out.println("WYBRANY PRZECIWNIK "+p.getNick());
			pck = new Pack("SELECT_OPONENT");
			pck.setPlayer(p);
			this.myClient.sendPack(pck);
		}else if(obj == this.ExitBtn){
			this.exitProcedure();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
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
	
	
}
