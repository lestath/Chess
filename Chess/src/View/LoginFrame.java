package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import CommunicationModel.Client;
import CommunicationModel.Pack;
import Game.Player;

/**
 * 
 * Klasa reprezentująca okno logowania na serwer
 *
 */
public class LoginFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Client[] MyClient; // tablica uruchomionych klientów
	private boolean OperarionSucess; // flaga sprawdzająca, czy poprzednia operacja zakończyła się sukcesem
	private boolean ConnectSucess; // flaga sprawdzająca czy połączenie zakończyło się sukcesem
	private int ClientIterator; // iterator numeru aktualnie działającego klienta w tablicy
	private JTextField Host;
	private JTextField Port;
	private JTextField Login;
	private JPasswordField Password;
	private JButton LoginBtn; 
	private JButton RegisterBtn; 
	private JLabel HostLab;
	private JLabel PortLab;
	private JLabel LoginLab;
	private JLabel PasswordLab;
	private JLabel InfoLab;
	
	
	
	public LoginFrame(){
		super("Chess");
		this.MyClient = new Client[10];
		this.setSize(300,250);
	    this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent event) {
	            exitProcedure();
	        }

	    });
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		this.OperarionSucess = true;
		this.ClientIterator = 0;
		this.ConnectSucess = true;
		
		Dimension dim = new Dimension(190,20);
		Dimension dim2 = new Dimension(90,20);

		this.InfoLab = new JLabel();
		this.InfoLab.setPreferredSize(new Dimension(290,40));
		
		this.HostLab = new JLabel("Host :");
		this.HostLab.setPreferredSize(dim2);
		this.Host = new JTextField("localhost");
		this.Host.setPreferredSize(dim);
		
		this.PortLab = new JLabel("Port :");
		this.PortLab.setPreferredSize(dim2);
		this.Port = new JTextField("4448");
		this.Port.setPreferredSize(dim);
		
		this.LoginLab = new JLabel("Login :");
		this.LoginLab.setPreferredSize(dim2);
		this.Login = new JTextField();
		this.Login.setPreferredSize(dim);
		
		this.PasswordLab = new JLabel("Hasło :");
		this.PasswordLab.setPreferredSize(dim2);
		this.Password = new JPasswordField();
		this.Password.setPreferredSize(dim);
		
		this.LoginBtn = new JButton("Zaloguj");
		this.LoginBtn.setPreferredSize(dim);
		this.LoginBtn.addActionListener(this);
		this.LoginBtn.setFocusPainted(false);
		
		
		this.RegisterBtn = new JButton("Rejestracja");
		this.RegisterBtn.setPreferredSize(dim);
		this.RegisterBtn.addActionListener(this);
		this.RegisterBtn.setFocusPainted(false);
		
		this.add(this.InfoLab);
		this.add(this.HostLab);
		this.add(Host);
		this.add(PortLab);
		this.add(Port);
		this.add(LoginLab);
		this.add(Login);
		this.add(PasswordLab);
		this.add(Password);
		this.add(LoginBtn);
		this.add(RegisterBtn);
		this.setVisible(true);
	}

	protected void exitProcedure() {
		for(int i=0;i<10;i++){
			if(this.MyClient[i]!=null){
				if(this.MyClient[i].getPlayerPanel()!=null){
					this.MyClient[i].getPlayerPanel().exitProcedure2();
					this.MyClient[i]=null;
				}
			}
		}
		this.dispose();
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
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object obj = arg0.getSource();
		if(this.Host.getText().isEmpty()){
			this.setMsg("Podaj Host",Color.red);
		}else if(this.Port.getText().isEmpty()){
			this.setMsg("Podaj Port",Color.red);
		}else if(this.Login.getText().isEmpty()){
			this.setMsg("Podaj Login",Color.red);
		}else if(this.Login.getText().length()>13){
			this.setMsg("Login max 13 znaków",Color.red);
		}else if(this.Password.getPassword().length==0){
			this.setMsg("Podaj hasło",Color.red);
		}else if(this.Password.getPassword().length>13){
			this.setMsg("Hasło max 13 znaków",Color.red);
		}else{
				int port = 4448;
				String pass = String.valueOf(this.Password.getPassword());
				System.out.println("Hasło :"+pass);
				Player play = new Player(this.Login.getText(),pass);
			try{
				 port = Integer.parseInt(this.Port.getText());
				 int i = 0;
				 if(this.OperarionSucess){
				 		while(this.MyClient[i]!=null){ // szuka wolnego miejsca dla klienta
				 			i=i+1;
				 			this.ClientIterator = i;
				 			if(i==10) break;
				 		}
				 		if(i==10){
				 			this.OperarionSucess = true;
				 			this.setMsg("Brak miejsca na nowego klienta",Color.RED);
				 			return;
				 		}else{
							this.MyClient[this.ClientIterator]=new Client(play,this,this.Host.getText(),port,this.ClientIterator);
							new Thread(this.MyClient[this.ClientIterator]).start();
							try {
								Thread.sleep(2000); // usypiamy wątek na 2s aby umożliwić wywołanamu przed chwilą wątkowi poprawne wykonanie
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				 		}
				 }
				if(!this.ConnectSucess){
					this.MyClient[this.ClientIterator] = null;
					return;
				}
			}catch(Exception pe){
				 pe.printStackTrace();
				 this.setMsg("Błąd Portu",Color.red); 
			}
			Pack p = new Pack("HELLO");
			p.setPlayer(play);
			if(obj == this.LoginBtn){
					p.setMessage("LOG_ME");
					this.MyClient[this.ClientIterator].sendPack(p);
			}else if(obj == this.RegisterBtn){
					p.setMessage("REG_ME"); //polecenie zarejestrowania 
					this.MyClient[this.ClientIterator].sendPack(p);
			}
			
		}
	}

	public boolean isOperarionSucess() {
		return OperarionSucess;
	}

	public void setOperarionSucess(boolean operarionSucess) {
		OperarionSucess = operarionSucess;
	}

	public boolean isConnectSucess() {
		return ConnectSucess;
	}

	public void setConnectSucess(boolean connectSucess) {
		ConnectSucess = connectSucess;
	}

	public Client[] getMyClient() {
		return MyClient;
	}

	public void setMyClient(Client[] myClient) {
		MyClient = myClient;
	}

}
