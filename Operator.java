package main;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * <p>Title: 2048的计算操作类</p>
 * <p>Description: 通过operation()操作</p>
 * @version 1.0
 * @author Xgl
 */
public class Operator {
    /* numList 待操作数组
     * numListLast 待操作数组的上一步
     * sideLength 边长
     * change 关于数组是否改变的布尔值
     * scores 成绩
     * 方向二维数组 操作方向位置储存处
     */
	public ArrayList<Integer> numListLast = new ArrayList<Integer>();
    private ArrayList<Integer> numList;
    private int sideLength;
    private int[][] UP;
    private int[][] LEFT;
    private int[][] RIGHT;
    private int[][] DOWN;
    private int scores;
	private boolean change;
    final public int IMTEMP = -100000;
    
  //setter、getter方法
    public ArrayList<Integer> getNumList() {
		return numList;
	}
    
    public void setNumList(ArrayList<Integer> numListIn){
    	for (int i = 0; i < numListIn.size(); i++) {
			this.numList.set(i, numListIn.get(i));
		}
    }
    
	public int getScores() {
		return scores;
	}
	
	public void setScores(int scores){
    	this.scores = scores;
    }
	
	public Operator(){
		this.numList = new ArrayList<Integer>();
	}
    
    /**
     * <p>移动的实现,直接调用成员变量获得结果</p>
     * @param direction 要移动的方向
     * @param scores 当前分数
     * @param numList 当前数字列表
     */
    public void operation(int direction, int scores, ArrayList<Integer> numList){
        this.scores = scores;
        this.numList = numList;
        this.sideLength = (int) Math.sqrt(numList.size());
        
        //当numList全0时，把numListLast也清零
        if(allZero(numList) == true || scores == IMTEMP){
        	for (int i = 0; i < sideLength*sideLength; i++) {
                numListLast.add(0);
            }
        }
        
        UP = new int[sideLength][sideLength];
        RIGHT = new int[sideLength][sideLength];
        DOWN = new int[sideLength][sideLength];
        LEFT = new int[sideLength][sideLength];

        //为向左LEFT移动初始化位置
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                LEFT[i][j] = sideLength*i+j;
            }
        }

        //为向下DOWN移动初始化位置
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                DOWN[i][j] = sideLength*j+i;
            }
        }

        //为向上UP移动初始化位置(反置DOWN)
        for (int i = 0; i < sideLength; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.clear();
            for (int j = 0; j < sideLength ; j++) {
                temp.add(DOWN[i][j]);
            }
            Collections.reverse(temp);
            for (int j = 0; j < temp.toArray().length; j++) {
                UP[i][j] = (int) temp.toArray()[j];
            }
        }

        //为向右RIGHT移动初始化位置(反置LEFT)
        for (int i = 0; i < sideLength; i++) {
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.clear();
            for (int j = 0; j < sideLength ; j++) {
                temp.add(LEFT[i][j]);
            }
            Collections.reverse(temp);
            for (int j = 0; j < temp.toArray().length; j++) {
                RIGHT[i][j] = (int) temp.toArray()[j];
            }
        }

        //移动逻辑
        if(direction == KeyEvent.VK_D){
            this.move(LEFT);
        }else if(direction == KeyEvent.VK_A){
            this.move(RIGHT);
        }else if(direction == KeyEvent.VK_W){
            this.move(UP);
        }else if(direction == KeyEvent.VK_S){
            this.move(DOWN);
        }else if(direction == KeyEvent.VK_B){
        	//悔步
        	for (int i = 0; i < sideLength*sideLength; i++) {
        		this.numList.set(i, this.numListLast.get(i));
        	}
        }
        else{System.err.println("input error");}

    }

    /**
     * <p>无参数，无返回，直接操作成员变量（即num数组），实现游戏中随机2,4效果</p>
     */
    protected void random(){
        int newOne[]={2,2,4};

        //获取空位置
        ArrayList<Integer> emptyIndex = new ArrayList<Integer>();
        for (int i = 0; i < this.numList.size(); i++) {
            if(this.numList.get(i)==0){
                emptyIndex.add(i);
            }
        }

        //进行随机24
        Random r = new Random();
        this.numList.set(emptyIndex.get
                (r.nextInt(emptyIndex.size())), 
                newOne[r.nextInt(3)]);
    }

    /**
     * <p>输入排列方向，操作成员变量数字列表</p>
     * @param b 排列顺序（方向）(二维数组)
     */
    protected void move(int[][] b){
    	String numBefore = this.numList.toString();
    	
    	//numListLast保存变化前的数字列表
    	if (numListLast.isEmpty()) {
    		for (int i = 0; i < sideLength*sideLength; i++) {
        		this.numListLast.add(this.numList.get(i));
        	}
		}else{
			for (int i = 0; i < sideLength*sideLength; i++) {
	    		this.numListLast.set(i, this.numList.get(i));
	    	}
		}
    	
        for (int j = 0; j< sideLength; j++){

            //nums 储存真实的数字,每次循环只操作num，作为一个temp使用
            //sequence 读取出b内的小顺序
            int[] nums = new int[sideLength];
            int[] sequence = b[j];
            for (int k=0;k < sideLength; k++){
                nums[k]= this.numList.get(sequence[k]);
            }

            //zeroExits 布尔值储存是否应该要消零
            boolean zeroExsists = true;
            //实现移动跨过0的效果,便于后面for循环的相加
            while (zeroExsists == true){
                zeroExsists = false;
                for (int m = 1; m < sideLength; m++){
                    if (nums[m] == 0 && nums[m-1] != 0){
                        nums[m] = nums[m-1];
                        nums[m-1] = 0;
                        zeroExsists = true;
                    }
                }
            }

            //实现相加、记录成绩功能
            for (int l = sideLength-1; l > 0; l--){
                   if (nums[l] == nums[l-1]){
                       nums[l] = nums[l]*2;
                       this.scores = this.scores+nums[l];
                       nums[l-1] = 0;
                   }
            }

            //实现移动跨过0的效果
            zeroExsists = true;
            while (zeroExsists == true){
                zeroExsists = false;
                for (int m = 1; m < sideLength; m++){
                    if (nums[m] == 0 && nums[m-1] != 0){
                        nums[m] = nums[m-1];
                        nums[m-1] = 0;
                        zeroExsists = true;
                    }
                }
            }

            //将移动后的值 赋值回原来的数字列表
            for (int k = 0; k < sideLength; k++){
                this.numList.set(sequence[k], nums[k]);
            }
        }

        //判断是否改变了数组
        String numAfter = this.numList.toString();
        if (numBefore.equals(numAfter)){
            this.change = false;
        }else{
            this.change = true;
            this.scores--;//每移动一次减一分;}
        }
        
        if (this.change == true || allZero(this.numList) == true) {
            this.random();//随机数
        }
    }
    
    /**
     * <br>检测是否全为0
     * @param numList
     * @return 该list是否全为0的bool值
     */
    protected boolean allZero(ArrayList<Integer> numList) {
    	for (int num : numList) {
            if(num!=0) return false;
        }
    	return true;
	}
}
