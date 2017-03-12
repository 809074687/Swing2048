package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import test.test;

/**
 * <br>类：负责控制2048的IO操作功能
 * @author Dell
 *
 */
public class IO {

	ArrayList<String> allScores;
	
	/**
	 *<br>构造器说明：初始化成绩榜
	 */
	public IO() {
		
        BufferedReader reader = null;  
        allScores = new ArrayList<String>();
        try {  
            reader = new BufferedReader(new FileReader("scores"));  
            String tempString = null;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
            	allScores.add(tempString);
            }  
            reader.close();  
        }catch (IOException e) {  
            e.printStackTrace();  
        }finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {}  
            }  
        } 
	}
	
	/**
	 *<br>方法说明：获取文件扩展名
	 **<br>返回类型：文件的扩展名
	 *@param f 文件名
	 */
	private static String getExtension(String f) {
		String ext = "";
        String s = f;
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
	}
	
	/**
	 *<br>方法说明：保存成绩
	 *<br>操作内容：在根目录的成绩文本中进行修改操作
	 *@param newScoresStr 格式为 name+“ ”+score 的成绩文本
	 *
	 */
	public void saveScores(String newScoresStr){
		int newScore = Integer.valueOf(newScoresStr.split(" ")[1]);
        //插入成绩
		boolean addOrNot = false;
		
        for (int i = 0; i < allScores.size(); i++) {
        	String tempScores = allScores.get(i);
        	int outScores = Integer.valueOf(tempScores.split(" ")[1]);
        	if (newScore >= outScores) {
				allScores.add(i, newScoresStr);
				addOrNot = true;
				break;
			}
		}
        if (addOrNot == false) {
        	allScores.add(newScoresStr);
		}
        
        //重新写入文件中
        BufferedWriter writer = null;
        try {
			writer = new BufferedWriter(new FileWriter("scores"));
			for (int i = 0; i < allScores.size(); i++) {
				String temp = allScores.get(i)+"\n";
				writer.write(temp);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *<br>方法说明：提供排行榜的文本
	 *@return 排行榜的文本
	 */
	public String showScores(){
		String reString = "";
		for (String string : allScores) {
			reString = reString + (string + "\n");
		}
		return reString;
	}
	
	/**
	 *<br>方法说明：游戏存档
	 *<br>返回类型：文件的扩展名
	 *@param saveName 存档名字
	 *@param name 玩家的名字
	 *@param scores 成绩
	 *@param numList 数字列表
	 *
	 */
	public boolean saveGame(String saveName, String name, int scores, ArrayList<Integer> numList) {
		BufferedWriter writer = null; 
		try {
			File file = new File(saveName+".save");
			if (file.exists()) {
				return false;
			}
		} catch (Exception e) {}
		
		try {  
			writer = new BufferedWriter(new FileWriter(saveName+".save")); 
			writer.write(saveName);
			writer.write(" "+name);
			writer.write(" "+scores);
			for (Integer integer : numList) {
				writer.write(" "+integer);
			}
			System.err.println("存档成功");
        }catch (IOException e) {  
            e.printStackTrace();  
        }finally {  
            if (writer != null) {  
                try {  
                	writer.close();  
                } catch (IOException e1) {}  
            }  
        } 
		return true;
	}
	
	/**
	 *<br>方法说明：给读档界面的按钮提供文本
	 *@return 所有存档的文本
	 */
	public String[] getSaveNames(){
		File file = new File(".");
		ArrayList<String> saveNamesList = new ArrayList<String>();
		String[] filesArray = file.list();
		for (String fStr : filesArray) {
			if (getExtension(fStr).equals("save")) {
				saveNamesList.add(fStr);
			}
		}
		return (saveNamesList.toArray(new String[saveNamesList.size()]));
	}
	
	/**
	 * <br> 方法说明：载入游戏的存档
	 * @param saveName 存档的名字
	 * @return 一个ArrayList，第一位保存用户名，第二位保存成绩，第三位为储存numList的ArrayList
	 */
	public ArrayList load(String saveName){
		ArrayList returnList = new ArrayList();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(saveName));
			String[] save = reader.readLine().split(" ");
			String name = save[1];
			int scores = Integer.valueOf(save[2]);
			ArrayList<Integer> numList = new ArrayList<Integer>();
			for (int i = 3; i < save.length; i++) {
				numList.add(Integer.valueOf(save[i]));
			}
			returnList.add(name);
			returnList.add(scores);
			returnList.add(numList);
			System.out.println("成功载入");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return returnList;
	}
}
