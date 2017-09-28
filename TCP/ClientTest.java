import javax.swing.JFrame;

public class ClientTest {
	public static void main(String[] args){
		Client cl1=new Client("127.0.0.1","AB",9789);
		cl1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cl1.startRunning();
	}
}
