package secondTry;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class SLogin extends JFrame{

	JLabel label;
	JTextField text;
	JButton button;
	
	public SLogin()
	{
		label=new JLabel("请输入用户名: ");
		text=new JTextField(20);
		button=new JButton("连接");
		//设置按钮监听
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (e.getSource()==button)
				{
					String name = text.getText();
					if (name==null||name.trim().equals(""))
					{
						JOptionPane.showMessageDialog(null,"请输入用户名");
						return;
					}
					new Chat(name);
					setVisible(false);
				}
					
			}
		});
		
		add(label);
		add(text);
		add(button);
	
		setLayout(new FlowLayout());
		setBounds(200,200,250,200);
		setVisible(true);
		
	}
	
	public static void main(String[] args) {
		new SLogin();
	}

}
