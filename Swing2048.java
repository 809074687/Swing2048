package main;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.ChangedCharSetException;

import main.Operator;

/**
 * <p>Swing2048的界面逻辑</p>
 * @author Dell
 * @成员变量 screenWidth、screenHeight 屏幕的尺寸，方便置中使用
 * 	 	   <br>length 数字列表的宽度
 * 	 	   <br>scores 成绩，给op类传值、设置面板成绩使用
 * 	 	   <br>jpNum、jlScores、fieldName、fieldSaveName、
 * 			dialogName、dialogSaveName 需要全局使用的容器。
 */
public class Swing2048 extends JFrame {
	
	private int length;
    private int scores;
    private JPanel jpNum;
    private int screenWidth;
    private int screenHeight;
    private JLabel jlScores;
    private Operator operator;
    private String name;
    private JTextField fieldName;
    private JTextField fieldSaveName;
    private Dialog dialogName;
    private JDialog dialogSaveName;
    private IO io;
    private String saveName;
    private JDialog messageload;
    
    /**
     * <p>读取屏幕的长宽，从而设置居中</p>
     */
    public Swing2048(){
    	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();//获取屏幕尺寸
    	screenWidth = dim.width;
    	screenHeight = dim.height;
    }
    
    /**
     * <p>主面板的设置</p>
     */
    public void init() throws InterruptedException{

        length = 4;
        operator = new Operator();
        io = new IO();
        
    //数字列表面板
        jpNum = new JPanel();
        jpNum.setLayout(new GridLayout(length, length, 5, 5));

        //默认开启4*4模式的初始化
        for (int i = 0; i < length*length; i++) {
        	operator.getNumList().add(0);
            ImageIcon imageIcon = new ImageIcon("src/test/image/"+0+".jpg");
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(70,70,Image.SCALE_DEFAULT));
            JLabel jl = new JLabel(imageIcon);
            jpNum.add(jl);
            jpNum.updateUI();
        }

    //功能面板
        JPanel jpFuctions = new JPanel();

        jlScores = new JLabel("scores:"+0);
        String[] sizes = {"4*4","5*5","6*6","7*7","8*8","9*9"};//可选项
        JComboBox<String> jcbChoice = new JComboBox<>(sizes);

        jpFuctions.setLayout(new BoxLayout(jpFuctions, BoxLayout.X_AXIS));

        jpFuctions.add(jlScores);
        jpFuctions.add(Box.createGlue()); 
        jpFuctions.add(jcbChoice);

        /**
         * <p>对WASD移动的键盘监听器</p>
         * @author Dell
         *
         */
        class MoveKeyListener implements KeyListener{
            public void keyPressed(KeyEvent e) {
            	
                System.out.println("press");
                int a = e.getKeyCode();
                operator.operation(a, scores, operator.getNumList());
                scores = operator.getScores();
                jlScores.setText("scores:" + scores);
                change();//移动后换图
                die();//判断是否死亡
            }
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
        }

    //添加按键移动监听器
        this.addKeyListener(new MoveKeyListener());
        jcbChoice.addKeyListener(new MoveKeyListener());
 
    //为选择大小添加监听器，改变大小，同时重置
        jcbChoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                @SuppressWarnings("unchecked")
                JComboBox<String> cb = (JComboBox<String>)e.getSource();
                String newSelection = (String)cb.getSelectedItem();
                length = Integer.parseInt
                            (String.valueOf
                            (newSelection.charAt(0)));
                resetNum();//重置num面板
                pack();
                //成绩清空
                scores = 0;
                jlScores.setText("scores:" + scores);
            }
        });
        
    //输入用户名窗口
        dialogName = new Dialog(this, true);
        dialogName.setTitle("请输入");
        fieldName = new JTextField(10);
        JButton buttonName = new JButton("确定玩家用户名");
        buttonName.addActionListener(nameOk);
        dialogName.add(fieldName,BorderLayout.NORTH);
        dialogName.add(buttonName,BorderLayout.SOUTH);
        setCenter(dialogName);
        
      //初始化
        this.setMenu();
        this.setTitle("2048——"+this.name);
        this.add(jpFuctions, BorderLayout.NORTH);
        this.add(jpNum, BorderLayout.SOUTH);
        this.setFocusable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setCenter(this);//居中
    }

    /**
     * <p>检测数字列表是否无法再移动，即游戏结束，进行弹框提醒</p>
     */
    public void die(){
    	boolean full = true;
    	for (int num : operator.getNumList()) {
            if(num==0) full = false;
        }
    	
        if (full) {
			boolean die = true;
			
			Operator tempOpe = new Operator();
			ArrayList<Integer> tempList = new ArrayList<>();
			int[] testKeyEvent = {KeyEvent.VK_S, KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_D};
			//再建立一个ArrayList，避开传值传递
			for (int i = 0; i < length*length; i++) {
				tempList.add(operator.getNumList().get(i));
			}
			for (int key : testKeyEvent) {
				tempOpe.operation(key, tempOpe.IMTEMP, tempList);
				if (!operator.getNumList().equals(tempOpe.getNumList())) {
					die = false;
				}
			}
			
			if (die == true) {
				JDialog message = new JDialog(this, true);
				message.add(new JLabel("可…可恶还是输了，你的成绩是: " + this.scores));
				setCenter(message);
				io.saveScores(name+" "+scores);//保存成绩
				
				resetNum();//重置num面板
				jpNum.updateUI();
				this.scores = 0;
				jlScores.setText("scores:" + this.scores);
			} 
		}
    }

    /**
     * <br>重置num面板
     */ 
    public void resetNum(){
        jpNum.removeAll();
        operator.setNumList(new ArrayList<Integer>());;
        operator.getNumList().clear();
        for (int i = 0; i < length*length; i++) {
        	operator.getNumList().add(0);
            ImageIcon imageIcon = new ImageIcon("src/test/image/"+0+".jpg");
            //对图像进行70*70格式化
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(70,70,Image.SCALE_DEFAULT));
            JLabel jl = new JLabel(imageIcon);
            jpNum.add(jl);
            jpNum.updateUI();
            jpNum.setLayout(new GridLayout(length, length, 5, 5));
        }
    }
    
    /**
     * 设置菜单
     */
    public void setMenu(){
    	MenuBar mb = new MenuBar();
    	Menu function = new Menu("功能");
    	MenuItem his = new MenuItem("排行榜");
    	MenuItem help = new MenuItem("游戏玩法");
    	MenuItem save = new MenuItem("保存游戏进度");
    	MenuItem load = new MenuItem("读取游戏进度");
    	MenuItem author = new MenuItem("制作者信息");
    	function.add(his);
    	function.add(help);
    	function.add(save);
    	function.add(load);
    	function.add(author);
    	mb.add(function);
    	this.setMenuBar(mb);
    	function.addActionListener(menuListener);
    }
    
    /**
     * 操作菜单的监听器
     */
    ActionListener menuListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	String strClick = e.getActionCommand();
        	System.out.println(strClick);
        	
        //点击游戏玩法的处理逻辑
        	if (strClick.equals("游戏玩法")) {
        		JDialog message = new JDialog(Swing2048.this, true);
        		message.setTitle("游戏玩法");
                message.add(new JLabel("按“WASD”进行移动，相同的数字会叠加，按“B”可悔步一次"));
                setCenter(message);
			}
        	
        //点击制作者信息的处理逻辑
        	if (strClick.equals("制作者信息")) {
        		JDialog message = new JDialog(Swing2048.this, true);
        		message.setTitle("制作者信息");
                message.add(new JLabel("Hey！ Powered By Xgl"));
                setCenter(message);
			}
        	
        //点击排行榜的逻辑
        	if (strClick.equals("排行榜")) {
        		String phbStr = io.showScores();
        		JDialog message = new JDialog(Swing2048.this, true);
        		message.setTitle("排行榜");
        		JTextArea paihangbang = new JTextArea(phbStr);
        		paihangbang.setEditable(false);
                message.add(paihangbang);
                setCenter(message);
			}
        	
        //保存进度
        	if (strClick.equals("保存游戏进度")) {
        		dialogSaveName = new JDialog(Swing2048.this, true);
        		dialogSaveName.setTitle("输入存档名");
                fieldSaveName = new JTextField(10);
                JButton buttonSaveName = new JButton("确定存档名");
                buttonSaveName.addActionListener(nameOk);
                dialogSaveName.add(fieldSaveName,BorderLayout.NORTH);
                dialogSaveName.add(buttonSaveName,BorderLayout.SOUTH);
                setCenter(dialogSaveName);
                
        	}
        	
        //读取进度
        	if (strClick.equals("读取游戏进度")) {
        		messageload = new JDialog(Swing2048.this, true);
        		messageload.setTitle("所有存档");
                
        		String[] SaveNames = io.getSaveNames();
        		messageload.setLayout(new GridLayout(SaveNames.length,1));
        		for (String string : SaveNames) {
					JButton jButton = new JButton(string);
					jButton.addActionListener(loadListener);
					messageload.add(jButton);
				}
        		setCenter(messageload);
        		
        	}
        }
    };
    
    /**
     * 确定键的监听器（将用户名和存档的合起来写了）
     */
    ActionListener nameOk = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("确定玩家用户名")) {
				name = fieldName.getText();
				if (name.equals("")) {
					name = "无名氏";
				}
				dialogName.dispose();
			}
			if (e.getActionCommand().equals("确定存档名")) {
				saveName = fieldSaveName.getText();
				dialogSaveName.dispose();
				if (!io.saveGame(saveName, name, scores, operator.getNumList())) {
					JDialog message = new JDialog(Swing2048.this, true);
	        		message.setTitle("存档失败");
	                message.add(new JLabel("已经有了同名存档"));
	                setCenter(message);
				}
			}
		}
	};
	
	/**
	 * 读档界面选取的按钮监听器
	 */
	ActionListener loadListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ArrayList loadList = new ArrayList();
			loadList = io.load(e.getActionCommand());
			Swing2048.this.name = (String) loadList.get(0); 
			operator.setScores((Integer)loadList.get(1));
			operator.setNumList((ArrayList<Integer>) loadList.get(2));
			Swing2048.this.setTitle("2048——"+Swing2048.this.name);
			change();
			messageload.dispose();
		}
	};
    
	/**
	 * 将容器放在屏幕中央并设置尺寸
	 * @param c 需要居中、pack尺寸中的容器c
	 */
	public void setCenter(Component c){
		((Window) c).pack();
		c.setLocation((screenWidth-c.getWidth())/2,(screenHeight-c.getHeight())/2);
		c.setVisible(true);
	}
	
	/**
	 * 主方法
	 */
    public static void main(String[] args) throws InterruptedException {
        new Swing2048().init();
    }
    
    /**
     * 为移动后的数字面板更换图片
     */
    public void change(){
    	//移动后换图
        for (int i = 0; i < length*length; i++) {
            JLabel jlTemp = (JLabel) jpNum.getComponent(i);
            ImageIcon imageIcon = new ImageIcon("src/test/image/"
            									+operator.getNumList().get(i)
            									+".jpg");
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(70,70,Image.SCALE_DEFAULT));
            jlTemp.setIcon(imageIcon);
        }
    }

}
