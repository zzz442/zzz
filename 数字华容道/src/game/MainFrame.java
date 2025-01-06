package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.SQLOutput;


//自定义窗口类，创建对象，展示一个主窗口
public class MainFrame extends JFrame {
    private static final String IMAGE_PATH = "stone-maze/src/img/";

    //准备一个数组，用来保存数字色块的行列位置：4行4列
    private int[][] imageData = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };

    //准备一个数组，定义最终游戏的成功的顺序
    private int[][] winData = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 0}
    };

    //定义两个变量记录当前空白色块的位置
    private int row;//行
    private int col;//列
    private int count = 0;//记录步数
    private int minCount = 0 ;

    public MainFrame() {

        minCount = initMinCount();

        //1、调用一个初始化方法，初始化窗口大小等信息
        initFrame();
        //4、打乱数组色块顺序，再展示图片
        initRadmoArray();
        //2、初始化界面，展示数字色块
        initImage();
        //3、添加菜单
        initMenu();
        //5、添加键盘监听事件
        initKeyPressEvent();
        //设置窗口是否可见
        this.setVisible(true);


        System.out.println("最小步数：" + minCount);
    }


    private void swichAndMove(Direction direction) {
        //控制图片移动
        switch (direction) {
            case UP:
                //上交换的条件是必须<3,然后才开始交换
                if (row < imageData.length - 1) {
                    //当前色块的位置 row col
                    //需要被交换的位置 row + 1  col
                    int temp = imageData[row][col];
                    imageData[row][col] = imageData[row + 1][col];
                    imageData[row + 1][col] = temp;
                    //更新当前空白色块的位置
                    row++;
                    //移动次数累加
                    count++;
                }
                break;
            case DOWN:
                if (row > 0) {
                    int temp = imageData[row][col];
                    imageData[row][col] = imageData[row - 1][col];
                    imageData[row - 1][col] = temp;
                    //更新当前空白色块的位置
                    row--;
                    //移动次数累加
                    count++;
                }
                break;
            case LEFT:
                if (col < imageData.length - 1) {
                    int temp = imageData[row][col];
                    imageData[row][col] = imageData[row][col + 1];
                    imageData[row][col + 1] = temp;
                    //更新当前空白色块的位置
                    col++;
                    //移动次数累加
                    count++;
                }
                break;
            case RIGHT:
                if (col > 0) {
                    int temp = imageData[row][col];
                    imageData[row][col] = imageData[row][col - 1];
                    imageData[row][col - 1] = temp;
                    //更新当前空白色块的位置
                    col--;
                    //移动次数累加
                    count++;
                }
                break;
        }
        initImage();


    }

    private void initKeyPressEvent() {
        //给当前窗口绑定上下左右按键事件
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //获取当前按钮的编号
                int keyCode = e.getKeyCode();
                //判断这个编号是不是上下左右键
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        //用户按了上键，图片上移
                        swichAndMove(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        //用户按了下键，图片下移
                        swichAndMove(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        //用户按了左键，图片左移
                        swichAndMove(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        //用户按了右键，图片右移
                        swichAndMove(Direction.RIGHT);
                        break;
                }
            }
        });
    }


    private void initRadmoArray() {
        //打乱二维数组中的元素顺序
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
//                int indexRadmo = (int) (Math.random() * 16);
//                int temp = imageData[i][j];
//                imageData[i][j] = imageData[indexRadmo / 4][indexRadmo % 4];
//                imageData[indexRadmo / 4][indexRadmo % 4] = temp;

                int H1 = (int) (Math.random() * imageData.length);
                int L1 = (int) (Math.random() * imageData.length);

                int H2 = (int) (Math.random() * imageData.length);
                int L2 = (int) (Math.random() * imageData.length);

                int temp = imageData[i][j];
                imageData[i][j] = imageData[H1][L1];
                imageData[H1][L1] = temp;
            }
        }
        //定位空白色块的位置
        //去二维数组中遍历每个数据，只要发现这个数据等于0，这个位置就是当前色块的位置
        OUT:
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
                if (imageData[i][j] == 0) {
                    row = i;
                    col = j;
                    break OUT;
                }
            }
        }
    }


    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("系统");
        JMenuItem again = new JMenuItem("重新开始");
        JMenuItem out = new JMenuItem("退出游戏");
        menu.add(again);
        menu.add(out);
        out.addActionListener(e -> {
            dispose();
        });
        again.addActionListener(e -> {
            //重启游戏
            this.dispose();
            new MainFrame();
        });
        menuBar.add(menu);
        this.setJMenuBar(menuBar);
    }

    private void initImage() {
        //先清空窗口上的图层
        this.getContentPane().removeAll();

        //刷新界面时可以给界面展示步数
        //给窗口添加一个展示文字的组件
        JLabel stepLabel = new JLabel("步数：" + count);
        stepLabel.setBounds(5, 2, 100, 20);
        //把文字展示成蓝色
        stepLabel.setForeground(Color.BLUE);
        //加粗
        stepLabel.setFont(new Font(null, Font.BOLD, 20));
        //字体改为楷体
        stepLabel.setFont(new Font("楷体", Font.BOLD, 20));
        this.add(stepLabel);



        //问问是否是第一次玩游戏，展示还没有历史胜利
        if (minCount != 0){
            JLabel stepLabel2 = new JLabel("历史胜利最小步数：" + minCount);
            stepLabel2.setBounds(200, 2, 1000, 20);
            //把文字展示成蓝色
            stepLabel2.setForeground(Color.BLUE);

            stepLabel2.setFont(new Font("楷体", Font.BOLD, 20));
            this.add(stepLabel2);
        }else {
            JLabel stepLabel2 = new JLabel("没有历史步数" );
            stepLabel2.setBounds(200, 2, 1000, 20);
            //把文字展示成蓝色
            stepLabel2.setForeground(Color.BLUE);

            stepLabel2.setFont(new Font("楷体", Font.BOLD, 20));
            this.add(stepLabel2);
        }

        //判断是否赢了
        if (isWin()) {
            //展示胜利的图片
            JLabel label = new JLabel(new ImageIcon(IMAGE_PATH + "win.png"));
            label.setBounds(0, 0, 1440, 909);
            this.add(label);

            //读取文件中的最小步数，看是否需要更新
            int fileMinCount = initMinCount();
            //判断这个步数是否是0，是0说明是第一次玩游戏，直接写入当前胜利的步数
            if (fileMinCount == 0 || fileMinCount > count) {
                writeMinCount(count);
            }
        }
        //1、展示一个行列矩阵的图片色块依次铺满窗口（4 * 4）
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
                //获取每个数字
                int num = imageData[i][j];
                //创建一个数字色块对象
                ImageIcon icon = new ImageIcon(IMAGE_PATH + num + ".png");
                //创建一个数字色块对象
                JLabel label = new JLabel(icon);
                //设置数字色块的位置
                label.setBounds(26 + j * 120, 23 + i * 120, 120, 120);
                this.add(label);
            }
        }
        //将背景添加到内容面板
        JLabel background = new JLabel(new ImageIcon(IMAGE_PATH + "background.png"));
        background.setBounds(0, -30, 552, 552);
        this.add(background);
        //在刷新图层
        this.repaint();
    }
    //把当前最小步数写入到文件中去更新

    public void writeMinCount(int count) {
        try (
                FileWriter fw = new FileWriter("gameText\\src\\score.txt");
                BufferedWriter bw = new BufferedWriter(fw);
        ) {
            //把当前步数写入到文件中去
            bw.write(count + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取score.txt文件中的最小步数
    private int initMinCount() {
        try (
                FileReader fr = new FileReader("gameText\\src\\score.txt");
                BufferedReader br = new BufferedReader(fr);
        ) {

            String line = br.readLine();
            return Integer.parseInt(line);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean isWin() {
        //判断游戏二维数组和赢了之后的二维数组的内容是否一样，如果有一个不一样，说明没有赢
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[i].length; j++) {
                if (imageData[i][j] != winData[i][j]) {
                    return false;
                }
            }
        }
        //赢了
        return true;
    }


    private void initFrame() {
        //设置窗口的标题
        this.setTitle("石子迷宫");
        //设置窗口的大小
        this.setSize(552, 572);
        //设置窗口的位置
        this.setLocation(300, 200);

        //设置窗口是否可以最大化
        this.setResizable(false);
        //设置窗口关闭方式
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗口居中显示
        this.setLocationRelativeTo(null);

    }


}
